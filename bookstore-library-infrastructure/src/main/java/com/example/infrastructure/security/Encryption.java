
package com.example.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;


/**
 * 默认的加密工具
 */
@Configuration
public class Encryption {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 使用默认加密算法进行编码
     */
    public String encode(CharSequence rawPassword) {
        return passwordEncoder().encode(Optional.ofNullable(rawPassword).orElse(""));
    }

}
