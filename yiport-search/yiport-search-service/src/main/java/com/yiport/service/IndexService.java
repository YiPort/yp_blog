package com.yiport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Index;

import java.util.List;


public interface IndexService extends IService<Index> {

    /**
     * 提交文章目录索引
     *
     * @param directoryIndex
     * @return
     */
    ResponseResult postArticleIndex(List<Index> directoryIndex, Long userId);

    /**
     * 获取文章目录索引
     *
     * @return
     */
    ResponseResult getArticleIndex();
}
