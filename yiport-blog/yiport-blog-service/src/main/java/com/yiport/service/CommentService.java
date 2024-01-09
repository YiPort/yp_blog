package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Comment;

public interface CommentService extends IService<Comment> {

    /**
     * 查询评论列表
     *
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    ResponseResult addComment(Comment comment);

    /**
     * 置顶/取消置顶文章评论
     *
     * @param id
     * @param label
     * @return
     */
    ResponseResult setCommentLabel(Long id, String label);

    ResponseResult<Void> deleteComment(Long id);
}