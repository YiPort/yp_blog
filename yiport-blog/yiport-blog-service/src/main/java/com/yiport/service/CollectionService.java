package com.yiport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Collection;


public interface CollectionService extends IService<Collection> {
    /**
     * 收藏文章
     *
     * @param articleId
     * @return
     */
    ResponseResult addCollection(Long articleId);

    /**
     * 获取收藏文章列表
     *
     * @return
     */
    ResponseResult getCollectList();

    /**
     * 取消收藏文章
     *
     * @param articleId
     * @return
     */
    ResponseResult deleteCollection(Long articleId);

}
