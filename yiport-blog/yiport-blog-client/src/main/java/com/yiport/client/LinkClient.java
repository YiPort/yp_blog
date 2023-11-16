package com.yiport.client;

import com.yiport.domain.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Feign客户端接口，用于友链服务的远程调用。
 */
@Import(LinkClientResolver.class)
@FeignClient(value = "blog-server", fallback = LinkClientResolver.class)  // Ensure to provide the correct fallback class
public interface LinkClient {

    /**
     * 查询友链列表。
     *
     * @return 包含友链列表的ResponseResult。
     */
    @GetMapping("/link/getAllLink")
    ResponseResult getAllLink();
}
