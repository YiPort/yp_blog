package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.Category;

/**
 * 分类表(Category)表服务接口
 *
 * @author YiPort
 * @since 2023-04-03 10:10:55
 */
public interface CategoryService extends IService<Category> {

    ResponseResult getCategoryList();
}

