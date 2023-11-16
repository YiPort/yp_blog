package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.CategoryVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

/**
 * Feign客户端接口，用于文章分类服务的远程调用。
 */
@Import(CategoryClientResolver.class)
@FeignClient(value = "blog-server", fallback = CategoryClientResolver.class)
public interface CategoryClient {

    /**
     * 获取分类列表。
     *
     * @return 包含分类列表的ResponseResult。
     */
    @GetMapping("/category/getCategoryList")
    ResponseResult getCategoryList();

    /**
     * 新增文章分类。
     *
     * @param category 分类信息。
     * @return 操作结果。
     */
    @PostMapping("/category/addCategory")
    ResponseResult addCategory(@RequestBody CategoryVO category);
}
