package com.yiport.domain.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 文章索引响应体
 */
@Data
public class IndexVO implements Serializable {
    private static final long serialVersionUID = -3242114426148476686L;

    /**
     * 主键
     */
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 索引类型
     */
    private String indexType;

    /**
     * 索引内容
     */
    private String value;

    /**
     * 索引位置
     */
    private BigDecimal indexPosition;

}
