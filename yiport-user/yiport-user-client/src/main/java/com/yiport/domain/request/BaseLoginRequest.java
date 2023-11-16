package com.yiport.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Request 基类
 */
@Data
public class BaseLoginRequest implements Serializable {
    private static final long serialVersionUID = -8721779617506927498L;

    /**
     * 登录类型
     */
   private String type;
}
