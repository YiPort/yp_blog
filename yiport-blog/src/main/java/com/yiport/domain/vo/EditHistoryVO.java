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
}
