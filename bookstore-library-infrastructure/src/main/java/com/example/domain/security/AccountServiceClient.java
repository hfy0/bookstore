package com.example.domain.security;

import com.example.domain.account.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户信息的远程服务客户端
 * 各个工程都可能涉及取当前用户之类的操作，将此客户端放到基础包以便通用
 **/
@FeignClient(name = "account")
public interface AccountServiceClient {
    @GetMapping("/restful/accounts/{username}")
    Account findByUsername(@PathVariable("username") String username);

    @GetMapping("/restful/accounts/hello")
    String hello();
}
