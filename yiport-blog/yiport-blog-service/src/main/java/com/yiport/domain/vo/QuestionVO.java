package com.yiport.domain.vo;

import com.yiport.annotation.Xss;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
public class QuestionVO {
    /**
     * 文章id
     */
    @NotEmpty(message = "非法请求参数")
    private Long articleId;

    /**
     * 提交问题的用户id
     */
    @NotEmpty(message = "没有登录")
    private Long createBy;

    /**
     * 问题描述
     */
    @Length(min = 1, max = 150, message = "问题描述不能超过150字符")
    @Xss(message = "问题描述不能包含脚本字符")
    private String questionDescription;

}
