package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 分类响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 分类名
     */
    @NotEmpty(message = "分类名不能为空")
    private String name;

    /**
     * 父分类id，如果没有父分类为-1
     */
    private Long pid;

    /**
     * 描述
     */
    @NotEmpty(message = "描述不能为空")
    private String description;

    /**
     * 创建人id
     */
    private Long createBy;

}
