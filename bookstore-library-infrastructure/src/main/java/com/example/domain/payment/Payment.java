package com.example.domain.payment;

import com.example.domain.account.Account;
import com.example.domain.myEnum.PayStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

public class Payment implements Serializable {
    private Integer id;

    private String payId;

    private Date createTime;

    private BigDecimal totalPrice;

    private Integer expires;

    private String paymentLink;

    private Integer payState;

    private static final long serialVersionUID = 1L;

    public Payment() {
    }

    public Payment(BigDecimal totalPrice, Integer expires) {
        setTotalPrice(totalPrice);
        setExpires(expires);
        setCreateTime(new Date());
        setPayState(PayStatus.WAITING.getCode());

        setPayId(UUID.randomUUID().toString());
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof Account) {
            // 产生支付单的时候是有用户的
            setPaymentLink("/pay/modify/" + getPayId() + "?state=PAYED&accountId=" + ((Account) principal).getId());
        } else {
            setPaymentLink("/pay/modify/" + getPayId() + "?state=PAYED");
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPayId() {
        return payId;
    }

    public void setPayId(String payId) {
        this.payId = payId == null ? null : payId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getExpires() {
        return expires;
    }

    public void setExpires(Integer expires) {
        this.expires = expires;
    }

    public String getPaymentLink() {
        return paymentLink;
    }

    public void setPaymentLink(String paymentLink) {
        this.paymentLink = paymentLink == null ? null : paymentLink.trim();
    }

    public Integer getPayState() {
        return payState;
    }

    public void setPayState(Integer payState) {
        this.payState = payState;
    }
}