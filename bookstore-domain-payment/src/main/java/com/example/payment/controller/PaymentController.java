package com.example.payment.controller;

import com.example.payment.service.PaymentService;
import com.example.domain.account.Account;
import com.example.infrastructure.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 支付相关
 */
@RestController
@RequestMapping("/restful/pay")
public class PaymentController {

    @Autowired
    private PaymentService service;

    /**
     * 修改支付单据的状态
     */
    @PatchMapping("/{payId}")
    // @RolesAllowed(Role.USER)
    public String updatePaymentState(@PathVariable("payId") String payId, @RequestParam("state") String state) {
        Account account = (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return updatePaymentStateAlias(payId, account.getId(), state);
    }

    @GetMapping("/modify/{payId}")
    public String updatePaymentStateAlias(@PathVariable("payId") String payId, @RequestParam("accountId") Integer accountId, @RequestParam("state") String state) {
        if ("PAYED".equals(state)) {
            service.accomplishPayment(accountId, payId);
        } else {
            service.cancelPayment(payId);
        }
        return CommonResponse.success();
    }
}
