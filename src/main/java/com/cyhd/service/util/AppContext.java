package com.cyhd.service.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Scope("singleton")
@Component
public class AppContext implements ApplicationContextAware {

    private static ApplicationContext appContext;
    
    private static List<ContextReadyListener> listeners = new ArrayList<ContextReadyListener>();
    
    
    public static void addContextReadyListener(ContextReadyListener listener){
    	listeners.add(listener);
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationcontext)
            throws BeansException {
        appContext = applicationcontext;
        for(ContextReadyListener listener : listeners){
        	listener.contextReady(applicationcontext);
        }
    }
    
    public static Object getBean(String beanId){
		if (appContext == null) return null;
		return appContext.getBean(beanId);
	}
    
    public static <T> T getBean(Class<T> requiredType){
    	if (appContext == null) return null;
    	return appContext.getBean(requiredType);
    }
    
}
