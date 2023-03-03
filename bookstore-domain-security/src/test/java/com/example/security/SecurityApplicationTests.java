package com.example.security;

import com.example.domain.security.AccountServiceClient;
import com.example.domain.account.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurityApplicationTests {

    @Autowired
    private AccountServiceClient accountServiceClient;


    @Test
    void contextLoads() {
        Account account = accountServiceClient.findByUsername("icyfenix");
        // Account{id=1, username='icyfenix', password='$2a$10$LTqKTXXRb26SYG3MvFG1UuKhMgc/i6IbUl2RgApiWd39y1EqlXbD6', name='周志明', avatar='', telephone='18888888888', email='icyfenix@gmail.com', location='唐家湾港湾大道科技一路3号远光软件股份有限公司'}
        System.out.println(account);
    }

}
