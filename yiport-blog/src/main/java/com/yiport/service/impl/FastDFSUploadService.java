package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import com.yiport.handler.exception.SystemException;
import com.yiport.service.UploadService;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class FastDFSUploadService implements UploadService {

    private static final List<String> CONTENT_TYPES = Arrays.asList("image/png", "image/jpg");

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    /**
     * 头像上传
     *
     * @param img
     * @return
     */
    @Override
    public ResponseResult uploadImg(MultipartFile img) {
        String url = uploadFastDFS(img);
        return ResponseResult.okResult(url);
    }

    public String uploadFastDFS(MultipartFile img) {
        String originalFilename = img.getOriginalFilename();//获取文件名

//        String[] split = originalFilename.split(".");


        //1.文件类型
        String contentType = img.getContentType();
        if (!CONTENT_TYPES.contains(contentType)){

        }

        try {
            //2.文件的内容校验
            BufferedImage bufferedImage = ImageIO.read(img.getInputStream());
            if (bufferedImage==null){
                throw new SystemException(AppHttpCodeEnum.FILE_CONTENT_ERROR);
            }

            //3.保存到服务器
//            img.transferTo(new File("D:\\DemoFiles\\images\\"+originalFilename));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = this.fastFileStorageClient.uploadFile(img.getInputStream(), img.getSize(), ext, null);



            //4.返回url路径
            return "http://images.yiport.top/" + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
            //ignore
        }
        return null;
    }

}
