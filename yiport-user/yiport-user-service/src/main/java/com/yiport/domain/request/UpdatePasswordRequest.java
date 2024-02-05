package com.yiport.domain.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import static com.yiport.constent.UserConstant.PASSWORD_REGEX;

/**
 * 修改密码请求体
 */
@Data
public class UpdatePasswordRequest
{

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱地址")
    private String email;

    @NotBlank(message = "验证码不能为空")
    private String captcha;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = PASSWORD_REGEX, message = "密码必须包含[数字][英文字母][特殊字符(!@#$%^&*)]并且长度在8-16之间")
    private String password;

    @NotBlank(message = "校验密码不能为空")
    private String checkPassword;
}
