package com.yiport.listener;


import com.yiport.service.LoginInfoService;
import com.yiport.utils.ServletUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureDisabledEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static com.yiport.constent.ExceptionDescription.ACCOUNT_DEACTIVATE;
import static com.yiport.constent.ExceptionDescription.PASSWORD_ERROR;
import static com.yiport.constent.UserConstant.FAIL;
import static com.yiport.constent.UserConstant.LOGIN_BY_ACCOUNT;

/**
 * 用户登录失败监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent>
{
    private final LoginInfoService loginInfoService;

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event)
    {
        HttpServletRequest request = ServletUtils.getRequest();
        String message;
        if (event instanceof AuthenticationFailureBadCredentialsEvent)
        {
            // 提供的凭据是错误的，用户名或者密码错误
            // 为了获取更准确的信息，提示“密码错误”（由于用户名错误在loadUserByUsername中已经捕获）
            message = PASSWORD_ERROR;
        }
        else if (event instanceof AuthenticationFailureDisabledEvent)
        {
            // 验证过了但是账户被禁用
            message = ACCOUNT_DEACTIVATE;
        }
        else if (event instanceof AuthenticationFailureServiceExceptionEvent)
        {
            // 其他任何在AuthenticationManager中内部发生的异常都会被封装成此类
            message = "AuthenticationManager内部异常";
        }
        else
        {
            message = "未知错误";
        }
        loginInfoService.recordLoginInfo(event.getAuthentication().getPrincipal().toString(),
                FAIL, LOGIN_BY_ACCOUNT, message, request);
    }
}
