package com.yiport.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 */
@Data
public class OtherUserVO implements Serializable
{
    private static final long serialVersionUID = 8253020330353735558L;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 性别
     */
    private Integer sex;

    /**
     * 用户状态 0-正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
     private String createTime;

    /**
     * UID
     */
    private Long uid;

    /**
     * 注册天数
     */
    private long totalDay;

}
