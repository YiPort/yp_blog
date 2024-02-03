package com.yiport.controller;

import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.bo.ArticleExamineBO;
import com.yiport.domain.entity.Article;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.SaveArticleVO;
import com.yiport.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

//    @GetMapping("/list")
//    public List<Article> test(){
//        return articleService.list();
//    }


    /**
     * 查询热门文章
     *
     * @Author: YiPort
     * @Date: 2023/4/2
     */
    @GetMapping("/hotArticleList")
    @SystemLog(businessName = "查询热门文章")
    public ResponseResult hotArticleList() {


        ResponseResult result = articleService.hotArticleList();
        return result;
    }

    /**
     * 查询最新发布文章
     *
     * @return
     */
    @GetMapping("/latestArticleList")
    @SystemLog(businessName = "查询最新发布文章")
    public ResponseResult latestArticleList()
    {
        return articleService.getLatestArticleList();
    }

    /**
     * 分页查询文章列表
     *
     * @Author: YiPort
     * @Date: 2023/4/5
     */
    @GetMapping("/articleList")
    @SystemLog(businessName = "查询文章列表")
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        return articleService.articleList(pageNum, pageSize, categoryId);
    }


    /**
     * 查询文章详情
     *
     * @Author: YiPort
     * @Date: 2023/4/5
     */
    @GetMapping("/articleDetail/{id}")
    @SystemLog(businessName = "查询文章详情")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id) {
        return articleService.getArticleDetail(id);
    }


    /**
     * 更新文章浏览量
     *
     * @param id
     * @return
     */
    @PutMapping("/updateViewCount/{id}")
    @SystemLog(businessName = "更新文章浏览量")
    @LimitRequest(time = 60*1000)
    public ResponseResult updateViewCount(@PathVariable("id") Long id) {
        return articleService.updateViewCount(id);
    }

    /**
     * 保存文章
     *
     * @param article
     * @return
     */
    @PostMapping(value = "/postArticle")
    @SystemLog(businessName = "保存文章")
    public ResponseResult postArticle(@RequestBody SaveArticleVO article) {
        return articleService.postArticle(article);
    }


    /**
     * 获取草稿
     *
     * @return
     */
    @GetMapping("/getDraft")
    @SystemLog(businessName = "获取草稿")
    public ResponseResult getDraftList() {
        return articleService.getDraftList();
    }


    /**
     * 获取编辑记录
     *
     * @return
     */
    @GetMapping("/getEditHistory")
    @SystemLog(businessName = "获取编辑记录")
    public ResponseResult getEditHistory() {
        return articleService.getEditHistory();
    }


    /**
     * 删除草稿
     *
     * @param articleId
     * @return
     */
    @DeleteMapping("/deleteDraft/{articleId}")
    @SystemLog(businessName = "删除草稿")
    public ResponseResult deleteDraft(@PathVariable Long articleId) {
        return articleService.deleteDraft(articleId);
    }


    /**
     * 获取我发布的文章总数
     *
     * @return
     */
    @GetMapping("/getMyArticleTotal")
    @SystemLog(businessName = "获取我发布的文章总数")
    public ResponseResult getMyArticleTotal()
    {
        return articleService.getMyArticleTotal();
    }

    /**
     * 获取我发布的文章总浏览量
     *
     * @return
     */
    @GetMapping("/getTotalView")
    @SystemLog(businessName = "获取我发布的文章总浏览量")
    public ResponseResult getTotalView()
    {
        return articleService.getTotalView();
    }


    /**
     * 查询提交审核文章
     */
    @GetMapping("/examineArticle")
    @SystemLog(businessName = "查询提交审核文章")
    public ResponseResult<PageVO> getNotExamineArticle(@RequestBody ArticleExamineBO articleExamineBO)
    {
        return articleService.getNotExamineArticle(articleExamineBO);
    }

    /**
     * 修改审核文章状态
     */
    @PutMapping("/editArticleExamine")
    @SystemLog(businessName = "修改审核文章状态")
    public ResponseResult<Void> editArticleExamine(@RequestBody Article article)
    {
        return articleService.editArticleExamine(article);
    }

}