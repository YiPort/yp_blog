package com.yiport.domain.vo;

import lombok.Data;

@Data
public class QuestionVO {
    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 提交问题的用户id
     */
    private Long createBy;

    /**
     * 问题描述
     */
    private String questionDescription;

}
