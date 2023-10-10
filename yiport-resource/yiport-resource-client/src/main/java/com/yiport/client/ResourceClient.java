package com.yiport.client;


import com.yiport.domain.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value="resource-service",fallback = ResourceClientResolver.class)
@RestController("/resource")
public interface ResourceClient {

    /**
     * 头像上传
     * 需要token
     *
     * @param img
     * @return
     */
    @RequestMapping(method = RequestMethod.POST,value = "/upload")
    ResponseResult uploadImg(MultipartFile img);

}
