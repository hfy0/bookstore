package com.example.account.controller;

import com.example.account.service.AccountService;
import com.example.account.validation.AuthenticatedAccount;
import com.example.account.validation.NotConflictAccount;
import com.example.account.validation.UniqueAccount;
import com.example.domain.account.Account;
import com.example.infrastructure.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.AssertTrue;


/**
 * 操作用户资源
 */
@RestController()
@RequestMapping("/restful")
public class AccountController {

    @Autowired
    private AccountService service;

    /**
     * 根据用户名称获取用户详情
     */
    @GetMapping("/accounts/{username}")
    // @PreAuthorize("#oauth2.hasAnyScope('SERVICE','BROWSER')")
    public Account getUser(@PathVariable("username") String username) {
        return service.findAccountByUsername(username);
    }

    /**
     * 创建新的用户
     */
    @PostMapping("/accounts")
    public String createUser(@Valid @UniqueAccount @RequestBody Account user) {
        service.createAccount(user);
        return CommonResponse.success();
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/accounts")
    // @PreAuthorize("#oauth2.hasAnyScope('BROWSER')")
    public String updateUser(@Valid @AuthenticatedAccount @NotConflictAccount Account user) {
        service.updateAccount(user);
        return CommonResponse.success();
    }

    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

}
