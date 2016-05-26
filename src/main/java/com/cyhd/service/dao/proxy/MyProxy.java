package com.cyhd.service.dao.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.service.dao.impl.BaseDao;

public class MyProxy extends BaseDao implements InvocationHandler {
	
	private static Logger logger = LoggerFactory.getLogger(MyProxy.class);

	private Object mapper;

	public MyProxy(Object mapper) {
		this.mapper = mapper;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		try {
			String methodName = method.getName();
			boolean isRead = isRead(methodName);
			logger.debug("DBDaoProxy invoke:" + mapper.toString() + ":"
					+ methodName + ",isread=" + isRead);
			if(isRead)
				this.setSlave();
			else
				this.setMaster();
			return method.invoke(mapper, args);
		} catch (Throwable e) {
			logger.error("DBDaoProxy invoke error",e);
			throw e;
			
		} finally {

		}

	}

	private boolean isRead(String name) {
		return name.toLowerCase().startsWith("get")
				|| name.toLowerCase().startsWith("read")
				|| name.toLowerCase().startsWith("search")
				|| name.toLowerCase().startsWith("find")
				|| name.toLowerCase().startsWith("select");
	}

}
