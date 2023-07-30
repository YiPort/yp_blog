package com.yiport.service.impl;

import com.yiport.constants.SystemConstants;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.Category;
import com.yiport.domain.entity.EditHistory;
import com.yiport.domain.vo.ArticleDetailVO;
import com.yiport.domain.vo.ArticleListVO;
import com.yiport.domain.vo.EditHistoryVO;
import com.yiport.domain.vo.HotArticleVO;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.SaveArticleVO;
import com.yiport.handler.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.service.ArticleService;
import com.yiport.service.CategoryService;
import com.yiport.service.EditHistoryService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yiport.constants.BlogBusinessConstants.ARTICLE_EDITLIST;
import static com.yiport.constants.BlogBusinessConstants.ARTICLE_VIEWCOUNT;
import static com.yiport.constants.BusinessConstants.BLOG_ADMIN;
import static com.yiport.constants.BusinessConstants.BLOG_LOGIN;
import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constants.SystemConstants.NOT_RELEASE;
import static com.yiport.constants.SystemConstants.RELEASE;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;


@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private RedisCache redisCache;

    @Resource
    private ArticleMapper articleMapper;

    @Autowired
    private EditHistoryService editHistoryService;

    /**
     * 查询热门文章列表
     *
     * @return
     */
    @Override
    public ResponseResult hotArticleList() {
        //查询热门文章 封装成ResponseResult返回
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        //必须是正式文章
        queryWrapper.eq(Article::getStatus, SystemConstants.ARTICLE_STATUS_NORMAL);
        //按照浏览量进行排序
        queryWrapper.orderByDesc(Article::getViewCount);
        //最多只查询10条
        Page<Article> page = new Page(1,10);
        page(page,queryWrapper);

        List<Article> articles = page.getRecords();

        //bean拷贝
//        List<HotArticleVo> articleVos = new ArrayList<>();
//        for (Article article : articles) {
//            HotArticleVo vo = new HotArticleVo();
//            BeanUtils.copyProperties(article,vo);
//            articleVos.add(vo);
//        }

        // 查询文章所属分类
        articles.forEach(article -> article.setCategoryName(((Function<Article, String>) article1 -> {
            Long categoryId = article1.getCategoryId();
            return categoryService.getById(categoryId).getName();
        }).apply(article)));

        List<HotArticleVO> articleVos = BeanCopyUtils.copyBeanList(articles, HotArticleVO.class);
        // 从 redis中获取浏览量
        articleVos.forEach(hotArticleVO -> {
            String redisKey = ARTICLE_VIEWCOUNT;
            Integer viewCount = redisCache.getCacheMapValue(redisKey, hotArticleVO.getId().toString());
            if (viewCount != null) {
                hotArticleVO.setViewCount(Long.valueOf(viewCount));
            }
        });


        return ResponseResult.okResult(articleVos);
    }

    /**
     * 分页查询文章列表
     *
     * @return
     */
    @Override
    public ResponseResult articleList(Integer pageNum, Integer pageSize, Long categoryId) {
        //查询条件
        LambdaQueryWrapper<Article> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 如果 有categoryId 就要 查询时要和传入的相同
        lambdaQueryWrapper.eq(Objects.nonNull(categoryId)&&categoryId>0 ,Article::getCategoryId,categoryId);
        // 状态是正式发布的
        lambdaQueryWrapper.eq(Article::getStatus,SystemConstants.ARTICLE_STATUS_NORMAL);
        // 对isTop进行降序
        lambdaQueryWrapper.orderByDesc(Article::getIsTop);

        //分页查询
        Page<Article> page = new Page<>(pageNum,pageSize);
        page(page,lambdaQueryWrapper);

        List<Article> articles = page.getRecords();
        //查询categoryName
        //写法一：articleId去查询articleName进行设置
//        for (Article article : articles) {
//            Category category = categoryService.getById(article.getCategoryId());
//            article.setCategoryName(category.getName());
//        }
        //写法二：通过链式查询
        articles.stream()
                .map(article -> article.setCategoryName(categoryService.getById(article.getCategoryId()).getName()))
                .collect(Collectors.toList());


        //封装查询结果
        List<ArticleListVO> articleListVOS = BeanCopyUtils.copyBeanList(page.getRecords(), ArticleListVO.class);
        // 从 redis中获取浏览量
        articleListVOS.forEach(articleListVO -> {
            String redisKey = ARTICLE_VIEWCOUNT;
            Integer viewCount = redisCache.getCacheMapValue(redisKey, articleListVO.getId().toString());
            if (viewCount != null) {
                articleListVO.setViewCount(Long.valueOf(viewCount));
            }
        });
        PageVO pageVo = new PageVO(articleListVOS, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 获取文章详情
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult getArticleDetail(Long id) {
        //根据id查询文章
        Article article = getById(id);
        //从redis中获取viewCount
        Integer viewCount = redisCache.getCacheMapValue(ARTICLE_VIEWCOUNT, id.toString());
        article.setViewCount(viewCount.longValue());
        //转换成VO
        ArticleDetailVO articleDetailVo = BeanCopyUtils.copyBean(article, ArticleDetailVO.class);
        //根据分类id查询分类名
        Long categoryId = articleDetailVo.getCategoryId();
        Category category = categoryService.getById(categoryId);
        if (category != null) {
            articleDetailVo.setCategoryName(category.getName());
        }
        //封装响应返回
        return ResponseResult.okResult(articleDetailVo);
    }

    /**
     * 更新文章浏览量
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult updateViewCount(Long id) {
        //更新redis中对应 id的浏览量
        redisCache.incrementCacheMapValue(ARTICLE_VIEWCOUNT, id.toString(), 1);
        return ResponseResult.okResult();
    }

    /**
     * 保存文章
     *
     * @param article
     * @return
     */
    @Override
    public ResponseResult postArticle(SaveArticleVO article) {
        // 必要参数校验
        if (StringUtils.isAnyBlank(article.getTitle(), article.getContent()) || article.getCategoryId() == null) {
            throw new SystemException(PARAMETER_ERROR, "文章标题、内容、分类不能为空");
        }

        String userId = article.getCreateBy().toString();
        String loginKey = BLOG_LOGIN + userId;
        String tokenKey = BLOG_TOKEN + userId;
        Object loginObj = redisCache.getCacheObject(loginKey);
        Object tokenObj = redisCache.getCacheObject(tokenKey);
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }
        String token = request.getHeader("Token");
        // 登录校验
        if (loginObj == null || tokenObj == null || token == null) {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后操作");
        }
        // 登录过期校验
        if (!token.equals(String.valueOf(tokenObj))) {
            throw new SystemException(NEED_LOGIN, "登录过期，请重新登录");
        }
        // 管理员权限校验
        String adminKey = BLOG_ADMIN + userId;
        if (redisCache.getCacheObject(adminKey) == null) {
            article.setStatus(NOT_RELEASE);
        }
        // isComment值转换
        if (article.getIsComment().equals("true")) {
            article.setIsComment("1");
        } else if (article.getIsComment().equals("false")) {
            article.setIsComment("0");
        }


        Article saveArticle = BeanCopyUtils.copyBean(article, Article.class);
        //设置保存时间
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化创建时间
        String createTime = currentTime.format(formatter);
        saveArticle.setCreateTime(createTime);
        if (article.getId() == null) {
            // 保存文章
            articleMapper.insert(saveArticle);
        } else {
            // 更新文章
            articleMapper.updateById(saveArticle);
        }
        String editKey = ARTICLE_EDITLIST + saveArticle.getCreateBy();
        // 将文章浏览量同步到 redis
        if (article.getStatus().equals(RELEASE)) {   //已发布文章
            String articleKey = "article:" + saveArticle.getId();
            long viewCount = article.getViewCount() == null ? 1 : article.getViewCount();
            redisCache.setCacheObject(articleKey, BigInteger.valueOf(viewCount));
            // 将事件 push入消息列表
            EditHistory editHistory = new EditHistory(Long.parseLong(userId), "发布了文章：" + saveArticle.getTitle(), createTime, "#0bbd87");
            editHistoryService.saveOrUpdate(editHistory);
            redisCache.setCacheList(editKey, Arrays.asList(editHistory));
            return ResponseResult.okResult(saveArticle.getId());
        } else {
            // 将事件 push入消息队列
            EditHistory editHistory = new EditHistory(Long.parseLong(userId), "编辑了文章：" + saveArticle.getTitle(), createTime, "#e6a23c");
            editHistoryService.saveOrUpdate(editHistory);
            redisCache.setCacheList(editKey, Arrays.asList(editHistory));
        }

        return ResponseResult.okResult(200, "保存成功！");
    }


    /**
     * 获取草稿
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult getDraftList(Long id) {
        // 校验登录状态
        checkLogin(id);
        // 获取草稿
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, NOT_RELEASE);
        queryWrapper.eq(Article::getCreateBy, id);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        List<SaveArticleVO> saveArticleVOS = BeanCopyUtils.copyBeanList(articles, SaveArticleVO.class);

        return ResponseResult.okResult(saveArticleVOS);
    }


    /**
     * 获取编辑记录
     *
     * @param id 用户id
     * @return
     */
    @Override
    public ResponseResult getEditHistory(Long id) {
        // 校验登录状态
        checkLogin(id);
        // 获取编辑记录
        String editKey = ARTICLE_EDITLIST + id;
        List<Object> cacheList = redisCache.getCacheList(editKey);
        if (cacheList.isEmpty()) {
            LambdaQueryWrapper<EditHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EditHistory::getUserId, id);
            List<EditHistory> editHistories = editHistoryService.list(wrapper);
            List<EditHistoryVO> editHistoryVOS = BeanCopyUtils.copyBeanList(editHistories, EditHistoryVO.class);

            cacheList = new ArrayList<>(editHistoryVOS);
            if (!cacheList.isEmpty()) {
                redisCache.setCacheList(editKey, editHistoryVOS);
            }
        }
        return ResponseResult.okResult(cacheList);
    }

    /**
     * 删除草稿
     *
     * @param id
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteDraft(Long id, Long articleId) {
        // 校验登录状态
        checkLogin(id);
        // 获取标题
        String title = getById(articleId).getTitle();
        // 删除草稿
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getCreateBy, id);
        queryWrapper.eq(Article::getId, articleId);
        articleMapper.delete(queryWrapper);
        // 获取时间戳,设置创建时间
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String editKey = ARTICLE_EDITLIST + id;
        // 将事件 push入消息列表
        EditHistory editHistory = new EditHistory(id, "删除了文章：" + title, createTime, "#F56C6C");
        EditHistoryVO editHistoryVO = BeanCopyUtils.copyBean(editHistory, EditHistoryVO.class);
        editHistoryService.saveOrUpdate(editHistory);
        redisCache.setCacheList(editKey, Arrays.asList(editHistoryVO));
        return ResponseResult.okResult();
    }

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
