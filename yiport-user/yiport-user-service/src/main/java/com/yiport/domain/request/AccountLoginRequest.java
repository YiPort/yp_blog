package com.yiport.domain.request;

import com.yiport.constent.UserConstant;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import java.io.Serializable;

import static com.yiport.constent.UserConstant.PASSWORD_REGEX;


/**
 * 账号密码登录请求体
 */
@Data
public class AccountLoginRequest implements Serializable
{
    private static final long serialVersionUID = -4835709278962674666L;
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = UserConstant.ACCOUNT_REGEX, message = "账号只含有汉字、数字、字母、下划线，并且长度在4-9之间")
    private String userName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = PASSWORD_REGEX, message = "密码必须包含[数字][英文字母][特殊字符(!@#$%^&*)]并且长度在8-16之间")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(max = 4, min = 1, message = "验证码错误")
    private String captcha;

    /**
     * UUID
     */
    @NotBlank(message = "验证码错误")
    private String uuid;

}
