package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.CommentVO;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CommentClient的备用处理器，当评论服务不可用时提供默认行为。
 */
@Slf4j
@Component
public class CommentClientResolver implements CommentClient {

    /**
     * 当服务不可用时，为查询评论列表提供默认响应。
     *
     * @param articleId 文章ID
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult commentList(Long articleId, Integer pageNum, Integer pageSize) {
        log.error("评论服务异常：为文章ID {} 获取评论列表请求失败", articleId);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "评论服务不可用");
    }

    /**
     * 当服务不可用时，为添加评论提供默认响应。
     *
     * @param comment 评论实体
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult addComment(CommentVO comment) {
        log.error("评论服务异常：添加评论请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "评论服务不可用");
    }

    /**
     * 当服务不可用时，为查询友联评论列表提供默认响应。
     *
     * @param pageNum 当前页码
     * @param pageSize 每页数量
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult linkCommentList(Integer pageNum, Integer pageSize) {
        log.error("评论服务异常：获取友联评论列表请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "评论服务不可用");
    }

    /**
     * 当服务不可用时，为置顶/取消置顶评论提供默认响应。
     *
     * @param id 评论ID
     * @param label 置顶/取消置顶标签
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult setCommentLabel(Long id, String label) {
        log.error("评论服务异常：为评论ID {} 执行 {} 操作请求失败", id, label.equals("top") ? "置顶" : "取消置顶");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "评论服务不可用");
    }
}
