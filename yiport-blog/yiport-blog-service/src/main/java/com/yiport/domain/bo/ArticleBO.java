package com.yiport.domain.bo;

import com.yiport.domain.PageBase;
import lombok.Data;

/**
 * 文章查询对象
 */
@Data
public class ArticleBO extends PageBase
{
    /**
     * 标题
     */
    private String title;

    /**
     * 所属分类id
     */
    private Long categoryId;

    /**
     * 是否置顶（0否，1是）
     */
    private String isTop;

    /**
     * 状态（0已发布，1草稿）
     */
    private String status;

    /**
     * 文章审核状态（0待审核，1审核通过，2驳回）
     */
    private String articleExamine;

    /**
     * 是否允许评论 1是，0否
     */
    private String isComment;

    /**
     * 创建人的用户id
     */
    private Long createBy;

}
