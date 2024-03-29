package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.constants.BlogConstants;
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
     *
     * @param articleId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/commentList")
    @SystemLog(businessName = "查询评论列表")
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize){
        return commentService.commentList(BlogConstants.ARTICLE_COMMENT,articleId,pageNum,pageSize);
    }

    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    @PostMapping("/")
    @SystemLog(businessName = "保存评论")
    public ResponseResult addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    /**
     * 查询友联评论列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/linkCommentList")
    @SystemLog(businessName = "查询友链评论")
    public ResponseResult linkCommentList(Integer pageNum,Integer pageSize){
        return commentService.commentList(BlogConstants.LINK_COMMENT,null,pageNum,pageSize);
    }
}