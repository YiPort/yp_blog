package cn.yiport.controller;

import cn.yiport.domain.ResponseResult;
import cn.yiport.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/category")
@Api(tags = "分类",description = "分类相关接口")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询分类列表
     * @return
     */
    @GetMapping("/getCategoryList")
    @ApiOperation(value = "查询分类列表",notes = "获取所有的分类列表")
    public ResponseResult getCategoryList(){
       return categoryService.getCategoryList();
    }
}