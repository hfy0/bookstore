package com.example.dto;

import com.example.domain.warehouse.Product;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * 支付结算单模型
 **/
public class Settlement {

    @Size(min = 1, message = "结算单中缺少商品清单")
    private List<Item> items;

    @NotNull(message = "结算单中缺少配送信息")
    private Purchase purchase;

    /**
     * 购物清单中的商品信息
     * 基于安全原因（避免篡改价格），该信息不会从客户端获取，需在服务端根据商品ID再查询出来
     */
    public transient Map<Integer, Product> productMap;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Purchase getPurchase() {
        return purchase;
    }

    public void setPurchase(Purchase purchase) {
        this.purchase = purchase;
    }


}
