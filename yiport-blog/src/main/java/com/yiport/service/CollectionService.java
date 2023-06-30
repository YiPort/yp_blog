package com.yiport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Collection;


public interface CollectionService extends IService<Collection> {
    /**
     * 收藏文章
     *
     * @param userId
     * @param articleId
     * @return
     */
    ResponseResult addCollection(Long userId, Long articleId);

    /**
     * 获取收藏文章列表
     *
     * @param userId
     * @return
     */
    ResponseResult getCollectList(Long userId);

}
