package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Index;
import com.yiport.domain.vo.SearchQuery;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * SearchClient请求失败时的熔断处理类
 */
@Slf4j
@Component
public class SearchClientResolver implements SearchClient{

    @Override
    public ResponseResult searchArticle(SearchQuery searchQuery) {
        log.error("搜索服务异常：searchArticle 请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"request fail");
    }

    @Override
    public ResponseResult postArticleIndex(List<Index> directoryIndex, Long userId) {
        log.error("搜索服务异常：postArticleIndex 请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"request fail");
    }

    @Override
    public ResponseResult getArticleIndex() {
        log.error("搜索服务异常：getArticleIndex 请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"request fail");
    }

}
