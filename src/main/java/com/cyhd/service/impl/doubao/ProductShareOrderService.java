package com.cyhd.service.impl.doubao;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.imageio.ImageIO;

import com.cyhd.service.impl.UserIntegalIncomeService;
import com.cyhd.service.util.GlobalConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.doubao.ProductShareMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.ProductShare;
import com.cyhd.service.dao.po.doubao.ProductShareLog;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.impl.QiniuService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.ImageUtil;
import com.cyhd.service.vo.doubao.ProductShareVo;


@Service
public class ProductShareOrderService extends BaseService {

	@Resource
	private ProductShareMapper productShareMapper; 
	@Resource
	private UserService userService; 
	
	@Resource
	ProductActivityService productActivityService;
	
	@Resource
	private OrderProductService orderProductService; 
	
	@Resource
	QiniuService qiniuService;
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;

	@Resource
	UserIntegalIncomeService integalIncomeService;
	
	//缓存晒单记录信息
	private CacheLRULiveAccessDaoImpl<List<ProductShareVo>> cacheShareOrder = new CacheLRULiveAccessDaoImpl<List<ProductShareVo>>(Constants.minutes_millis * 5, 1024);
	//缓存晒单记录总数信息
	private CacheLRULiveAccessDaoImpl<Integer> cacheShareCount = new CacheLRULiveAccessDaoImpl<Integer>(Constants.minutes_millis * 5, 1024);

	//晒单后奖励10金币
	private final static int REWARD_GOLDCOIN_AMOUNT = 10;

	/**
	 * 根据Id获取晒单信息
	 * @param id
	 * @return
	 */
	public ProductShare getProductShare(int id) {
		return productShareMapper.getProductShare(id);
	}


	/**
	 * 获取用户对某次活动的晒单
	 * @param userId
	 * @param activityId
	 * @return
	 */
	public ProductShare getMyShareByActivityId(int userId, int activityId){
		return productShareMapper.getMyShareByActivityId(userId, activityId);
	}


	/**
	 * 根据商品ID获取往期晒单记录
	 * 
	 * @param productId
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ProductShareVo> getHistoryShareOrderList(int productId,int start, int size) {
		String key = "HistoryProductShareList_"+ productId +"_"+ start + "_"+ size;
		List<ProductShareVo> list = cacheShareOrder.get(key);
		if(list == null) {
			List<ProductShare> productShareList  =  productShareMapper.getProductShareListByProductId(productId, start, size);
			
			list = new ArrayList<ProductShareVo>();
			for(ProductShare ps : productShareList) {
				ProductShareVo vo = new ProductShareVo();
				 
				if(ps.getUser_id() > 0)
					vo.setUser(userService.getUserById(ps.getUser_id()));
				vo.setProductShare(ps);
				if(ps.getProduct_activity_id() > 0) {
					vo.setProductActivity(productActivityService.getProductActivityById(ps.getProduct_activity_id()));
				}
				if(ps.getOrder_product_id() > 0) {
					vo.setOrderProduct(orderProductService.getOrderProductById(ps.getOrder_product_id()));
				}
				
				list.add(vo);
			}
			cacheShareOrder.set(key, list);
		}
		return list;
	}
	
	/**
	 * 统计往期晒单记录
	 * 
	 * @param productId
	 * @param start
	 * @param size
	 * @return
	 */
	public int countProductShareListByProductId(int productId) {
		String key = "HistoryProductShareListCount_" + productId;
		Integer count = cacheShareCount.get(key);
		if(count == null) {
			count = productShareMapper.countProductShareListByProductId(productId);
			cacheShareCount.set(key, count);
		}
		return count;
	}
	/**
	 * 统计晒单记录
	 * 
	 * @param userId 
	 * @return
	 */
	public int countShare() {
		String key = "ProductShareListCount";
		Integer count = cacheShareCount.get(key);
		if(count == null) {
			count = productShareMapper.countShare();
			cacheShareCount.set(key, count);
		}
		return count;
	}
	/**
	 * 晒单记录
	 * 
	 * @param userId
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ProductShareVo> getProductShareList(int start, int size) {
		String key = "ProductShareList_" + start + "_"+ size;
		List<ProductShareVo> list = cacheShareOrder.get(key);
		if(list == null) {
			List<ProductShare> productShareList  =  productShareMapper.getProductShareList(start, size);
			
			list = new ArrayList<ProductShareVo>();
			for(ProductShare ps : productShareList) {
				ProductShareVo vo = new ProductShareVo();
				 
				if(ps.getUser_id() > 0)
					vo.setUser(userService.getUserById(ps.getUser_id()));
				vo.setProductShare(ps);
				if(ps.getProduct_activity_id() > 0) {
					vo.setProductActivity(productActivityService.getProductActivityById(ps.getProduct_activity_id()));
				}
				if(ps.getOrder_product_id() > 0) {
					vo.setOrderProduct(orderProductService.getOrderProductById(ps.getOrder_product_id()));
				}
				
				list.add(vo);
			}
			cacheShareOrder.set(key, list);
		}
		return list;
	}
	/**
	 * 我的晒单记录
	 * 
	 * @param userId 
	 * @return
	 */
	public int countMyProductShare(int userId) {
		return productShareMapper.countMyProductShare(userId);
	}
	
	/**
	 * 我的晒单记录
	 * 
	 * @param userId
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ProductShareVo> getMyShareOrderList(int userId,int start, int size) {
		List<ProductShare> productShareList  =  productShareMapper.getMyProductShareList(userId, start, size);
		
		List<ProductShareVo> l = new ArrayList<ProductShareVo>();
		for(ProductShare ps : productShareList) {
			ProductShareVo vo = new ProductShareVo();
			 
			if(ps.getUser_id() > 0)
				vo.setUser(userService.getUserById(ps.getUser_id()));
			vo.setProductShare(ps);
			if(ps.getProduct_activity_id() > 0) {
				vo.setProductActivity(productActivityService.getProductActivityById(ps.getProduct_activity_id()));
			}
			if(ps.getOrder_product_id() > 0) {
				vo.setOrderProduct(orderProductService.getOrderProductById(ps.getOrder_product_id()));
			}
			
			l.add(vo);
		}
		return l;
	}
	/**
	 * 添加晒单数据
	 * @param productShare
	 * @return
	 */
	public boolean add(ProductShare productShare) {
		if(productShareMapper.add(productShare) > 0) {
			ProductShareLog log = new ProductShareLog();
			log.setOperator(productShare.getUser_id());
			log.setOperator_time(new Date());
			log.setRemarks("提交晒单");
			log.setShare_id(productShare.getId());
			log.setStatus(productShare.getStatus());
			return productShareMapper.addLog(log) > 0;
		}
		return false;
	}

	/**
	 * 用户晒单成功后，奖励用户
	 * @return
	 */
	public boolean rewardUserAfterShare(int userId, int orderProductId,int clientType){
		return integalIncomeService.addRewardedIntegral(userId, REWARD_GOLDCOIN_AMOUNT, "一元夺宝晒单", clientType, "" + orderProductId);
	}

	/**
	 * 晒单图片
	 * @param file
	 * @param u
	 * @return
	 */
	public String uploadFileToQiniu(MultipartFile file, User u) {
		 // 判断文件是否为空  
        if (!file.isEmpty()) {
            try {  
                // 文件保存路径  
            	int last = file.getOriginalFilename().lastIndexOf('.');
            	String ext = file.getOriginalFilename().substring(last+1);
            	long currentTime = System.currentTimeMillis();
            	String pic_name = u.getInvite_code()+"_share_"+ currentTime +"." + ext;
    			if (qiniuService.checkFileExist(pic_name))  {
    				pic_name = u.getInvite_code()+"_share_"+ currentTime +"." + ext;
    			}
    			String thumb_name = u.getInvite_code()+"_share_"+ currentTime +"_thumb." + ext;

				//暂不使用水印
				BufferedImage image = ImageIO.read(file.getInputStream());
//				try{
//					image = ImageUtil.createMark(file.getInputStream(), "秒赚大钱", Color.WHITE, ext);
//				}catch (Exception exp){
//					image = ImageIO.read(file.getInputStream());
//				}

				if (!GlobalConfig.isDeploy){
					File tmpFile = File.createTempFile(pic_name, ext);
					Files.copy(file.getInputStream(), Paths.get(tmpFile.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

					logger.info("tmp file saved to:" + tmpFile.getAbsolutePath());
				}

				byte[] bytes = ImageUtil.bufferedImage(image, 0.8f);
    			qiniuService.uploadMediaFile(pic_name, bytes);

    			bytes  = ImageUtil.resize(image, 200, 200);
    			qiniuService.uploadMediaFile(thumb_name, bytes);



                String url = qiniuService.getResourceURLByFileName(thumb_name);
                return url;
            } catch (Exception e) {  
                logger.error("updateImage error, file:{}, user:{}", file, u);
            }  
        }  
        return "";  
	}
	 
}
