package com.example.account.controller;

import com.example.account.service.AccountService;
import com.example.account.validation.AuthenticatedAccount;
import com.example.account.validation.NotConflictAccount;
import com.example.account.validation.UniqueAccount;
import org.example.domain.Account;
import org.example.infrastructure.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;


/**
 * 操作用户资源
 */
@RestController()
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService service;

    /**
     * 根据用户名称获取用户详情
     */
    @GetMapping("/{username}")
    // @PreAuthorize("#oauth2.hasAnyScope('SERVICE','BROWSER')")
    public Account getUser(@PathVariable("username") String username) {
        return service.findAccountByUsername(username);
    }

    /**
     * 创建新的用户
     */
    @PostMapping("/")
    public String createUser(@Valid @UniqueAccount Account user) {
        service.createAccount(user);
        return CommonResponse.success();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/")
    // @PreAuthorize("#oauth2.hasAnyScope('BROWSER')")
    public String updateUser(@Valid @AuthenticatedAccount @NotConflictAccount Account user) {
        service.updateAccount(user);
        return CommonResponse.success();
    }

    @GetMapping("/hello")
    public String hello() {
        boolean b = false;
        test(b);
        return "hello";
    }

    public void test(@Valid @AssertTrue boolean b) {
    }
}
