package com.yiport.domain.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 编辑记录实体类
 *
 * @author yiport
 * @version 2023/6/7 15:13
 */
@Data
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@TableName("yp_edit_history")
public class EditHistory
{
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    private Long id;

    /**
     * 用户id
     */
    private Long userId;
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

    /**
     * 创建人的用户id
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新人的用户id
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 删除标志（0代表未删除，1代表已删除）
     */
    private Integer delFlag;

    public EditHistory(String content, String timestamp, String color) {

        this.content = content;
        this.timestamp = timestamp;
        this.color = color;

        this.createTime = timestamp;
    }


    public EditHistory(Long userId, String content, String timestamp, String color) {
        this.userId = userId;
        this.content = content;
        this.timestamp = timestamp;
        this.color = color;


        this.createBy = userId;
        this.createTime = timestamp;
    }

    public EditHistory(Long userId,String content, String timestamp, String color,String icon,String size) {

        this.userId=userId;
        this.content = content;
        this.timestamp = timestamp;
        this.color = color;
        this.icon=icon;
        this.size=size;

        this.createBy = userId;
        this.createTime = timestamp;
    }
}
