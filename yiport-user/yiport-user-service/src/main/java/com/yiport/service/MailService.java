package com.yiport.service;


import com.yiport.domain.ResponseResult;

/**
 *
 */
public interface MailService
{
    /**
     * 发送邮箱验证码
     */
    ResponseResult<Void> sendMailCaptcha(String email);

    /**
     * 修改邮箱
     */
    ResponseResult<Void> verifyMail(String email, String captcha);
}
