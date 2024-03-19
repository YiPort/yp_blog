package com.yiport.domain.request;

import com.yiport.domain.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 用户分页查询对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserRequest extends PageBase
{

    /**
     * UID
     */
    private String uid;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 性别
     */
    private String sex;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户状态 0-正常，1-封禁
     */
    private String status;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 角色
     */
    private Integer userRole;

}
