package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 编辑记录响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditHistoryVO {

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 事件内容
     */
    private String content;

    /**
     * 时间戳
     */
    private String timestamp;

    /**
     * 图标颜色
     */
    private String color;


    /**
     * 图标
     */
    private String icon;

    /**
     * 图标尺寸
     */
    private String size;

}
