package com.yiport.provider;

import com.yiport.domain.entity.LoginUser;
import com.yiport.service.MailCommonService;
import com.yiport.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import static com.yiport.constent.UserConstant.LOGIN_BY_MAIL;

/**
 * 邮箱验证码认证器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailAuthenticationProvider implements AuthenticationProvider
{
    private final UserDetailsServiceImpl userDetailsService;
    private final MailCommonService mailCommonService;

    /**
     * 进行验证码认证
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException
    {
        log.info("邮箱验证码 开始登录验证");
        String email = authentication.getPrincipal().toString();
        String mailCaptcha = authentication.getCredentials().toString();
        // 验证码验证
        mailCommonService.verifyCaptcha(LOGIN_BY_MAIL, email, mailCaptcha);
        // 获取用户信息
        LoginUser loginUser = (LoginUser) userDetailsService.loadUserByUsername(email);
        // 返回经过认证的Authentication
        MailAuthenticationToken result = new MailAuthenticationToken(loginUser, null,
                loginUser.getAuthorities());
        result.setDetails(authentication.getDetails());
        log.info("邮箱验证码 登录验证完成");
        return result;
    }

    @Override
    public boolean supports(Class<?> authentication)
    {
        boolean res = MailAuthenticationToken.class.isAssignableFrom(authentication);
        log.info("邮箱验证码 是否进行登录验证 res:{}", res);
        return res;
    }
}
