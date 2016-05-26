package com.cyhd.service.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.service.constants.QiniuConstants;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

/**
 * 七牛云存储Manager
 *
 */
@Service
public class QiniuDao {

	private Logger logger = LoggerFactory.getLogger(QiniuDao.class);
	
	private UploadManager uploadManager = new UploadManager();
	
	private Auth auth = null;
	{
		auth = Auth.create(QiniuConstants.accessKey, QiniuConstants.secretKey);
    	
	}
	
	/**
	 * upload file (image, voice eg.)
	 * @param bucketName  qiniu bucket name
	 * @param key         file unique key 
	 * @param localFile   file url 
	 * @return
	 */
	public boolean upload(String bucketName, String key, byte[] data) {
        try {
        	logger.info("qiniu start upload key={}, data length={}", key, data.length);
        	String token = getUpToken0(auth);
        	Response ret = uploadManager.put(data, key, token);
        	
			boolean success = ret.isOK();
			if(!success)
				logger.error("qiniu upload key={}, ret={}", key, ret.statusCode);
			return success;
		} catch (Exception e) {
			logger.error("QiniuDaoImpl.upload error", e); 
			e.printStackTrace();
		}
        
        return false ;
	}

	/**
	 * 获取上传凭证，1小时有效期
	 * @return
	 */
	public String getUploadToken(){
		return auth.uploadToken(QiniuConstants.media_bucket);
	}
	
	protected String getUpToken0(Auth auth){
	    return auth.uploadToken(QiniuConstants.media_bucket);
	}
	
	/**
	 * 获取下载文件的路径
	 * @param bucketName  qiniu bucket name
	 * @param key         file unique key
	 * @return
	 */
	public String getFileUrl(String bucketName, String key) {
		
        String downloadUrl = "http://" + QiniuConstants.domain_suffix + "/" + key ; 
        
        return downloadUrl ;
	}
	/***
	 * 判断某个文件是否存在 <br/>
	 * <b>真心不知道 这个sdk怎么回事 你应该给我返回响应码 而不是异常</b><br/>
	 *TODO 如果sdk升级解决这个问题 此处需要解决
	 * @param fileName
	 * @return
	 */
	public boolean checkFileExist(String fileName){
		try {
			// 真心不知道 这个sdk怎么回事 你应该给我返回响应码 而不是异常
			BucketManager bucketManager = new BucketManager(auth);
			FileInfo info = bucketManager.stat(QiniuConstants.media_bucket, fileName);
			return info != null;
		} catch (QiniuException e) {
			logger.error("七牛检查exists：{}",e.response.toString());
		}
		return false;
	}
	
	public void delete(String fileName){
		BucketManager bucketManager = new BucketManager(auth);
		try {
			bucketManager.delete(QiniuConstants.media_bucket, fileName);
		} catch (QiniuException e) {
			logger.error("七牛删除数据：{}",e.response.toString());
		}
	}
	public static void main(String[] args) throws Exception {
		//System.out.println(System.currentTimeMillis() / 1000 +100 * 365 * 24 * 3600);
		Auth auth = Auth.create(QiniuConstants.accessKey, QiniuConstants.secretKey);
    	
		BucketManager bucketManager = new BucketManager(auth);
		//bucketManager.fetch("http://"+QiniuConstants.domain_suffix, QiniuConstants.media_bucket, "a8e7304dd1b133a72bb2b878b8ceba1a_1.png");
		
		String bucket = QiniuConstants.media_bucket;
		String key = "a8e7304dd1b133a72bb2b878b8ceba1ae_1.png";
		long start = System.currentTimeMillis();
		FileInfo info = bucketManager.stat(bucket , key );
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(info);
	}
	
	
}
