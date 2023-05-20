package com.yiport.controller;

import com.yiport.domain.ResponseResult;
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
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    
    /** 
    * @Description: 查询文章详情
    * @Author: YiPort
    * @Date: 2023/4/5 
    */
    @GetMapping("/{id}")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }



    @PutMapping("/updateViewCount/{id}")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }

}