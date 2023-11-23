package com.yiport.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 评论响应体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentVO {

    /**
     * 主键id
     */
    private Long id;

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 根评论id
     */
    private Long rootId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 过滤敏感词后的评论内容
     */
    private String filterContent;

    /**
     * 所回复的目标评论的userid
     */
    private Long toCommentUserId;

    /**
     * 所回复的目标评论的user name
     */
    private String toCommentUserName;


    /**
     * 回复目标评论id
     */
    private Long toCommentId;

    /**
     * 创建人的昵称
     */
    private String createNick;

    /**
     * 创建人的头像
     */
    private String avatar;

    /**
     * 评论标签
     */
    private String label;

    /**
     * 评论状态 0-未精选，1-精选评论
     */
    private String status;

    /**
     * 创建人的用户id
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private String createTime;


    /**
     * 子评论列表
     */
    private List<CommentVO> children;

}