package com.yiport.client;

import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * ResourceClient请求失败时的熔断处理类
 */
@Slf4j
@Component
public class ResourceClientResolver implements ResourceClient {

    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        log.error("资源管理服务异常：uploadImg 请求失败");
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,"request fail");
    }
}
