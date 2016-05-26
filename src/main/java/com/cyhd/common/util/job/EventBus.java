package com.cyhd.common.util.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.async.Async;


/**
 *
 */
public class EventBus {
	
	private static Logger logger = LoggerFactory.getLogger(EventBus.class);
	
	@SuppressWarnings("rawtypes")
	private static Map<Class, List<PriorityHandler>> handlersMap = new HashMap<Class, List<PriorityHandler>>();

	private static int defaultPriority = Integer.MAX_VALUE / 2;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static synchronized <T> void registerHandler(Class<T> cls, JobHandler<T> handler) {
		List<PriorityHandler> list = handlersMap.get(cls);
		if(list == null) {
			list = new ArrayList<PriorityHandler>();
			handlersMap.put(cls, list);
		}
		list.add(new PriorityHandler<T>(handler, defaultPriority));
		Collections.sort(list);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static synchronized <T> void registerHandler(Class<T> cls, JobHandler<T> handler, int priority) {
		List<PriorityHandler> list = handlersMap.get(cls);
		if(list == null) {
			list = new ArrayList<PriorityHandler>();
			handlersMap.put(cls, list);
		}
		
		list.add(new PriorityHandler<T>(handler, priority));
		Collections.sort(list);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> void emit(final T object) {
		final List<PriorityHandler> handlers = handlersMap.get(object.getClass());
		
		if(logger.isDebugEnabled())
			logger.debug("eventbus received msg: {}, handler num: {}", object.getClass().getName(), handlers != null ? handlers.size() : 0);
		
		if(handlers != null) 
				Async.exec(new Runnable() {
					@Override
					public void run() {
						for(PriorityHandler<?> handler : handlers) {
							try {
							((JobHandler<T>)handler.handler).handle(object);
							}
							catch(Throwable t){
								logger.error("error when exec handler: {}",t);
							}
						}
					}
				});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> void emitImmediately(final T object) {
		final List<PriorityHandler> handlers = handlersMap.get(object.getClass());
		if(handlers != null)
			for(final PriorityHandler<?> handler : handlers) {
				try {
					((JobHandler<T>)handler.handler).handle(object);
				}
				catch(Exception ex) {
					logger.error("error when exec handler .", ex);
				}
			}
	}
}
