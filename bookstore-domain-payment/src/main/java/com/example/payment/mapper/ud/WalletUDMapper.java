package com.example.payment.mapper.ud;

import org.apache.ibatis.annotations.Select;
import com.example.domain.payment.Wallet;

public interface WalletUDMapper {

    @Select("select * from wallet where account_id = #{accountId,jdbcType=INTEGER}")
    Wallet findByAccountId(Integer accountId);
}
