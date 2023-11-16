package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.CommentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

/**
 * Feign客户端接口，用于评论服务的远程调用。
 */
@Import(CommentClientResolver.class)
@FeignClient(value = "blog-server", fallback = CommentClientResolver.class)
public interface CommentClient {

    /**
     * 查询评论列表。
     *
     * @param articleId 文章ID。
     * @param pageNum 页码。
     * @param pageSize 页大小。
     * @return 包含评论列表的ResponseResult。
     */
    @GetMapping("/comment/commentList")
    ResponseResult commentList(@RequestParam(required = false) Long articleId, 
                               @RequestParam Integer pageNum, 
                               @RequestParam Integer pageSize);

    /**
     * 发表评论。
     *
     * @param comment 评论内容。
     * @return 操作结果。
     */
    @PostMapping("/comment/")
    ResponseResult addComment(@RequestBody CommentVO comment);

    /**
     * 查询友链评论列表。
     *
     * @param pageNum 页码。
     * @param pageSize 页大小。
     * @return 包含友链评论列表的ResponseResult。
     */
    @GetMapping("/comment/linkCommentList")
    ResponseResult linkCommentList(@RequestParam Integer pageNum, 
                                   @RequestParam Integer pageSize);

    /**
     * 置顶/取消置顶文章评论。
     *
     * @param id 评论ID。
     * @param label 状态标签（置顶或取消置顶）。
     * @return 操作结果。
     */
    @PutMapping("/comment/setCommentTop/{id}/{label}")
    ResponseResult setCommentLabel(@PathVariable Long id, 
                                   @PathVariable String label);
}
