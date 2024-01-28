package com.yiport.controller;


import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.service.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 邮件发送控制器
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
    @GetMapping("/sendMailCaptcha")
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
}
