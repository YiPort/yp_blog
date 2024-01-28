package com.yiport.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
import com.yiport.domain.vo.EditUserVO;
import com.yiport.domain.vo.OtherUserVO;
import com.yiport.domain.vo.UserVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.service.UserService;
import com.yiport.utils.RedisCache;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.JwtUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constent.UserConstant.EMAIL_REGEX;
import static com.yiport.constent.UserConstant.EXPIRATION;
import static com.yiport.constent.UserConstant.LIMIT_TIME;
import static com.yiport.constent.UserConstant.NULL_REGEX;
import static com.yiport.constent.UserConstant.RELOAD_TIME;
import static com.yiport.constent.UserConstant.TOKEN_HEADER_KEY;
import static com.yiport.constent.UserConstant.USER_INFO;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 用户表(User)表服务实现类
 *
 * @author yiport
 * @since 2023-05-01 13:50:17
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 更新个人信息
     *
     * @param editUserVO
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(EditUserVO editUserVO) {
        String username = editUserVO.getUsername();
        String sex = editUserVO.getSex();
        String avatarUrl = editUserVO.getAvatar();
        String userPassword = editUserVO.getPassword();
        String checkPassword = editUserVO.getCheckPassword();
        // 获取时间戳,设置创更新时间
        if (StringUtils.isAnyBlank(username))
        {
            throw new SystemException(PARAMETER_ERROR, "昵称不能为空");
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, editUserVO.getId());
        User user = getOne(queryWrapper);
        user.setSex(sex);
        user.setAvatar(avatarUrl);
        if (Objects.nonNull(userPassword) && !StringUtils.isAllBlank(userPassword, checkPassword))
        {
            Matcher matcher1 = Pattern.compile(NULL_REGEX).matcher(userPassword);
            if (matcher1.find() || (userPassword.length() < 8 || userPassword.length() > 16))
            {
                throw new SystemException(PARAMETER_ERROR, "密码为8~16位且不能包含空字符");
            }
            if (!userPassword.equals(checkPassword))
            {
                throw new SystemException(PARAMETER_ERROR, "两次输入的密码不一致");
            }
            // 加密
            String encryptPassword = passwordEncoder.encode(userPassword);
            user.setPassword(encryptPassword);
        }
        User one = getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUserName, username)
                .ne(User::getId, editUserVO.getId()));
        if (Objects.nonNull(one))
        {
            throw new SystemException(PARAMETER_ERROR, "昵称已存在");
        }
        user.setUserName(username);
        updateById(user);
        return reloadToken(user.getId());
    }


    /**
     * 获取用户登录态
     *
     * @return
     */
    @Override
    public ResponseResult<UserVO> getCurrent() {
        String token = request.getHeader("Token");
        long id;
        try
        {
            Claims claims = JwtUtil.parseJWT(token);
            id = Long.parseLong(claims.getId());
        }
        catch (Exception e)
        {
            throw new SystemException("Token非法！");
        }
        User user = getById(id);
        UserVO userVO = BeanCopyUtils.copyBean(user, UserVO.class);

        return ResponseResult.okResult(userVO);
    }

    /**
     * 管理员根据用户昵称查询用户
     *
     * @param username 用户昵称
     * @return
     */
    @Override
    public ResponseResult<List<UserVO>> searchByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(username)) {
            queryWrapper.like("user_name", username);
        } else {
            return ResponseResult.okResult();
        }
        List<User> userList = list(queryWrapper);
        // 返回脱敏后的 List<User>
        List<UserVO> userVOS = BeanCopyUtils.copyBeanList(userList, UserVO.class);

        return ResponseResult.okResult(userVOS);
    }

    /**
     * 管理员分页查询用户
     *
     * @param current  当前页
     * @param pageSize 页面容量
     * @return
     */
    @Override
    public ResponseResult<List<UserVO>> searchUsers(String current, String pageSize) {

        if (StringUtils.isBlank(current) || !NumberUtils.isDigits(current)) current = "0";
        if (StringUtils.isBlank(pageSize) || !NumberUtils.isDigits(pageSize)) pageSize = "10";

        Page<User> page = new Page<>(Long.parseLong(current), Long.parseLong(pageSize));
        page(page);
        List<User> users = page.getRecords();

        List<UserVO> userVOS = BeanCopyUtils.copyBeanList(users, UserVO.class);

        return ResponseResult.okResult(userVOS);
    }

    /**
     * 管理员根据 id删除用户
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult<Void> deleteUserById(String id)
    {
        if (StringUtils.isBlank(id) || !NumberUtils.isDigits(id)) {
            throw new SystemException(PARAMETER_ERROR, "请求参数错误");
        }
        if (Long.parseLong(id) <= 0) {
            throw new SystemException(PARAMETER_ERROR, "参数错误，id大于0");
        }
        removeById(id);
        return ResponseResult.okResult(null);
    }


    /**
     * 刷新Token
     */
    public ResponseResult<Object> reloadToken(Long id)
    {
        String token = request.getHeader(TOKEN_HEADER_KEY);
        Claims claims;
        String jwt;
        Map<Object, Object> map = new HashMap<>();
        // 解析token
        try
        {
            claims = JwtUtil.parseJWT(token);
        }
        catch (Exception e)
        {
            throw new SystemException(NEED_LOGIN, "Token非法！");
        }
        // 获取用户信息
        String userId = claims.getId();
        if (Objects.nonNull(id) && !id.toString().equals(userId))
        {
            throw new SystemException(NO_OPERATOR_AUTH);
        }
        User user = getById(userId);
        UserVO userVO = BeanCopyUtils.copyBean(user, UserVO.class);
        map.put(USER_INFO, userVO);
        String tokenKey = BLOG_TOKEN + userId;

        long now = System.currentTimeMillis();
        long startTime = claims.getIssuedAt().getTime();
        // 距离登录时间超过五天重新登录
        if (now - startTime < LIMIT_TIME)
        {
            long expiration = claims.getExpiration().getTime();
            // 距离过期时间小于12小时刷新token过期时间，并将token返回
            if (expiration - now < RELOAD_TIME)
            {
                jwt = JwtUtil.createJWT(userId, JSON.toJSONString(user), EXPIRATION);
            }
            else
            {
                return ResponseResult.okResult(userVO);
            }
        }
        else
        {
            redisCache.deleteObject(tokenKey);
            throw new SystemException(NEED_LOGIN, "用户未登录");
        }
        redisCache.setCacheObject(tokenKey, jwt);
        map.put(TOKEN_HEADER_KEY, jwt);
        return ResponseResult.okResult(map);
    }

    /**
     * 获取其他用户信息
     */
    @Override
    public ResponseResult<OtherUserVO> getOtherUser(Long userId)
    {
        User user = getById(userId);
        if (Objects.isNull(user))
            throw new SystemException(PARAMETER_ERROR, "用户不存在");
        OtherUserVO otherUserVO = BeanCopyUtils.copyBean(user, OtherUserVO.class);
        Date date=null;
        try {
          date = new SimpleDateFormat("yyyy-MM-dd").parse(user.getCreateTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long totalDay = DateUtil.betweenDay( date, new Date(System.currentTimeMillis()), true) + 1;
        otherUserVO.setTotalDay(totalDay);
        return ResponseResult.okResult(otherUserVO);
    }

}

