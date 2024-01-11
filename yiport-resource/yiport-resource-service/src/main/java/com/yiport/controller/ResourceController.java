package com.yiport.controller;

import com.alibaba.fastjson.annotation.JSONField;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.service.FastDFSService;
import com.yiport.service.MinioService;
import com.yiport.service.QiniuService;
import com.yiport.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController("/resource")
public class ResourceController {
    @Resource
    private FastDFSService fastDFSService;

    @Resource
    private QiniuService qiniuService;
    @Resource
    private MinioService minioService;


    /**
     * 图片上传
     * 需要token
     *
     * @param img
     * @return
     */
    @PostMapping("/upload")
//    @SystemLog(businessName = "上传文件头像")
    public ResponseResult uploadImg(MultipartFile img){
        return minioService.uploadImg(img);
    }
}
