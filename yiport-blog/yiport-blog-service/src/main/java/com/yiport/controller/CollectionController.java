package com.yiport.controller;


import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.service.CollectionService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 收藏接口
 */
@RestController
@RequestMapping("/collection")
public class CollectionController {
    @Resource
    private CollectionService collectionService;

    /**
     * 收藏文章
     *
     * @param articleId
     * @return
     */
    @PutMapping("/addCollection/{articleId}")
    @SystemLog(businessName = "收藏文章")
    public ResponseResult addCollection( @PathVariable Long articleId) {
        return collectionService.addCollection(articleId);
    }

    /**
     * 获取收藏文章列表
     *
     * @return
     */
    @GetMapping("/getCollectList")
    @SystemLog(businessName = "获取收藏文章列表")
    public ResponseResult getCollectList() {
        return collectionService.getCollectList();
    }

    /**
     * 取消收藏文章
     *
     * @param articleId
     * @return
     */
    @DeleteMapping("/deleteCollection/{articleId}")
    @SystemLog(businessName = "取消收藏文章")
    public ResponseResult deleteCollection(@PathVariable Long articleId) {
        return collectionService.deleteCollection( articleId);
    }

}
