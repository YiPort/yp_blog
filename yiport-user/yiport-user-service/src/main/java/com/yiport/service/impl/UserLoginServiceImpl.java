package com.yiport.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.constent.UserConstant;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.EmailLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserLoginVO;
import com.yiport.domain.vo.UserVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.provider.MailAuthenticationToken;
import com.yiport.service.LoginCommonService;
import com.yiport.service.LoginInfoService;
import com.yiport.service.UserLoginService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.CaptchaTextCreator;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
import com.google.code.kaptcha.Producer;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.yiport.enums.AppHttpCodeEnum;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yiport.constants.BusinessConstants.BLOG_ADMIN;
import static com.yiport.constants.BusinessConstants.BLOG_LOGIN;
import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constent.ExceptionDescription.ACCOUNT_EXIST;
import static com.yiport.constent.ExceptionDescription.ACCOUNT_PASSWORD_ERROR;
import static com.yiport.constent.ExceptionDescription.PASSWORD_DIFFERENT;
import static com.yiport.constent.UserConstant.CAPTCHA_CODES;
import static com.yiport.constent.UserConstant.ADMIN_ROLE;
import static com.yiport.constent.UserConstant.EXPIRATION;
import static com.yiport.constent.UserConstant.FAIL;
import static com.yiport.constent.UserConstant.LOGIN_BY_ACCOUNT;
import static com.yiport.constent.UserConstant.LOGIN_BY_EMAIL;
import static com.yiport.constent.UserConstant.LOGIN_SUCCESS;
import static com.yiport.constent.UserConstant.NICKNAME_PREFIX;
import static com.yiport.constent.UserConstant.NULL_REGEX;
import static com.yiport.constent.UserConstant.REGISTER_SUCCESS;
import static com.yiport.constent.UserConstant.SECTION_MARK;
import static com.yiport.constent.UserConstant.SPECIAL_REGEX;
import static com.yiport.constent.UserConstant.TOKEN_HEADER_KEY;
import static com.yiport.constent.UserConstant.USER_INFO;
import static com.yiport.enums.AppHttpCodeEnum.NICKNAME_EXIST;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.SUCCESS;
import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.USERNAME_EXIST;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserLoginServiceImpl extends ServiceImpl<UserMapper, User> implements UserLoginService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final  RedisCache redisCache;
    private final CaptchaTextCreator captchaTextCreator;
    private final Producer captchaProducerMath;
    private final HttpServletRequest request;
    private final UserMapper userMapper;
    private final LoginInfoService loginInfoService;
    private final LoginCommonService loginCommonService;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @Override
    public ResponseResult<Void> userRegister(UserRegisterRequest userRegisterRequest) {
        String userName = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String captcha = userRegisterRequest.getCaptcha();
        String uuid = userRegisterRequest.getUuid();
        // 密码和校验密码不相同
        if (!userPassword.equals(checkPassword))
        {
            loginInfoService.recordLoginInfo(userName, FAIL, PASSWORD_DIFFERENT, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(PARAMETER_ERROR, PASSWORD_DIFFERENT);
        }
        // 校验验证码
        loginCommonService.validateCaptcha(userName, captcha, uuid, request);
        // 账号不能重复
        User oneUser = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, userName));
        if (Objects.nonNull(oneUser))
        {
            loginInfoService.recordLoginInfo(userName, FAIL, ACCOUNT_EXIST, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(USERNAME_EXIST, ACCOUNT_EXIST);
        }
        // 加密
        String encryptPassword = passwordEncoder.encode(userPassword);
        User user = new User();
        user.setUserName(userName);
        user.setPassword(encryptPassword);
        // 分配UID
        Long common = userMapper.getUidForRegister(SECTION_MARK);
        user.setUid(common);
        // 分配昵称
        user.setNickName(NICKNAME_PREFIX + common);
        // 插入数据
        save(user);
        loginInfoService.recordLoginInfo(userName, UserConstant.SUCCESS, REGISTER_SUCCESS, LOGIN_BY_ACCOUNT, request);
        return ResponseResult.okResult();
    }

    /**
     * 账号密码登录
     *
     * @param userLoginRequest 用户登录请求体
     * @return 结果
     */
    @Override
    public ResponseResult<Map<String, Object>> userLoginByAccount(AccountLoginRequest userLoginRequest) {
        String userName = userLoginRequest.getUserName();
        String userPassword = userLoginRequest.getPassword();
        String captcha = userLoginRequest.getCaptcha();
        String uuid = userLoginRequest.getUuid();
        // 密码不能含有空字符
        Matcher matcher1 = Pattern.compile(NULL_REGEX).matcher(userPassword);
        if (matcher1.find())
        {
            loginInfoService.recordLoginInfo(userName, FAIL, ACCOUNT_PASSWORD_ERROR, LOGIN_BY_ACCOUNT, request);
            throw new SystemException(PARAMETER_ERROR, ACCOUNT_PASSWORD_ERROR);
        }
        // 校验验证码
        loginCommonService.validateCaptcha(userName, captcha, uuid, request);
        // 记录用户的登录状态
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userName, userPassword));
        return loginCommon(userName, authenticate, LOGIN_BY_ACCOUNT);

    }

    /**
     * 邮箱验证码登录
     *
     * @param emailLoginRequest 邮箱验证码登录请求体
     * @return 结果
     */
    @Override
    public ResponseResult<Map<String, Object>> userLoginByEmail(EmailLoginRequest emailLoginRequest)
    {
        String email = emailLoginRequest.getEmail();
        String mailCaptcha = emailLoginRequest.getCaptcha();
        // 记录用户的登录状态
        Authentication authenticate = authenticationManager.authenticate(
                new MailAuthenticationToken(email, mailCaptcha));
        return loginCommon(email, authenticate, LOGIN_BY_EMAIL);
    }

    /**
     * 获取图片验证码
     *
     * @return
     */
    @Override
    public ResponseResult getCaptchaImage()
    {
        // 生成 UUID
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String redisKey = CAPTCHA_CODES + uuid;

        // 生成验证码
        String text = captchaTextCreator.getText();
        int index = text.lastIndexOf("@");
        String code = text.substring(0, index);
        BufferedImage image = captchaProducerMath.createImage(code);

        // 将 text存入 Redis,设置过期时间为两分钟
        redisCache.setCacheObject(redisKey, text, 2, TimeUnit.MINUTES);

        FastByteArrayOutputStream fo = new FastByteArrayOutputStream();
        try
        {
            ImageIO.write(image, "jpg", fo);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Map<Object, Object> map = new HashMap<>();
        map.put("captcha", Base64.encode(fo.toByteArray()));
        map.put("uuid", uuid);

        return ResponseResult.okResult(map);
    }

    /**
     * 用户登出
     *
     * @return
     */
    @Override
    public ResponseResult<Void> logout() {
        String token = request.getHeader("Token");
        Claims claims;
        String userId;
        // 解析token
        try
        {
            claims = JwtUtil.parseJWT(token);
            userId = claims.getId();
        }
        catch (Exception e)
        {
            throw new RuntimeException("Token非法！");
        }
        // 删除 redis中的 authenticate
        String redisKey = BLOG_LOGIN + userId;
        redisCache.deleteObject(redisKey);
        // 删除 redis中的 token
        String tokenKey = BLOG_TOKEN + userId;
        redisCache.deleteObject(tokenKey);
        // 删除 redis中的权限信息
        User user = JSON.parseObject(claims.getSubject(), User.class);
        if (user.getUserRole().equals(ADMIN_ROLE))
        {
            String adminKey = BLOG_ADMIN+ userId;
            redisCache.deleteObject(adminKey);
        }
        return ResponseResult.okResult();
    }
    /**
     * 登录通用方法
     *
     * @param userAccount  账号
     * @param authenticate Authentication
     */
    public ResponseResult<Map<String, Object>> loginCommon(String userAccount, Authentication authenticate, String type)
    {
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId, JSON.toJSONString(loginUser.getUser()), EXPIRATION);
        // 将 loginUser存入 redis
        setRedisCache(loginUser);
        // 将 token存入 redis
        String tokenKey = BLOG_TOKEN + userId;
        redisCache.setCacheObject(tokenKey, jwt);
        // 记录登录日志
        loginInfoService.recordLoginInfo(userAccount, UserConstant.SUCCESS, LOGIN_SUCCESS, type, request);
        UserVO userVO = BeanCopyUtils.copyBean(loginUser.getUser(), UserVO.class);
        Map<String, Object> map = new HashMap<>();
        map.put(TOKEN_HEADER_KEY, jwt);
        map.put(USER_INFO, userVO);
        // 返回 Token和 userInfo
        return ResponseResult.okResult(map);
    }

    public void setRedisCache(LoginUser loginUser)
    {
        // 将 loginUser存入 redis
        String redisKey = BLOG_LOGIN + loginUser.getUser().getId();
        redisCache.setCacheObject(redisKey, loginUser);
    }

}