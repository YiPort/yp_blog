package com.yiport.provider;

import com.yiport.domain.entity.LoginUser;
import com.yiport.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

/**
 * 账号密码认证器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountAuthenticationProvider implements AuthenticationProvider
{
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 进行账号密码认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String userAccount = authentication.getPrincipal().toString();
        // 获取用户信息
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(userAccount);
        // 返回经过认证的Authentication
        UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
                loginUser, null, loginUser.getAuthorities());
        result.setDetails(authentication.getDetails());
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
