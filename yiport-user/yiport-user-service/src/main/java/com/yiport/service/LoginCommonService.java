package com.yiport.service;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.entity.User;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.yiport.constent.ExceptionDescription.ACCOUNT_DEACTIVATE;
import static com.yiport.constent.ExceptionDescription.ACCOUNT_ERROR;
import static com.yiport.constent.ExceptionDescription.ACCOUNT_PASSWORD_ERROR;
import static com.yiport.constent.ExceptionDescription.CAPTCHA_ERROR;
import static com.yiport.constent.ExceptionDescription.CAPTCHA_EXPIRE;
import static com.yiport.constent.ExceptionDescription.MAIL_NOT_BINDING;
import static com.yiport.constent.UserConstant.BAN_STATUS;
import static com.yiport.constent.UserConstant.CAPTCHA_CODES;
import static com.yiport.constent.UserConstant.CAPTCHA_RESULT_SPLIT;
import static com.yiport.constent.UserConstant.FAIL;
import static com.yiport.constent.UserConstant.LOGIN_BY_ACCOUNT;
import static com.yiport.constent.UserConstant.LOGIN_BY_EMAIL;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 登录通用校验方法
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginCommonService
{
    private final RedisCache redisCache;
    private final LoginInfoService loginInfoService;
    private final UserMapper userMapper;

    /**
     * 校验验证码
     *
     * @param userName 用户名
     * @param captcha     验证码
     * @param uuid        唯一标识
     */
    public void validateCaptcha(String userName, String captcha, String uuid, HttpServletRequest request)
    {
        String key = CAPTCHA_CODES + uuid;
        String text;
        text = redisCache.getCacheObject(key);
        // 验证码失效
        if (StringUtils.isAnyBlank(text))
        {
            loginInfoService.recordLoginInfo(userName, FAIL, CAPTCHA_EXPIRE, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(PARAMETER_ERROR, CAPTCHA_EXPIRE);
        }
        // 验证码错误
        String result = text.substring(text.lastIndexOf(CAPTCHA_RESULT_SPLIT) + 1);
        if (!result.equals(captcha))
        {
            loginInfoService.recordLoginInfo(userName, FAIL, CAPTCHA_ERROR, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(PARAMETER_ERROR, CAPTCHA_ERROR);
        }
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     * @return 结果
     */
    public User loadUserByUsername(String userName, HttpServletRequest request)
    {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userName));
        if (ObjectUtil.isNull(user))
        {
            loginInfoService.recordLoginInfo(userName, FAIL, ACCOUNT_ERROR, LOGIN_BY_ACCOUNT, request);
            log.info("登录用户：{} 不存在.", userName);
            throw new SystemException(ACCOUNT_PASSWORD_ERROR);
        }
        else if (BAN_STATUS.toString().equals(user.getStatus()))
        {
            loginInfoService.recordLoginInfo(userName, FAIL, ACCOUNT_DEACTIVATE, LOGIN_BY_ACCOUNT, request);
            log.info("登录用户：{} 已被停用.", userName);
            throw new SystemException(ACCOUNT_DEACTIVATE);
        }
        return user;
    }

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 用户名
     * @return 结果
     */
    public User loadUserByEmail(String email, HttpServletRequest request)
    {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if (ObjectUtil.isNull(user))
        {
            loginInfoService.recordLoginInfo(email, FAIL, MAIL_NOT_BINDING, LOGIN_BY_EMAIL, request);
            log.info("登录用户：{} 不存在.", email);
            throw new SystemException(MAIL_NOT_BINDING);
        }
        else if (BAN_STATUS.equals(user.getStatus()))
        {
            loginInfoService.recordLoginInfo(email, FAIL, ACCOUNT_DEACTIVATE, LOGIN_BY_EMAIL, request);
            log.info("登录用户：{} 已被停用.", email);
            throw new SystemException(ACCOUNT_DEACTIVATE);
        }
        return user;
    }

}
