package com.example.security.config;

import com.example.config.ResourceServerConfiguration;
import com.example.security.provider.PreAuthenticatedAuthenticationProvider;
import com.example.security.provider.UsernamePasswordAuthenticationProvider;
import com.example.domain.security.AuthenticAccountDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class AuthenticationServerConfig extends WebSecurityConfig {

    @Autowired
    private AuthenticAccountDetailsService authenticAccountDetailsService;
    
    @Autowired
    private UsernamePasswordAuthenticationProvider userProvider;

    @Autowired
    private PreAuthenticatedAuthenticationProvider preProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 需要把AuthenticationManager主动暴漏出来
     * 以便在授权服务器{@link AuthorizationServerConfig}中可以使用它来完成用户名、密码的认证
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置Spring Security的安全认证服务
     * Spring Security的Web安全设置，将在资源服务器配置{@link ResourceServerConfiguration}中完成
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(authenticAccountDetailsService).passwordEncoder(passwordEncoder);
        auth.authenticationProvider(userProvider);
        // 谁放前面谁先执行
        // 这里我通过源码观察到，如果前一个 AuthenticationProvider 的返回值不为 null，那么之后的AuthenticationProvider将不会执行
        auth.authenticationProvider(preProvider);
    }
}
