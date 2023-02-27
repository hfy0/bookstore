package com.example.account.service;

import com.example.account.mapper.ud.AccountUDMapper;
import org.example.domain.Account;
import com.example.account.mapper.AccountMapper;
import org.example.infrastructure.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    @Autowired
    private AccountMapper mapper;
    @Autowired
    private AccountUDMapper udMapper;

    private Encryption encoder = new Encryption();

    public void createAccount(Account account) {
        account.setPassword(encoder.encode(account.getPassword()));
        mapper.insert(account);
    }

    public Account findAccountByUsername(String username) {
        return udMapper.findByUsername(username);
    }

    public void updateAccount(Account account) {
        mapper.updateByPrimaryKeySelective(account);
    }

}
