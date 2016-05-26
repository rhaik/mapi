package com.cyhd.web.action.doubao;


import java.net.URLDecoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.service.dao.po.UserEnchashmentAccount;
import com.cyhd.service.impl.UserEnchashmentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.Validator;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.UserAddress;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.doubao.OrderProductService;
import com.cyhd.service.impl.doubao.OrderService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.impl.doubao.UserDuobaoCoinService;
import com.cyhd.service.vo.doubao.ProductActivityVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/doubao/api/my")
public class MyDoubaoApiAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	OrderService orderService;
	
	@Resource
	OrderProductService orderProductService;

	@Resource
	ProductActivityService productActivityService;

	@Resource
	UserIncomeService incomeService;

	@Resource
	UserDuobaoCoinService duobaoCoinService;

	@Resource
	UserEnchashmentService userEnchashmentService;
	
	private static final String prefix = "doubao/api/my/";


	/**
	 * 夺宝币兑换
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/recharge", method = RequestMethod.POST)
	public ModelAndView rechargeDuobao(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int type = ServletRequestUtils.getIntParameter(request, "type", 1);
		int amountParam = ServletRequestUtils.getIntParameter(request, "amount", 0); //夺宝币数量，以个为单位，需转换成fen

		ModelAndView mv = new ModelAndView("doubao/api/order/status.json.ftl");
		if (amountParam <= 0){
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "兑换数量错误");
			return mv;
		}

		UserDuobaoCoinService.RechargeAmount rechargeAmount = UserDuobaoCoinService.RechargeAmount.getByAmount(amountParam);
		if (rechargeAmount == null){
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "兑换数量错误");
			return mv;
		}

		int amount= MoneyUtils.yuan2fen(rechargeAmount.getAmount());
		int payAmount = MoneyUtils.yuan2fen(rechargeAmount.getPayAmount());

		User user = getUser(request);
		if (type == 1) {
			UserIncome income = incomeService.getUserIncome(user.getId());

			int balance = income.getBalance();
			if (income.getEncash_total() <= 0){
				balance = income.getBalance() - 200;
			}

			if (balance < payAmount){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_WALLET_NOT_ENOUTH, "您的可兑换余额不足");
				return mv;
			}

			if (user.isBlack()){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_USER_MASKED, "您已被封禁");
				return mv;
			}

			UserEnchashmentAccount account = userEnchashmentService.getUserEnchashmentAccount(user.getId());
			if (account != null && account.isMasked()){
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_USER_MASKED, "您的账号异常，请联系客服处理");
				return mv;
			}

			if(incomeService.exchangeDuobaoCoin(user.getId(), payAmount) && duobaoCoinService.addDuobaoCoin(user.getId(), amount)) {
				fillStatus(mv);
			}else {
				fillErrorStatus(mv, ErrorCode.ERROR_CODE_UNKNOWN, "兑换失败，请稍后重试");
			}

		}else {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "暂不支持该充值方式");
			return mv;
		}

		return mv;
	}

	/**
	 * 夺宝记录列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/lotteryrecord", method = RequestMethod.GET)
	public ModelAndView lotteryrecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		int type = ServletRequestUtils.getIntParameter(request, "type", 1);
		User u = getUser(request);
		
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 1);
		int start = pageIndex * defaultPageSize;


		List<ProductActivityVo> orderList = productActivityService.getUserJoinedActivities(u.getId(), type, start, defaultPageSize);

		List<Integer> activityList = orderList.stream().map(productActivityVo -> productActivityVo.getProductActivity().getId()).collect(Collectors.toList());
		Map<Integer, OrderProduct> orderProductMap = orderProductService.getUserOrderProductGroupByActivities(u.getId(), activityList);

		for (ProductActivityVo activityVo : orderList){
			activityVo.setOrderProduct(orderProductMap.get(activityVo.getProductActivity().getId()));
		}

		mv.addObject("orderList", orderList);
		mv.addObject("orderProductMap", orderProductMap);
		mv.setViewName(prefix + "lotteryrecord.json.ftl");
		mv.addObject("title", "夺宝记录");
		return mv; 
	}


	/**
	 * 获取用户的夺宝号列表
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/lotteryNumber", method = RequestMethod.GET)
	public ModelAndView lotteryNumbers(HttpServletRequest request)throws Exception {
		ModelAndView mv = new ModelAndView(prefix + "lotterynumber.json.ftl");
		int activityId = ServletRequestUtils.getIntParameter(request, "activityId", 0);
		User u = getUser(request);

		if (activityId > 0){
			int start = 0, limit = 0;
			if (request.getParameter("page") != null){
				int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
				start = pageIndex * defaultPageSize;
				limit = defaultPageSize;
			}

			mv.addObject("lotteryList", orderProductService.getLotteryByUserAndProductActivityId(u.getId(), activityId, start, limit));
		}
		fillStatus(mv);
		return mv;
	}

	/**
	 * 添加收货人信息
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/saveConsignee", method = RequestMethod.POST)
	public ModelAndView saveConsignee(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		mv.setViewName("doubao/api/order/status.json.ftl");
		
		int orderProductId = ServletRequestUtils.getIntParameter(request, "orderProductId", 0);
		int addressid = ServletRequestUtils.getIntParameter(request, "addressid", 0);
		 
		if(orderProductId<=0) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "夺宝订单不能为空！");
			return mv;
		}
		if(addressid<=0) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "收货地址不能为空！");
			return mv;
		}
		OrderProduct op = orderProductService.getOrderProductById(orderProductId);
		if(op == null) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品不存在！");
			return mv;
		}
		if(op.getConsignee_time() != null) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "此订单收货地址已确认！");
			return mv;
		}
		UserAddress ua = orderService.getUserAddressById(addressid);
		if(ua == null) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "收货地址不存在");
			return mv;
		}
		if(op.getUser_id() != u.getId()) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "用户异常！");
			return mv;
		}
		
		if(orderProductService.saveConsignee(op.getUser_id(), op.getId(), ua.getName(), ua.getMobile(), ua.getAddress())) {
			fillStatus(mv);
		} else {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "保存收货信息失败！");
		}
		 
		mv.addObject("title", "保存收货地址");
		return mv; 
	}
	/**
	 * 确认收货
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/confirmGoods", method = RequestMethod.POST)
	public ModelAndView confirmGoods(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		mv.setViewName("doubao/api/order/status.json.ftl");
		
		int orderProductId = ServletRequestUtils.getIntParameter(request, "orderProductId", 0); 
		if(orderProductId<=0) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品ID不能为空！");
			return mv;
		}
		OrderProduct op = orderProductService.getOrderProductById(orderProductId);
		if(op == null) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品不存在！");
			return mv;
		}
		if(op.getUser_id() != u.getId()) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "用户异常！");
			return mv;
		}
		if(!op.isWaitingConfirm()) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品状态不是已发货！");
			return mv;
		}
		if(orderProductService.confirmGoods(op.getUser_id(), op.getId())) {
			fillStatus(mv);
		} else {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "签收失败！");
		}
		 
		mv.addObject("title", "订单详情");
		return mv; 
	}
	
	/**
	 * 添加收货地址
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/saveUserAddress", method = RequestMethod.POST)
	public ModelAndView saveUserAddress(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		mv.setViewName("doubao/api/order/status.json.ftl");
		
		int id = ServletRequestUtils.getIntParameter(request, "addressId", 0);
		String name = ServletRequestUtils.getStringParameter(request, "name");
		if(StringUtil.isBlank(name)) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "收货人不能为空！");
			return mv;
		}
		name = URLDecoder.decode(name, "utf-8");
		String mobile = ServletRequestUtils.getStringParameter(request, "mobile"); 
		if(StringUtil.isBlank(mobile)) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "收货人手机号不能为空！");
			return mv;
		}
		if(!Validator.isMobile(mobile)) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "收货人手机号格式不正确！");
			return mv;
		}
		String address = ServletRequestUtils.getStringParameter(request, "address"); 
		if(address==null || address.isEmpty()) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "收货人地址不能为空！");
			return mv;
		}
		address = URLDecoder.decode(address, "utf-8");
		int preferred = ServletRequestUtils.getIntParameter(request, "preferred", 0);
		UserAddress userAddress = null ;
		if(id > 0) {
			userAddress = orderService.getUserAddressById(id);
		} 
		if(userAddress == null) {
			userAddress = new UserAddress();
		}
		//取消原来的默认地址
		if(preferred == 1 && !userAddress.isDefault()) {
			orderService.updateCancelDefault(u.getId());
		}
		userAddress.setMobile(mobile);
		userAddress.setName(name);
		userAddress.setAddress(address);
		userAddress.setUser_id(u.getId());
		userAddress.setCreatetime(new Date());
		userAddress.setPreferred(preferred);
		if(orderService.saveUserAddress(userAddress)) {
			fillStatus(mv);
		} else {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "保存收货地址失败！");
		}
		 
		mv.addObject("title", "收货地址");
		return mv; 
	}
	/**
	 * 删除收货地址
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/deleteUserAddress", method = RequestMethod.POST)
	public ModelAndView deleteUserAddress(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		mv.setViewName("doubao/api/order/status.json.ftl");
		
		int useraddressid = ServletRequestUtils.getIntParameter(request, "addressId", 0);
		if(useraddressid <= 0) {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "缺少id！");
			return mv;
		}

		boolean result = orderService.deleteUserAddress(u.getId(), useraddressid);
		fillStatus(mv);
		return mv; 
	}
	/**
	 * 编辑个人信息:用户昵称,手机号
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/editinfor", method = RequestMethod.POST)
	public ModelAndView editinfor(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("doubao/api/order/status.json.ftl");
		User u = getUser(request);
		
		String name = ServletRequestUtils.getStringParameter(request, "name", null);
		String avatar = ServletRequestUtils.getStringParameter(request, "image", null);


		logger.info("修改用户信息，userid={}, name={}, image={}, 修改成：name={}, image={}", u.getId(), u.getName(), u.getAvatar(), name, avatar);

		if(StringUtil.isNotBlank(avatar) && userService.setAvatar(u.getId(), URLDecoder.decode(avatar, "utf-8"))) {
			fillStatus(mv);
		} else if(StringUtil.isNotBlank(name) && userService.setName(u.getId(), URLDecoder.decode(name, "utf-8"))) {
			fillStatus(mv);
		} else {
			fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER,"修改信息失败");
		}
		return mv; 
	}
}
