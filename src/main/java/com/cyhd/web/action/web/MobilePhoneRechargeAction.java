package com.cyhd.web.action.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.ValidateCode;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.po.Recharge;
import com.cyhd.service.dao.po.Ref;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.impl.RechargeService;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.Recharfe99douInterfaceUtil;
import com.cyhd.service.vo.RechargeVo;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/web/recharge/")
public class MobilePhoneRechargeAction extends BaseAction {
	@Resource
	RechargeService rechargeService;
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;
	
	private static final String prefix = "/web/recharge/";
	/**
	 * 验证码
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "/captchas")
	public void captchas(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 设置响应的类型格式为图片格式  
        response.setContentType("image/jpeg");  
        //禁止图像缓存。  
        response.setHeader("Pragma", "no-cache");  
        response.setHeader("Cache-Control", "no-cache");  
        response.setDateHeader("Expires", 0);  
          
        User u = getUser(request);
          
        ValidateCode vCode = new ValidateCode(120,40,5,0);  
        memcachedCacheDao.set(CacheUtil.getCaptchasKey(u.getId()), vCode.getCode(), Constants.minutes_millis * 10);
        vCode.write(response.getOutputStream());
	}
	
	@Resource
	UserIncomeService userIncomeService;
	/**
	 * 充值页面
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/mobilephone.html")
	public ModelAndView mobilephone(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "mobilephone.html.ftl");
		
		User u = getUser(request);
		int channel=1;
		String areachannel ="",rs="";
		String mobile = u.getMobile();
		if(mobile!="") {
			Recharfe99douInterfaceUtil dou = new Recharfe99douInterfaceUtil();
			Ref<String> msg = new Ref<String>();
			rs = dou.MobileQuery(mobile, msg);
			
			if(rs!="") {
				String[] temp = rs.split(";");
				String channelName = temp[0].split(":")[1];
				String area = temp[1].split(":")[1];
				areachannel = area+channelName;
				if(channelName.indexOf("移动") >=0) {
					channel = 1;
				} else if(channelName.indexOf("联通 ")>=0) {
					channel = 2;
				} else if(channelName.indexOf("电信")>=0) {
					channel = 3;
				}
			}
		}
		UserIncome userIncome = userIncomeService.getUserIncome(u.getId());
		mv.addObject("balance", userIncome.getBalance());
		mv.addObject("recharge", rechargeService.getRechargeDenominationList(channel));
		mv.addObject("areachannel", "("+areachannel+")");
		mv.addObject("mobilearea", rs);
		mv.addObject("mobile", u.getMobile());
		mv.addObject("title", "手机充值");
		return mv;
	}
	
	/**
	 * 充值记录列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/list.html")
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		
		User u = getUser(request);
		List<RechargeVo> vo = rechargeService.getRechargeListByUserId(u.getId());
		if(vo.size() > 0) {
			mv.setViewName(prefix + "list.html.ftl");
			mv.addObject("recharge", vo);
		} else {
			mv.addObject("description", "做限时任务");
			mv.setViewName("common/nodata.html.ftl");
		}
		mv.addObject("title", "手机充值记录");
		return mv;
	}
	private static Logger log = LoggerFactory.getLogger("recharge");
	/**
	 * 回调通知
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/notify.3w", method= RequestMethod.POST)
	public void notify(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String status = ServletRequestUtils.getStringParameter(request, "status");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("partner", ServletRequestUtils.getStringParameter(request, "partner"));
		map.put("account", ServletRequestUtils.getStringParameter(request, "account"));
		map.put("account_info", ServletRequestUtils.getStringParameter(request, "account_info"));
		map.put("pid", ServletRequestUtils.getStringParameter(request, "pid"));
		map.put("out_trade_id", ServletRequestUtils.getStringParameter(request, "out_trade_id"));
		map.put("success_qty", ServletRequestUtils.getStringParameter(request, "success_qty"));
		map.put("fail_qty", ServletRequestUtils.getStringParameter(request, "fail_qty"));
		map.put("total_price", ServletRequestUtils.getStringParameter(request, "total_price"));
		map.put("status", status);
		map.put("_sign", ServletRequestUtils.getStringParameter(request, "_sign"));
		
		Map<String, String[]> params = request.getParameterMap();  
        String queryString = "";  
        for (String key : params.keySet()) {  
            String[] values = params.get(key);  
            for (int i = 0; i < values.length; i++) {  
                String value = values[i];  
                queryString += key + "=" + value + "&";  
            }  
        }  
        // 去掉最后一个空格  
        queryString = queryString.substring(0, queryString.length() - 1);   
		log.info("Huafei notify request params " + request.getRequestURL() + "?" + queryString); 
		
		Recharfe99douInterfaceUtil dou = new Recharfe99douInterfaceUtil();
		Ref<String> out_trade_id = new Ref<String>();
		Ref<Integer> success_qty = new Ref<Integer>();
		Ref<Integer> fail_qty = new Ref<Integer>();
		Ref<String> msg = new Ref<String>();
		int rs = dou.VerifyNotify(map, out_trade_id, success_qty, fail_qty, msg);
		if(rs == 0) { //验证成功
			int total_price =  MoneyUtils.yuan2fen(ServletRequestUtils.getDoubleParameter(request, "total_price"));
			long orderSn = Long.parseLong(out_trade_id.getValue());
			Recharge re = rechargeService.getByOrderSn(orderSn);
			if(re.getStatus() == Recharge.ORDER_STATUS_RECHARGE){
				if(rechargeService.updateStatus(orderSn, status, total_price, msg.getValue())){
					response.getWriter().write("ok");
					log.info("Huafei notify request update ordersn {} status success", out_trade_id.getValue());
				} else {
					log.info("Huafei notify request update ordersn {} status fail", out_trade_id.getValue());
				}
			} else {
				log.info("Huafei notify request update ordersn {} status not 1", out_trade_id.getValue());
			}
		} else {
			log.info("Huafei notify request validate ordersn {}  fail:{}" , out_trade_id.getValue(), msg.getValue());
		}
	}
}
