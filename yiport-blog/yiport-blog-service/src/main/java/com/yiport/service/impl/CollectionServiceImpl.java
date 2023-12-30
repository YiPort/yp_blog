package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.Collection;
import com.yiport.domain.vo.ArticleListVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.CollectionMapper;
import com.yiport.service.CollectionService;
import com.yiport.utils.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.List;

import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constants.BlogConstants.RELEASE;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 收藏业务层
 */
@Service
public class CollectionServiceImpl extends ServiceImpl<CollectionMapper, Collection>
        implements CollectionService {
    @Autowired
    private RedisCache redisCache;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CollectionMapper collectionMapper;

    /**
     * 收藏文章
     *
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult addCollection(Long userId, Long articleId) {
        //校验登录状态
        checkLogin(userId);
        // 校验文章是否存在
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE);
        queryWrapper.eq(Article::getId, articleId);
        if (articleMapper.selectOne(queryWrapper) == null) {
            throw new SystemException(PARAMETER_ERROR, "文章不存在");
        }
        LambdaQueryWrapper<Collection> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Collection::getArticleId, articleId);
        queryWrapper1.eq(Collection::getCreateBy, userId);
        if (collectionMapper.selectOne(queryWrapper1) != null) {
            throw new SystemException(PARAMETER_ERROR, "该文章已收藏");
        }
        // 添加收藏
        Collection collection = new Collection();
        collection.setArticleId(articleId);
        collection.setCreateBy(userId);
        collectionMapper.insert(collection);

        return ResponseResult.okResult();
    }

    /**
     * 获取收藏文章列表
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getCollectList(Long userId) {
        //校验登录状态
        checkLogin(userId);

        List<ArticleListVO> collectList = collectionMapper.getCollectList(userId);

        return ResponseResult.okResult(collectList);
    }

    /**
     * 取消收藏文章
     *
     * @param userId
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteCollection(Long userId, Long articleId) {
        //校验登录状态
        checkLogin(userId);
        //删除收藏文章
        LambdaQueryWrapper<Collection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collection::getCreateBy, userId);
        queryWrapper.eq(Collection::getArticleId, articleId);
        collectionMapper.delete(queryWrapper);
        return ResponseResult.okResult();
    }

    /**
     * 校验登录状态
     *
     * @param userId
     */
    private void checkLogin(Long userId) {
        // id校验
        if (userId <= 0) {
            throw new SystemException(PARAMETER_ERROR);
        }
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        // token校验
        String token = request.getHeader("Token");
        if (StringUtils.isAnyBlank(token)) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        String tokenKey = BLOG_TOKEN + userId;
        Object cacheObject = redisCache.getCacheObject(tokenKey);
        if (cacheObject == null) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        if (!token.equals(String.valueOf(cacheObject))) {
            throw new SystemException(NEED_LOGIN, "登录过期，请重新登录");
        }
    }
}

