package com.example.payment.validation;

import com.example.payment.service.ProductServiceClient;

import com.example.dto.Settlement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 结算单验证器
 * <p>
 * 结算单能够成功执行的约束是清单中每一项商品的库存量都足够。
 **/
@Component
public class SettlementValidator implements ConstraintValidator<SufficientStock, Settlement> {

    @Autowired
    private ProductServiceClient service;

    @Override
    public boolean isValid(Settlement value, ConstraintValidatorContext context) {
        return value.getItems().stream().noneMatch(i -> service.queryStockpile(i.getProductId()).getAmount() < i.getAmount());
    }
}
