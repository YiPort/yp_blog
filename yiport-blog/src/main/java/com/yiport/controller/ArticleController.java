package com.yiport.controller;

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
    * @Description: 查询热门文章
    * @Author: YiPort
    * @Date: 2023/4/2 
    */
    @GetMapping("/hotArticleList")
    @SystemLog(businessName = "查询热门文章")
    public ResponseResult hotArticleList(){
        

        ResponseResult result =  articleService.hotArticleList();
        return result;
    }


    /**
     * @Description: 分页查询文章列表
     * @Author: YiPort
     * @Date: 2023/4/5
     */
    @GetMapping("/articleList")
    @SystemLog(businessName = "查询文章列表")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    
    /** 
    * @Description: 查询文章详情
    * @Author: YiPort
    * @Date: 2023/4/5 
    */
    @GetMapping("/{id}")
    @SystemLog(businessName = "查询文章详情")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }


    @PutMapping("/updateViewCount/{id}")
    @SystemLog(businessName = "更新文章浏览量")
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

}