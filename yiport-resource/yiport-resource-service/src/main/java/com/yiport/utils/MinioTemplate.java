package com.yiport.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yiport.config.properties.OssProperties;
import com.yiport.model.OssFile;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@AllArgsConstructor
public class MinioTemplate
{

    /**
     * MinIO 客户端
     */
    MinioClient minioClient;

    /**
     * MinIO 配置类
     */
    OssProperties ossProperties;

    /**
     * 查询所有存储桶
     *
     * @return Bucket 集合
     */
    @SneakyThrows
    public List<Bucket> listBuckets()
    {
        return minioClient.listBuckets();
    }

    /**
     * 桶是否存在
     *
     * @param bucketName 桶名
     * @return 是否存在
     */
    @SneakyThrows
    public boolean bucketExists(String bucketName)
    {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建存储桶
     *
     * @param bucketName 桶名
     */
    @SneakyThrows
    public void makeBucket(String bucketName)
    {
        if (!bucketExists(bucketName))
        {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());

        }
    }

    /**
     * 删除一个空桶 如果存储桶存在对象不为空时，删除会报错。
     *
     * @param bucketName 桶名
     */
    @SneakyThrows
    public void removeBucket(String bucketName)
    {
        minioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 上传文件
     *
     * @param inputStream      流
     * @param originalFileName 原始文件名
     * @param bucketName       桶名
     * @return OssFile
     */
    @SneakyThrows
    public OssFile putObject(InputStream inputStream, String bucketName, String originalFileName, String contentType)
    {
        String uuidFileName = generateOssUuidFileName(originalFileName);
        try
        {
            if (StrUtil.isEmpty(bucketName))
            {
                bucketName = ossProperties.getDefaultBucketName();
            }
            minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(uuidFileName).contentType(contentType).stream(
                                    inputStream, inputStream.available(), -1)
                            .build());
            return new OssFile(ossProperties.getDomainName() + StrUtil.SLASH + ossProperties.getDefaultBucketName() + StrUtil.SLASH + uuidFileName, originalFileName);
        }
        finally
        {
            if (inputStream != null)
            {
                inputStream.close();
            }
        }
    }

    /**
     * 返回临时带签名、过期时间一天、Get请求方式的访问URL
     *
     * @param bucketName  桶名
     * @param ossFilePath Oss文件路径
     * @return URL
     */
    @SneakyThrows
    public String getPresignedObjectUrl(String bucketName, String ossFilePath)
    {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .object(ossFilePath)
                        .expiry(60 * 60 * 24)
                        .build());
    }

    /**
     * GetObject接口用于获取某个文件（Object）。此操作需要对此Object具有读权限。
     *
     * @param bucketName  桶名
     * @param ossFilePath Oss文件路径
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String ossFilePath)
    {
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(ossFilePath).build());
    }

    /**
     * 查询桶的对象信息
     *
     * @param bucketName 桶名
     * @param recursive  是否递归查询
     * @return 查询桶的对象信息
     */
    @SneakyThrows
    public Iterable<Result<Item>> listObjects(String bucketName, boolean recursive)
    {
        return minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).recursive(recursive).build());
    }

    /**
     * 生成随机文件名，防止重复
     *
     * @param originalFilename 原始文件名
     * @return 文件名
     */
    public String generateOssUuidFileName(String originalFilename)
    {
        String fe = "";
        if (originalFilename.contains(".")) {
            int i = originalFilename.lastIndexOf('.');
            fe = i > 0 ? originalFilename.substring(i) : "jpg";
        }
        return DateUtil.format(new Date(), "yyyy-MM-dd") + StrUtil.SLASH + UUID.randomUUID().toString().replace("-", "") + fe;
    }

    /**
     * 获取带签名的临时上传元数据对象，前端可获取后，直接上传到Minio
     *
     * @param bucketName 桶名称
     * @param fileName   文件名
     * @return 元数据对象
     */
    @SneakyThrows
    public Map<String, String> getPresignedPostFormData(String bucketName, String fileName)
    {
        // 为存储桶创建一个上传策略，过期时间为7天
        PostPolicy policy = new PostPolicy(bucketName, ZonedDateTime.now().plusDays(7));
        // 设置一个参数key，值为上传对象的名称
        policy.addEqualsCondition("key", fileName);
        // 添加Content-Type以"image/"开头，表示只能上传照片
        policy.addStartsWithCondition("Content-Type", "image/");
        // 设置上传文件的大小 64kiB to 10MiB.
        policy.addContentLengthRangeCondition(64 * 1024, 10 * 1024 * 1024);
        return minioClient.getPresignedPostFormData(policy);
    }

    /**
     * 初始化默认存储桶
     */
    @PostConstruct
    public void initDefaultBucket()
    {
        String defaultBucketName = ossProperties.getDefaultBucketName();
        if (bucketExists(defaultBucketName))
        {
            log.info("默认存储桶已存在-{}", defaultBucketName);
        }
        else
        {
            log.info("创建默认存储桶-{}", defaultBucketName);
            makeBucket(defaultBucketName);
        }
    }

}

