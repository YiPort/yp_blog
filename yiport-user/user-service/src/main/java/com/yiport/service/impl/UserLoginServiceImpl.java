package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.vo.UserLoginVO;
import com.yiport.domain.vo.UserInfoVO;
import com.yiport.handler.exception.SystemException;
import com.yiport.service.UserLoginService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.CaptchaTextCreator;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
import com.google.code.kaptcha.Producer;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.yiport.enums.AppHttpCodeEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.FastByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yiport.constent.UserConstant.ADMIN_ROLE;

@Service
public class UserLoginServiceImpl implements UserLoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CaptchaTextCreator captchaTextCreator;

    @Autowired
    private  Producer captchaProducerMath;

    @Override
    public ResponseResult<UserLoginVO> login(User user) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if (Objects.isNull(authenticate)) {
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("ypblog:login:" + userId, loginUser);

        //把token和userinfo封装 返回
        //把User转换成UserInfoVo
        UserInfoVO userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVO.class);
        UserLoginVO vo = new UserLoginVO(jwt, userInfoVo);
        return ResponseResult.okResult(vo);
    }

    /**
     * 账号密码登录
     *
     * @param userLoginRequest 用户登录请求体
     * @return 结果
     */
    @Override
    public ResponseResult<UserLoginVO> userLoginByAccount(AccountLoginRequest userLoginRequest) {
        String userAccount = userLoginRequest.getUserName();
        String userPassword = userLoginRequest.getPassword();
        String captcha = userLoginRequest.getCaptcha();
        String uuid = userLoginRequest.getUuid();
        // 1.1、非空校验
        if (StringUtils.isAnyEmpty(userAccount, userPassword)) {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "账号密码不能为空");
        }
        // 1.2、账号为4~9位
        if (userAccount.length() < 4 || userAccount.length() > 9)
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "账号为4~9位");
        }
        // 1.3、密码为8~16位
        if (userPassword.length() < 8 || userPassword.length() > 16)
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "密码为8~16位");
        }
        // 1.4、账号不能包含特殊字符
        String validPattern = "[\\s`!@#$%^&*_\\-~()+=|{}':;,\\[\\].<>/\\\\?！￥…（）—【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "账号不能包含特殊字符");
        }
        // 1.5、密码不能含有空字符
        String validPattern1 = "[\\s]";
        Matcher matcher1 = Pattern.compile(validPattern1).matcher(userPassword);
        if (matcher1.find())
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "密码不能包含空字符");
        }
        // 1.6、验证码为1~4位
        if (captcha.length() < 1 || captcha.length() > 4)
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "验证码为1~4位");
        }
        // 1.7、验证码失效
        String key = "captcha_codes:" + uuid;
        String text;
        text = redisCache.getCacheObject(key);
        if (StringUtils.isAnyBlank(text))
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "验证码失效请重试");
        }

        String result = text.substring(text.lastIndexOf("@") + 1);
        if (!result.equals(captcha))
        {
            throw new SystemException(AppHttpCodeEnum.PARAMETER_ERROR, "验证码错误请重试");
        }

        // 2、记录用户的登录状态
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userAccount, userPassword);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR, "账号或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("ypblog:login:" + userId, loginUser);
        // 将 token存入 redis
        String tokenKey = "ypblog:token:" + userId;
        redisCache.setCacheObject(tokenKey, jwt, 1, TimeUnit.DAYS);
        // 将管理员权限信息存入 redis
        if (loginUser.getUser().getType().equals(ADMIN_ROLE)) ;
        {
            String adminKey = "ypblog:admin:" + userId;
            redisCache.setCacheObject(adminKey, jwt, 1, TimeUnit.DAYS);
        }
        //把token和userinfo封装 返回
        //把User转换成UserInfoVo
        UserInfoVO userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserInfoVO.class);
        UserLoginVO vo = new UserLoginVO(jwt, userInfoVo);
        return ResponseResult.okResult(vo);
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
        String redisKey = "captcha_codes:" + uuid;

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


    @Override
    public ResponseResult<AppHttpCodeEnum> logout() {
        //获取token 解析获取userid
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        //获取userid
        Long userId = loginUser.getUser().getId();
        //删除redis中的用户信息
        redisCache.deleteObject("ypblog:login:" + userId);
        return ResponseResult.okResult();
    }
}