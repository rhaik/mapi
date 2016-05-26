package com.cyhd.service.impl.doubao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants; 
import com.cyhd.service.dao.db.mapper.doubao.ProductActivityRuleMapper;
import com.cyhd.service.dao.db.mapper.doubao.ProductMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.doubao.Product;
import com.cyhd.service.dao.po.doubao.ProductActivityRule;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.util.CacheUtil;

/**
 * 一元夺宝商品服务
 */
@Service
public class ProductService extends BaseService {
	//缓存商品信息
	private CacheLRULiveAccessDaoImpl<Product> cacheProduct = new CacheLRULiveAccessDaoImpl<Product>(Constants.minutes_millis * 5, 128);
	
	//缓存商品信息
	private CacheLRULiveAccessDaoImpl<ProductActivityRule> cacheProductActivityRule = new CacheLRULiveAccessDaoImpl<ProductActivityRule>(Constants.minutes_millis * 5, 128);

	@Resource
	private ProductMapper productMapper; 
	
	@Resource
	private ProductActivityRuleMapper productActivityRuleMapper; 
	
	/**
	 * 根据Id获取商品信息
	 * @param id
	 * @return
	 */
	public Product getProductById(int id) {
		String key = CacheUtil.getProductKey(id);
		Product p = cacheProduct.get(key);
		if(p==null) {
			p = productMapper.getProductById(id);
			cacheProduct.set(key, p);
		}
		return p;
	}
	/**
	 * 获取当前未进行夺宝的商品规则
	 * @return
	 */
	public List<ProductActivityRule> getNotDoingProductActivityRuleList() {
		return productActivityRuleMapper.getNotDoingProductActivityRuleList();
	}
	/**
	 * 根据Id获取商品信息
	 * @param id
	 * @return
	 */
	public ProductActivityRule getProductActivityRuleById(int id) {
		String key = CacheUtil.getProductRuleKey(id);
		ProductActivityRule p = cacheProductActivityRule.get(key);
		if(p==null) {
			p = productActivityRuleMapper.getProductActivityRuleById(id);
			cacheProductActivityRule.set(key, p);
		}
		return p;
	}

	/**
	 * 修改已售的数量
	 * @param productId
	 * @return
	 */
	public boolean updateSellStock(int productId){
		return productMapper.updateSellStock(productId) > 0;
	}
}
