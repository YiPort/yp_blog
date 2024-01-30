package com.yiport.domain.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

/**
 * 修改密码请求体
 */
@Data
public class UpdatePasswordRequest
{

    @NotEmpty(message = "邮箱不能为空")
    @Email(message = "请输入正确的邮箱地址")
    private String email;

    @NotEmpty(message = "验证码不能为空")
    private String captcha;

    @NotEmpty(message = "密码不能为空")
    @Length(max = 16, min = 8, message = "密码为8~16位")
    private String password;

    @NotEmpty(message = "校验密码不能为空")
    private String checkPassword;
}
