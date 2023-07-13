package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.domain.vo.UserVO;
import com.yiport.domain.vo.UserInfoVO;
import com.yiport.handler.exception.SystemException;
import com.yiport.mapper.UserMapper;
import com.yiport.service.UserService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.SecurityUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 用户表(User)表服务实现类
 *
 * @author yiport
 * @since 2023-05-01 13:50:17
 */
@Service("userService")
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Override
    public ResponseResult userInfo() {
        //获取当前用户id
        Long userId = SecurityUtils.getUserId();
        //根据用户id查询用户信息
        User user = getById(userId);
        //封装成UserInfoVo
        UserInfoVO vo = BeanCopyUtils.copyBean(user, UserInfoVO.class);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult updateUserInfo(User user) {
        // 获取时间戳,设置创更新时间
        String updateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        user.setUpdateTime(updateTime);
        updateById(user);
        return ResponseResult.okResult();
    }


    /**
     * 获取用户登录态
     *
     * @return
     */
    @Override
    public ResponseResult<UserVO> getCurrent() {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        UserVO newUserVO = BeanCopyUtils.copyBean(loginUser.getUser(), UserVO.class);

        return ResponseResult.okResult(newUserVO);
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

}

