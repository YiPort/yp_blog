package com.yiport.controller;

import com.alibaba.fastjson.annotation.JSONField;
import com.yiport.annotation.LimitRequest;
import com.yiport.annotation.SystemLog;
import com.yiport.domain.ResponseResult;
import com.yiport.service.FastDFSService;
import com.yiport.service.MinioService;
import com.yiport.service.QiniuService;
import com.yiport.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

import static com.yiport.constants.SystemConstants.TRUE;

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
    @LimitRequest(time = 24 * 60 * 60 * 1000, count = 10, type = "USER", limitAdmin = false, tip = TRUE)
    public ResponseResult uploadImg(MultipartFile img)
    {
        return minioService.uploadImg(img);
    }

    /**
     * 删除文章图片
     */
    @DeleteMapping("/deleteImage")
    @SystemLog(businessName = "删除文章图片")
    public ResponseResult deleteImage(String url)
    {
        return minioService.deleteImage(url);
    }

    /**
     * 图片上传
     * 需要token
     *
     * @param img
     * @return
     */
    @PostMapping("/uploadAvatar")
//    @SystemLog(businessName = "上传文件头像")
    @LimitRequest(time = 24 * 60 * 60 * 1000, count = 2, type = "USER", limitAdmin = false, description = "每日只能修改两次头像", tip = TRUE)
    public ResponseResult uploadAvatar(MultipartFile img){
        return minioService.uploadImg(img);
    }
}
