/*
 * Copyright 2012-2020. the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. More information from:
 *
 *        https://github.com/fenixsoft
 */

package com.example.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT访问令牌
 * <p>
 * Spring Security OAuth2的{@link JwtAccessTokenConverter}提供了令牌的基础结构（令牌头、部分负载，如过期时间、JTI）的转换实现
 * 继承此类，在加入自己定义的负载信息即可使用。
 * <p>
 * 一般来说负载中至少要告知服务端当前用户是谁，但又不应存放过多信息导致HTTP Header过大，尤其不应存放敏感信息。
 */
public class JWTAccessToken extends JwtAccessTokenConverter {

    @Autowired
    public JWTAccessToken(UserDetailsService userDetailsService) {
        // 设置从资源请求中带上来的JWT令牌转换回安全上下文中的用户信息的查询服务
        // 如果不设置该服务，则从JWT令牌获得的Principal就只有一个用户名（令牌中确实就只存了用户名）

        // 将用户信息查询服务提供给默认的令牌转换器，使得转换令牌时自动根据用户名还原出完整的用户对象
        // 这方便了后面编码（可以在直接获得登陆用户信息），但也稳定地为每次请求增加了一次（从数据库/缓存）查询，自行取舍
        DefaultUserAuthenticationConverter converter = new DefaultUserAuthenticationConverter();
        converter.setUserDetailsService(userDetailsService);
        ((DefaultAccessTokenConverter) getAccessTokenConverter()).setUserTokenConverter(converter);
    }

    /**
     * 增强令牌
     * 增强主要就是在令牌的负载中加入额外的信息
     */
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Authentication user = authentication.getUserAuthentication();

        // 对于密码模式才会有用户信息，在微服务之间的客户端认证模式下不需要增强令牌
        if (user != null) {
            String[] authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toArray(String[]::new);
            Map<String, Object> payLoad = new HashMap<>();
            // Spring Security OAuth的JWT令牌默认实现中就加入了一个“user_name”的项存储了当前用户名

            payLoad.put("username", user.getName());
            payLoad.put("authorities", authorities);
            payLoad.put("iss", "我是令牌的签发者");
            payLoad.put("sub", "bookstore");
            ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(payLoad);
        }
        return super.enhance(accessToken, authentication);
    }
}
