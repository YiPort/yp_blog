package com.yiport.domain.bo;

import lombok.Data;

/**
 * 评论管理分页查询对象
 */
@Data
public class CommentBO
{
    private Integer pageNum;

    private Integer pageSize;

    private String type;

    private Long articleId;

    private String startTime;

    private String endTime;
}
