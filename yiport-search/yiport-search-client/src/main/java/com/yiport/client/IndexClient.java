package com.yiport.client;


import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Index;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 文章索引控制层
 */
@FeignClient("search-service")
@RequestMapping("/search/index")
public interface IndexClient {


    /**
     * 提交文章目录索引
     *
     * @param directoryIndex
     * @return
     */
    @PostMapping("/postArticleIndex/{userId}")
    ResponseResult postArticleIndex(@RequestBody List<Index> directoryIndex, @PathVariable Long userId);

    /**
     * 获取文章目录索引
     *
     * @return
     */
    @GetMapping("/getArticleIndex")
    ResponseResult getArticleIndex() ;
}
