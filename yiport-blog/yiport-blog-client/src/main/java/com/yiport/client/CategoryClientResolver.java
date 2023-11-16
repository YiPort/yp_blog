package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.vo.CategoryVO;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Fallback handler for CategoryClient, providing default behaviors when category-server is unavailable.
 */
@Slf4j
@Component
public class CategoryClientResolver implements CategoryClient {

    /**
     * Provides a default response for fetching category list when the service is unavailable.
     *
     * @return An error result indicating a system error.
     */
    @Override
    public ResponseResult getCategoryList() {
        log.error("Category service exception: getCategoryList request failed");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "Category service unavailable");
    }

    /**
     * Provides a default response for adding a new category when the service is unavailable.
     *
     * @param category Category information to be added.
     * @return An error result indicating a system error.
     */
    @Override
    public ResponseResult addCategory(CategoryVO category) {
        log.error("Category service exception: addCategory request failed");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "Category service unavailable");
    }

    // Additional methods related to category operations could be added here with similar fallback implementations.
}
