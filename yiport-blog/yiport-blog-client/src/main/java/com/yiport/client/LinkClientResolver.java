package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * LinkClient的备用处理器，当友链服务不可用时提供默认行为。
 */
@Slf4j
@Component
public class LinkClientResolver implements LinkClient {

    /**
     * 当服务不可用时，为获取所有友链提供默认响应。
     *
     * @return 表示系统错误的错误结果。
     */
    @Override
    public ResponseResult getAllLink() {
        log.error("友链服务异常：获取所有友链请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "友链服务不可用");
    }
}
