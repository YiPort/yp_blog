package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.EditUserVO;
import com.yiport.domain.vo.OtherUserVO;
import com.yiport.domain.vo.UserVO;
import com.yiport.service.UserLoginService;
import com.yiport.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    /**
     * 更新个人信息
     *
     * @param editUserVO
     * @return
     */
    @PutMapping("/saveUserInfo")
    @SystemLog(businessName = "更新个人信息")
    public ResponseResult updateUserInfo(@Validated  @RequestBody EditUserVO editUserVO){
        return userService.updateUserInfo(editUserVO);
    }

    /**
     * 获取用户登录态
     *
     * @return
     */
    @GetMapping("/current")
    @SystemLog(businessName = "获取用户登录态")
    public ResponseResult current() {
        return userService.getCurrent();
    }

    /**
     * 管理员分页查询用户
     *
     * @param current  当前页
     * @param pageSize 页面容量
     * @return
     */
    @GetMapping("/searchUsers")
    @PreAuthorize("hasAuthority('1')")
    @SystemLog(businessName = "管理员分页查询用户")
    public ResponseResult<List<UserVO>> searchUsers(@RequestParam("current") String current, @RequestParam("pageSize") String pageSize) {
        return userService.searchUsers(current, pageSize);
    }

    /**
     * 管理员根据id删除用户
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('1')")
    @SystemLog(businessName = "管理员根据id删除用户")
    public ResponseResult deleteUser(@RequestParam("id") @RequestBody String id) {
        return userService.deleteUserById(id);
    }

    /**
     * 获取其他用户信息
     */
    @GetMapping("/getOtherUser/{userId}")
    @SystemLog(businessName = "获取其他用户信息")
    public ResponseResult<OtherUserVO> getOtherUser(@PathVariable Long userId)
    {
        return userService.getOtherUser(userId);
    }

}