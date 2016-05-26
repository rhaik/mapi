package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.constants.QiniuConstants;
import com.cyhd.service.dao.QiniuDao;

/**
 * 七牛云存储service
 *
 */
@Service
public class QiniuService extends BaseService{

	@Resource
	private QiniuDao qiniuManager ;
	
	public String uploadMediaFile(String key, byte[] datas) {
		if(qiniuManager.upload(QiniuConstants.media_bucket, key, datas)) 
			return qiniuManager.getFileUrl(QiniuConstants.media_bucket, key);
		else return "" ;
	}
	/**
	 * 检查文件是否在七牛上 <br/>
	 * @param fileName
	 * @return
	 */
	public boolean checkFileExist(String fileName){
		return qiniuManager.checkFileExist(fileName);
	}
	
	/**
	 * 请注意 该方法不会上传文件 只是生成一个url
	 * @param key 文件名
	 * @return
	 */
	public String getResourceURLByFileName(String fileName){
		return qiniuManager.getFileUrl(QiniuConstants.media_bucket, fileName);
	}

	/**
	 * 获取上传的凭证
	 * @return
	 */
	public String getUploadToken(){
		return qiniuManager.getUploadToken();
	}
	
	public void deleteFile(String fileName){
		qiniuManager.delete(fileName);
	}
}
