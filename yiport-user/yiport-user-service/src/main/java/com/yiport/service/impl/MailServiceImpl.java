package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.UpdatePasswordRequest;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.service.MailCommonService;
import com.yiport.service.MailService;
import com.yiport.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yiport.constent.ExceptionDescription.PASSWORD_DIFFERENT;
import static com.yiport.constent.UserConstant.GET_ACCOUNT_MAIL_CAPTCHA;
import static com.yiport.constent.UserConstant.LOGIN_BY_MAIL_MESSAGE;
import static com.yiport.constent.UserConstant.NULL_REGEX;
import static com.yiport.constent.UserConstant.UPDATE_PASSWORD_MAIL_CAPTCHA;
import static com.yiport.constent.UserConstant.VALIDATION_MESSAGE;
import static com.yiport.constent.UserConstant.VERIFY_MAIL_CAPTCHA;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;
import static com.yiport.enums.MailTypeEnum.LOGIN_BY_MAIL;
import static com.yiport.enums.MailTypeEnum.RETRIEVE_ACCOUNT;
import static com.yiport.enums.MailTypeEnum.UPDATE_MAIL;
import static com.yiport.enums.MailTypeEnum.UPDATE_PASSWORD;

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
    private final MailCommonService mailCommonService;

    /**
     * 发送邮箱验证码
     */
    @Override
    public ResponseResult<Void> sendMailCaptcha(String email)
    {
        // 验证邮箱未绑定
        mailCommonService.verifyNotBind(email);
        // 发送验证码
        mailCommonService.sendTextMail(email, VALIDATION_MESSAGE, UPDATE_MAIL);
        return ResponseResult.okResult();
    }

    /**
     * 修改邮箱
     */
    @Override
    public ResponseResult<Void> verifyMail(String email, String captcha)
    {
        mailCommonService.verifyCaptcha(VERIFY_MAIL_CAPTCHA, email, captcha);
        // 验证邮箱未绑定
        mailCommonService.verifyNotBind(email);
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
        // 验证邮箱已绑定
        mailCommonService.verifyHasBind(email);
        // 发送验证码
        mailCommonService.sendTextMail(email, VALIDATION_MESSAGE, RETRIEVE_ACCOUNT);
        return ResponseResult.okResult();
    }

    /**
     * 找回账号
     */
    @Override
    public ResponseResult<String> retrieveAccount(String email, String captcha)
    {
        mailCommonService.verifyCaptcha(GET_ACCOUNT_MAIL_CAPTCHA, email, captcha);
        // 验证邮箱已绑定
        User user = mailCommonService.verifyHasBind(email);
        redisCache.deleteObject(GET_ACCOUNT_MAIL_CAPTCHA + email);
        return ResponseResult.okResult(user.getUserName());
    }

    /**
     * 发送忘记密码邮箱验证码
     */
    @Override
    public ResponseResult<Void> sendUpdatePasswordCaptcha(String email)
    {
        // 验证邮箱已绑定
        mailCommonService.verifyHasBind(email);
        // 发送验证码
        mailCommonService.sendTextMail(email, VALIDATION_MESSAGE, UPDATE_PASSWORD);
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
        // 密码和校验密码不一致
        if (!userPassword.equals(updatePasswordRequest.getCheckPassword()))
        {
            throw new SystemException(PARAMETER_ERROR, PASSWORD_DIFFERENT);
        }
        // 校验验证码
        mailCommonService.verifyCaptcha(UPDATE_PASSWORD_MAIL_CAPTCHA, email, updatePasswordRequest.getCaptcha());
        // 验证邮箱已绑定
        User user = mailCommonService.verifyHasBind(email);
        user.setPassword(passwordEncoder.encode(userPassword));
        userMapper.updateById(user);
        redisCache.deleteObject(UPDATE_PASSWORD_MAIL_CAPTCHA + email);
        return ResponseResult.okResult();
    }

    /**
     * 发送邮箱登录验证码
     */
    @Override
    public ResponseResult<Void> sendLoginByMailCaptcha(String email)
    {
        // 验证邮箱已绑定
        mailCommonService.verifyHasBind(email);
        // 发送验证码
        mailCommonService.sendTextMail(email, LOGIN_BY_MAIL_MESSAGE, LOGIN_BY_MAIL);
        return ResponseResult.okResult();
    }

}
