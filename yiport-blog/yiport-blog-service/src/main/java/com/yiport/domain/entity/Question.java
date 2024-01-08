package com.yiport.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 反馈表
 *
 * @TableName yp_question
 */
@Data
@ToString
@EqualsAndHashCode
@TableName(value = "yp_question")
public class Question implements Serializable
{
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
     * 问题描述
     */
    private String questionDescription;
    /**
     * 提交问题的用户id
     */
    private Long createBy;
    /**
     * 提交时间
     */
    private Date createTime;
    /**
     * 更新人
     */
    private Long updateBy;
    /**
     * 最后编辑时间
     */
    private Date updateTime;
    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;


}