package com.example.domain.warehouse;

import java.io.Serializable;

public class Stockpile implements Serializable {
    private Integer id;

    private Integer amount;

    private Integer frozen;

    private Integer productId;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getFrozen() {
        return frozen;
    }

    public void setFrozen(Integer frozen) {
        this.frozen = frozen;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public void decrease(Integer number) {
        this.frozen -= number;
    }

    public void increase(Integer number) {
        this.frozen += number;
    }

    public void thawed(Integer number) {
        frozen(-1 * number);
    }

    public void frozen(Integer number) {
        this.amount -= number;
        this.frozen += number;
    }
}