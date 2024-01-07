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
import com.yiport.utils.JwtUtil;
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

    @Autowired
    private  HttpServletRequest httpServletRequest;

    /**
     * 收藏文章
     *
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult addCollection( Long articleId) {
        //校验登录状态
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
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
     * @return
     */
    @Override
    public ResponseResult getCollectList() {
        //校验登录状态
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        List<ArticleListVO> collectList = collectionMapper.getCollectList(userId);

        return ResponseResult.okResult(collectList);
    }

    /**
     * 取消收藏文章
     *
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteCollection(Long articleId) {
        //校验登录状态
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        //删除收藏文章
        LambdaQueryWrapper<Collection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Collection::getCreateBy, userId);
        queryWrapper.eq(Collection::getArticleId, articleId);
        collectionMapper.delete(queryWrapper);
        return ResponseResult.okResult();
    }

}

