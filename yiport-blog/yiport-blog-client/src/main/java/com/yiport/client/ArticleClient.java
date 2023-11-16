package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.ArticleDetailVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文章服务的Feign客户端接口。
 */
@Import(ArticleClientResolver.class)
@FeignClient(value = "blog-server", fallback = ArticleClientResolver.class)
public interface ArticleClient {

    /**
     * 获取热门文章列表。
     *
     * @return 包含文章列表的RestResult。
     */
    @GetMapping("/article/hot")
    ResponseResult<List<ArticleDetailVO>> getHotArticles();

    /**
     * 分页获取文章列表。
     *
     * @param pageNum 页码。
     * @param pageSize 每页文章数量。
     * @param categoryId 文章分类ID。
     * @return 包含文章详细信息的RestResult。
     */
    @GetMapping("/article/list")
    ResponseResult<Map<String, Object>> getArticles(@RequestParam Integer pageNum, @RequestParam Integer pageSize, @RequestParam Long categoryId);

    /**
     * 获取指定文章的详细信息。
     *
     * @param id 文章ID。
     * @return 包含文章详细信息的RestResult。
     */
    @GetMapping("/article/detail/{id}")
    ResponseResult<ArticleDetailVO> getArticleDetail(@PathVariable("id") Long id);

    /**
     * 更新文章的浏览次数。
     *
     * @param id 文章ID。
     * @return 确认更新的RestResult。
     */
    @PutMapping("/article/view/{id}")
    ResponseResult<Void> updateArticleViewCount(@PathVariable("id") Long id);

    // 如果需要其他文章相关的操作，可以在这里继续添加方法定义。
}
