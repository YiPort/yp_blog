package com.yiport.controller;

import com.yiport.annotation.LimitRequest;
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

import static com.yiport.constent.UserConstant.TRUE;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserLoginService userLoginService;

    /**
     * 修改用户信息
     *
     * @param editUserVO
     * @return
     */
    @PutMapping("/saveUserInfo")
    @SystemLog(businessName = "修改用户信息")
    public ResponseResult<Void> saveUserInfo(@Validated(EditUserVO.UPDATE_INFO.class)
                                             @RequestBody EditUserVO editUserVO)
    {
        return userService.updateUserInfo(editUserVO);
    }

    /**
     * 修改密码
     */
    @SystemLog(businessName = "修改密码")
    @PutMapping("/updatePassword")
    @LimitRequest(time = 60 * 1000, count = 3, tip = TRUE)
    public ResponseResult<Void> updatePassword(@Validated(EditUserVO.UPDATE_PASSWORD.class)
                                               @RequestBody EditUserVO editUserVO)
    {
        return userService.updatePassword(editUserVO);
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
     * 获取其他用户信息
     */
    @GetMapping("/getOtherUser/{userId}")
    @SystemLog(businessName = "获取其他用户信息")
    public ResponseResult<OtherUserVO> getOtherUser(@PathVariable Long userId)
    {
        return userService.getOtherUser(userId);
    }

}