package com.yiport.client;

import com.yiport.domain.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

/**
 * Feign客户端接口，用于文章收藏服务的远程调用。
 */
@Import(CollectionClientResolver.class)
@FeignClient(value = "blog-server", fallback = CollectionClientResolver.class)
public interface CollectionClient {

    /**
     * 收藏文章。
     *
     * @param userId 用户ID。
     * @param articleId 文章ID。
     * @return 操作结果。
     */
    @PutMapping("/collection/addCollection/{userId}/{articleId}")
    ResponseResult addCollection(@PathVariable Long userId, @PathVariable Long articleId);

    /**
     * 获取用户的收藏文章列表。
     *
     * @param userId 用户ID。
     * @return 包含收藏文章列表的ResponseResult。
     */
    @GetMapping("/collection/getCollectList/{userId}")
    ResponseResult getCollectList(@PathVariable Long userId);

    /**
     * 取消收藏文章。
     *
     * @param userId 用户ID。
     * @param articleId 文章ID。
     * @return 操作结果。
     */
    @DeleteMapping("/collection/deleteCollection/{userId}/{articleId}")
    ResponseResult deleteCollection(@PathVariable Long userId, @PathVariable Long articleId);
}
