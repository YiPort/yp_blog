package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Category;
import com.yiport.domain.vo.CategoryVO;
import com.yiport.mapper.CategoryMapper;
import com.yiport.service.CategoryService;
import com.yiport.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


import static com.yiport.constants.SystemConstants.STATUS_NORMAL;

/**
 * 分类表(Category)表服务实现类
 *
 * @author YiPort
 * @since 2023-04-03 10:10:55
 */
@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Autowired
    private RedisCache redisCache;

    @Override
    public ResponseResult getCategoryList() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();

        // 是正常状态的分类
        queryWrapper.eq(Category::getStatus, STATUS_NORMAL);
        List<Category> categories = categoryMapper.selectList(queryWrapper);

        List<CategoryVO> categoryListVOS = BeanCopyUtils.copyBeanList(categories, CategoryVO.class);
        return ResponseResult.okResult(categoryListVOS);
    }
}
