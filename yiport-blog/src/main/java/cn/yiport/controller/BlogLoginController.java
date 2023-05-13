package cn.yiport.controller;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.User;
import cn.yiport.domain.request.AccountLoginRequest;
import cn.yiport.enums.AppHttpCodeEnum;
import cn.yiport.handler.exception.SystemException;
import cn.yiport.service.BlogLoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
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
//    public ResponseResult login(@RequestBody User user){
    public ResponseResult login( @RequestBody AccountLoginRequest accountLoginRequest){
//       if(!StringUtils.hasText(user.getUserName())){
//           throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
//       }
//        return blogLoginService.login(user);
        String userName = accountLoginRequest.getUserName();
        String userPassword = accountLoginRequest.getPassword();
        String captcha = accountLoginRequest.getCaptcha();
        String uuid = accountLoginRequest.getUuid();
        if (StringUtils.isBlank(userName) ) {
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        if (StringUtils.isBlank(userPassword)){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (StringUtils.isBlank(captcha)){
            throw new SystemException(AppHttpCodeEnum.PASSWORD_NOT_NULL);
        }
        if (StringUtils.isBlank(uuid)){

        }

        return blogLoginService.userLoginByAccount(accountLoginRequest);
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

    /**
     * 获取图片验证码
     *
     * @return
     */
    @GetMapping("/user/captchaImage")
    public ResponseResult getCaptchaImage() {
        return blogLoginService.getCaptchaImage();
    }


}