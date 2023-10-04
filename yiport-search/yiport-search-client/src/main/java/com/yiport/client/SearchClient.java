package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.SearchQuery;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("search-service")
@RequestMapping("/search")
public interface SearchClient {



    /**
     * 搜索博客
     *
     * @return 博客列表
     */
    @GetMapping("/searchArticle")
    ResponseResult searchArticle(SearchQuery searchQuery);

}
