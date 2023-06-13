package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Category;
import com.yiport.domain.vo.CategoryVO;
import com.yiport.mapper.CategoryMapper;
import com.yiport.handler.exception.SystemException;
import com.yiport.service.CategoryService;
import com.yiport.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.utils.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;


import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constants.SystemConstants.STATUS_NORMAL;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

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


    /**
     * 新建文章分类
     *
     * @param category
     * @return
     */
    @Override
    public ResponseResult addCategory(CategoryVO category) {
        // 非空校验
        if (StringUtils.isAnyBlank(category.getName(), category.getDescription())) {
            throw new SystemException(PARAMETER_ERROR, "分类名和描述不能为空");
        }
        // 登陆校验
        if (category.getCreateBy() <= 0) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后再试");
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        String token = request.getHeader("token");
        if (token == null) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后再试");
        }
        String tokenKey = BLOG_TOKEN + category.getCreateBy();
        Object cacheObject = redisCache.getCacheObject(tokenKey);
        if (cacheObject == null || !String.valueOf(cacheObject).equals(token)) {
            throw new SystemException(NEED_LOGIN, "登录过期，请重新登陆");
        }
        Category category1 = BeanCopyUtils.copyBean(category, Category.class);

        category1.setCreateTime(new Date());
        save(category1);
        return ResponseResult.okResult();
    }


}
