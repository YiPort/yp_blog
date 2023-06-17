package com.yiport.service;

import com.yiport.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

public interface UploadService {

    /**
     * 头像上传
     *
     * @param img
     * @return
     */
    ResponseResult uploadImg(MultipartFile img);
}
