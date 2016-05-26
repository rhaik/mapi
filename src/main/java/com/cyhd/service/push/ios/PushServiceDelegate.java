package com.cyhd.service.push.ios;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.service.monitor.ServiceMonitor;
import com.notnoop.apns.ApnsDelegate;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.DeliveryError;
import com.notnoop.apns.internal.Utilities;

@Service
public class PushServiceDelegate implements ApnsDelegate {
    
	@Resource
	ServiceMonitor serviceMonitor;
	
	private static final String businessName = "IosPushService";
	private static final String methodName = "push";
	
    public static final Logger PUSHLOG = LoggerFactory.getLogger("push");

    private String unmarshall(ApnsNotification message) {
        StringBuilder sb = new StringBuilder();
        sb.append("identifier=").append(message.getIdentifier()).append(", ");
        sb.append("expiry=").append(message.getExpiry()).append(", ");
        sb.append("token=").append(Utilities.encodeHex(message.getDeviceToken())).append(", ");
        sb.append("payload=").append(new String(message.getPayload()));
        return sb.toString();
    }

    @Override
    public void messageSendFailed(ApnsNotification message, Throwable e) {
    	serviceMonitor.reportErr(businessName, methodName, 1);
    	serviceMonitor.reportSucc(businessName, methodName, -1);
        PUSHLOG.warn("Notification send failed: " + unmarshall(message) + " -> " + e);
    }

    @Override
    public void connectionClosed(DeliveryError e, int messageIdentifier) {
    	//serviceMonitor.reportErr(businessName, methodName, 1);
        PUSHLOG.warn("Notification error code: " + messageIdentifier + " ->" + e.code());
    }
	@Override
	public void cacheLengthExceeded(int arg0) {
		
	}
	@Override
	public void messageSent(ApnsNotification message, boolean resent) {
		serviceMonitor.reportSucc(businessName, methodName, 1);
		PUSHLOG.info("Notification sent: " + unmarshall(message));
	}
	@Override
	public void notificationsResent(int resendCount) {
		
	}

}
