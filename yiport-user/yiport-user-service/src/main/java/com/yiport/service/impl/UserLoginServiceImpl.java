package com.yiport.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.AccountLoginRequest;
import com.yiport.domain.request.UserRegisterRequest;
import com.yiport.domain.vo.UserLoginVO;
import com.yiport.domain.vo.UserVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.service.UserLoginService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.CaptchaTextCreator;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
import com.google.code.kaptcha.Producer;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.yiport.enums.AppHttpCodeEnum;
import io.jsonwebtoken.Claims;
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
import static com.yiport.constent.UserBusinessConstants.CAPTCHA_CODES;
import static com.yiport.constent.UserConstant.ADMIN_ROLE;
import static com.yiport.constent.UserConstant.EXPIRATION;
import static com.yiport.enums.AppHttpCodeEnum.NICKNAME_EXIST;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.SUCCESS;
import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.USERNAME_EXIST;

@Service
public class UserLoginServiceImpl extends ServiceImpl<UserMapper, User> implements UserLoginService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CaptchaTextCreator captchaTextCreator;

    @Autowired
    private Producer captchaProducerMath;

    @Autowired
    private HttpServletRequest request;

    @Resource
    private UserMapper userMapper;



    /**
     * 用户注册（停用）
     *
     * @param user
     * @return
     */
    @Override
    public ResponseResult<AppHttpCodeEnum> register(User user) {
        // 1.校验
        String userAccount = user.getUserName();
        String nickName = user.getNickName();
        String userPassword = user.getPassword();
        // 1.1、非空校验（使用 Apache Commons Lang库）
        if (StringUtils.isAnyEmpty(userAccount, userPassword)) {
            throw new SystemException(PARAMETER_ERROR, "账号密码不能为空");
        }
        // 1.2、账号为4~9位
        if (userAccount.length() < 4 || userAccount.length() > 9) {
            throw new SystemException(PARAMETER_ERROR, "账号为4~9位");
        }
        // 1.3、密码为8~16位
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new SystemException(PARAMETER_ERROR, "密码为8~16位");
        }
        // 1.4、账号不能包含特殊字符
        String validPattern = "[\\s`!@#$%^&*_\\-~()+=|{}':;,\\[\\].<>/\\\\?！￥…（）—【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new SystemException(PARAMETER_ERROR, "账号不能包含特殊字符");
        }
        // 1.5、密码不能含有空字符
        String validPattern1 = "[\\s]";
        Matcher matcher1 = Pattern.compile(validPattern1).matcher(userPassword);
        if (matcher1.find()) {
            throw new SystemException(PARAMETER_ERROR, "密码不能包含空字符");
        }
        // 1.6、账号不能重复（将数据库查询校验放到最后）
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new SystemException(USERNAME_EXIST, "账号已存在");
        }
        // 1.7、昵称不能重复（将数据库查询校验放到最后）
        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(User::getNickName, nickName);
        int count1 = count(lambdaQueryWrapper);
        if (count1 > 0) {
            throw new SystemException(NICKNAME_EXIST, "昵称已存在");
        }

        // 2.加密
        String encryptPassword = passwordEncoder.encode(userPassword);
        user.setPassword(encryptPassword);
        // 3.插入数据
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new SystemException(SYSTEM_ERROR, "系统错误");
        }
        return ResponseResult.okResult(SUCCESS);
    }

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @Override
    public ResponseResult<AppHttpCodeEnum> userRegister(UserRegisterRequest userRegisterRequest) {
        String userAccount = userRegisterRequest.getUserName();
        String userPassword = userRegisterRequest.getPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String captcha = userRegisterRequest.getCaptcha();
        String uuid = userRegisterRequest.getUuid();
        // 1.校验
        // 1.1、非空校验（使用 Apache Commons Lang库）
        if (StringUtils.isAnyEmpty(userAccount, userPassword, checkPassword)) {
            throw new SystemException(PARAMETER_ERROR, "账号密码不能为空");
        }
        // 1.2、账号为4~9位
        if (userAccount.length() < 4 || userAccount.length() > 9) {
            throw new SystemException(PARAMETER_ERROR, "账号为4~9位");
        }
        // 1.3、密码为8~16位
        if (userPassword.length() < 8 || userPassword.length() > 16) {
            throw new SystemException(PARAMETER_ERROR, "密码为8~16位");
        }
        // 1.4、账号不能包含特殊字符
        String validPattern = "[\\s`!@#$%^&*_\\-~()+=|{}':;,\\[\\].<>/\\\\?！￥…（）—【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new SystemException(PARAMETER_ERROR, "账号不能包含特殊字符");
        }
        // 1.5、密码不能含有空字符
        String validPattern1 = "[\\s]";
        Matcher matcher1 = Pattern.compile(validPattern1).matcher(userPassword);
        if (matcher1.find()) {
            throw new SystemException(PARAMETER_ERROR, "密码不能包含空字符");
        }
        // 1.6、密码和校验密码不相同
        if (!userPassword.equals(checkPassword)) {
            throw new SystemException(PARAMETER_ERROR, "密码和校验密码不相同");
        }
        // 1.7、验证码为1~4位
        if (captcha.length() < 1 || captcha.length() > 4) {
            throw new SystemException(PARAMETER_ERROR, "验证码为1~4位");
        }
        // 1.8、验证码失效
        String key = CAPTCHA_CODES + uuid;
        String text;
        text = redisCache.getCacheObject(key);
        if (StringUtils.isAnyBlank(text)) {
            throw new SystemException(PARAMETER_ERROR, "验证码失效请重试");
        }
        // 1.9、验证码错误
        String result = text.substring(text.lastIndexOf("@") + 1);
        if (!result.equals(captcha)) {
            throw new SystemException(PARAMETER_ERROR, "验证码错误请重试");
        }
        // 1.10、账号不能重复（将数据库查询校验放到最后）
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new SystemException(USERNAME_EXIST, "账号已存在");
        }

        // 2.加密
        String encryptPassword = passwordEncoder.encode(userPassword);
        // 3.插入数据
        User user = new User();
        user.setUserName(userAccount);
        user.setPassword(encryptPassword);
        // 分配UID
        Long common = userMapper.getUidForRegister("common");
        user.setUid(common);
        // 3.插入数据
        save(user);

        return ResponseResult.okResult(SUCCESS);
    }

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
        redisCache.setCacheObject(BLOG_LOGIN + userId, loginUser);

        //把token和userinfo封装 返回
        //把User转换成UserVo
        UserVO userInfoVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserVO.class);
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
            throw new SystemException(PARAMETER_ERROR, "账号密码不能为空");
        }
        // 1.2、账号为4~9位
        if (userAccount.length() < 4 || userAccount.length() > 9)
        {
            throw new SystemException(PARAMETER_ERROR, "账号为4~9位");
        }
        // 1.3、密码为8~16位
        if (userPassword.length() < 8 || userPassword.length() > 16)
        {
            throw new SystemException(PARAMETER_ERROR, "密码为8~16位");
        }
        // 1.4、账号不能包含特殊字符
        String validPattern = "[\\s`!@#$%^&*_\\-~()+=|{}':;,\\[\\].<>/\\\\?！￥…（）—【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find())
        {
            throw new SystemException(PARAMETER_ERROR, "账号不能包含特殊字符");
        }
        // 1.5、密码不能含有空字符
        String validPattern1 = "[\\s]";
        Matcher matcher1 = Pattern.compile(validPattern1).matcher(userPassword);
        if (matcher1.find())
        {
            throw new SystemException(PARAMETER_ERROR, "密码不能包含空字符");
        }
        // 1.6、验证码为1~4位
        if (captcha.length() < 1 || captcha.length() > 4)
        {
            throw new SystemException(PARAMETER_ERROR, "验证码为1~4位");
        }
        // 1.7、验证码失效
        String key = CAPTCHA_CODES + uuid;
        String text;
        text = redisCache.getCacheObject(key);
        if (StringUtils.isAnyBlank(text))
        {
            throw new SystemException(PARAMETER_ERROR, "验证码失效请重试");
        }

        String result = text.substring(text.lastIndexOf("@") + 1);
        if (!result.equals(captcha))
        {
            throw new SystemException(PARAMETER_ERROR, "验证码错误请重试");
        }

        // 2、记录用户的登录状态
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userAccount, userPassword);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        if (Objects.isNull(authenticate)) {
            throw new SystemException(AppHttpCodeEnum.LOGIN_ERROR, "账号或密码错误");
        }
        // 根据 userId生成 Token存入 redis(有效期24小时)
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUser().getId().toString();
        String jwt = JwtUtil.createJWT(userId, JSON.toJSONString(loginUser.getUser()), EXPIRATION);
        //把用户信息存入redis
        String redisKey=BLOG_LOGIN + userId;
        redisCache.setCacheObject(redisKey, loginUser);
        // 将 token存入 redis
        String tokenKey = BLOG_TOKEN + userId;
        redisCache.setCacheObject(tokenKey, jwt);
        // 将管理员权限信息存入 redis
        if (loginUser.getUser().getUserRole().equals(ADMIN_ROLE))
        {
            String adminKey = BLOG_ADMIN + userId;
            redisCache.setCacheObject(adminKey, jwt);
        }
        //把token和userinfo封装 返回
        //把User转换成UserVo
        UserVO userVo = BeanCopyUtils.copyBean(loginUser.getUser(), UserVO.class);
        UserLoginVO vo = new UserLoginVO(jwt, userVo);
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
    public ResponseResult<AppHttpCodeEnum> logout() {
        // 从 SecurityContextHolder中获取 loginUser
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
        return ResponseResult.okResult(SUCCESS, "退出成功");
    }

}