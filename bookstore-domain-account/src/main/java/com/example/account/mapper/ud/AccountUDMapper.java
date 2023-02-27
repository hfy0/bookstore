package com.example.account.mapper.ud;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.domain.Account;

import java.util.Collection;

public interface AccountUDMapper {
    @Select("select * from account where username = #{username,jdbcType=VARCHAR}")
    Account findByUsername(String username);

    @Select("select * from account where id = #{id,jdbcType=INTEGER}")
    Account findById(Integer id);

    @Select("select * from account where username = #{username,jdbcType=VARCHAR} or email = #{email,jdbcType=VARCHAR} or telephone = #{telephone,jdbcType=VARCHAR}")
    Collection<Account> findByUsernameOrEmailOrTelephone(@Param("username") String username, @Param("email") String email, @Param("telephone") String telephone);
}
