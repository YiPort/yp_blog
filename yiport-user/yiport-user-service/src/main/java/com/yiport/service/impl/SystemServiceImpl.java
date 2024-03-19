package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginInfo;
import com.yiport.domain.entity.User;
import com.yiport.domain.request.LoginInfoRequest;
import com.yiport.domain.request.UserRequest;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.UserVO;
import com.yiport.mapper.UserMapper;
import com.yiport.service.LoginInfoService;
import com.yiport.service.SystemService;
import com.yiport.utils.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 系统 业务层
 */
@Service
@RequiredArgsConstructor
public class SystemServiceImpl implements SystemService
{
    private final UserMapper userMapper;
    private final LoginInfoService loginInfoService;

    /**
     * 管理员分页查询用户
     */
    @Override
    public ResponseResult<PageVO<UserVO>> selectPageUser(UserRequest userRequest)
    {
        Page<User> page = userRequest.build();
        userMapper.selectPage(page, new LambdaQueryWrapper<User>()
                .orderByDesc(User::getCreateTime)
                .eq(!StringUtils.isEmpty(userRequest.getStatus()), User::getStatus, userRequest.getStatus())
                .eq(!StringUtils.isEmpty(userRequest.getUid()), User::getUid, userRequest.getUid())
                .like(!StringUtils.isEmpty(userRequest.getNickName()), User::getNickName, userRequest.getNickName())
                .like(!StringUtils.isEmpty(userRequest.getUserName()), User::getUserName, userRequest.getUserName())
                .eq(!StringUtils.isEmpty(userRequest.getSex()), User::getSex, userRequest.getSex())
                .like(!StringUtils.isEmpty(userRequest.getEmail()), User::getEmail, userRequest.getEmail())
                .eq(userRequest.getUserRole() != null, User::getUserRole, userRequest.getUserRole())
                .ge(!StringUtils.isEmpty(userRequest.getStartTime()), User::getCreateTime, userRequest.getStartTime())
                .le(!StringUtils.isEmpty(userRequest.getEndTime()), User::getCreateTime, userRequest.getEndTime()));
        List<User> users = page.getRecords();
        List<UserVO> userVOS = BeanCopyUtils.copyBeanList(users, UserVO.class);
        PageVO<UserVO> pageVO = new PageVO<>(userVOS, page.getTotal());

        return ResponseResult.okResult(pageVO);
    }

    /**
     * 管理员根据 id删除用户
     */
    @Override
    public ResponseResult<Void> deleteUserById(Long id)
    {
        userMapper.deleteById(id);
        return ResponseResult.okResult();
    }

    /**
     * 管理员分页查询访问信息
     */
    @Override
    public ResponseResult<PageVO<LoginInfo>> selectPageLoginInfo(LoginInfoRequest loginInfoRequest)
    {
        Page<LoginInfo> page = loginInfoRequest.build();
        loginInfoService.page(page, new LambdaQueryWrapper<LoginInfo>()
                .orderByDesc(LoginInfo::getLoginTime)
                .eq(!StringUtils.isEmpty(loginInfoRequest.getStatus()), LoginInfo::getStatus, loginInfoRequest.getStatus())
                .like(!StringUtils.isEmpty(loginInfoRequest.getUserName()), LoginInfo::getUserName, loginInfoRequest.getUserName())
                .like(!StringUtils.isEmpty(loginInfoRequest.getLoginLocation()), LoginInfo::getLoginLocation, loginInfoRequest.getLoginLocation())
                .like(!StringUtils.isEmpty(loginInfoRequest.getMsg()), LoginInfo::getMsg, loginInfoRequest.getMsg())
                .ge(!StringUtils.isEmpty(loginInfoRequest.getStartTime()), LoginInfo::getLoginTime, loginInfoRequest.getStartTime())
                .le(!StringUtils.isEmpty(loginInfoRequest.getEndTime()), LoginInfo::getLoginTime, loginInfoRequest.getEndTime()));
        List<LoginInfo> loginInfos = page.getRecords();
        PageVO<LoginInfo> pageVO = new PageVO<>(loginInfos, page.getTotal());

        return ResponseResult.okResult(pageVO);
    }

    /**
     * 管理员删除访问信息
     */
    @Override
    public ResponseResult<Void> deleteLoginInfo(Long id)
    {
        loginInfoService.removeById(id);
        return ResponseResult.okResult();
    }

}
