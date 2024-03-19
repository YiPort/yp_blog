package com.yiport.domain.request;

import com.yiport.domain.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 访问信息分页查询对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LoginInfoRequest extends PageBase
{

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 登录地点
     */
    private String loginLocation;

    /**
     * 登录状态（0成功 1失败）
     */
    private String status;

    /**
     * 提示消息
     */
    private String msg;

    /**
     * 访问时间
     */
    private Date loginTime;

}
