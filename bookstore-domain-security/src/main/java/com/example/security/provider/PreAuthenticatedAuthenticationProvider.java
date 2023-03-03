package com.example.security.provider;


import com.example.domain.security.AuthenticAccount;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class PreAuthenticatedAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication.getPrincipal() instanceof UsernamePasswordAuthenticationToken) {
            AuthenticAccount user = (AuthenticAccount) ((UsernamePasswordAuthenticationToken) authentication.getPrincipal()).getPrincipal();
            // 检查用户没有停用、锁定、密码过期、账号过期等问题
            // 在本项目中这些功能都未启用，实际上此检查肯定是会通过的，但为了严谨和日后扩展，还是依次进行了检查
            if (user.isEnabled() && user.isCredentialsNonExpired() && user.isAccountNonExpired() && user.isCredentialsNonExpired()) {
                return new PreAuthenticatedAuthenticationToken(user, "", user.getAuthorities());
            } else {
                throw new DisabledException("用户状态不正确");
            }
        } else {
            throw new PreAuthenticatedCredentialsNotFoundException("预验证失败，传上来的令牌是怎么来的？");
        }
    }

    /**
     * 判断该验证器能处理哪些认证
     */
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(PreAuthenticatedAuthenticationToken.class);
    }
}
