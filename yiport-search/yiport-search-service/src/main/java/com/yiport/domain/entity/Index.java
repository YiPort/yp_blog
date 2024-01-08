package com.yiport.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 索引表
 *
 * @TableName yp_index
 */
@Data
@ToString
@EqualsAndHashCode
@TableName(value = "yp_index")
public class Index implements Serializable {
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
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
    @TableField("index_value")
    private String value;
    /**
     * 索引位置
     */
    private BigDecimal indexPosition;
    /**
     * 提交时间
     */
    private String createTime;
    /**
     * 最后编辑时间
     */
    private String updateTime;
    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;

}