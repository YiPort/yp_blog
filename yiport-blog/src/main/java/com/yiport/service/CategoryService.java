package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Category;
import com.yiport.domain.vo.CategoryVO;

/**
 * 分类表(Category)表服务接口
 *
 * @author YiPort
 * @since 2023-04-03 10:10:55
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();


    /**
     * 新建文章分类
     *
     * @param category
     * @return
     */
    ResponseResult addCategory(CategoryVO category);

}

