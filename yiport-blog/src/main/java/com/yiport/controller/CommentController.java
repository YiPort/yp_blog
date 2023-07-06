package com.yiport.controller;

import com.yiport.constants.SystemConstants;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Comment;
import com.yiport.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 查询评论列表
     * 不需要token请求头
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/commentList")
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize){
        return commentService.commentList(SystemConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    /**
     * 发表评论
     * 需要token头
     * @param comment
     * @return
     */
    @PostMapping
    public ResponseResult addComment(@RequestBody Comment comment){
        return commentService.addComment(comment);
    }

    /**
     * 查询友联评论列表
     * 不需要token请求头
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/linkCommentList")
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        return commentService.commentList(SystemConstants.LINK_COMMENT,null,pageNum,pageSize);
    }
}