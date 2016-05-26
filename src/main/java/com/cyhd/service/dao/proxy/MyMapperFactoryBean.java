package com.cyhd.service.dao.proxy;

import java.lang.reflect.Proxy;

import org.mybatis.spring.mapper.MapperFactoryBean;

public class MyMapperFactoryBean<T> extends MapperFactoryBean<T> {

	public T getObject() throws Exception {
		
		T o = super.getObject() ;
		MyProxy myProxy = new MyProxy(o) ;
		o = (T) Proxy.newProxyInstance(o.getClass().getClassLoader(), o.getClass().getInterfaces(), myProxy) ;
		return o ;
	}
}
