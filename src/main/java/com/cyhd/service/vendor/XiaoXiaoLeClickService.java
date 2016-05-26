package com.cyhd.service.vendor;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.web.common.ClientInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by huzhimin on 16/5/20.
 */

@Service("XiaoXiaoLeClickSevice")
public class XiaoXiaoLeClickService implements IVendorClickService{

    @Resource
    private UserInstalledAppService userInstalledAppService;

    @Override
    public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
        return disctinct(vendor, app, user, appTask, clientInfo);
    }

    @Override
    public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
        return onClickApp(vendor, null, app, appTask, clientInfo);
    }

    @Override
    public boolean disctinct(AppVendor vendor, App app, User user, AppTask appTask, ClientInfo clientInfo) {
        String data = doDistinct(vendor, app, clientInfo.getIdfa());
        net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(data);
        int success = 2;
        if(json != null ){
            success = json.optInt(clientInfo.getIdfa(), 2);
            if(success == 1){
                try {
                    userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
                } catch (Exception e) {}
            }

        }
        return success == 0;
    }


    @Override
    public String disctinct(AppVendor vendor, App app, String idfas) {
        return handleDiactinct(doDistinct(vendor, app, idfas), 1);
    }

    @Override
    public Map<String, Integer> disctinctNew(AppVendor vendor, App app, String idfas) {
        return handleDiactinctNew(doDistinct(vendor, app, idfas), 1);
    }


    private String doDistinct(AppVendor vendor, App app, String idfas){

        StringBuilder sb = new StringBuilder(320);
        sb.append("http://121.40.195.122/SaveSeed/game/checkRepeat");
        sb.append("?appid=").append(app.getAppstore_id());
        sb.append("&idfa=").append(idfas);
        sb.append("&timestamp=").append(System.currentTimeMillis());

        String query = sb.toString();
        try {
            logger.info("请求消消乐排重接口开始,idfa:{}",idfas);
            String response = HttpUtil.get(query, null);
            logger.info("请求消消乐排重接口结束,idfa:{},response:{}",idfas,response);
            return response;
        } catch (Exception e) {
            logger.info("请求消消乐排重接口异常,idfa:{},casue:",idfas,e);
        }
        return "{}";
    }
}
