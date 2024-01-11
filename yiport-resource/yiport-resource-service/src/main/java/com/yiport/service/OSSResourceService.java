package com.yiport.service;

import com.yiport.domain.ResponseResult;
import com.yiport.model.OssFile;
import org.springframework.web.multipart.MultipartFile;

public interface OSSResourceService {

    /**
     * 头像上传
     *
     * @param img
     * @return
     */
    @Deprecated
    ResponseResult uploadImg(MultipartFile img);


    /**
     * 上传头像
     */
    ResponseResult<OssFile> uploadAvatar(MultipartFile file);

}
