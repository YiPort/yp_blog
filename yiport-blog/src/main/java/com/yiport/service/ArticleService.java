package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Article;
import com.yiport.domain.vo.SaveArticleVO;

public interface ArticleService extends IService<Article> {
    ResponseResult hotArticleList();

    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    ResponseResult getArticleDetail(Long id);

    ResponseResult updateViewCount(Long id);


    /**
     * 保存文章
     *
     * @param article
     * @return
     */
    ResponseResult postArticle(SaveArticleVO article);

    /**
     * 获取草稿
     *
     * @param id
     * @return
     */
    ResponseResult getDraftList(Long id);


}
