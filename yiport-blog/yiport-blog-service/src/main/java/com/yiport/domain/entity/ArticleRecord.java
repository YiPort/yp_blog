package com.yiport.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @TableName article_record
 */
@TableName(value = "yp_article_record")
@Data
@EqualsAndHashCode
public class ArticleRecord implements Serializable
{
    /**
     * 编辑记录id
     */
    @TableId(type = IdType.AUTO)
    private Long recordId;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 所属分类id
     */
    private Long categoryId;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 是否允许评论 1是，0否
     */
    private String isComment;

    /**
     * 创建人的用户id
     */
    private Long createBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", recordId=").append(recordId);
        sb.append(", title=").append(title);
        sb.append(", content=").append(content);
        sb.append(", summary=").append(summary);
        sb.append(", categoryId=").append(categoryId);
        sb.append(", thumbnail=").append(thumbnail);
        sb.append(", isComment=").append(isComment);
        sb.append(", createBy=").append(createBy);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}