package com.cyhd.service.vendor;

import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.web.common.ClientInfo;
import org.springframework.stereotype.Service;

/**
 * 默认的点击服务，主要用来测试，永远返回true
 * Created by hy on 9/16/15.
 */
@Service("defaultVendorService")
public class DefaultVendorClickService implements IVendorClickService{

    @Override
    public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
        logger.info("default vendor service: onClickApp");
        return true;
    }

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {
		// TODO Auto-generated method stub
		return false;
	}
}
