package cn.yiport.controller;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.Article;
import cn.yiport.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}