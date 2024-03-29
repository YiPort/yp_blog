package com.yiport.controller;

import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
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
    @GetMapping("/{id}")
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
    @LimitRequest(time = 20*1000)
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
     * @param id
     * @return
     */
    @GetMapping("/getDraft/{id}")
    @SystemLog(businessName = "获取草稿")
    public ResponseResult getDraftList(@PathVariable Long id) {
        return articleService.getDraftList(id);
    }


    /**
     * 获取编辑记录
     *
     * @param id
     * @return
     */
    @GetMapping("/getEditHistory/{id}")
    @SystemLog(businessName = "获取编辑记录")
    public ResponseResult getEditHistory(@PathVariable Long id) {
        return articleService.getEditHistory(id);
    }


    /**
     * 删除草稿
     *
     * @param id
     * @param articleId
     * @return
     */
    @DeleteMapping("/deleteDraft/{id}/{articleId}")
    @SystemLog(businessName = "删除草稿")
    public ResponseResult deleteDraft(@PathVariable Long id, @PathVariable Long articleId) {
        return articleService.deleteDraft(id, articleId);
    }


    /**
     * 获取我发布的文章总数
     *
     * @param id
     * @return
     */
    @GetMapping("/getMyArticleTotal/{id}")
    @SystemLog(businessName = "获取我发布的文章总数")
    public ResponseResult getMyArticleTotal(@PathVariable Long id)
    {
        return articleService.getMyArticleTotal(id);
    }

    /**
     * 获取我发布的文章总浏览量
     *
     * @param id
     * @return
     */
    @GetMapping("/getTotalView/{id}")
    @SystemLog(businessName = "获取我发布的文章总浏览量")
    public ResponseResult getTotalView(@PathVariable Long id)
    {
        return articleService.getTotalView(id);
    }
}