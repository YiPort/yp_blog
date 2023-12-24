package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Article;
import com.yiport.domain.vo.SaveArticleVO;

public interface ArticleService extends IService<Article> {

    /**
     * 查询热门文章列表
     *
     * @return
     */
    ResponseResult hotArticleList();

    /**
     * 分页查询文章列表
     *
     * @Author: YiPort
     * @Date: 2023/4/5
     */
    ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId);

    /**
     * 查询文章详情
     *
     * @Author: YiPort
     * @Date: 2023/4/5
     */
    ResponseResult getArticleDetail(Long id);

    /**
     * 更新文章浏览量
     *
     * @param id
     * @return
     */
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


    /**
     * 获取编辑记录
     *
     * @param id
     * @return
     */
    ResponseResult getEditHistory(Long id);


    /**
     * 删除草稿
     *
     * @param id
     * @param articleId
     * @return
     */
    ResponseResult deleteDraft(Long id, Long articleId);


    /**
     * 获取我发布的文章总数
     *
     * @param id
     * @return
     */
    ResponseResult getMyArticleTotal(Long id);

    /**
     * 获取我发布的文章总浏览量
     *
     * @param id
     * @return
     */
    ResponseResult getTotalView(Long id);

    /**
     * 查询最新发布文章
     *
     * @return
     */
    ResponseResult getLatestArticleList();

}
