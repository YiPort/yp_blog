package com.yiport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.Question;
import com.yiport.domain.vo.QuestionVO;


/**
 *
 */
public interface QuestionService extends IService<Question> {

    /**
     * 提交文章问题
     *
     * @param questionVO
     * @return
     */
    ResponseResult postQuestion(QuestionVO questionVO);

    /**
     * 获取文章问题列表
     *
     * @param userId
     * @return
     */
    ResponseResult getQuestionList();

    /**
     * 删除文章问题
     *
     * @param userId
     * @param id
     * @return
     */
    ResponseResult deleteQuestion(Long id);

}
