package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.Question;
import com.yiport.domain.vo.QuestionVO;
import com.yiport.handler.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.QuestionMapper;
import com.yiport.service.QuestionService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.RedisCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.yiport.constants.BusinessConstants.BLOG_ADMIN;
import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constants.SystemConstants.RELEASE;
import static com.yiport.enums.AppHttpCodeEnum.*;

/**
 *
 */
@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question>
        implements QuestionService {
    @Autowired
    private RedisCache redisCache;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private QuestionMapper questionMapper;

    /**
     * 提交文章问题
     *
     * @param questionVO
     * @return
     */
    @Override
    public ResponseResult postQuestion(QuestionVO questionVO) {
        Long userId = questionVO.getCreateBy();
        Long articleId = questionVO.getArticleId();
        // 校验登录状态
        checkLogin(userId);
        // 问题描述长度校验
        String questionDescription = questionVO.getQuestionDescription();
        if (questionDescription.length() > 150) {
            throw new SystemException(PARAMETER_ERROR, "问题描述超过指定长度");
        }
        // 校验文章是否存在
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Article::getStatus, RELEASE);
        queryWrapper.eq(Article::getId, articleId);
        if (articleMapper.selectOne(queryWrapper) == null) {
            throw new SystemException(PARAMETER_ERROR, "文章不存在");
        }
        // 保存问题
        Question question = BeanCopyUtils.copyBean(questionVO, Question.class);
        save(question);
        return ResponseResult.okResult();
    }

    /**
     * 获取文章问题列表
     *
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getQuestionList(Long userId) {
        // 校验登录状态
        checkLogin(userId);
        // 权限校验
        String adminKey = BLOG_ADMIN + userId;
        if (redisCache.getCacheObject(adminKey) == null) {
            throw new SystemException(NO_OPERATOR_AUTH, "无权限");
        }
        // 查询
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        List<Question> questionList = questionMapper.selectList(queryWrapper);
        return ResponseResult.okResult(questionList);
    }

    /**
     * 删除文章问题
     *
     * @param userId
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteQuestion(Long userId, Long id) {
        // 校验登录状态
        checkLogin(userId);
        // 权限校验
        String adminKey = BLOG_ADMIN + userId;
        if (redisCache.getCacheObject(adminKey) == null) {
            throw new SystemException(NO_OPERATOR_AUTH, "无权限");
        }
        // 删除记录
        removeById(id);
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
        String token = request.getHeader("token");
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




