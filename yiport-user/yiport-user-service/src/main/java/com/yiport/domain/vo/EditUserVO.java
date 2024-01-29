package com.yiport.domain.vo;

import lombok.Data;

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
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

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
    private String password;

    /**
     * 校验密码
     */
    private String checkPassword;

    /**
     * 电话
     */
    private String phonenumber;

}
