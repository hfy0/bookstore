package com.example.payment.service;

import com.example.domain.myEnum.DeliveredStatus;
import com.example.domain.warehouse.Product;
import com.example.domain.warehouse.Stockpile;
import com.example.dto.Settlement;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 仓库商品和库存相关远程服务
 */
@FeignClient(name = "warehouse")
public interface ProductServiceClient {

    default void replenishProductInformation(Settlement bill) {
        bill.productMap = Stream.of(getProducts()).collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    @GetMapping("/restful/products/{id}")
    Product getProduct(@PathVariable("id") Integer id);

    @GetMapping("/restful/products")
    Product[] getProducts();

    default void decrease(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.DECREASE, amount);
    }

    default void increase(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.INCREASE, amount);
    }

    default void frozen(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.FROZEN, amount);
    }

    default void thawed(Integer productId, Integer amount) {
        setDeliveredStatus(productId, DeliveredStatus.THAWED, amount);
    }

    @PatchMapping("/restful/products/stockpile/delivered/{productId}")
    void setDeliveredStatus(@PathVariable("productId") Integer productId, @RequestParam("status") DeliveredStatus status, @RequestParam("amount") Integer amount);

    @GetMapping("/restful/products/stockpile/{productId}")
    Stockpile queryStockpile(@PathVariable("productId") Integer productId);
}
