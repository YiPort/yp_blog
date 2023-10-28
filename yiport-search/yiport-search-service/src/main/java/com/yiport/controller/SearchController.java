package com.yiport.controller;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.ArticleDoc;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.SearchQuery;
import com.yiport.service.SearchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Resource
    SearchService searchService;

    /**
     * 搜索博客
     *
     * @return 博客列表
     */
    @GetMapping("/searchArticle")
    public ResponseResult searchArticle(SearchQuery searchQuery){

        return searchService.searchArticle(searchQuery);

    }

}
