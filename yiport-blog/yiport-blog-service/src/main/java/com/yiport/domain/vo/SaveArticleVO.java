package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 保存文章请求体
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveArticleVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

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
     * 是否置顶（0否，1是）
     */
    private String isTop;

    /**
     * 状态（0已发布，1草稿）
     */
    private String status;

    /**
     * 是否允许评论 1是，0否
     */
    private String isComment;

    /**
     * 创建人的用户id
     */
    private Long createBy;

    /**
     * 访问量
     */
    private Long viewCount;

}
