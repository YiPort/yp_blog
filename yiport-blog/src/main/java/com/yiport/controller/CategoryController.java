package com.yiport.controller;

import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.CategoryVO;
import com.yiport.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类列表
     *
     * @return
     */
    @GetMapping("/getCategoryList")
    @SystemLog(businessName = "查询分类列表")
    public ResponseResult getCategoryList() {
        return categoryService.getCategoryList();
    }


    /**
     * 新建文章分类
     *
     * @param category
     * @return
     */
    @PostMapping("/addCategory")
    @SystemLog(businessName = "新建文章分类")
    public ResponseResult addCategory(@RequestBody CategoryVO category) {
        return categoryService.addCategory(category);
    }
}