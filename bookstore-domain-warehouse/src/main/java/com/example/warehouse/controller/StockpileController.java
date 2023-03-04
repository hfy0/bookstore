package com.example.warehouse.controller;

import com.example.domain.security.Role;
import com.example.warehouse.service.StockpileService;

import com.example.domain.myEnum.DeliveredStatus;
import com.example.domain.warehouse.Stockpile;
import com.example.infrastructure.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.RolesAllowed;

/**
 * 库存
 */
@RestController
@RequestMapping("/restful/products/stockpile")
public class StockpileController {

    @Autowired
    private StockpileService service;

    /**
     * 将指定的产品库存调整为指定数额
     */
    @PatchMapping("/{productId}")
    @RolesAllowed(Role.ADMIN)
    @PreAuthorize("#oauth2.hasAnyScope('BROWSER')")
    public String updateStockpile(@PathVariable("productId") Integer productId, @RequestParam("amount") Integer amount) {
        service.setStockpileAmountByProductId(productId, amount);
        return CommonResponse.success();
    }

    /**
     * 查询指定产品的库存
     */
    @GetMapping("/{productId}")
    @RolesAllowed(Role.ADMIN)
    @PreAuthorize("#oauth2.hasAnyScope('BROWSER','SERVICE')")
    public Stockpile queryStockpile(@PathVariable("productId") Integer productId) {
        return service.getStockpile(productId);
    }

    // 以下是开放给内部微服务调用的方法

    /**
     * 将指定的产品库存调整为指定数额
     */
    @PatchMapping("/delivered/{productId}")
    @PreAuthorize("#oauth2.hasAnyScope('SERVICE')")
    public String setDeliveredStatus(@PathVariable("productId") Integer productId, @RequestParam("status") DeliveredStatus status, @RequestParam("amount") Integer amount) {
        service.setDeliveredStatus(productId, status, amount);
        return CommonResponse.success();
    }
}
