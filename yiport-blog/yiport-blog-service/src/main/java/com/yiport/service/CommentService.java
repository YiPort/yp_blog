package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Comment;
import com.yiport.domain.vo.PageVO;

public interface CommentService extends IService<Comment> {

    /**
     * 查询文章评论列表
     */
    ResponseResult<PageVO> getCommentList(Long articleId, Integer pageNum, Integer pageSize);

    /**
     * 查询友链评论列表
     */
    ResponseResult<PageVO> getLinkCommentList(Integer pageNum, Integer pageSize);

    /**
     * 保存评论
     */
    ResponseResult<Void>  addComment(Comment comment);

    /**
     * 置顶/取消置顶文章评论
     */
    ResponseResult<Void> setCommentLabel(Long id, String label);

    /**
     * 管理员删除评论
     */
    ResponseResult<Void> deleteComment(Long id);
}