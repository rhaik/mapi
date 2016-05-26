package com.cyhd.service.monitor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.job.AsyncJob;
import com.cyhd.common.util.job.JobHandler;
import com.cyhd.service.dao.MongoDBDao;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.util.GlobalConfig;
import com.mongodb.BasicDBObject;

@Service
public class MonitorService extends BaseService {
	
	@Resource
	MongoDBDao mongoDbDao;
	
	@Resource
	ServiceMonitor serviceMonitor;
	
//	@Resource
//	SmsService smsService;
	
	private static final String monitor_table_name = "monitor";
	
	AsyncJob<Monitor> job = null;
	
	AsyncJob<Collection<Monitor>> alertJob = null;
	
	@PostConstruct
	public void init(){
		JobHandler<Monitor> writer = new JobHandler<Monitor>() {
			@Override
			public boolean handle(Monitor t) {
				return addMonitor(t);
			}
		};
		job = new AsyncJob<Monitor>("monitor_writer", writer);
		
		JobHandler<Collection<Monitor>> alerter = new JobHandler<Collection<Monitor>>() {
			@Override
			public boolean handle(Collection<Monitor> t) {
				return checkAlert(t);
			}
		};
		alertJob = new AsyncJob<Collection<Monitor>>("monitor_alerter", alerter);
	}
	
	
	@Scheduled(cron="0 */5 * * * *")
	public void monitor(){
		Calendar cal = GregorianCalendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		
		int interval = hour * 12 + minute/5;
		synchronized (serviceMonitor.monitors) {
			for(Monitor monitor : serviceMonitor.monitors.values()){
				int totalCount = monitor.getSucc() + monitor.getError();
				monitor.setTotal(totalCount);
				monitor.setAvgtime(getAveTime(monitor.getTotaltime(), totalCount));
				monitor.setErrrate(getErrorRate(monitor.getSucc(), monitor.getError()));
				monitor.setInterval(interval);
				monitor.setDay(DateUtil.getTodayStartDate());
				monitor.setCreatetime(cal.getTime());
				job.offer(monitor);
			}
			List<Monitor> m = new ArrayList<Monitor>();
			m.addAll(serviceMonitor.monitors.values());
			alertJob.offer(m);
		}
		serviceMonitor.monitors.clear();
	}
	
	private static final String[] alertBusies = {"jedis", "mongodb"};
	private static final String[] ignoreBusies = {"UserSettingMapper", "UserAppendMapper", "SnsFavourMapper", "WorkmateMapper", "UserRelationMapper"};
	
//	@Resource
//	private EmailService emailService;
	/**
	 * 异步告警
	 * @param monitors
	 * @return
	 */
	private boolean checkAlert(Collection<Monitor>  monitors){
		if(!(GlobalConfig.isApiServer || GlobalConfig.isJobServer))
			return true;
				
		StringBuffer smsSb = new StringBuffer();
		StringBuffer emailSb = new StringBuffer();
		int index = 1;
		for(Monitor monitor : monitors){
			String busiName = monitor.getBusinessname();
			String method = monitor.getBusinessmethod();
			int avgTime = monitor.getAvgtime();
			int succ = monitor.getSucc();
			int total = monitor.getSucc() + monitor.getError();
			double errorRate = monitor.getErrrate();
			boolean ignore = false;
			for(String busi : ignoreBusies){
				if(busi.equalsIgnoreCase(busiName)){
					ignore = true;
					break;
				}
			}
			if(ignore)
				continue;
			
			boolean alert = false;
			for(String busi : alertBusies){
				if(busi.equalsIgnoreCase(busiName)){
					alert = true;
					break;
				}
			}
			if(!alert){
				if(method.equals("push")){
					alert =true;
				}
			}
			if(total > 100 && (avgTime > 1500 || errorRate > 0.4)){
				StringBuffer sb = new StringBuffer();
				sb.append(index++).append(" alert:").append(monitor.getServername()).append(":").append(monitor.getServerip()).append(":")
					.append(busiName).append(".").append(method).append(",avgtime=").append(avgTime).append(",errRate=").append(errorRate)
					.append(",succ=").append(succ).append(",error=").append(monitor.getError()).append("。");
				emailSb.append(sb);
				emailSb.append("<br/>");
				if(alert){
					smsSb.append(sb);
				}
			}
		}
		String s = smsSb.toString();
		if(!StringUtils.isEmpty(s)){
			logger.error("MonitorService.monitor report sms alert: {}" , s);
			//smsService.notifyDevelopers(s);
		}
		s = emailSb.toString();
		if(!StringUtils.isEmpty(s)){
			logger.error("MonitorService.monitor report email alert: {}" , s);
			//emailService.sendAlertEmail(s);
		}
		return true;
	}
	
	private int getAveTime(long totaltime, int totalCount) {
		if(totalCount == 0)
			return 0;
		return (int)(totaltime/totalCount);
	}

	public ConcurrentHashMap<String, Monitor> getMonitors(){
		return serviceMonitor.monitors;
		//return null;
	}
	
	private static Double getErrorRate(int succ, int error){
		int total = succ + error;
		if(error == 0 || total == 0){
			return 0.0;
		}
		BigDecimal bg = new BigDecimal(error);
		bg = bg.divide(BigDecimal.valueOf(total), 4, BigDecimal.ROUND_HALF_EVEN);
		return bg.doubleValue();
	}
	
	protected boolean addMonitor(Monitor monitor){
		try{
			BasicDBObject obj = new BasicDBObject();
			obj.append("servername", monitor.getServername());
			obj.append("serverip", monitor.getServerip());
			obj.append("businessname", monitor.getBusinessname());
			obj.append("businessmethod", monitor.getBusinessmethod());
			obj.append("total", monitor.getTotal());
			obj.append("succ", monitor.getSucc());
			obj.append("error", monitor.getError());
			obj.append("timeout", monitor.getTimeout());
			obj.append("avgtime", monitor.getAvgtime());
			obj.append("errrate", monitor.getErrrate());
			obj.append("day", toUTCTime(monitor.getDay()));
			obj.append("interval", monitor.getInterval());
			obj.append("createtime", toUTCTime(monitor.getCreatetime()));
			
			//mongoDbDao.insert(obj, monitor_table_name);
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	private Date toUTCTime(Date date){
		Calendar cal = GregorianCalendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, 8);
		return cal.getTime();
	}
	
	public static void main(String[] args){
		System.out.println(getErrorRate(2, 1));
	}
	

}
