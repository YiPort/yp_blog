package com.yiport.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yiport.constants.BlogConstants;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.bo.ArticleExamineBO;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.ArticleRecord;
import com.yiport.domain.entity.Category;
import com.yiport.domain.entity.EditHistory;
import com.yiport.domain.vo.ArticleDetailVO;
import com.yiport.domain.vo.ArticleListVO;
import com.yiport.domain.vo.EditHistoryVO;
import com.yiport.domain.vo.HotArticleVO;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.SaveArticleVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.ArticleRecordMapper;
import com.yiport.service.ArticleService;
import com.yiport.service.CategoryService;
import com.yiport.service.EditHistoryService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.LoginUtils;
import com.yiport.utils.MarkdownUtils;
import com.yiport.utils.RedisCache;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.yiport.constants.BlogBusinessConstants.ARTICLE_EDITLIST;
import static com.yiport.constants.BlogBusinessConstants.ARTICLE_VIEWCOUNT;
import static com.yiport.constants.BlogConstants.ARTICLE_STATUS_NORMAL;
import static com.yiport.constants.BlogConstants.BLUE;
import static com.yiport.constants.BlogConstants.FAIL;
import static com.yiport.constants.BlogConstants.GREEN;
import static com.yiport.constants.BlogConstants.LARGE;
import static com.yiport.constants.BlogConstants.NORMAL;
import static com.yiport.constants.BlogConstants.NOT_EXAMINE;
import static com.yiport.constants.BlogConstants.NOT_PASS;
import static com.yiport.constants.BlogConstants.NOT_RELEASE;
import static com.yiport.constants.BlogConstants.PASS;
import static com.yiport.constants.BlogConstants.RED;
import static com.yiport.constants.BlogConstants.RELEASE;
import static com.yiport.constants.BlogConstants.SUCCESS;
import static com.yiport.constants.BlogConstants.UPLOAD;
import static com.yiport.constants.BlogConstants.YELLOW;
import static com.yiport.constants.MQConstants.BLOG_DELETE_KEY;
import static com.yiport.constants.MQConstants.BLOG_INSERT_KEY;
import static com.yiport.constants.MQConstants.BLOG_TOPIC_EXCHANGE;
import static com.yiport.constants.MQConstants.BLOG_UPDATE_KEY;
import static com.yiport.constants.SystemConstants.ADMIN_ID_1;
import static com.yiport.constants.SystemConstants.ADMIN_ID_2;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.SORRY_NOT_FOUND;



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

    @Autowired
    private ArticleRecordMapper articleRecordMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private  HttpServletRequest httpServletRequest;

    /**
     * 查询热门文章列表
     *
     * @return
     */
    @Override
    public ResponseResult hotArticleList() {
        // 查询浏览量最高的前10篇文章的信息
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE)
                .eq(Article::getArticleExamine, PASS)
                .orderByDesc(Article::getViewCount);
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
            String redisKey = ARTICLE_VIEWCOUNT+ hotArticleVO.getId().toString();
            hotArticleVO.setViewCount(Long.valueOf(redisCache.getCacheObject(redisKey).toString()));
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
        lambdaQueryWrapper.eq(Article::getStatus, BlogConstants.ARTICLE_STATUS_NORMAL);
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
            String redisKey = ARTICLE_VIEWCOUNT+ articleListVO.getId().toString();
            articleListVO.setViewCount(Long.valueOf(redisCache.getCacheObject(redisKey).toString()));
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
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE)
                .eq(Article::getArticleExamine, PASS)
                .eq(Article::getId, id);
        Article article = articleMapper.selectOne(queryWrapper);
        if (Objects.isNull(article)) {
            throw new SystemException(SORRY_NOT_FOUND, "没有找到文章");
        }
        //从redis中获取viewCount
        String redisKey = ARTICLE_VIEWCOUNT + article.getId().toString();
        article.setViewCount(Long.valueOf(redisCache.getCacheObject(redisKey).toString()));
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
        String redisKey = ARTICLE_VIEWCOUNT + id.toString();
        //更新redis中对应 id的浏览量
        Long increment = redisCache.increment(redisKey);
        // 若 increment的结果<=1，则传入的文章 id不存在
        Optional<Long> increment1 = Optional.ofNullable(increment);
        if (increment1.isPresent() && increment <= 1) {
            redisCache.deleteObject(redisKey);
        }
        return ResponseResult.okResult(increment);
    }

    /**
     * 保存文章
     *
     * @param article
     * @return
     */
    @Override
    public ResponseResult postArticle(SaveArticleVO article) {

        String userId = JwtUtil.checkToken(httpServletRequest);
        Article saveArticle = BeanCopyUtils.copyBean(article, Article.class);
        boolean isAdmin = StringUtils.equalsAny(userId, ADMIN_ID_1.toString(), ADMIN_ID_2.toString());
        if (isAdmin)
        {
            saveArticle.setArticleExamine(PASS);
        }
        else
        {
            saveArticle.setArticleExamine(NOT_EXAMINE);
        }
        //设置保存时间
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化创建时间
        String createTime = currentTime.format(formatter);
        saveArticle.setCategoryName(categoryService.getById(article.getCategoryId()).getName());
        String editKey = ARTICLE_EDITLIST + saveArticle.getCreateBy();
        if (article.getId() == null) {
            // 保存文章
            saveArticle.setCreateTime(createTime);
            articleMapper.insert(saveArticle);
            // 将文章浏览量同步到 redis
            if (saveArticle.getStatus().equals(RELEASE) && isAdmin)     //发布文章
            {
                String articleKey = ARTICLE_VIEWCOUNT + saveArticle.getId() ;
                long viewCount = article.getViewCount() == null ||article.getViewCount()==0 ? 1 : article.getViewCount();
                redisCache.setCacheObject(articleKey, BigInteger.valueOf(viewCount));
                // 将事件 push入消息列表
                EditHistory editHistory = new EditHistory(Long.valueOf(userId), "发布了文章：" + saveArticle.getTitle(), createTime,  GREEN, null, NORMAL);
                editHistoryService.saveOrUpdate(editHistory);
                redisCache.setCacheList(editKey, Arrays.asList(editHistory));

                //设置文章摘要为当前文章的内容节选,用于文章搜索
                saveArticle.setSummary(MarkdownUtils.markdown2PlainText(saveArticle.getContent()));
                //清空内容，减轻网络传输压力
                saveArticle.setContent("");
                //保存文章，发送MQ消息
                String articleDocs = JSON.toJSONString(saveArticle);
                rabbitTemplate.convertAndSend(BLOG_TOPIC_EXCHANGE, BLOG_INSERT_KEY, articleDocs);

                return ResponseResult.okResult(saveArticle.getId(), "发布成功");
            }
            else if (saveArticle.getStatus().equals(RELEASE) && !isAdmin)
            {
                // 将文章浏览量同步到 redis
                String articleKey = ARTICLE_VIEWCOUNT + saveArticle.getId();
                // viewCount为null时为新发布的文章或草稿，不为空时为已发布文章
                long viewCount = article.getViewCount() == null ||article.getViewCount()==0 ? 1 : article.getViewCount();
                redisCache.setCacheObject(articleKey, BigInteger.valueOf(viewCount));
                // 将事件 push入消息队列
                EditHistory editHistory = new EditHistory(Long.valueOf(userId), "提交了文章审核申请：" +
                        saveArticle.getTitle(), createTime, GREEN, UPLOAD, LARGE);
                editHistoryService.saveOrUpdate(editHistory);
                redisCache.setCacheList(editKey, Arrays.asList(editHistory));
                return ResponseResult.okResult(saveArticle.getId(), "审核申请成功");
            }else {  //草稿
                // 将事件 push入消息队列
                EditHistory editHistory = new EditHistory(Long.valueOf(userId), "创建了草稿：" +
                        saveArticle.getTitle(), createTime, GREEN, null, NORMAL);
                editHistoryService.saveOrUpdate(editHistory);
                redisCache.setCacheList(editKey, Arrays.asList(editHistory));
                return ResponseResult.okResult(200, "保存成功！");
            }
        } else {
            // 更新文章
            Article previousArticle = articleMapper.selectById(article.getId());
            ArticleRecord articleRecord = BeanCopyUtils.copyBean(previousArticle, ArticleRecord.class);
            articleRecordMapper.insert(articleRecord);
            saveArticle.setUpdateTime(createTime);
            articleMapper.updateById(saveArticle);
            if (saveArticle.getStatus().equals(RELEASE) && isAdmin)  //编辑已发布文章
            {
                // 将文章浏览量同步到 redis
                String articleKey = ARTICLE_VIEWCOUNT +saveArticle.getId();;
                // viewCount为null时为新发布的文章或草稿，不为空时为已发布文章
                long viewCount = article.getViewCount() == null ||article.getViewCount()==0 ? 1 : article.getViewCount();
                redisCache.setCacheObject(articleKey, BigInteger.valueOf(viewCount));
                // 将事件 push入消息队列
                EditHistory editHistory = new EditHistory(Long.valueOf(userId),articleRecord.getRecordId(), "编辑了文章：" +
                        saveArticle.getTitle(), createTime, BLUE, null, NORMAL);
                redisCache.setCacheList(editKey, Arrays.asList(editHistory));

                //设置文章摘要为当前文章的内容节选,用于文章搜索
                saveArticle.setSummary(MarkdownUtils.markdown2PlainText(saveArticle.getContent()));
                //清空内容，减轻网络传输压力
                saveArticle.setContent("");
                //保存文章，发送MQ消息
                String articleDocs = JSON.toJSONString(saveArticle);
                rabbitTemplate.convertAndSend(BLOG_TOPIC_EXCHANGE, BLOG_UPDATE_KEY, articleDocs);

                return ResponseResult.okResult(saveArticle.getId(), "编辑成功");
            } else if (saveArticle.getStatus().equals(RELEASE) && !isAdmin)
            {
                // 将文章浏览量同步到 redis
                String articleKey = ARTICLE_VIEWCOUNT + saveArticle.getId();
                // viewCount为null时为新发布的文章或草稿，不为空时为已发布文章
                long viewCount = article.getViewCount() == null ||article.getViewCount()==0 ? 1 : article.getViewCount();
                redisCache.setCacheObject(articleKey, BigInteger.valueOf(viewCount));
                // 将事件 push入消息队列
                EditHistory editHistory = new EditHistory(Long.parseLong(userId),articleRecord.getRecordId(),
                        "提交了编辑文章审核申请：" + saveArticle.getTitle(), createTime, BLUE, UPLOAD, LARGE);
                redisCache.setCacheList(editKey, Arrays.asList(editHistory));
                //消息队列发送删除文章，发送MQ消息
                rabbitTemplate.convertAndSend(BLOG_TOPIC_EXCHANGE, BLOG_DELETE_KEY, article.getId());
                return ResponseResult.okResult(saveArticle.getId(), "审核申请成功");
            } else    //编辑草稿
            {
                // 将事件 push入消息队列
                EditHistory editHistory = new EditHistory(Long.valueOf(userId),articleRecord.getRecordId(),
                        "编辑了草稿：" + saveArticle.getTitle(), createTime, YELLOW, null, NORMAL);
                redisCache.setCacheList(editKey, Arrays.asList(editHistory));
                //消息队列发送删除文章，发送MQ消息
                rabbitTemplate.convertAndSend(BLOG_TOPIC_EXCHANGE, BLOG_DELETE_KEY, article.getId());
                return ResponseResult.okResult(200, "草稿已保存");
            }
        }
    }


    /**
     * 获取草稿
     *
     * @return
     */
    @Override
    public ResponseResult getDraftList() {
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        // 获取草稿
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, NOT_RELEASE);
        queryWrapper.eq(Article::getCreateBy, userId);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        List<SaveArticleVO> saveArticleVOS = BeanCopyUtils.copyBeanList(articles, SaveArticleVO.class);

        return ResponseResult.okResult(saveArticleVOS);
    }


    /**
     * 获取编辑记录
     *
     * @return
     */
    @Override
    public ResponseResult getEditHistory() {
        // 校验登录状态
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        // 获取编辑记录
        String editKey = ARTICLE_EDITLIST + userId;
        List<Object> cacheList = redisCache.getCacheList(editKey);
        if (cacheList.isEmpty()) {
            LambdaQueryWrapper<EditHistory> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(EditHistory::getUserId, userId);
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
     * @param articleId
     * @return
     */
    @Override
    public ResponseResult deleteDraft(Long articleId) {
        // 校验登录状态
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        // 获取标题
        String title = getById(articleId).getTitle();
        // 删除草稿
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getCreateBy, userId);
        queryWrapper.eq(Article::getId, articleId);
        articleMapper.delete(queryWrapper);
        // 获取时间戳,设置创建时间
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String editKey = ARTICLE_EDITLIST + userId;
        // 将事件 push入消息列表
        EditHistory editHistory = new EditHistory(userId, "删除了草稿：" + title, createTime, "#F56C6C");
        EditHistoryVO editHistoryVO = BeanCopyUtils.copyBean(editHistory, EditHistoryVO.class);
        editHistoryService.saveOrUpdate(editHistory);
        redisCache.setCacheList(editKey, Arrays.asList(editHistoryVO));
        return ResponseResult.okResult();
    }

    /**
     * 获取我发布的文章总数
     *
     * @return
     */
    @Override
    public ResponseResult getMyArticleTotal()
    {
        //登录状态校验
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        //根据 userId查询已发布文章
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getCreateBy, userId)
                .eq(Article::getStatus, RELEASE);
        Long integer = articleMapper.selectCount(queryWrapper);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("myArticleTotal", integer);
        return ResponseResult.okResult(map);
    }

    /**
     * 获取我发布的文章总浏览量
     *
     * @return
     */
    @Override
    public ResponseResult getTotalView()
    {
        // 登录校验
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        // 根据 userId获取发布的文章总浏览量
        QueryWrapper<Article> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("create_by", userId);
        queryWrapper.select("IFNULL(SUM(view_count),0) AS view_count");
        Article article = articleMapper.selectOne(queryWrapper);
        HashMap<Object, Object> map = new HashMap<>();
        map.put("totalView", article.getViewCount());

        return ResponseResult.okResult(map);
    }

    /**
     * 查询最新发布文章
     *
     * @return
     */
    @Override
    public ResponseResult getLatestArticleList()
    {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE)
                .eq(Article::getArticleExamine, PASS)
                .orderByDesc(Article::getCreateTime)
                .last("limit 0,3");
        List<Article> articles = articleMapper.selectList(queryWrapper);

        List<ArticleListVO> articleListVOS = BeanCopyUtils.copyBeanList(articles, ArticleListVO.class);
        articleListVOS.forEach(articleListVO ->
                articleListVO.setCreateTime(articleListVO.getCreateTime().substring(0, 16))
        );
        return ResponseResult.okResult(articleListVOS);
    }

    /**
     * 查询提交审核文章
     */
    @Override
    public ResponseResult<PageVO> getNotExamineArticle(ArticleExamineBO articleExamineBO)
    {
        LoginUtils.checkRole(httpServletRequest);
        LambdaQueryWrapper<Article> qw = new LambdaQueryWrapper<Article>()
                .eq(Article::getStatus, RELEASE)
                .in(StringUtils.isEmpty(articleExamineBO.getArticleExamine()),
                        Article::getArticleExamine, NOT_EXAMINE, NOT_PASS)
                .eq(!StringUtils.isEmpty(articleExamineBO.getArticleExamine()),
                        Article::getArticleExamine, articleExamineBO.getArticleExamine())
                .eq(Objects.nonNull(articleExamineBO.getCreateBy()),
                        Article::getCreateBy, articleExamineBO.getCreateBy())
                .ge(!StringUtils.isEmpty(articleExamineBO.getStartTime()),
                        Article::getCreateTime, articleExamineBO.getStartTime())
                .le(!StringUtils.isEmpty(articleExamineBO.getEndTime()),
                        Article::getCreateTime, articleExamineBO.getEndTime());
        Page<Article> page = new Page<>(articleExamineBO.getPageNum(), articleExamineBO.getPageSize());
        page(page, qw);
        return ResponseResult.okResult(new PageVO(page.getRecords(), page.getTotal()));
    }

    /**
     * 修改审核文章状态
     */
    @Override
    public ResponseResult<Void> editArticleExamine(Article article)
    {
        LoginUtils.checkRole(httpServletRequest);
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        int rows = articleMapper.updateById(article);
        // 获取时间戳
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String editKey = ARTICLE_EDITLIST + article.getCreateBy();
        if (rows > 0)
        {
            // 将文章浏览量同步到 redis
            String articleKey = ARTICLE_VIEWCOUNT + article.getId();
            // viewCount为null时为新发布的文章或草稿，不为空时为已发布文章
            long viewCount = article.getViewCount() == null ? 1 : article.getViewCount();
            redisCache.setCacheObject(articleKey, BigInteger.valueOf(viewCount));
            EditHistory editHistory = null;
            if (article.getArticleExamine().equals(PASS))
            {
                // 将事件 push入消息队列
                editHistory = new EditHistory(userId, "文章审核通过：" + article.getTitle(),
                        now, GREEN, SUCCESS, LARGE);
            }
            if (article.getArticleExamine().equals(NOT_PASS))
            {
                // 将事件 push入消息队列
                editHistory = new EditHistory(userId, "文章审核未通过：" + article.getTitle() +
                        "(" + article.getNotPassMessage() + ")", now, RED, FAIL, LARGE);
            }
            editHistoryService.saveOrUpdate(editHistory);
            redisCache.setCacheList(editKey, Arrays.asList(editHistory));
        }
        return ResponseResult.resultByRows(rows);
    }

    /**
     * 获取文章编辑详情
     */
    @Override
    public ResponseResult<ArticleRecord> getArticleEditRecord(Long recordId)
    {
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        ArticleRecord articleRecord = articleRecordMapper.selectOne(new LambdaQueryWrapper<ArticleRecord>()
                .eq(ArticleRecord::getRecordId, recordId)
                .eq(ArticleRecord::getCreateBy, userId));
        if (articleRecord == null)
        {
            throw new SystemException(PARAMETER_ERROR, "未找到草稿，请联系管理员");
        }
        return ResponseResult.okResult(articleRecord);
    }


}
