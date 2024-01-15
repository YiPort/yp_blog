package com.yiport.domain.vo;

import lombok.Data;

/**
 * 更新评论用户信息请求体
 */
@Data
public class UpdateCommentVO
{
    /**
     * 创建人ID
     */
    private Long createBy;
    /**
     * 创建人的昵称
     */
    private String createNick;
    /**
     * 创建人的头像
     */
    private String avatarUrl;
}
