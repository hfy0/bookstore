package com.example.payment.mapper.ud;

import org.apache.ibatis.annotations.Select;
import com.example.domain.payment.Payment;

public interface PaymentUDMapper {

    @Select("select * from payment where pay_id = #{payId,jdbcType=VARCHAR}")
    Payment getByPayId(String payId);
}
