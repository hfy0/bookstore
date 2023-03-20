package com.example.payment.service;

import com.example.config.CacheConfiguration;
import com.example.domain.myEnum.PayStatus;
import com.example.domain.payment.Wallet;
import com.example.dto.Item;
import com.example.payment.mapper.PaymentMapper;
import com.example.payment.mapper.WalletMapper;
import com.example.payment.mapper.ud.PaymentUDMapper;
import com.example.payment.mapper.ud.WalletUDMapper;
import com.example.domain.payment.Payment;
import com.example.dto.Settlement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    /**
     * 默认支付单超时时间：2分钟（缓存TTL时间的一半）
     */
    private static final Integer DEFAULT_PRODUCT_FROZEN_EXPIRES = CacheConfiguration.SYSTEM_DEFAULT_EXPIRES / 2;

    private final Timer timer = new Timer();

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private WalletUDMapper walletUDMapper;

    @Autowired
    private ProductServiceClient stockpileService;

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private PaymentUDMapper paymentUDMapper;

    @Autowired
    private ProductServiceClient productService;

    @Resource(name = "settlement")
    private Cache settlementCache;

    /**
     * 根据结算清单的内容执行，生成对应的支付单
     */
    public Payment executeBySettlement(Settlement bill) {
        // 从服务中获取商品的价格，计算要支付的总价（安全原因，这个不能由客户端传上来）
        productService.replenishProductInformation(bill);
        // 冻结部分库存（保证有货提供）,生成付款单
        Payment payment = producePayment(bill);
        // 设立解冻定时器（超时未支付则释放冻结的库存和资金）
        setupAutoThawedTrigger(payment);
        return payment;
    }

    /**
     * 完成支付
     * 立即取消解冻定时器，执行扣减库存和资金
     */
    public void accomplishPayment(Integer accountId, String payId) {
        // 订单从冻结状态变为派送状态，扣减库存
        BigDecimal price = accomplish(payId);
        try {
            // 扣减货款
            decrease(accountId, price);
        } catch (Throwable e) {
            // 扣款失败，将已支付的记录回滚恢复库存
            rollbackSettlement(PayStatus.PAYED, payId);
            throw e;
        }
        // 支付成功的清除缓存
        settlementCache.evict(payId);
    }

    /**
     * 取消支付
     * 立即触发解冻定时器，释放库存和资金
     */
    public void cancelPayment(String payId) {
        // 释放冻结的库存
        cancel(payId);
        // 支付成功的清除缓存
        settlementCache.evict(payId);
    }

    /**
     * 生成支付单：根据结算单冻结指定的货物，计算总价，生成支付单
     */
    public Payment producePayment(Settlement bill) {
        // 计算订单的总价
        BigDecimal total = BigDecimal.ZERO;
        List<Item> items = bill.getItems();
        for (Item item : items) {
            Integer productId = item.getProductId();
            Integer amount = item.getAmount();
            stockpileService.frozen(productId, amount);

            BigDecimal money = bill.productMap.get(productId).getPrice().multiply(BigDecimal.valueOf(amount));
            // 注意这里要累计。total.add(money)是+不是+=
            total = total.add(money);
        }
        // 12元固定运费，客户端写死的，这里陪着演一下，避免总价对不上
        total = total.add(BigDecimal.valueOf(12));

        Payment payment = new Payment(total, DEFAULT_PRODUCT_FROZEN_EXPIRES);
        paymentMapper.insert(payment);
        // 将支付单存入缓存
        settlementCache.put(payment.getPayId(), bill);
        log.info("创建支付订单，总额：{}", payment.getTotalPrice());
        return payment;
    }

    /**
     * 完成支付单。意味着客户已经完成付款
     */
    public BigDecimal accomplish(String payId) {
        synchronized (payId.intern()) {
            Payment payment = paymentUDMapper.getByPayId(payId);
            if (PayStatus.WAITING.getCode() == (int) payment.getPayState()) {
                payment.setPayState(PayStatus.PAYED.getCode());
                paymentMapper.updateByPrimaryKeySelective(payment);
                accomplishSettlement(PayStatus.PAYED, payment.getPayId());
                log.info("编号为{}的支付单已处理完成，等待支付", payId);
                return payment.getTotalPrice();
            } else {
                throw new UnsupportedOperationException("当前订单不允许支付，当前状态为：" + payment.getPayState());
            }
        }
    }

    /**
     * 取消支付单
     */
    public void cancel(String payId) {
        synchronized (payId.intern()) {
            Payment payment = paymentUDMapper.getByPayId(payId);
            if (PayStatus.WAITING.getCode() == (int) payment.getPayState()) {
                payment.setPayState(PayStatus.CANCEL.getCode());
                paymentMapper.updateByPrimaryKeySelective(payment);
                accomplishSettlement(PayStatus.CANCEL, payment.getPayId());
                log.info("编号为{}的支付单已被取消", payId);
            } else {
                throw new UnsupportedOperationException("当前订单不允许取消，当前状态为：" + PayStatus.parse(String.valueOf(payment.getPayState())));
            }
        }
    }

    /**
     * 设置支付单自动冲销解冻的触发器
     * <p>
     * 如果在触发器超时之后，如果支付单未仍未被支付（状态是WAITING）
     * 则自动执行冲销，将冻结的库存商品解冻，以便其他人可以购买，并将Payment的状态修改为ROLLBACK。
     */
    public void setupAutoThawedTrigger(Payment payment) {
        timer.schedule(new TimerTask() {
            public void run() {
                synchronized (payment.getPayId().intern()) {
                    // 使用2分钟之前的Payment到数据库中查出当前的Payment
                    Payment currentPayment = paymentMapper.selectByPrimaryKey(payment.getId());
                    if (currentPayment == null) {
                        throw new RuntimeException(payment.getId().toString());
                    }

                    if (PayStatus.WAITING.getCode() == (int) currentPayment.getPayState()) {
                        log.info("支付单{}当前状态为：WAITING，转变为：TIMEOUT", payment.getId());
                        accomplishSettlement(PayStatus.TIMEOUT, payment.getPayId());
                    }
                }
            }
        }, payment.getExpires());
    }

    /**
     * 根据支付状态，实际调整库存（扣减库存或者解冻）
     */
    private void accomplishSettlement(PayStatus endState, String payId) {
        Settlement settlement = (Settlement) Objects.requireNonNull(Objects.requireNonNull(settlementCache.get(payId)).get());
        settlement.getItems().forEach(i -> {
            if (PayStatus.PAYED.equals(endState)) {
                stockpileService.decrease(i.getProductId(), i.getAmount());
            } else {
                // 其他状态，无论是TIMEOUT还是CANCEL，都进行解冻
                stockpileService.thawed(i.getProductId(), i.getAmount());
            }
        });
    }

    /**
     * 回滚库存调整
     */
    public void rollbackSettlement(PayStatus endState, String payId) {
        Settlement settlement = (Settlement) Objects.requireNonNull(Objects.requireNonNull(settlementCache.get(payId)).get());
        settlement.getItems().forEach(i -> {
            if (PayStatus.PAYED.equals(endState)) {
                stockpileService.increase(i.getProductId(), i.getAmount());
            } else {
                stockpileService.frozen(i.getProductId(), i.getAmount());
            }
        });
    }

    /**
     * 账户资金减少
     */
    public void decrease(Integer accountId, BigDecimal amount) {
        Wallet wallet = walletUDMapper.findByAccountId(accountId);
        // 初始账户资金 999999999
        if (wallet == null) {
            wallet = new Wallet(BigDecimal.valueOf(999999999), accountId);
            walletMapper.insert(wallet);
        }
        if (wallet.getMoney().compareTo(amount) >= 0) {
            wallet.setMoney(wallet.getMoney().subtract(amount));
            walletMapper.updateByPrimaryKeySelective(wallet);
            log.info("支付成功。用户余额：{}，本次消费：{}", wallet.getMoney(), amount);
        } else {
            throw new RuntimeException("用户余额不足以支付，请先充值");
        }
    }

    /**
     * 账户资金增加
     */
    public void increase(Integer accountId, Double amount) {
    }

    /**
     * 账户资金冻结
     * 从正常资金中移动指定数量至冻结状态
     */
    public void frozen(Integer accountId, Double amount) {
    }

    /**
     * 账户资金解冻
     * 从冻结资金中移动指定数量至正常状态
     */
    public void thawed(Integer accountId, Double amount) {
    }
}
