package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * CollectionClient的备用处理器，当收藏服务不可用时提供默认行为。
 */
@Slf4j
@Component
public class CollectionClientResolver implements CollectionClient {

    /**
     * 当服务不可用时，为添加文章到收藏提供默认响应。
     *
     * @param userId 用户ID，即执行收藏操作的用户。
     * @param articleId 要收藏的文章ID。
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult addCollection(Long userId, Long articleId) {
        log.error("收藏服务异常：为用户ID {} 和文章ID {} 添加收藏请求失败", userId, articleId);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "收藏服务不可用");
    }

    /**
     * 当服务不可用时，为获取用户的收藏文章列表提供默认响应。
     *
     * @param userId 用户ID，即查询收藏列表的用户。
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult getCollectList(Long userId) {
        log.error("收藏服务异常：获取用户ID {} 的收藏列表请求失败", userId);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "收藏服务不可用");
    }

    /**
     * 当服务不可用时，为取消文章收藏提供默认响应。
     *
     * @param userId 用户ID，即执行取消收藏操作的用户。
     * @param articleId 要取消收藏的文章ID。
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult deleteCollection(Long userId, Long articleId) {
        log.error("收藏服务异常：为用户ID {} 和文章ID {} 取消收藏请求失败", userId, articleId);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "收藏服务不可用");
    }
}
