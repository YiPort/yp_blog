package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginInfo;
import com.yiport.domain.request.LoginInfoRequest;
import com.yiport.domain.request.UserRequest;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.UserVO;
import com.yiport.service.SystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 系统 控制层
 */
@RestController
@RequestMapping("/user/system")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('1')")
public class SystemController
{
    private final SystemService systemService;

    /**
     * 管理员分页查询用户
     */
    @SystemLog(businessName = "管理员分页查询用户")
    @GetMapping("/searchList")
    public ResponseResult<PageVO<UserVO>> selectPageUser(UserRequest userRequest)
    {
        return systemService.selectPageUser(userRequest);
    }

    /**
     * 管理员根据 id删除用户
     */
    @SystemLog(businessName = "管理员根据id删除用户")
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> deleteUser(@PathVariable Long id)
    {
        return systemService.deleteUserById(id);
    }

    /**
     * 管理员分页查询访问信息
     */
    @GetMapping("/loginInfo/searchList")
    @SystemLog(businessName = "管理员分页查询访问信息")
    public ResponseResult<PageVO<LoginInfo>> selectPageLoginInfo(LoginInfoRequest loginInfoRequest)
    {
        return systemService.selectPageLoginInfo(loginInfoRequest);
    }

    /**
     * 管理员删除访问信息
     */
    @DeleteMapping("/loginInfo/deleteLoginInfo/{id}")
    @SystemLog(businessName = "管理员删除访问信息")
    public ResponseResult<Void> deleteLoginInfo(@PathVariable Long id)
    {
        return systemService.deleteLoginInfo(id);
    }

}
