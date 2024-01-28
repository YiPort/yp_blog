package com.yiport.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.service.MailService;
import com.yiport.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.yiport.constent.UserConstant.MAIL_CAPTCHA_TIME;
import static com.yiport.constent.UserConstant.VALIDATION_MESSAGE;
import static com.yiport.constent.UserConstant.VERIFY_MAIL_CAPTCHA;
import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;

/**
 *
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService
{
    private final JavaMailSenderImpl javaMailSender;
    private final RedisCache redisCache;
    private final UserMapper userMapper;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * 发送邮箱验证码
     */
    @Override
    public ResponseResult<Void> sendMailCaptcha(String email)
    {
        String captcha = RandomStringUtils.random(4, "0123456789");
        redisCache.setCacheObject(VERIFY_MAIL_CAPTCHA + email, captcha + ":" + email, MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
        String content = "您正在验证邮箱，请填写验证码：" + captcha + "（" + MAIL_CAPTCHA_TIME + "分钟内有效，若非本人操作请忽略）";
        sendMail(email, VALIDATION_MESSAGE, content);
        return ResponseResult.okResult();
    }

    /**
     * 修改邮箱
     */
    @Override
    public ResponseResult<Void> verifyMail(String email, String captcha)
    {
        verifyCaptcha(email, captcha);
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, loginUser.getUser().getId()));
        user.setEmail(email);
        userMapper.updateById(user);
        return ResponseResult.okResult();
    }

    /**
     * 发送邮件
     *
     * @param email   收件邮箱
     * @param subject 主题
     * @param content 内容
     */
    public void sendMail(String email, String subject, String content)
    {
        try
        {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage());
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人
            mimeMessageHelper.setTo(email);
            //邮件主题
            mimeMessageHelper.setSubject(subject);
            //邮件内容
            mimeMessageHelper.setText(content);
            //邮件发送时间
            mimeMessageHelper.setSentDate(new Date());
            //发送邮件
            javaMailSender.send(mimeMessageHelper.getMimeMessage());
            log.debug("发送邮件成功：" + sendMailer + "->" + email);
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            log.debug("发送邮件失败：" + e.getMessage());
        }
    }

    /**
     * 验证码校验
     */
    public void verifyCaptcha(String email, String captcha)
    {
        String emailCaptcha = redisCache.getCacheObject(VERIFY_MAIL_CAPTCHA + email);
        if (Objects.isNull(emailCaptcha))
        {
            throw new SystemException(SYSTEM_ERROR, "验证码错误");
        }
        String[] captchaAndEmail = emailCaptcha.split(":");
        if (!captchaAndEmail[0].equals(captcha))
        {
            throw new SystemException(SYSTEM_ERROR, "验证码错误");
        }
    }
}
