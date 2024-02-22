package com.yiport.handler;

import com.yiport.domain.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 用户系统异常处理
 */
@RestControllerAdvice
@Slf4j
public class UserSystemExceptionHandler {

    /**
     * SpringSecurity身份验证异常（密码验证错误）
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseResult<Void> handleSpringSecurityVerifyException(BadCredentialsException e)
    {
        log.error(e.getMessage(), e);
        return ResponseResult.errorResult(PARAMETER_ERROR, e.getMessage());
    }

    /**
     * SpringSecurity身份验证异常（用户名错误）
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public ResponseResult<Void> handleSpringSecurityVerifyException(InternalAuthenticationServiceException e)
    {
        log.error(e.getMessage(), e);
        return ResponseResult.errorResult(PARAMETER_ERROR, e.getMessage());
    }

}
