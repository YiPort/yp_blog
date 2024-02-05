package com.yiport.domain.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.yiport.constent.UserConstant.ACCOUNT_REGEX;
import static com.yiport.constent.UserConstant.PASSWORD_REGEX;

/**
 * 用户注册请求体
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 946359976113063279L;

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Pattern(regexp = ACCOUNT_REGEX, message = "账号只含有汉字、数字、字母、下划线，并且长度在4-9之间")
    private String userName;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = PASSWORD_REGEX, message = "密码必须包含[数字][英文字母][特殊字符(!@#$%^&*)]并且长度在8-16之间")
    private String password;

    /**
     * 校验密码
     */
    @NotBlank(message = "校验密码不能为空")
    private String checkPassword;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Length(max = 4, min = 1, message = "验证码错误")
    private String captcha;

    /**
     * 验证码UUID
     */
    @NotBlank(message = "验证码错误")
    private String uuid;

}
