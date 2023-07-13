package com.yiport.domain.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserInfoVO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 账号
     */
    private String userName;


    /**
     * 头像
     */
    private String avatar;

    /**
     * 用户性别（0男，1女，2未知）
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态 0-正常
     */
    private Integer status;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 用户角色 0-普通用户 1-管理员
     */
    private String userRole;
}
