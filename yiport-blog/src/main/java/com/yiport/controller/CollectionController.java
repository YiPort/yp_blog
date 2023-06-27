package com.yiport.controller;


import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.service.CollectionService;
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
     * @param userId
     * @param articleId
     * @return
     */
    @PutMapping("/addCollection/{userId}/{articleId}")
    @SystemLog(businessName = "收藏文章")
    public ResponseResult addCollection(@PathVariable Long userId, @PathVariable Long articleId) {
        return collectionService.addCollection(userId, articleId);
    }
}
