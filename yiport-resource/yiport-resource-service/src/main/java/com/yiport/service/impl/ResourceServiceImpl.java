package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.exception.SystemException;
import com.yiport.model.OssFile;
import com.yiport.service.OSSResourceService;
import com.yiport.service.ResourceService;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.MinioTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.yiport.constants.HTTPConstants.JPEG;
import static com.yiport.constants.HTTPConstants.JPG;
import static com.yiport.constants.HTTPConstants.PNG;
import static com.yiport.enums.AppHttpCodeEnum.SYSTEM_ERROR;

//@Service("minioResourceService")
public class ResourceServiceImpl implements OSSResourceService {

    @Autowired
    private HttpServletRequest httpServletRequest;
    
    @Autowired
    private MinioTemplate minioTemplate;


    
    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        return null;
    }

    /**
     * 上传头像
     */
    @Override
    public ResponseResult<OssFile> uploadAvatar(MultipartFile file)
    {
        String userId = JwtUtil.checkToken(httpServletRequest);
        String token = httpServletRequest.getHeader("TOKEN");
        if (file.getSize() > 1024 * 1024)
            throw new SystemException(SYSTEM_ERROR, "图片不能超过1MB");
        if (!StringUtils.containsAny(file.getContentType(), JPG, JPEG, PNG))
            throw new SystemException(SYSTEM_ERROR, "图片只能是 JPG/JPEG/PNG 格式");
        OssFile ossFile;
        try
        {
            ossFile = minioTemplate.putObject(file.getInputStream(), null, file.getOriginalFilename(), file.getContentType());
        }
        catch (IOException e)
        {
            throw new SystemException(SYSTEM_ERROR);
        }

        return ResponseResult.okResult(ossFile);
    }
    
    
}
