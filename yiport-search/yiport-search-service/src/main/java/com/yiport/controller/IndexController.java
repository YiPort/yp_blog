package com.yiport.controller;


import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Index;
import com.yiport.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章索引控制层
 */
@RestController
@RequestMapping("/search/index")
public class IndexController {
    @Autowired
    private IndexService indexService;

    /**
     * 提交文章目录索引
     *
     * @param directoryIndex
     * @return
     */
    @PostMapping("/postArticleIndex/{userId}")
    @SystemLog(businessName = "提交文章目录索引")
    public ResponseResult postArticleIndex(@RequestBody List<Index> directoryIndex, @PathVariable Long userId) {
        return indexService.postArticleIndex(directoryIndex, userId);
    }

    /**
     * 获取文章目录索引
     *
     * @return
     */
    @GetMapping("/getArticleIndex")
    @SystemLog(businessName = "获取文章目录索引")
    public ResponseResult getArticleIndex() {
        return indexService.getArticleIndex();
    }
}
