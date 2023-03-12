package com.example.payment.controller;

import com.example.payment.service.PaymentService;
import com.example.payment.validation.SufficientStock;
import com.example.domain.payment.Payment;
import com.example.dto.Settlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * 结算清单相关
 **/
@RestController
@RequestMapping("/restful")
public class SettlementController {

    @Autowired
    private PaymentService service;

    /**
     * 提交一张交易结算单，根据结算单中的物品，生成支付单
     */
    @PostMapping("/settlements")
    // @RolesAllowed(Role.USER)
    public Payment executeSettlement(@Valid @SufficientStock @RequestBody Settlement settlement) {
        return service.executeBySettlement(settlement);
    }
}
