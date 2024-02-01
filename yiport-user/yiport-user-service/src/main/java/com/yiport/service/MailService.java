package com.yiport.service;


import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.UpdatePasswordRequest;

/**
 * 邮件 服务层
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


    /**
     * 发送找回账号邮箱验证码
     */
    ResponseResult<Void> sendRetrieveAccountCaptcha(String email);

    /**
     * 找回账号
     */
    ResponseResult<String> retrieveAccount(String email, String captcha);

    /**
     * 发送忘记密码邮箱验证码
     */
    ResponseResult<Void> sendUpdatePasswordCaptcha(String email);

    /**
     * 邮箱验证-修改密码
     */
    ResponseResult<String> updatePasswordByMail(UpdatePasswordRequest updatePasswordRequest);

}
