package com.yiport.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.entity.User;
import com.yiport.enums.MailTypeEnum;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.yiport.constent.ExceptionDescription.CAPTCHA_ERROR;
import static com.yiport.constent.ExceptionDescription.MAIL_BINDING_ACCOUNT;
import static com.yiport.constent.ExceptionDescription.MAIL_NOT_BINDING;
import static com.yiport.constent.UserConstant.ACCOUNT_LOGIN_MESSAGE;
import static com.yiport.constent.UserConstant.GET_ACCOUNT_MAIL_CAPTCHA;
import static com.yiport.constent.UserConstant.MAIL_CAPTCHA_TIME;
import static com.yiport.constent.UserConstant.UPDATE_PASSWORD_MAIL_CAPTCHA;
import static com.yiport.constent.UserConstant.VERIFY_MAIL_CAPTCHA;
import static com.yiport.enums.AppHttpCodeEnum.EMAIL_EXIST;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;

/**
 * 邮件通用方法
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailCommonService
{
    private final JavaMailSenderImpl javaMailSender;
    private final RedisCache redisCache;
    private final UserMapper userMapper;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * 查询用户绑定邮箱
     *
     * @param userName 用户名
     * @return 邮箱
     */
    public String getMailByAccount(String userName)
    {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userName));
        return user.getEmail();
    }

    /**
     * 发送普通文本邮件
     *
     * @param email    收件邮箱
     * @param subject  主题
     * @param typeEnum 邮件类型
     */
    public void sendTextMail(String email, String subject, MailTypeEnum typeEnum)
    {
        String captcha = RandomStringUtils.random(6, "0123456789");
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
            mimeMessageHelper.setText(getMailContent(typeEnum, email, captcha));
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
     * 发送登录告警邮件
     *
     * @param email       邮箱
     * @param userAccount 账号
     * @param loginTime   登录时间
     * @param ip          IP
     * @param address     登录地址
     */
    public void sendLoginMail(String email, String userAccount, String loginTime, String ip, String address)
    {
        String mailContent = "你好, " + userAccount + "\n\n" + "你的帐号于 " + loginTime + " 在非常用的IP地址 " +
                ip + "(" + address + ")" + " 登录，如非本人操作，建议尽快修改帐户密码。";
        try
        {
            //true 代表支持复杂的类型
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage());
            //邮件发信人
            mimeMessageHelper.setFrom(sendMailer);
            //邮件收信人
            mimeMessageHelper.setTo(email);
            //邮件主题
            mimeMessageHelper.setSubject(ACCOUNT_LOGIN_MESSAGE);
            //邮件内容
            mimeMessageHelper.setText(mailContent);
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
     * 邮箱验证码校验
     *
     * @param key     redis-key
     * @param email   邮箱
     * @param captcha 验证码
     */
    public void verifyCaptcha(String key, String email, String captcha)
    {
        String emailCaptcha = redisCache.getCacheObject(key + email);
        if (Objects.isNull(emailCaptcha))
        {
            throw new SystemException(SYSTEM_ERROR, CAPTCHA_ERROR);
        }
        String[] captchaAndEmail = emailCaptcha.split(":");
        if (!captchaAndEmail[0].equals(captcha))
        {
            throw new SystemException(SYSTEM_ERROR, CAPTCHA_ERROR);
        }
    }

    /**
     * 验证邮箱已绑定
     *
     * @param email 邮箱
     */
    public User verifyHasBind(String email)
    {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.isNull(user))
        {
            throw new SystemException(PARAMETER_ERROR, MAIL_NOT_BINDING);
        }
        return user;
    }

    /**
     * 验证邮箱未绑定
     *
     * @param email 邮箱
     */
    public void verifyNotBind(String email)
    {
        User verifyUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.nonNull(verifyUser))
        {
            throw new SystemException(EMAIL_EXIST, MAIL_BINDING_ACCOUNT);
        }
    }

    /**
     * 拼接验证码邮件内容
     *
     * @param type 邮件类型
     */
    public String getMailContent(MailTypeEnum type, String email, String captcha)
    {
        switch (type)
        {
            case UPDATE_MAIL:
                redisCache.setCacheObject(VERIFY_MAIL_CAPTCHA + email, captcha + ":" + email,
                        MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
                break;
            case RETRIEVE_ACCOUNT:
                redisCache.setCacheObject(GET_ACCOUNT_MAIL_CAPTCHA + email, captcha + ":" + email,
                        MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
                break;
            case UPDATE_PASSWORD:
                redisCache.setCacheObject(UPDATE_PASSWORD_MAIL_CAPTCHA + email, captcha + ":" + email,
                        MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
                break;
        }
        return "您正在" + type.getType() + "，请填写验证码：\n" + captcha + "\n" + MAIL_CAPTCHA_TIME +
                "分钟内有效。该邮件为系统邮件，请勿回复，若非本人操作请忽略。";
    }

}
