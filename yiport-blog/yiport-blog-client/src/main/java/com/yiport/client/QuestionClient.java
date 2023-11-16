package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.QuestionVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.*;

/**
 * Feign客户端接口，用于文章问题服务的远程调用。
 */
@Import(QuestionClientResolver.class)
@FeignClient(value = "blog-server", fallback = QuestionClientResolver.class)
public interface QuestionClient {

    /**
     * 提交文章问题。
     *
     * @param questionVO 文章问题详情。
     * @return 操作结果。
     */
    @PostMapping("/question/postQuestion")
    ResponseResult postQuestion(@RequestBody QuestionVO questionVO);

    /**
     * 获取文章问题列表。
     *
     * @param userId 用户ID。
     * @return 包含文章问题列表的ResponseResult。
     */
    @GetMapping("/question/getQuestionList/{userId}")
    ResponseResult getQuestionList(@PathVariable Long userId);

    /**
     * 删除文章问题。
     *
     * @param userId 用户ID。
     * @param id 问题ID。
     * @return 操作结果。
     */
    @DeleteMapping("/question/deleteQuestion/{userId}/{id}")
    ResponseResult deleteQuestion(@PathVariable Long userId, @PathVariable Long id);
}
