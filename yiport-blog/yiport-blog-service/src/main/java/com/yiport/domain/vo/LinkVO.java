package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 友链响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 网站名称
     */
    private String name;

    /**
     * 网站logo图标链接
     */
    private String logo;

    /**
     * 友链描述
     */
    private String description;

    /**
     * 网站地址
     */
    private String address;

}
