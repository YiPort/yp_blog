package com.yiport.controller;


import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.QuestionVO;
import com.yiport.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @LimitRequest(time = 2*60*1000, description = "两分钟后再提交")
    public ResponseResult postQuestion(@RequestBody QuestionVO questionVO) {
        return questionService.postQuestion(questionVO);
    }


    /**
     * 获取文章问题列表
     *
     * @return
     */
    @GetMapping("/getQuestionList")
    @SystemLog(businessName = "获取文章问题列表")
    public ResponseResult getQuestionList() {
        return questionService.getQuestionList();
    }

    /**
     * 删除文章问题
     *
     * @param articleId
     * @return
     */
    @DeleteMapping("/deleteQuestion/{articleId}")
    @SystemLog(businessName = "删除文章问题")
    public ResponseResult deleteQuestion(@PathVariable Long articleId) {
        return questionService.deleteQuestion(articleId);
    }

}
