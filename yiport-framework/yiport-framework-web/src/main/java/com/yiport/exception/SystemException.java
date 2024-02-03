package com.yiport.exception;

import com.yiport.enums.AppHttpCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;

@EqualsAndHashCode(callSuper = true)
@Data
public class SystemException extends RuntimeException
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

    public SystemException()
    {
        super(SYSTEM_ERROR.getMsg());
        this.code = SYSTEM_ERROR.getCode();
        this.msg = SYSTEM_ERROR.getMsg();
    }

    public SystemException(String description)
    {
        super(description);
        this.code = SYSTEM_ERROR.getCode();
        this.msg = SYSTEM_ERROR.getMsg();
        this.description = description;
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
    }

    public SystemException(AppHttpCodeEnum httpCodeEnum, String description)
    {
        super(httpCodeEnum.getMsg());
        this.code = httpCodeEnum.getCode();
        this.msg = httpCodeEnum.getMsg();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getDescription() {
        return description;
    }
}