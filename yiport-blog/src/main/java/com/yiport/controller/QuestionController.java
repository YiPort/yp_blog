package com.yiport.controller;


import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.QuestionVO;
import com.yiport.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController {
    @Autowired
    private QuestionService questionService;

    /**
     * 提交文章问题
     *
     * @param questionVO
     * @return
     */
    @PostMapping("/postQuestion")
    @SystemLog(businessName = "提交文章问题")
    public ResponseResult postQuestion(@RequestBody QuestionVO questionVO) {
        return questionService.postQuestion(questionVO);
    }
}
