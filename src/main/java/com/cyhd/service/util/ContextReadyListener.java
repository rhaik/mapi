package com.cyhd.service.util;

import org.springframework.context.ApplicationContext;

/**
 * 监听spring context load ready事件，并通知给各个应用
 * @author luckyee
 *
 */
public interface ContextReadyListener {
	public void contextReady(ApplicationContext applicationcontext);
}
