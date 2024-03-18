package com.yiport.domain.vo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import static com.yiport.constent.UserConstant.PASSWORD_REGEX;

/**
 * 用户修改信息
 *
 */
@Data
public class EditUserVO
{
    /**
     * id
     */
    @NotNull(message = "用户ID不能为空", groups = {UPDATE_INFO.class, UPDATE_PASSWORD.class})
    private Long id;

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空", groups = UPDATE_INFO.class)
    @Size(max = 16, message = "昵称长度不能超过16字符", groups = UPDATE_INFO.class)
    private String nickName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String sex;

    /**
     * 新密码
     */
    @NotBlank(message = "密码不能为空", groups = UPDATE_PASSWORD.class)
    @Pattern(regexp = PASSWORD_REGEX, message = "密码必须包含[数字][英文字母][特殊字符(!@#$%^&*)]并且长度在8-16之间",
            groups = UPDATE_PASSWORD.class)
    private String password;

    /**
     * 校验密码
     */
    @NotBlank(message = "校验密码不能为空", groups = UPDATE_PASSWORD.class)
    private String checkPassword;

    /**
     * 电话
     */
    private String phonenumber;

    /**
     * 修改密码分组
     */
    public interface UPDATE_PASSWORD
    {
    }

    /**
     * 修改用户信息分组
     */
    public interface UPDATE_INFO
    {
    }

}
