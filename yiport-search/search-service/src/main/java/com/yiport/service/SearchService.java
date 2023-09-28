package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.SearchQuery;

public interface SearchService {

    /**
     * 搜索博客内容
     *
     * @param searchQuery 搜索条件
     * @return 搜索到的结果
     */
    ResponseResult searchArticle(SearchQuery searchQuery);

}
