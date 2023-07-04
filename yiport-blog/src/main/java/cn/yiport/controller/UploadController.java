package cn.yiport.controller;

import cn.yiport.domain.ResponseResult;
import cn.yiport.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(tags = "上传文件",description = "上传文件相关接口")
public class UploadController {
    @Autowired
    private UploadService uploadService;

    /**
     * 头像上传
     * 需要token请求头
     * @param img
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "上传文件",notes = "上传头像，需要token请求头")
    public ResponseResult uploadImg(MultipartFile img){
        return uploadService.uploadImg(img);
    }
}
