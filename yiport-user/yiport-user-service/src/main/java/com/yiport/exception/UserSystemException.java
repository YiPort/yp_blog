package com.yiport.exception;

import com.yiport.enums.AppHttpCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;

/**
 * 自定义异常类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSystemException extends RuntimeException
{
    private static final long serialVersionUID = -5926292653065284660L;
    /**
     * 状态码
     */
    private Integer code;
    /**
     * 信息提示
     */
    private String msg;
    /**
     * 详细描述
     */
    private String description;

    public UserSystemException()
    {
        super(SYSTEM_ERROR.getMsg());
        this.code = SYSTEM_ERROR.getCode();
        this.msg = SYSTEM_ERROR.getMsg();
    }

    public UserSystemException(String description)
    {
        super(description);
        this.code = SYSTEM_ERROR.getCode();
        this.msg = SYSTEM_ERROR.getMsg();
    }

    public UserSystemException(AppHttpCodeEnum httpCodeEnum)
    {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    public UserSystemException(AppHttpCodeEnum httpCodeEnum, String description)
    {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
        this.description = description;
    }

}