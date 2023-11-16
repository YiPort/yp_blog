package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.QuestionVO;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * QuestionClient的备用处理器，当问题服务不可用时提供默认行为。
 */
@Slf4j
@Component
public class QuestionClientResolver implements QuestionClient {

    /**
     * 当服务不可用时，为提交问题提供默认响应。
     *
     * @param questionVO 提交的问题视图对象。
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult postQuestion(QuestionVO questionVO) {
        log.error("问题服务异常：提交问题请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "问题服务不可用");
    }

    /**
     * 当服务不可用时，为获取文章问题列表提供默认响应。
     *
     * @param userId 用户ID
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult getQuestionList(Long userId) {
        log.error("问题服务异常：获取用户ID {} 的文章问题列表请求失败", userId);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "问题服务不可用");
    }

    /**
     * 当服务不可用时，为删除文章问题提供默认响应。
     *
     * @param userId 用户ID
     * @param id 问题ID
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult deleteQuestion(Long userId, Long id) {
        log.error("问题服务异常：为用户ID {} 删除问题ID {} 的请求失败", userId, id);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "问题服务不可用");
    }
}
