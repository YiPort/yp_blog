package com.yiport.domain.bo;


import com.yiport.domain.PageBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ArticleExamineBO extends PageBase
{

    /**
     * 文章审核状态（0待审核，1审核通过，2驳回）
     */
    private String articleExamine;

    /**
     * 创建人的用户id
     */
    private Long createBy;
}
