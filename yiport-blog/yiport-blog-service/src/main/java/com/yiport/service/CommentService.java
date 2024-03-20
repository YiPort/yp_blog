package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.bo.CommentBO;
import com.yiport.domain.entity.Comment;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.UpdateCommentVO;

public interface CommentService extends IService<Comment> {

    /**
     * 查询文章评论列表
     */
    ResponseResult<PageVO> getCommentList(CommentBO commentBO);

    /**
     * 查询友链评论列表
     */
    ResponseResult<PageVO> getLinkCommentList(CommentBO commentBO);

    /**
     * 保存评论
     */
    ResponseResult<Void>  addComment(Comment comment);

    /**
     * 置顶/取消置顶文章评论
     */
    ResponseResult<Void> setCommentLabel(Long id, String label);

    /**
     * 管理员查询评论
     */
    ResponseResult<PageVO> getAllCommentList(CommentBO commentBO);

    /**
     * 管理员删除评论
     */
    ResponseResult<Void> deleteComment(Long id);


    /**
     * 用户删除评论
     */
    ResponseResult<Void> deleteMyComment(Long id);

    /**
     * 更新评论用户信息
     */
    ResponseResult<Void> updateComment(UpdateCommentVO updateCommentVO);

}