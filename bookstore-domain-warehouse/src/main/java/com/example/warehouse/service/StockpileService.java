package com.example.warehouse.service;

import com.example.warehouse.mapper.StockpileMapper;
import com.example.warehouse.mapper.ud.StockpileUDMapper;
import com.example.domain.myEnum.DeliveredStatus;
import com.example.domain.warehouse.Stockpile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

/**
 * 商品库存
 */
@Service
@Transactional
public class StockpileService {

    private static final Logger log = LoggerFactory.getLogger(com.example.warehouse.service.StockpileService.class);

    @Autowired
    private StockpileMapper stockpileMapper;

    @Autowired
    private StockpileUDMapper stockpileUDMapper;

    /**
     * 根据产品查询库存
     */
    public Stockpile getStockpile(Integer productId) {
        Stockpile stockpile = stockpileUDMapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(stockpile)) {
            new RuntimeException("该商品无库存信息 productId=" + productId.toString());
        }
        return stockpile;
    }

    /**
     * 将指定的产品库存调整为指定数额
     */
    public void setStockpileAmountByProductId(Integer productId, Integer amount) {
        Stockpile stockpile = stockpileUDMapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(stockpile)) {
            new RuntimeException("该商品无库存信息 productId=" + productId.toString());
        }

        stockpile.setAmount(amount);
        stockpileMapper.updateByPrimaryKeySelective(stockpile);
    }

    /**
     * 调整商品出库状态
     */
    public void setDeliveredStatus(Integer productId, DeliveredStatus status, Integer amount) {
        switch (status) {
            case DECREASE:
                decrease(productId, amount);
                break;
            case INCREASE:
                increase(productId, amount);
                break;
            case FROZEN:
                frozen(productId, amount);
                break;
            case THAWED:
                thawed(productId, amount);
                break;
        }
    }

    /**
     * 货物售出
     * 从冻结状态的货物中扣减
     */
    private void decrease(Integer productId, Integer amount) {
        Stockpile stockpile = stockpileUDMapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(stockpile)) {
            new RuntimeException("该商品无库存信息 productId=" + productId.toString());
        }

        stockpile.decrease(amount);
        stockpileMapper.updateByPrimaryKeySelective(stockpile);
        log.info("库存出库，商品：{}，数量：{}，现有库存：{}，现存冻结：{}", productId, amount, stockpile.getAmount(), stockpile.getFrozen());
    }

    /**
     * 货物增加
     * 增加指定数量货物至正常货物状态
     */
    private void increase(Integer productId, Integer amount) {
        Stockpile stockpile = stockpileUDMapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(stockpile)) {
            new RuntimeException("该商品无库存信息 productId=" + productId.toString());
        }

        stockpile.increase(amount);
        stockpileMapper.updateByPrimaryKeySelective(stockpile);
        log.info("库存入库，商品：{}，数量：{}，现有库存：{}，现存冻结：{}", productId, amount, stockpile.getAmount(), stockpile.getFrozen());
    }


    /**
     * 货物冻结
     * 从正常货物中移动指定数量至冻结状态
     */
    private void frozen(Integer productId, Integer amount) {
        Stockpile stockpile = stockpileUDMapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(stockpile)) {
            new RuntimeException("该商品无库存信息 productId=" + productId.toString());
        }

        stockpile.frozen(amount);
        stockpileMapper.updateByPrimaryKeySelective(stockpile);
        log.info("冻结库存，商品：{}，数量：{}，现有库存：{}，现存冻结：{}", productId, amount, stockpile.getAmount(), stockpile.getFrozen());
    }

    /**
     * 货物解冻
     * 从冻结货物中移动指定数量至正常状态
     */
    private void thawed(Integer productId, Integer amount) {
        Stockpile stockpile = stockpileUDMapper.selectByProductId(productId);
        if (ObjectUtils.isEmpty(stockpile)) {
            new RuntimeException("该商品无库存信息 productId=" + productId.toString());
        }
        stockpile.thawed(amount);
        stockpileMapper.updateByPrimaryKeySelective(stockpile);
        log.info("解冻库存，商品：{}，数量：{}，现有库存：{}，现存冻结：{}", productId, amount, stockpile.getAmount(), stockpile.getFrozen());
    }
}
