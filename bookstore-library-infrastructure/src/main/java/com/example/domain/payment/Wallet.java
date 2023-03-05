package com.example.domain.payment;

import java.io.Serializable;
import java.math.BigDecimal;

public class Wallet implements Serializable {
    private Integer id;

    private BigDecimal money;

    private Integer accountId;

    private static final long serialVersionUID = 1L;

    public Wallet() {
    }

    public Wallet(BigDecimal money, Integer accountId) {
        this.money = money;
        this.accountId = accountId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }
}