package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class PageVO<T> implements Serializable
{

    private static final long serialVersionUID = 5081548464042833461L;
    /**
     * 存放数据
     */
    private List<T> rows;

    /**
     * 总记录数
     */
    private Long total;
}
