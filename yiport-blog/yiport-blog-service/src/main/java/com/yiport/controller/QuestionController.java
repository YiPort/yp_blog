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
     * @param userId 用户id
     * @return
     */
    @GetMapping("/getQuestionList/{userId}")
    @SystemLog(businessName = "获取文章问题列表")
    public ResponseResult getQuestionList(@PathVariable Long userId) {
        return questionService.getQuestionList(userId);
    }

    /**
     * 删除文章问题
     *
     * @param userId
     * @param id
     * @return
     */
    @DeleteMapping("/deleteQuestion/{userId}/{id}")
    @SystemLog(businessName = "删除文章问题")
    public ResponseResult deleteQuestion(@PathVariable Long userId, @PathVariable Long id) {
        return questionService.deleteQuestion(userId, id);
    }

}
