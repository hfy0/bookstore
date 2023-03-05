package com.example.domain.security;

import com.example.domain.account.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 认证用户的数据仓库
 **/
@Component
public class AuthenticAccountRepository {
    @Autowired
    private AccountServiceClient accountServiceClient;

    public AuthenticAccount findByUsername(String username) {
        Account account = accountServiceClient.findByUsername(username);
        return new AuthenticAccount(account);
    }
}
