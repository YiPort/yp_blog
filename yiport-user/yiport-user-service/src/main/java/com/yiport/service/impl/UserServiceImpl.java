package com.yiport.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.User;
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

import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constent.UserConstant.EXPIRATION;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
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

    /**
     * 更新个人信息
     *
     * @param userVO
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(UserVO userVO) {
        // 获取时间戳,设置创更新时间
        String updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getId, userVO.getId());
        User user = getOne(queryWrapper);

        queryWrapper.eq(User::getId, user.getId());
        user.setNickName(userVO.getNickName());
        user.setEmail(userVO.getEmail());
        user.setSex(userVO.getSex());
        user.setAvatar(userVO.getAvatar());
        //设置保存时间
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        user.setUpdateTime(String.valueOf(currentTime));
        user.setUpdateBy(userVO.getId());
        updateById(user);
        return reloadToken();
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
    public ResponseResult deleteUserById(String id) {
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
     * 刷新 Token
     *
     * @return
     */
    public ResponseResult reloadToken()
    {
        String token = request.getHeader("Token");
        Claims claims;
        String jwt;
        // 解析token
        try
        {
            claims = JwtUtil.parseJWT(token);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException("Token非法！");
        }
        String userId = claims.getId();
        long now = System.currentTimeMillis();
        long startTime = claims.getIssuedAt().getTime();
        // 距离登录时间超过五天重新登录
        if (now - startTime < 5 * 24 * 60 * 60 * 1000L)
        {
            long expiration = claims.getExpiration().getTime();
            User user = getById(userId);
            // 距离过期时间小于6小时刷新token过期时间
            if (expiration - now < 6 * 60 * 60 * 1000L)
            {
                jwt = JwtUtil.createJWT(userId, JSON.toJSONString(user), EXPIRATION);
            }
            else
            {
                jwt = JwtUtil.updateJWT(userId, JSON.toJSONString(user), claims.getIssuedAt(), claims.getExpiration());
            }
        }
        else
        {
            throw new SystemException(NEED_LOGIN, "用户未登录");
        }
        redisCache.setCacheObject(BLOG_TOKEN + userId, jwt);
        Map<Object, Object> map = new HashMap<>();
        map.put("Token", jwt);
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

