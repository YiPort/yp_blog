package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.bo.CommentBO;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.Comment;
import com.yiport.domain.vo.CommentVO;
import com.yiport.domain.vo.PageVO;
import com.yiport.domain.vo.UpdateCommentVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.CommentMapper;
import com.yiport.service.CommentService;
import com.yiport.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.LoginUtils;
import com.yiport.utils.RedisCache;
import com.yiport.utils.SensitiveWordsUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.yiport.constants.BlogConstants.ARTICLE_COMMENT;
import static com.yiport.constants.BlogConstants.IS_COMMENT;
import static com.yiport.constants.BlogConstants.LINK_COMMENT;
import static com.yiport.constants.BlogConstants.NORMAL_COMMENT;
import static com.yiport.constants.BlogConstants.NOT_TOP_COMMENT;
import static com.yiport.constants.BlogConstants.ROOT_COMMENT;
import static com.yiport.constants.BlogConstants.TOP_COMMENT;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author YiPort
 * @since 2023-04-25 07:42:00
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Resource
    private ArticleMapper articleMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CommentMapper commentMapper;


    /**
     * 查询文章评论列表
     */
    @Override
    public ResponseResult<PageVO> getCommentList( CommentBO commentBO)
    {
        // 查询 当前文章下的 精选 文章 根评论
        LambdaQueryWrapper<Comment> lambdaQueryWrapper = new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, commentBO.getArticleId())
                .eq(Comment::getType, ARTICLE_COMMENT)
                .eq(Comment::getStatus, NORMAL_COMMENT)
                .eq(Comment::getToCommentId, ROOT_COMMENT)
                .orderByDesc(Comment::getLabel)
                .orderByAsc(StringUtils.isBlank(commentBO.getOrder()) || commentBO.getOrder().equals("ASC"),
                        Comment::getCreateTime)
                .orderByDesc(StringUtils.isNotBlank(commentBO.getOrder()) &&
                        commentBO.getOrder().equals("DESC"), Comment::getCreateTime);
        // 分页
        Page<Comment> page = new Page<>(commentBO.getPageNum(), commentBO.getPageSize());
        page(page, lambdaQueryWrapper);
        // 根评论列表
        List<Comment> rootComments = page.getRecords();
        // 根评论响应体集合
        List<CommentVO> rootArticleCommentVOS = BeanCopyUtils.copyBeanList(rootComments, CommentVO.class);
        // 遍历根评论响应体集合
        for (CommentVO rootArticleCommentVO : rootArticleCommentVOS)
        {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getArticleId, commentBO.getArticleId());
            queryWrapper.eq(Comment::getType, ARTICLE_COMMENT);
            queryWrapper.eq(Comment::getStatus, NORMAL_COMMENT);
            queryWrapper.eq(Comment::getToCommentId, rootArticleCommentVO.getId());
            queryWrapper.orderByAsc(Comment::getCreateTime);
            // 根评论 id对应的子评论列表
            List<Comment> comments = commentMapper.selectList(queryWrapper);
            // 根评论 id对应的子评论请求体列表
            List<CommentVO> articleCommentVOS = BeanCopyUtils.copyBeanList(comments, CommentVO.class);
            // 将子评论请求体列表放入对应的根评论请求体列表中
            rootArticleCommentVO.setChildren(articleCommentVOS);
        }
        // 查询评论总数
        List<Comment> rootCommentTotal = commentMapper.selectList(lambdaQueryWrapper);
        long total = page.getTotal();   //评论总条数
        for (Comment rootComment : rootCommentTotal)
        {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getArticleId, commentBO.getArticleId());
            queryWrapper.eq(Comment::getType, ARTICLE_COMMENT);
            queryWrapper.eq(Comment::getStatus, NORMAL_COMMENT);
            queryWrapper.eq(Comment::getToCommentId, rootComment.getId());
            // 根评论 id对应的子评论列表
            List<Comment> comments = commentMapper.selectList(queryWrapper);
            total += comments.size();
        }

        PageVO pageVO = new PageVO(rootArticleCommentVOS, total);

        return ResponseResult.okResult(pageVO);
    }

    /**
     * 查询友链评论列表
     */
    @Override
    public ResponseResult<PageVO> getLinkCommentList(CommentBO commentBO)
    {
        LambdaQueryWrapper<Comment> rootQueryWrapper = new LambdaQueryWrapper<>();
        // 查询 友链评论 根评论
        rootQueryWrapper.eq(Comment::getType, LINK_COMMENT)
                .eq(Comment::getRootId, ROOT_COMMENT)
                .eq(Comment::getStatus, NORMAL_COMMENT)
                .orderByDesc(Comment::getLabel)
                .orderByAsc(StringUtils.isBlank(commentBO.getOrder()) || commentBO.getOrder().equals("ASC"),
                        Comment::getCreateTime)
                .orderByDesc(StringUtils.isNotBlank(commentBO.getOrder()) &&
                        commentBO.getOrder().equals("DESC"), Comment::getCreateTime);
        // 分页
        Page<Comment> page = new Page<>(commentBO.getPageNum(), commentBO.getPageSize());
        page(page, rootQueryWrapper);
        // 获取根评论响应请求体集合
        List<Comment> rootComments = page.getRecords();
        List<CommentVO> rootLinkCommentVOS = BeanCopyUtils.copyBeanList(rootComments, CommentVO.class);
        // 遍历，将子评论插入
        for (CommentVO rootLinkCommentVO : rootLinkCommentVOS)
        {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getToCommentId, rootLinkCommentVO.getId());
            queryWrapper.eq(Comment::getType, LINK_COMMENT);
            queryWrapper.eq(Comment::getStatus, NORMAL_COMMENT);
            queryWrapper.orderByAsc(Comment::getCreateTime);
            List<Comment> comments = list(queryWrapper);
            List<CommentVO> linkCommentVOS = BeanCopyUtils.copyBeanList(comments, CommentVO.class);
            rootLinkCommentVO.setChildren(linkCommentVOS);
        }
        // 查询评论总数
        List<Comment> linkCommentRootTotal = commentMapper.selectList(rootQueryWrapper);
        long total = page.getTotal();   //评论总条数
        for (Comment linkCommentRoot : linkCommentRootTotal)
        {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getToCommentId, linkCommentRoot.getId());
            queryWrapper.eq(Comment::getType, LINK_COMMENT);
            queryWrapper.eq(Comment::getStatus, NORMAL_COMMENT);
            List<Comment> comments = list(queryWrapper);
            total += comments.size();
        }

        PageVO pageVO = new PageVO(rootLinkCommentVOS, total);

        return ResponseResult.okResult(pageVO);
    }



    /**
     * 发表评论
     *
     * @param comment
     * @return
     */
    @Override
    public ResponseResult addComment(Comment comment) {
        // Token解析
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        comment.setCreateBy(userId);
        String type = comment.getType();
        String content = comment.getContent();

        //参数不为空
        if (StringUtils.isAnyBlank(type, content) ) {
            throw new SystemException(PARAMETER_ERROR);
        }
        //限制长度
        if (content.length() > 300)
        {
            throw new SystemException(PARAMETER_ERROR, "评论内容不能超过300字符");
        }
        //评论内容不能为空
        if (type.equals(ARTICLE_COMMENT))
        {
            LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Article::getId, comment.getArticleId())
                    .eq(Article::getIsComment, IS_COMMENT);
            if (Objects.isNull(articleMapper.selectOne(queryWrapper)))
            {
                throw new SystemException(PARAMETER_ERROR, "该文章不允许评论");
            }
        }
        //设置保存时间
        // 获取当前时间
        LocalDateTime currentTime = LocalDateTime.now();
        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 格式化创建时间
        String createTime = currentTime.format(formatter);
        comment.setCreateTime(createTime);
        // 过滤敏感词
        Map<Object, Object> sensitiveWordsMap = redisCache.getCacheObject("sensitiveWordsMap");
        Map<String, Object> filterResult = SensitiveWordsUtils.getFilterResult(content, SensitiveWordsUtils.MatchType.MAX_MATCH, sensitiveWordsMap);
        comment.setFilterContent(filterResult.get("text").toString());
        boolean result = save(comment);
        return ResponseResult.okResult(result);
    }


    /**
     * 置顶/取消置顶文章评论
     *
     * @param id
     * @param label
     * @return
     */
    @Override
    public ResponseResult setCommentLabel(Long id, String label)
    {
        // Token解析
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        // 判断是否为文章博主
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getId, id)
                .eq(Comment::getStatus, NORMAL_COMMENT);
        Comment comment = getOne(queryWrapper);
        if (Objects.isNull(comment))
        {
            // 无法获取当前评论，抛出异常
            throw new SystemException(NO_OPERATOR_AUTH);
        }
        Long articleId = comment.getArticleId();
        LambdaQueryWrapper<Article> articleWrapper = new LambdaQueryWrapper<>();
        articleWrapper.eq(Article::getId, articleId);
        Long createBy = articleMapper.selectOne(articleWrapper).getCreateBy();
        if (userId != createBy)
        {
            // 当前文章，归属关系不符
            throw new SystemException(NO_OPERATOR_AUTH);
        }

        String oldLabel = comment.getLabel();
        // 当前评论为非置顶评论
        if (oldLabel.equals(NOT_TOP_COMMENT))
        {
            LambdaQueryWrapper<Comment> commentWrapper = new LambdaQueryWrapper<>();
            commentWrapper.eq(Comment::getArticleId, articleId)
                    .eq(Comment::getStatus, NORMAL_COMMENT)
                    .eq(Comment::getLabel, TOP_COMMENT);
            Comment topComment = getOne(commentWrapper);
            if (Objects.nonNull(topComment))
            {
                topComment.setLabel(NOT_TOP_COMMENT);
                updateById(topComment);
            }
            comment.setLabel(label);
        }
        // 当前评论为置顶评论
        if (oldLabel.equals(TOP_COMMENT))
        {
            comment.setLabel(label);
        }
        // 修改label
        updateById(comment);
        return ResponseResult.okResult();
    }

    /**
     * 管理员删除评论
     */
    @Override
    public ResponseResult<Void> deleteComment(Long id)
    {
        LoginUtils.checkRole(httpServletRequest);
        removeById(id);
        return ResponseResult.okResult();
    }

    /**
     * 管理员查询评论
     */
    @Override
    public ResponseResult<PageVO> getAllCommentList(CommentBO commentBO)
    {
        LoginUtils.checkRole(httpServletRequest);
        Page<Comment> page = new Page<>(commentBO.getPageNum(), commentBO.getPageSize());
        page(page, new LambdaQueryWrapper<Comment>()
                .orderByDesc(Comment::getCreateTime)
                .eq(!StringUtils.isEmpty(commentBO.getType()), Comment::getType, commentBO.getType())
                .eq(Objects.nonNull(commentBO.getArticleId()), Comment::getArticleId, commentBO.getArticleId())
                .ge(!StringUtils.isEmpty(commentBO.getStartTime()), Comment::getCreateTime, commentBO.getStartTime())
                .le(!StringUtils.isEmpty(commentBO.getEndTime()), Comment::getCreateTime, commentBO.getEndTime()));
        List<Comment> comments = page.getRecords();
        PageVO pageVO = new PageVO(comments, page.getTotal());

        return ResponseResult.okResult(pageVO);
    }

    /**
     * 用户删除评论
     */
    @Override
    public ResponseResult<Void> deleteMyComment(Long id)
    {
        String userId = JwtUtil.checkToken(httpServletRequest);
        Comment comment = getById(id);
        if(!userId.equals(comment.getCreateBy().toString())){
            throw new SystemException(NO_OPERATOR_AUTH);
        }
        removeById(id);
        return ResponseResult.okResult();
    }

    /**
     * 更新评论用户信息
     */
    @Override
    public ResponseResult<Void> updateComment(UpdateCommentVO updateCommentVO)
    {
        JwtUtil.checkToken(httpServletRequest);
        Comment comment = BeanCopyUtils.copyBean(updateCommentVO, Comment.class);
        comment.setAvatar(updateCommentVO.getAvatarUrl());
        commentMapper.update(comment, new LambdaQueryWrapper<Comment>()
                .eq(Comment::getCreateBy, updateCommentVO.getCreateBy()));
        return ResponseResult.okResult();
    }

}


