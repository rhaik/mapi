package com.cyhd.service.monitor;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cyhd.service.util.GlobalConfig;

@Aspect
@Component
public class ServiceMonitor {

	protected static Logger logger = LoggerFactory.getLogger("monitor");

	private int timeLimit = 100;
	private String prefix = GlobalConfig.server_name + "_" + GlobalConfig.server_ip;

	ConcurrentHashMap<String, Monitor> monitors = new ConcurrentHashMap<String, Monitor>();

	@Pointcut("execution(* com.cyhd.service.dao.db.mapper.*.*(..))")
	private void oprDB() {
	}

	@Pointcut("execution(* com.cyhd.service.dao.*Dao.*(..))")
	private void oprDao() {
	}

	@Pointcut("execution(* com.cyhd.service.push.getui.*Pusher.push(..))")
	private void getuiPush() {
	}

	@Around("oprDB() || oprDao() || getuiPush()")
	public Object monitor(ProceedingJoinPoint joinPoint) throws Throwable {
		long begin = System.currentTimeMillis();

		String className = joinPoint.getSignature().getDeclaringTypeName();
		int index = className.lastIndexOf(".");
		if (index > 0) {
			className = className.substring(index + 1);
		}
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String methodName = methodSignature.getName();
		Class<?> returnType = methodSignature.getMethod().getReturnType();

		Monitor m = createMonitor(className, methodName);
		Object[] args = joinPoint.getArgs();
		Object obj = null;
		try {
			obj = joinPoint.proceed(args);
			if (returnType == void.class) {
				m.increaseSucc();
			} else {
				if (obj == null) {
					m.increaseSucc();
				} else {
					if (returnType == String.class && methodName.equals("push")) {
						if (((String) obj).contains("异常"))
							m.increaseErr();
						else
							m.increaseSucc();
					} else {
						m.increaseSucc();
					}
				}
			}
		} catch (Throwable e) {
			m.increaseErr();
			logger.error("MonitorService.monitor error. className:" + className + ",methodName:" + methodName + ", args:" + Arrays.toString(args), e);
			throw e;
		} finally {
			int spendTime = Math.abs((int) (System.currentTimeMillis() - begin));
			m.increaseTotaltime(spendTime);
			if (spendTime > timeLimit) {
				m.increaseTimeout();
				logger.error(className + "." + methodName + " spend time:" + spendTime + " ms");
			}
		}
		return obj;
	}

	private Monitor createMonitor(String businessName, String methodName) {
		String key = prefix + "_" + businessName + "_" + methodName;
		Monitor m = monitors.get(key);
		if (m == null) {
			m = new Monitor();
			m.setServername(GlobalConfig.server_name);
			m.setServerip(GlobalConfig.server_ip);
			m.setBusinessname(businessName);
			m.setBusinessmethod(methodName);
			monitors.put(key, m);
		}
		return m;
	}

	/**
	 * 用做从手动监控，适用于异步调用的情况
	 * 
	 * @param businessName
	 * @param methodName
	 * @param count
	 */
	public void reportSucc(String businessName, String methodName, int count) {
		Monitor m = createMonitor(businessName, methodName);
		m.setSucc(m.getSucc() + count);
	}

	public void reportErr(String businessName, String methodName, int count) {
		Monitor m = createMonitor(businessName, methodName);
		m.setError(m.getError() + count);
	}

	public void reportTimeout(String businessName, String methodName, int count) {
		Monitor m = createMonitor(businessName, methodName);
		m.setTimeout(m.getTimeout() + count);
	}

	public void reportTotalTime(String businessName, String methodName, int count) {
		Monitor m = createMonitor(businessName, methodName);
		m.increaseTotaltime(count);
	}

}
