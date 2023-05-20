package cn.yiport.controller;

import cn.yiport.domain.ResponseResult;
import cn.yiport.domain.entity.Article;
import cn.yiport.service.ArticleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@Api(tags = "文章",description = "文章相关接口")
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
    @ApiOperation(value = "查询热门文章",notes = "查询热门文章，根据浏览量进行排序")
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
    @ApiOperation(value = "分页查询文章列表",notes = "根据文章页码和文章容量，分页查询文章列表，如果有categoryId就要查询时要和传入的相同")
    public ResponseResult articleList(Integer pageNum,Integer pageSize,Long categoryId){
        return articleService.articleList(pageNum,pageSize,categoryId);
    }

    
    /** 
    * @Description: 查询文章详情
    * @Author: YiPort
    * @Date: 2023/4/5 
    */
    @GetMapping("/{id}")
    @ApiOperation(value = "查询文章详情",notes = "根据文章id查询文章详情")
    public ResponseResult getArticleDetail(@PathVariable("id") Long id){
        return articleService.getArticleDetail(id);
    }


    /**
     * @Description 更新文章浏览量
     * @param id
     * @return
     */
    @PutMapping("/updateViewCount/{id}")
    @ApiOperation(value = "更新文章浏览量",notes = "根据文章id，更新文章浏览量")
    public ResponseResult updateViewCount(@PathVariable("id") Long id){
        return articleService.updateViewCount(id);
    }

}