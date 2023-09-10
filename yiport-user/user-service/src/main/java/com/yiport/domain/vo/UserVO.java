package com.yiport.domain.vo;

import lombok.Data;

import java.io.Serializable;


@SuppressWarnings("serial")
@Data
public class UserVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 用户名（账号）
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;


    /**
     * 用户类型：0代表普通用户，1代表管理员
     */
    private String userRole;

    /**
     * 账号状态（0正常 1停用）
     */
    private String status;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phonenumber;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String sex;

    /**
     * 用户头像
     */
    private String avatar;


    /**
     * 创建时间
     */
    private String createTime;


}

