package com.yiport.service.impl;

import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import com.yiport.exception.SystemException;
import com.yiport.service.MinioService;
import com.yiport.utils.PathUtils;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;


@Service("minioService")
public class MinioServiceImpl implements MinioService {


	String buckName = "blog-images";
	String preUrl = "http://images.yiport.top/";
	@Resource
	private MinioClient minioClient;

	@Override
	public ResponseResult uploadImg(MultipartFile img) {
		//判断文件类型
		//获取原始文件名
		String originalFilename = img.getOriginalFilename();
		//对原始文件名进行判断
		if (!originalFilename.endsWith(".png") && !originalFilename.endsWith(".jpg") && !originalFilename.endsWith(".jpeg")) {
			throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
		}

		//如果判断通过上传文件到OSS
		String filePath = PathUtils.generateFilePath(originalFilename);
		String url = upload(img, filePath, buckName); //  2099/2/3/wqeqeqe.png
		return ResponseResult.okResult(url);
	}

	/**
	 * 判断bucket是否存在
	 */
	@Override
	public boolean existBucket(String name) throws SystemException {
		try {
			return minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
		} catch (Exception e) {
			throw new SystemException();
		}
	}

	/**
	 * 上传文件，请手动关闭inputStream
	 */
	@Override
	public void upload(String filePath, String bucketName, InputStream inputStream, long objectSize, long partSize, String contentType)
			throws SystemException {
		try {
			minioClient.putObject(PutObjectArgs
					.builder()
					.bucket(bucketName)
					.object(filePath)
					// 文件大小和分片大小，填-1默认为5Mib
					.stream(inputStream, objectSize, partSize)
					.contentType(contentType)
					.build());
		} catch (Exception e) {
			e.printStackTrace();
			throw new SystemException("文件上传异常");
		}
	}

	/**
	 * 上传文件
	 *
	 * @param multipartFile 文件
	 * @param bucketName    桶名称
	 * @throws MinioException minio异常
	 * @throws IOException    字节流为空
	 */
	@Override
	public void upload(MultipartFile multipartFile, String bucketName) throws SystemException, IOException {
		try (
				InputStream inputStream = multipartFile.getInputStream()
		) {
			this.upload(
					multipartFile.getOriginalFilename(),
					bucketName,
					inputStream,
					multipartFile.getSize(),
					-1,
					multipartFile.getContentType()
			);
		}
	}

	@Override
	public String upload(MultipartFile multipartFile, String filePath, String bucketName) throws SystemException {
		try (
				InputStream inputStream = multipartFile.getInputStream()
		) {
			this.upload(
					filePath,
					bucketName,
					inputStream,
					multipartFile.getSize(),
					-1,
					multipartFile.getContentType()
			);
		} catch (IOException e) {
			throw new SystemException("文件读取失败");
		}
		return preUrl + buckName + "/" + filePath;
	}

	/**
	 * 下载文件，需要自己关闭outputStream
	 *
	 * @param filePath     文件名(包括路径)
	 * @param bucketName   储存桶
	 * @param outputStream 输出流
	 * @deprecated 请使用 {@link MinioServiceImpl#download(String, String, HttpServletResponse) }
	 */
	@Override
	public void download(String filePath, String bucketName, ServletOutputStream outputStream) throws SystemException, IOException {
		InputStream inputStream = this.download(filePath, bucketName);
		IOUtils.copy(inputStream, outputStream);
		inputStream.close();
	}

	/**
	 * 下载文件,使用response
	 *
	 * @param filePath   文件路径
	 * @param bucketName 桶名称
	 * @param response   输出的响应体
	 */
	@Override
	public void download(String filePath, String bucketName, HttpServletResponse response) throws SystemException, IOException {
		try (InputStream inputStream = this.download(filePath, bucketName)) {
			try (ServletOutputStream outputStream = response.getOutputStream()) {
				IOUtils.copy(inputStream, outputStream);
			}
		}
	}

	/**
	 * 下载文件，需要手动关闭流，否则会一直占用资源
	 *
	 * @param filePath 文件名(包括路径)
	 */
	@Override
	public InputStream download(String filePath, String bucketName) throws SystemException {
		try {
			return minioClient.getObject(GetObjectArgs
					.builder()
					.bucket(bucketName)
					.object(filePath)
					.build());
		} catch (Exception e) {
			throw new SystemException("文件下载异常," + e.getMessage());
		}
	}

	@Override
	public void removeFile(String fileName, String bucketName) throws SystemException {
		try {
			minioClient.removeObject(
					RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build()
			);
		} catch (Exception e) {
			throw new SystemException("文件删除异常,");
		}
	}

	@Override
	public void getObjectList(String bucketName) throws Exception {
		Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
		for (Result<Item> result : results) {
			Item item = result.get();
			System.out.println(item.objectName());
		}
	}

}
