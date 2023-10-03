package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageVO implements Serializable {

    /**
     * 页面数据列表
     */
    private List rows;

    /**
     * 总记录数
     */
    private Long total;
}