package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO {

    /**
     * 页面数据列表
     */
    private List rows;

    /**
     * 总记录数
     */
    private Long total;
}