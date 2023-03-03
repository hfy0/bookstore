package com.example.account.service;

import com.example.account.mapper.ud.AccountUDMapper;
import com.example.domain.account.Account;
import com.example.account.mapper.AccountMapper;
import com.example.infrastructure.security.Encryption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountMapper mapper;
    @Autowired
    private AccountUDMapper udMapper;

    @Autowired
    private Encryption encoder;

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
