package com.yiport.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.yiport.enums.AppHttpCodeEnum;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {
    private Integer code;
    private String msg;
    private T data;

    /**
     * 详细描述
     */
    private String description;

    public ResponseResult()
    {
        this.code = AppHttpCodeEnum.SUCCESS.getCode();
        this.msg = AppHttpCodeEnum.SUCCESS.getMsg();
    }

    public ResponseResult(Integer code, T data)
    {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data)
    {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data, String description)
    {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.description = description;
    }

    public ResponseResult(Integer code, String msg)
    {
        this.code = code;
        this.msg = msg;
    }

    public static <T> ResponseResult<T> errorResult(int code, String msg)
    {
        ResponseResult<T> result = new ResponseResult<>();
        return result.error(code, msg);
    }

    public static <T> ResponseResult<T> errorResult(int code, String msg, String description)
    {
        ResponseResult<T> result = new ResponseResult<>();
        return result.error(code, msg, description);
    }

    public static <T> ResponseResult<T> okResult()
    {
        return new ResponseResult<>();
    }

    public static <T> ResponseResult<T> okResult(int code, String msg)
    {
        ResponseResult<T> result = new ResponseResult<>();
        return result.ok(code, null, msg);
    }

    public static <T> ResponseResult<T> okResult(int code, String msg, String description)
    {
        ResponseResult<T> result = new ResponseResult<>();
        return result.ok(code, null, msg, description);
    }

    public static <T> ResponseResult<T> okResult(T data)
    {
        ResponseResult<T> result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS, AppHttpCodeEnum.SUCCESS.getMsg());
        if (data != null)
        {
            result.setData(data);
        }
        return result;
    }

    public static <T> ResponseResult<T> okResult(T data, String description)
    {
        ResponseResult<T> result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS, AppHttpCodeEnum.SUCCESS.getMsg(), description);
        if (data != null)
        {
            result.setData(data);
        }
        return result;
    }

    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum enums)
    {
        return setAppHttpCodeEnum(enums, enums.getMsg());
    }

    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum enums, String msg)
    {
        return setAppHttpCodeEnum(enums, msg);
    }

    public static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum enums)
    {
        return okResult(enums.getCode(), enums.getMsg());
    }

    private static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum enums, String msg)
    {
        return okResult(enums.getCode(), msg);
    }

    private static <T> ResponseResult<T> setAppHttpCodeEnum(AppHttpCodeEnum enums, String msg, String description)
    {
        return okResult(enums.getCode(), msg, description);
    }

    public ResponseResult<T> error(Integer code, String msg)
    {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public ResponseResult<T> error(Integer code, String msg, String description)
    {
        this.code = code;
        this.msg = msg;
        this.description = description;
        return this;
    }

    public ResponseResult<T> ok(Integer code, T data)
    {
        this.code = code;
        this.data = data;
        return this;
    }

    public ResponseResult<T> ok(Integer code, T data, String msg)
    {
        this.code = code;
        this.data = data;
        this.msg = msg;
        return this;
    }

    public ResponseResult<T> ok(Integer code, T data, String msg, String description)
    {
        this.code = code;
        this.data = data;
        this.msg = msg;
        this.description = description;
        return this;
    }

    public ResponseResult<T> ok(T data)
    {
        this.data = data;
        return this;
    }

    public Integer getCode()
    {
        return code;
    }

    public void setCode(Integer code)
    {
        this.code = code;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public T getData()
    {
        return data;
    }

    public void setData(T data)
    {
        this.data = data;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}