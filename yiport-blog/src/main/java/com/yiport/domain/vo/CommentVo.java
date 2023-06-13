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
    private Long id;
    //文章id
    private Long articleId;
    //根评论id
    private Long rootId;
    //评论内容
    private String content;
    //所回复的目标评论的userid
    private Long toCommentUserId;
    private String toCommentUserName;
    //回复目标评论id
    private Long toCommentId;

    private String createNick;

    private String status;

    private Long createBy;

    private String createTime;


    private List<CommentVO> children;

}