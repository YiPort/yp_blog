package com.yiport.client;


import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * Fallback handler for ArticleClient, providing default behaviors when article-server is unavailable.
 */
@Slf4j
@Component
public class ArticleClientResolver implements ArticleClient {

    /**
     * Provides a default response for fetching hot articles when the service is unavailable.
     *
     * @return A failure result with an empty list.
     */
    @Override
    public ResponseResult getHotArticles() {
        log.error("Article service exception: getHotArticles request failed");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"Article service unavailable");
    }

    /**
     * Provides a default response for fetching paginated article list when the service is unavailable.
     *
     * @param pageNum Page number.
     * @param pageSize Number of articles per page.
     * @param categoryId Category ID for filtering articles.
     * @return A failure result with an empty map.
     */
    @Override
    public ResponseResult getArticles(Integer pageNum, Integer pageSize, Long categoryId) {
        log.error("Article service exception: getArticles request failed");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"Article service unavailable");
    }

    /**
     * Provides a default response for fetching article details when the service is unavailable.
     *
     * @param id Article ID.
     * @return A failure result with null detail.
     */
    @Override
    public ResponseResult getArticleDetail(Long id) {
        log.error("Article service exception: getArticleDetail request failed");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"Article service unavailable");
    }

    /**
     * Provides a default response for updating article view count when the service is unavailable.
     *
     * @param id Article ID.
     * @return A confirmation of failure.
     */
    @Override
    public ResponseResult updateArticleViewCount(Long id) {
        log.error("Article service exception: updateArticleViewCount request failed");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"Article service unavailable");
    }

    // Additional methods related to article operations could be added here with similar fallback implementations.
}
