package com.yiport.controller;


import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.request.UpdatePasswordRequest;
import com.yiport.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件 控制器
 */
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
@Slf4j
public class MailController
{
    private final MailService mailService;

    /**
     * 发送邮箱验证码
     */
    @GetMapping("/sendVerifyMailCaptcha")
    @SystemLog(businessName = "发送邮箱验证码")
    @LimitRequest(time = 60 * 1000, description = "请一分钟后再试", tip = true)
    public ResponseResult<Void> sendMailCaptcha(String email)
    {
        return mailService.sendMailCaptcha(email);
    }

    /**
     * 邮箱验证-修改邮箱
     */
    @PostMapping("/verifyMail")
    @SystemLog(businessName = "修改邮箱")
    @LimitRequest(time = 24 * 60 * 1000, count = 2, description = "24小时内允许修改两次", tip = true)
    public ResponseResult<Void> verifyMail(String email, String captcha)
    {
        return mailService.verifyMail(email, captcha);
    }

    /**
     * 发送找回账号邮箱验证码
     */
    @GetMapping("/sendRetrieveAccountCaptcha")
    @SystemLog(businessName = "发送找回账号邮箱验证码")
    @LimitRequest(time = 60 * 1000, description = "请一分钟后再试", tip = true)
    public ResponseResult<Void> sendRetrieveAccountCaptcha(String email)
    {
        return mailService.sendRetrieveAccountCaptcha(email);
    }

    /**
     * 邮箱验证-找回账号
     */
    @GetMapping("/retrieveAccount")
    @SystemLog(businessName = "找回账号")
    public ResponseResult<String> retrieveAccount(String email, String captcha)
    {
        return mailService.retrieveAccount(email, captcha);
    }

    /**
     * 发送忘记密码邮箱验证码
     */
    @GetMapping("/sendUpdatePasswordCaptcha")
    @SystemLog(businessName = "发送忘记密码邮箱验证码")
    @LimitRequest(time = 60 * 1000, description = "请一分钟后再试", tip = true)
    public ResponseResult<Void> sendUpdatePasswordCaptcha(String email)
    {
        return mailService.sendUpdatePasswordCaptcha(email);
    }

    /**
     * 邮箱验证-修改密码
     */
    @PostMapping("/updatePasswordByMail")
    @SystemLog(businessName = "验证邮箱修改密码")
    public ResponseResult<String> updatePasswordByMail(@Validated @RequestBody UpdatePasswordRequest updatePasswordRequest)
    {
        return mailService.updatePasswordByMail(updatePasswordRequest);
    }

}
