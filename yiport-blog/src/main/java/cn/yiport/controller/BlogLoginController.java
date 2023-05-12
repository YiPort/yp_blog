package cn.yiport.controller;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;
import cn.yiport.enums.AppHttpCodeEnum;
import cn.yiport.handler.exception.SystemException;
import cn.yiport.service.BlogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BlogLoginController {
    @Autowired
    private BlogLoginService blogLoginService;

    /**
     * 用户登录
     * @param user
     * @return
     */
    @PostMapping("/login")
    public ResponseResult login(@RequestBody User user){
       if(!StringUtils.hasText(user.getUserName())){
           throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
       }
        return blogLoginService.login(user);
    }

    /**
     * 用户退出登录
     * 需要token请求头
     * @return
     */
    @PostMapping("/logout")
    public ResponseResult logout(){
        return blogLoginService.logout();
    }
}