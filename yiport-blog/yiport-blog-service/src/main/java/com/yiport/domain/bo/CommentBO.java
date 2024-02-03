package com.yiport.domain.bo;

import com.yiport.domain.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 评论管理分页查询对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentBO extends PageBase
{
    /**
     * 评论类型
     */
    private String type;

    /**
     * 文章ID
     */
    private Long articleId;
}
