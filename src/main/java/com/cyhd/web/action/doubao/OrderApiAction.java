package com.cyhd.web.action.doubao;


import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.doubao.ProductActivity;
import com.cyhd.service.dao.po.doubao.UserDuobaoCoin;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.doubao.OrderService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.impl.doubao.UserDuobaoCoinService;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/doubao/api/order")
public class OrderApiAction extends BaseAction {
	
	@Resource
	OrderService orderService;
	
	@Resource
	ProductActivityService productActivityService;

	@Resource
	UserDuobaoCoinService duobaoCoinService;
	
	private static final String prefix = "doubao/api/order/";


	/**
	 * 添加到购物车
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 *
	@RequestMapping(value = "/addcart", method = RequestMethod.POST)
	public ModelAndView addcart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "addcart.json.ftl");
		User u = getUser(request);
		
		int productActivityId = ServletRequestUtils.getIntParameter(request, "product_activity_id");
		int number = ServletRequestUtils.getIntParameter(request, "number", 5);
		if(number<=0) {
			logger.error("product number than 1!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "商品数量必须大于1！");
			return mv;
		}
		ProductActivity productActivity = productActivityService.getProductActivityById(productActivityId);
		if(productActivity == null) {
			logger.error("product activity not exist!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "商品不存在！");
			return mv;
		}
		//当前活动是否进行
		if(! productActivity.isActivityTime()) {
			logger.error("product activity not doing!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "当前商品不在活动范围！");
			return mv;
		}
		if(productActivity.isFull()) {
			String productName = productActivity.getProduct_name();
			productActivity = productActivityService.buildNextPeriod(productActivity);
			if(productActivity == null) {
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "商品["+productName+"]已过期！");
				return mv;
			}  
		}
		if((productActivity.getBuy_number() + number) > productActivity.getNumber()) {
			number = productActivity.getNumber() - productActivity.getBuy_number();
		}
		Cart cart = new Cart();
		cart.setProduct_id(productActivity.getProduct_id());
		cart.setProduct_activity_id(productActivityId);
		cart.setNumber(number);
		cart.setUser_id(u.getId());
		mv.addObject("count", 0);
		if(orderService.isExistCartProductByProductId(cart.getUser_id(), cart.getProduct_id())) {
			orderService.addCartNumber(u.getId(), cart.getProduct_id(), cart.getProduct_activity_id(), number);
			fillStatus(mv);
		} else {
			if(orderService.addCart(cart)) {
				mv.addObject("count", 1);
				fillStatus(mv);
			} else {
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "添加购物车失败");
			}
		}
		mv.addObject("title", "添加购物车");
		return mv; 
	}
	*/
	
	/**
	 * 获取购物车数量
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/cartNumber", method = RequestMethod.GET)
	public ModelAndView cartNumber(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "cartNumber.json.ftl");
		User u = getUser(request);
		 
		mv.addObject("number", 0);
		mv.addObject("title", "购物车数量");
		return mv; 
	}


	/**
	 * 提交夺宝订单，目前只支持使用余额进行购买
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/sumbitOrder", method = RequestMethod.POST)
    public ModelAndView sumbitOrder(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "status.json.ftl");


		//传入的参数：活动id和购买的数量
		int activityId = ServletRequestUtils.getIntParameter(request, "activityId", 0);
		int number = ServletRequestUtils.getIntParameter(request, "number", 1);

		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		//检查是否绑定手机号码
		if(u.getMobile() == null || u.getMobile().isEmpty()) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "请先绑定您的手机号！");
			return mv;
		}

		if (activityId < 0) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "参数错误");
			return mv;
		}

		ProductActivity activity = productActivityService.getProductActivityById(activityId);
		if (activity == null) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "参数错误");
			return mv;
		}

		//当前活动是否进行
		if(! activity.isActivityTime() || activity.getStatus() != ProductActivity.STATUS_DOING) {
			logger.error("product activity not doing!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "很抱歉，您来晚了，本次活动已结束");
			return mv;
		}

		if(activity.isFull()) {
			if(activity == null) {
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "很抱歉，您来晚了，本次活动已结束");
				return mv;
			}
		}
		if((activity.getBuy_number() + number) > activity.getNumber()) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "夺宝次数超过剩余所需次数");
			return mv;
		}

		int payAmount = number * activity.getPrice();
		UserDuobaoCoin duobaoCoin = duobaoCoinService.getUserDuobaoCoin(u.getId());
		if(duobaoCoin == null || duobaoCoin.getBalance() < payAmount) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您的夺宝币不足！");
			return mv;
		}

		try{
			//创建订单
			orderService.createOrder(activityId, number, clientInfo.getPlatform(), u.getId(), RequestUtil.getIpAddr(request));
			this.fillStatus(mv);
		}catch(Exception e) {
			logger.error("create order error", e);
			throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, e.getMessage(), e);
		}

		return mv;
	}



	/**
	 * 删除购物车商品
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView

	@RequestMapping(value = "/cart/delete", method = RequestMethod.POST)
	public ModelAndView delete(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "status.json.ftl");
		User u = getUser(request);
	  
		int productActivityId = ServletRequestUtils.getIntParameter(request, "product_activity_id");
		
		if(orderService.deleteCartProductActivityId(u.getId(), productActivityId)) {
			this.fillStatus(mv);
		} else {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER,"删除失败");
		}
		mv.addObject("title", "清单");
		return mv; 
	}

	 */
 
}
