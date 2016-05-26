package com.cyhd.web.common.util;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 控制每个客户端发送的请求数，如果超出最大请求数，则屏蔽请求。
 * @author free
 *
 */
public class RequestController {

	private static final Logger logger = LoggerFactory.getLogger("apirequest");
	private ConcurrentHashMap<String, Integer> accessControls = new ConcurrentHashMap<String, Integer>(); 
	
	// 默认时间区间大小（单位：毫秒，计算请求数）
	private static long DEFAULT_GAP = 1* 60 * 1000;
	
	// 上次开始计数时间，用于控制时间间隔
	private volatile long lastStartTime = System.currentTimeMillis();
	
	// 时间区间大小
	private long timeGap;
	
	// 最大允许请求数
	private int maxAllow;
	
	public RequestController(int maxAllow){
		this(maxAllow, DEFAULT_GAP);
	}
	
	public RequestController(int maxAllow, long timeGap){
		this.maxAllow = maxAllow;
		this.timeGap = timeGap;
	}
	
	public boolean access(String clientId){
		if(StringUtils.isEmpty(clientId)){
			if(logger.isErrorEnabled()){
				logger.error("clientid is empty!");
			}
			return false;
		}
		Integer count = accessControls.get(clientId);
		if(count == null){
			count = new Integer(0);
		}
		// 计数小于允许的最大请求数，则直接放过
		if(count <= maxAllow){
			accessControls.put(clientId, new Integer(count+1));
			if(logger.isDebugEnabled()){
				logger.debug("allow client=" + clientId + ", count = " + count);
			}
			return true;
		}
		if(logger.isErrorEnabled()){
			logger.error("not allow client=" + clientId + ", count = " + count);
		}
		synchronized (this) {
			long now = System.currentTimeMillis();
			long gap = now - lastStartTime;
			if(gap >= timeGap){
				if(logger.isDebugEnabled()){
					logger.debug("clear count and reset time.");
				}
				accessControls.clear();
				lastStartTime = now;
			}
		}
		return false;
		
	}
}
