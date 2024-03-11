package com.yiport.provider;

import com.yiport.domain.entity.LoginUser;
import com.yiport.exception.SystemException;
import com.yiport.service.LoginInfoService;
import com.yiport.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.yiport.constent.ExceptionDescription.ACCOUNT_DEACTIVATE;
import static com.yiport.constent.ExceptionDescription.ACCOUNT_PASSWORD_ERROR;
import static com.yiport.constent.ExceptionDescription.PASSWORD_ERROR;
import static com.yiport.constent.UserConstant.FAIL;
import static com.yiport.constent.UserConstant.LOGIN_BY_ACCOUNT;

/**
 * 账号密码认证器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AccountAuthenticationProvider implements AuthenticationProvider
{
    private final UserDetailsServiceImpl userDetailsService;
    private final LoginInfoService loginInfoService;
    private final HttpServletRequest request;

    @Lazy
    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;

    /**
     * 进行账号密码认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        String userAccount = authentication.getPrincipal().toString();
        String userPassword = authentication.getCredentials().toString();
        // 获取用户信息
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(userAccount);
        // 密码校验
        if (!bCryptPasswordEncoder.matches(userPassword, loginUser.getUser().getPassword()))
        {
            loginInfoService.recordLoginInfo(userAccount, FAIL, PASSWORD_ERROR, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(ACCOUNT_PASSWORD_ERROR);
        }
        // 账号状态校验
        if (!loginUser.isEnabled())
        {
            loginInfoService.recordLoginInfo(userAccount, FAIL, ACCOUNT_DEACTIVATE, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(ACCOUNT_DEACTIVATE);
        }
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
