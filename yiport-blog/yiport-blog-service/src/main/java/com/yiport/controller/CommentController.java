package com.yiport.controller;

import com.yiport.annotation.LimitRequest;
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
    @PostMapping("/saveComment")
    @LimitRequest(time = 60 * 1000, count = 2, type = "USER", tip = true, description = "休息一下再评论吧~")
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

    /**
     * 置顶/取消置顶文章评论
     *
     * @param id
     * @param label
     * @return
     */
    @PutMapping("/setCommentTop/{id}/{label}")
    @SystemLog(businessName = "置顶/取消置顶文章评论")
    public ResponseResult setCommentLabel(@PathVariable Long id, @PathVariable String label)
    {
        return commentService.setCommentLabel(id, label);
    }


    /**
     * 管理员删除评论
     */
    @DeleteMapping("/deleteComment/{id}")
    @SystemLog(businessName = "管理员删除评论")
    public ResponseResult<Void> deleteComment(@PathVariable Long id)
    {
        return commentService.deleteComment(id);
    }

}