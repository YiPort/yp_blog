package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.UpdatePasswordRequest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yiport.constent.UserConstant.GET_ACCOUNT_MAIL_CAPTCHA;
import static com.yiport.constent.UserConstant.MAIL_CAPTCHA_TIME;
import static com.yiport.constent.UserConstant.NULL_REGEX;
import static com.yiport.constent.UserConstant.UPDATE_PASSWORD_MAIL_CAPTCHA;
import static com.yiport.constent.UserConstant.VALIDATION_MESSAGE;
import static com.yiport.constent.UserConstant.VERIFY_MAIL_CAPTCHA;
import static com.yiport.enums.AppHttpCodeEnum.EMAIL_EXIST;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;

/**
 * 邮件 服务层实现类
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MailServiceImpl implements MailService
{
    private final JavaMailSenderImpl javaMailSender;
    private final RedisCache redisCache;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String sendMailer;

    /**
     * 发送邮箱验证码
     */
    @Override
    public ResponseResult<Void> sendMailCaptcha(String email)
    {
        User verifyUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.nonNull(verifyUser))
        {
            throw new SystemException(EMAIL_EXIST, "该邮箱已绑定账号");
        }
        String captcha = RandomStringUtils.random(6, "0123456789");
        redisCache.setCacheObject(VERIFY_MAIL_CAPTCHA + email, captcha + ":" + email,
                MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
        String content = "您正在验证邮箱，请填写验证码：\n" + captcha + "\n" + MAIL_CAPTCHA_TIME +
                "分钟内有效。该邮件为系统邮件，请勿回复，若非本人操作请忽略";
        sendMail(email, VALIDATION_MESSAGE, content);
        return ResponseResult.okResult();
    }

    /**
     * 修改邮箱
     */
    @Override
    public ResponseResult<Void> verifyMail(String email, String captcha)
    {
        verifyCaptcha(VERIFY_MAIL_CAPTCHA, email, captcha);
        User verifyUser = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.nonNull(verifyUser))
        {
            throw new SystemException(EMAIL_EXIST, "该邮箱已绑定账号");
        }
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getId, loginUser.getUser().getId()));
        user.setEmail(email);
        userMapper.updateById(user);
        redisCache.deleteObject(VERIFY_MAIL_CAPTCHA + email);
        return ResponseResult.okResult();
    }

    /**
     * 发送找回账号邮箱验证码
     */
    @Override
    public ResponseResult<Void> sendRetrieveAccountCaptcha(String email)
    {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.isNull(user))
        {
            throw new SystemException(PARAMETER_ERROR, "该邮箱没有与账号绑定");
        }
        String captcha = RandomStringUtils.random(6, "0123456789");
        redisCache.setCacheObject(GET_ACCOUNT_MAIL_CAPTCHA + email, captcha + ":" + email,
                MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
        String content = "您正在使用邮箱找回账号，请填写验证码：\n" + captcha + "\n" + MAIL_CAPTCHA_TIME +
                "分钟内有效。该邮件为系统邮件，请勿回复，若非本人操作请忽略";
        sendMail(email, VALIDATION_MESSAGE, content);
        return ResponseResult.okResult();
    }

    /**
     * 找回账号
     */
    @Override
    public ResponseResult<String> retrieveAccount(String email, String captcha)
    {
        verifyCaptcha(GET_ACCOUNT_MAIL_CAPTCHA, email, captcha);
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.isNull(user))
        {
            throw new SystemException(PARAMETER_ERROR, "该邮箱没有与账号绑定");
        }
        redisCache.deleteObject(GET_ACCOUNT_MAIL_CAPTCHA + email);
        return ResponseResult.okResult(user.getUserName());
    }

    /**
     * 发送忘记密码邮箱验证码
     */
    @Override
    public ResponseResult<Void> sendUpdatePasswordCaptcha(String email)
    {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.isNull(user))
        {
            throw new SystemException(PARAMETER_ERROR, "该邮箱没有与账号绑定");
        }
        String captcha = RandomStringUtils.random(6, "0123456789");
        redisCache.setCacheObject(UPDATE_PASSWORD_MAIL_CAPTCHA + email, captcha + ":" + email,
                MAIL_CAPTCHA_TIME, TimeUnit.MINUTES);
        String content = "您正在使用邮箱修改密码，请填写验证码：\n" + captcha + "\n" + MAIL_CAPTCHA_TIME +
                "分钟内有效。该邮件为系统邮件，请勿回复，若非本人操作请忽略";
        sendMail(email, VALIDATION_MESSAGE, content);
        return ResponseResult.okResult();
    }

    /**
     * 邮箱验证-修改密码
     */
    @Override
    public ResponseResult<String> updatePasswordByMail(UpdatePasswordRequest updatePasswordRequest)
    {
        String userPassword = updatePasswordRequest.getPassword();
        String email = updatePasswordRequest.getEmail();
        Matcher matcher1 = Pattern.compile(NULL_REGEX).matcher(userPassword);
        if (matcher1.find())
        {
            throw new SystemException(PARAMETER_ERROR, "密码不能包含空字符");
        }
        if (!userPassword.equals(updatePasswordRequest.getCheckPassword()))
        {
            throw new SystemException(PARAMETER_ERROR, "两次输入的密码不一致");
        }
        verifyCaptcha(UPDATE_PASSWORD_MAIL_CAPTCHA, email, updatePasswordRequest.getCaptcha());
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (Objects.isNull(user))
        {
            throw new SystemException(PARAMETER_ERROR, "该邮箱没有与账号绑定");
        }
        user.setPassword(passwordEncoder.encode(userPassword));
        userMapper.updateById(user);
        redisCache.deleteObject(UPDATE_PASSWORD_MAIL_CAPTCHA + email);
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
            throw new SystemException(SYSTEM_ERROR, "验证码错误");
        }
        String[] captchaAndEmail = emailCaptcha.split(":");
        if (!captchaAndEmail[0].equals(captcha))
        {
            throw new SystemException(SYSTEM_ERROR, "验证码错误");
        }
    }
}
