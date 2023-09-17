package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章详情响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailVO {
    private Long id;

    /**
     * 是否允许评论 1是，0否
     */
    private String isComment;

    /**
     * 创建人的用户id
     */
    private Long createBy;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 所属分类id
     */
    private Long categoryId;

    /**
     * 所属分类名
     */
    private String categoryName;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 文章内容
     */
    private String content;

    /**
     * 访问量
     */
    private Long viewCount;

    /**
     * 创建时间
     */
    private String createTime;
}
