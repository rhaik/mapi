package com.cyhd.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.cyhd.common.util.StringUtil;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.AcountMapper;
import com.cyhd.service.dao.po.Account;
import com.cyhd.service.util.CacheUtil;


@Service
public class AccountService extends BaseService {

	@Resource
	AcountMapper accountMapper;
	
	@Resource(name=CacheUtil.RAM_LA_RESOURCE)
	CacheDao cacheDao;
	
	private Account defaultAccount=null;

	private final int ttlInMillis = 15*Constants.minutes_millis;
	
	private LiveAccess<List<Account>> accountLive = null;

	private volatile boolean load = false;

	public void loadIds(){
		if(load){
			return ;
		}try{
			load = true;
			List<Account> ids = accountMapper.getAllIds();
			accountLive = new LiveAccess<List<Account>>(ttlInMillis, ids);
		}finally{
			load = false;
		}
	}
	
	@PostConstruct
	public void initDefaultAccount(){
		int defaultAccountid = 1;
		defaultAccount = this.getAccount(defaultAccountid);
		if(defaultAccount == null){
			logger.error("get Wx Account error!!");
		}else{
			logger.info("default wx Account:{}" + defaultAccount);
		}
	}
	
	
	public Account getDefaultAccount(){
		return defaultAccount;
	}
	
	
	public Account getAccount(int id){
		String key = "account_" + id;
		Account acc =  (Account)cacheDao.get(key);
		if(acc == null){
			acc = accountMapper.getAccount(id);
			if(acc != null){
				cacheDao.set(key, acc);
			}
		}
		if(acc == null){
			logger.error("AccountService get account error! accountid={}", id);
		}
		return acc;
	}

	public void updateAccessToken(Account account) {
		try{
			accountMapper.updateAccessToken(account);
		}catch(Exception e){
			logger.error("update accesstoken error, accountid="+account.getId(), e);
		}
	}
	
	/**获取随机的微信id*/
	public Account getRandomAccount(){
		List<Account> ids =  accountLive.getElement();
		if (ids != null) {
			int index = (int) (Math.random() * ids.size());
			return ids.get(index);
		}
		return null;
	}

	/**
	 * 根据域名获取账户
	 * @param host http host,不包含http前缀
	 * @return
	 */
	public Account getAccountByHost(String host){
		List<Account> ids =  accountLive.getElement();
		if (ids != null && StringUtil.isNotBlank(host)){
			return ids.stream().filter(act -> act.getHost() != null && act.getHost().contains(host)).findAny().orElse(getDefaultAccount());
		}
		return getDefaultAccount();
	}
	
	public Account getAccountByArticleId(int article){
		List<Account> accList = accountLive.getElement();
		return accList.get(article%accList.size());
	}
}
