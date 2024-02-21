package com.yiport.controller;

import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.constants.BlogConstants;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.bo.CommentBO;
import com.yiport.domain.entity.Comment;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.UpdateCommentVO;
import com.yiport.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.yiport.constants.SystemConstants.TRUE;

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
        return commentService.getCommentList(articleId, pageNum, pageSize);
    }

    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    @PostMapping("/saveComment")
    @LimitRequest(time = 60 * 1000, count = 2, type = "USER", tip = TRUE, description = "休息一下再评论吧~")
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
        return commentService.getLinkCommentList(pageNum, pageSize);
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

    /**
     * 管理员查询评论
     */
    @GetMapping("/allCommentList")
    @SystemLog(businessName = "管理员查询所有评论")
    public ResponseResult<PageVO> allCommentList(CommentBO commentBO)
    {
        return commentService.getAllCommentList(commentBO);
    }

    /**
     * 用户删除评论
     */
    @DeleteMapping("/deleteMyComment/{id}")
    @SystemLog(businessName = "用户删除评论")
    public ResponseResult<Void> deleteMyComment(@PathVariable Long id)
    {
        return commentService.deleteMyComment(id);
    }

    /**
     * 更新评论用户信息
     */
    @PutMapping("/updateComment")
    @SystemLog(businessName = "更新评论用户信息")
    public ResponseResult<Void> updateComment(@RequestBody UpdateCommentVO updateCommentVO)
    {
        return commentService.updateComment(updateCommentVO);
    }

}