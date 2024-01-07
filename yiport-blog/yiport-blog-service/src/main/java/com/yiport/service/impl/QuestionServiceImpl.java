package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Article;
import com.yiport.domain.entity.Question;
import com.yiport.domain.vo.QuestionVO;
import com.yiport.exception.SystemException;
import com.yiport.mapper.ArticleMapper;
import com.yiport.mapper.QuestionMapper;
import com.yiport.service.QuestionService;
import com.yiport.utils.BeanCopyUtils;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.LoginUtils;
import com.yiport.utils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.yiport.constants.BlogConstants.RELEASE;
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

    @Autowired
    private  HttpServletRequest httpServletRequest;

    /**
     * 提交文章问题
     *
     * @param questionVO
     * @return
     */
    @Override
    public ResponseResult postQuestion(QuestionVO questionVO) {
        // 校验登录角色
        Long userId = Long.valueOf(JwtUtil.checkToken(httpServletRequest));
        questionVO.setCreateBy(userId);
        Long articleId = questionVO.getArticleId();
        // 问题描述长度校验
        String questionDescription = questionVO.getQuestionDescription();
        if (questionDescription.length() > 150) {
            throw new SystemException(PARAMETER_ERROR, "问题描述超过指定长度");
        }
        if (questionDescription.length() == 0)
        {
            throw new SystemException(PARAMETER_ERROR, "问题描述不能为空");
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
     * @return
     */
    @Override
    public ResponseResult getQuestionList() {
        // 校验登录角色
        LoginUtils.checkRole(httpServletRequest);
        // 查询
        LambdaQueryWrapper<Question> queryWrapper = new LambdaQueryWrapper<>();
        List<Question> questionList = questionMapper.selectList(queryWrapper);
        return ResponseResult.okResult(questionList);
    }

    /**
     * 删除文章问题
     *
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteQuestion( Long id) {
        // 校验登录角色
        LoginUtils.checkRole(httpServletRequest);
        // 删除记录
        removeById(id);
        return ResponseResult.okResult();
    }

}




