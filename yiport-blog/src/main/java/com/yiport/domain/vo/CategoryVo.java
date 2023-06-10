package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分类响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVo {
    private Long id;
    //分类名
    private String name;
}
