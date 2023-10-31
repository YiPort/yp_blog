package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Index;
import com.yiport.domain.vo.SearchQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "search-service",fallback = SearchClientResolver.class)
public interface SearchClient {



    /**
     * 搜索博客
     *
     * @return 博客列表
     */
    @RequestMapping(method = RequestMethod.GET,value = "/search/searchArticle")
    ResponseResult searchArticle( SearchQuery searchQuery);

    /**
     * 提交文章目录索引
     *
     * @param directoryIndex
     * @return
     */
    @RequestMapping(method = RequestMethod.POST,value = "/search/index/postArticleIndex/{userId}")
    ResponseResult postArticleIndex(@RequestBody List<Index> directoryIndex, @PathVariable("userId") Long userId);

    /**
     * 获取文章目录索引
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET,value = "/search/index/getArticleIndex")
    ResponseResult getArticleIndex() ;
}
