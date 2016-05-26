package com.cyhd.web.action.doubao;



import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.doubao.ProductActivity;
import com.cyhd.service.dao.po.doubao.UserDuobaoCoin;
import com.cyhd.service.impl.doubao.*;
import com.cyhd.service.util.GrayStrategyUtil;
import com.cyhd.service.util.UserAgentUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.UserAddress;
import com.cyhd.service.impl.QiniuService;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.vo.doubao.ProductActivityVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/doubao/my")
public class MyDoubaoAction extends BaseAction {

	@Resource
	UserService userService;
	
	@Resource
	OrderService orderService;
	
	@Resource
	OrderProductService orderProductService;
	
	@Resource
	UserIncomeService userIncomeService;
	
	@Resource
	ProductActivityService productActivityService;

	@Resource
	UserDuobaoCoinService duobaoCoinService;

	@Resource
	ProductShareOrderService productShareOrderService;
	
	@Resource
	QiniuService qiniuService;
	private static final String prefix = "doubao/web/my/";

	/**
	 * 我的夺宝
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
//		mv.setViewName(prefix + "index.html.ftl");
		mv.setViewName(prefix + "index_new.html.ftl");
		User u = getUser(request);


		//增加用户的夺宝币
		UserDuobaoCoin duobaoCoin = duobaoCoinService.getUserDuobaoCoin(u.getId());
		if (duobaoCoin == null){
			duobaoCoin = new UserDuobaoCoin();
			duobaoCoin.setUser_id(u.getId());
			duobaoCoinService.add(duobaoCoin);
		}

		mv.addObject("duobaoCoin", duobaoCoin);
		mv.addObject("user", u);
		mv.addObject("title", "我");

		return mv;
	}


	/**
	 * 用户参与的夺宝活动列表
	 * lotteryrecord.html跳转到夺宝记录页面
	 * winrecord.html 处理中奖记录请求，跳转到中奖记录页面
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = {"/lotteryrecord.html","/winrecord.html"}, method = RequestMethod.GET)
	public ModelAndView lotteryrecord(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);

		//type=1 进行中或揭晓中 type=3 已揭晓  type=5 已过期 type=10已中奖
		int type ;
		if(request.getRequestURI().indexOf("/winrecord.html") != -1) {
			mv.setViewName(prefix + "winrecord.html.ftl");
			type = ServletRequestUtils.getIntParameter(request, "type", 10);
		} else {
			mv.setViewName(prefix + "duobao_record.html.ftl");
			type = ServletRequestUtils.getIntParameter(request, "type", 1);
		}

		int total = productActivityService.countUserJoinedActivites(u.getId(), type);
		int totalPage = (total + defaultPageSize - 1) / defaultPageSize;
		List<ProductActivityVo> orderList = Collections.EMPTY_LIST;
		if(totalPage > 0) {
			int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
			int start = pageIndex * defaultPageSize;

			orderList = productActivityService.getUserJoinedActivities(u.getId(), type, start, defaultPageSize);

			List<Integer> activityList = orderList.stream().map(productActivityVo -> productActivityVo.getProductActivity().getId()).collect(Collectors.toList());
			Map<Integer, OrderProduct> orderProductMap = orderProductService.getUserOrderProductGroupByActivities(u.getId(), activityList);

			for (ProductActivityVo activityVo : orderList){
				activityVo.setOrderProduct(orderProductMap.get(activityVo.getProductActivity().getId()));
			}
		}

		mv.addObject("type", type);
		mv.addObject("totalPage", totalPage);
		mv.addObject("orderList", orderList);
		

		mv.addObject("title", "夺宝记录");
		return mv; 
	}


	/**
	 * 根据活动id查询中奖情况
	 * @param pid
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/lotterydetail/pa-{pid:\\d+}.html", method = RequestMethod.GET)
	public ModelAndView activityLotteryDetail(@PathVariable("pid")int pid, HttpServletRequest request) throws Exception {
		ProductActivity activity = null;
		if (pid > 0){
			 activity = productActivityService.getProductActivityById(pid);
		}

		User user = getUser(request);
		if (activity == null || activity.getLottery_user() != user.getId()){
			return new ModelAndView("404");
		}

		return viewLotteryDetail(request, activity.getLottery_order_product());
	}
	
	/**
	 * 夺宝订单详情
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/lotterydetail/{id:\\d+}.html", method = RequestMethod.GET)
	public ModelAndView lotteryDetail(@PathVariable("id")int id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		return viewLotteryDetail(request, id);
	}

	/**
	 * 查看中奖订单详情
	 * @param request
	 * @param id
	 * @return
	 * @throws Exception
	 */
	protected ModelAndView viewLotteryDetail(HttpServletRequest request, int id) throws Exception{
		ModelAndView mv = new ModelAndView();
		OrderProduct orderProduct = orderProductService.getOrderProductById(id);

		User u = getUser(request);
		String title = "夺宝订单详情";
		if(orderProduct != null) {
			if (u.getId() != orderProduct.getUser_id() || orderProduct.getLottery() != 1){
				return new ModelAndView("404");
			}

			ProductActivityVo pa = productActivityService.getVoByActivityId(orderProduct.getProduct_activity_id());
			UserAddress ua = null;
			if(orderProduct.getShipping_status() == 1) {
				int addressid = ServletRequestUtils.getIntParameter(request, "addressid", 0);
				if(addressid > 0) {
					ua = orderService.getUserAddressById(addressid);
				} else {
					ua = orderService.getUserDefaultAddress(u.getId());
				}
				title = "确认收货地址";
				mv.setViewName(prefix + "confirm_address.html.ftl");
			}else {
				mv.setViewName(prefix + "lotterydetail_new.html.ftl");

				//获取用户的分享记录
				if (orderProduct.getShare() == 1){
					mv.addObject("productShare", productShareOrderService.getMyShareByActivityId(u.getId(), orderProduct.getProduct_activity_id()));
				}
			}

			mv.addObject("ua", ua);
			mv.addObject("orderProduct", orderProduct);
			mv.addObject("productActivity", pa);
			mv.addObject("isConsignee", orderProduct.getConsignee_time() == null);
		} else {
			mv.addObject("description", "购买商品参与夺宝");
			mv.setViewName("/common/nodata.html.ftl");
		}
		mv.addObject("title", title);
		return mv;
	}
	
	/**
	 * 收货地址
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/address.html", method = RequestMethod.GET)
	public ModelAndView address(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"address.html.ftl");
		int useraddressid = ServletRequestUtils.getIntParameter(request, "addressId", 0);
		UserAddress ua = new UserAddress();
		if(useraddressid > 0) {
			ua = orderService.getUserAddressById(useraddressid);
			mv.addObject("title", "编辑收货地址");
		} else {
			mv.addObject("title", "添加收货地址");
		}
		mv.addObject("ua", ua);
		mv.addObject("useraddressid", useraddressid);
		return mv;
	}

	/**
	 * 收货地址列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/addressList.html", method = RequestMethod.GET)
	public ModelAndView getUserAddress(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);

		mv.setViewName(prefix+"addressList.html.ftl");
		if (userAgent.isInAppView()){
			mv.setViewName(prefix + "appAddrList.html.ftl");
		}

		mv.addObject("ualist", orderService.getUserAddressList(u.getId()));
		mv.addObject("toEdit", request.getParameter("toEdit") != null);
		mv.addObject("addressId", ServletRequestUtils.getIntParameter(request, "addressId", 0));
		mv.addObject("title", "收货地址列表");
		return mv; 
	}


	/**
	 * 收货地址列表
	 *
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/recharge.html", method = RequestMethod.GET)
	public ModelAndView recharge(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User user = getUser(request);

		UserIncome userIncome = userIncomeService.getUserIncome(user.getId());

		UserDuobaoCoin duobaoCoin = duobaoCoinService.getUserDuobaoCoin(user.getId());

		int availBalance = userIncome.getBalance();
		if (userIncome.getEncash_total() <= 0) {
			availBalance = userIncome.getBalance() - 200;
			if (availBalance < 0){
				availBalance = 0;
			}
		}

		mv.addObject("income", userIncome);
		mv.addObject("availBalance", availBalance);
		mv.addObject("duobaoCoin", duobaoCoin);
		mv.addObject("allowedAmount", UserDuobaoCoinService.RechargeAmount.values());

		mv.setViewName(prefix+"recharge.html.ftl");
		mv.addObject("title", "兑换夺宝币");
		return mv;
	}
	
	@RequestMapping(value="/editinfor.html",method=RequestMethod.GET)
	public ModelAndView editinfor(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		//增加用户的夺宝币
		UserDuobaoCoin duobaoCoin = duobaoCoinService.getUserDuobaoCoin(u.getId());
		if (duobaoCoin == null){
			duobaoCoin = new UserDuobaoCoin();
			duobaoCoin.setUser_id(u.getId());
			duobaoCoinService.add(duobaoCoin);
		}
		mv.addObject("user", u);
		mv.addObject("duobaoCoin", duobaoCoin);
		mv.addObject("qiniuToken", qiniuService.getUploadToken());
		mv.addObject("title", "编辑信息");
		mv.setViewName(prefix + "editinfor.html.ftl");
		return mv;
	}
}
