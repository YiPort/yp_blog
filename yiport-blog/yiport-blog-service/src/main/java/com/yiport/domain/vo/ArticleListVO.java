package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章列表响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 所属分类名
     */
    private String categoryName;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 访问量
     */
    private Long viewCount;

    /**
     * 创建时间
     */
    private String createTime;


}