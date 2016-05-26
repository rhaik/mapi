package com.cyhd.web.action.doubao;


import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.dao.po.UserBanner;
import com.cyhd.service.dao.po.doubao.*;
import com.cyhd.service.impl.UserBannerService;
import com.cyhd.service.impl.doubao.*;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.UserAgentUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.WeixinShareService;
import com.cyhd.service.vo.doubao.ProductActivityVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/doubao")
public class IndexAction extends BaseAction {
	@Resource
	ProductActivityService productActivityService;
	
	@Resource
	OrderProductService orderProductService;
	
	@Resource
	ProductShareOrderService productShareOrderService;
	
	@Resource
	OrderService orderService;
	
	@Resource
	ProductService productService;
	
	@Resource
	ThirdShishicaiService thirdShishicaiService;

	@Resource
	private WeixinShareService weixinShareService;

	@Resource
	UserDuobaoCoinService duobaoCoinService;

	@Resource
	UserBannerService userBannerService;
	
	private static final String prefix = "doubao/web/index/";

	/**
	 * 首页
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/index.html", method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "index_new.html.ftl");

		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int type = ServletRequestUtils.getIntParameter(request, "type", ProductActivity.TYPE_DEFAULT);

		User u = getUser(request);
		mv.addObject("user", u);

		int start = pageIndex * defaultPageSize;
		mv.addObject("productList", productActivityService.getProductActivityByType(type, start, defaultPageSize));
		mv.addObject("title", "1元夺宝");

		//获取轮播图
		List<UserBanner> banners = userBannerService.getBanners(UserBannerService.CATEGORY_DUOBAO);
		mv.addObject("banners", banners);

		return mv; 
	}

	/**
	 * 根据商品id，去往其最新一期的活动页面
	 * @param productId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/latest/{id:\\d+}.html", method = RequestMethod.GET)
	public ModelAndView toLatest(@PathVariable("id") int productId, HttpServletRequest request, HttpServletResponse response) throws  Exception {
		//防止重定向被缓存
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		response.setDateHeader("Expires", 0); // Proxies.

		ProductActivity activity = productActivityService.getLatestProductActivityByProductId(productId);
		if (activity != null){
			return  redirectForApp(request, "/doubao/product/" + activity.getId() + ".html");
		}
		//没有最新一期的活动
		return new ModelAndView("404");
	}

	/**
	 * 商品详情页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/product/{id:\\d+}.html", method = RequestMethod.GET)
    public ModelAndView detail(@PathVariable("id")int activityId, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "detail_new.html.ftl");

		if (request.getParameter("old") != null){
			mv.setViewName(prefix + "detail.html.ftl");
		}
		
//		Integer id = IdEncoder.decode(encodedId);
//		if(id == null) {
//			throw ErrorCode.getParameterErrorException("参数错误!");
//		}
		ProductActivityVo vo = productActivityService.getVoByActivityId(activityId);
		if(vo == null) {
			throw ErrorCode.getParameterErrorException("参数错误!");
		}
		User u = getUser(request);
		mv.addObject("product", vo);
		mv.addObject("user", u);
		mv.addObject("userBuy", orderProductService.countUserBuy(u.getId(), activityId));
		int total = orderProductService.countProductBuyHistory(vo.getProductActivity().getId());
		if(total > 0) {
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("orderProductList", orderProductService.getOrderProductByActivityId(vo.getProductActivity().getId(), 0, defaultPageSize));
		}

		//如果用户夺宝币为空，则为用户创建夺宝币记录
		UserDuobaoCoin duobaoCoin = duobaoCoinService.getUserDuobaoCoin(u.getId());
		if (duobaoCoin == null){
			duobaoCoin = new UserDuobaoCoin();
			duobaoCoin.setUser_id(u.getId());
			duobaoCoinService.add(duobaoCoin);
		}
		mv.addObject("duobaoCoin", duobaoCoin);
		mv.addObject("ua", UserAgentUtil.getUserAgent(request));

		String destUrl = GlobalConfig.base_url + "www/doubao/share/"+u.getInvite_code()+"/"+activityId;
		mv.addObject("shareUrl", destUrl);
		mv.addObject("baseUrl", GlobalConfig.base_url);
		mv.addObject("currentTime", System.currentTimeMillis()); 
		mv.addObject("title", "商品详情");
		return mv; 
	}
	
	/**
	 * 商品详情页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/product/content-{id:\\d+}.html", method = RequestMethod.GET)
    public ModelAndView productContent(@PathVariable("id")int productId, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "content.html.ftl");
		mv.addObject("product", productService.getProductById(productId));
		mv.addObject("title", "图文详情");
		return mv; 
	}
	/**
	 * 商品往期揭晓列表页
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/history/{id:\\w+}.html", method = RequestMethod.GET)
    public ModelAndView history(@PathVariable("id")int productId, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		
		//Integer id = IdEncoder.decode(encodedId);
		//if(id == null) {
		//	throw ErrorCode.getParameterErrorException("参数错误!");
		//}
		int total = productActivityService.countHistoryActivity(productId);
		if(total > 0) {
			mv.setViewName(prefix + "history_new.html.ftl");
			int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
			int start = pageIndex * defaultPageSize;
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("productId",productId);

			List<ProductActivityVo> activityList = productActivityService.getHistoryOrderActivityList(productId, start, defaultPageSize);
			mv.addObject("productActivityHistory", activityList);
		} else {
			mv.addObject("description", "参与活动");
			mv.setViewName("/common/nodata.html.ftl");
		}
		mv.addObject("title", "往期揭晓");
		return mv; 
	}
	
	/**
	 * 商品计算页面
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/productcalc/{id:\\d+}.html", method = RequestMethod.GET)
    public ModelAndView productcalc(@PathVariable("id")int id, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "productcalc_new.html.ftl");
		
		ProductActivity pa = productActivityService.getProductActivityById(id);
		if(pa == null) {
			throw ErrorCode.getParameterErrorException("参数错误!");
		}

		List<OrderHistory> ohList = orderService.getOrderHistoryList(id, ProductActivityCalculateService.HISTORY_ORDER_NUM);
		long total = 0;
		if(ohList!=null) {
			for(OrderHistory oh : ohList) {
				total+= oh.getTime_value();
			}
		}
		ThridShishicai ssc = thirdShishicaiService.getByPeriods(pa.getShishicai());
		mv.addObject("productActivity", pa);
		mv.addObject("orderHistory", ohList);
		mv.addObject("shishicai", ssc.getLottery_number());
		mv.addObject("total", total);
		mv.addObject("title", "商品计算夺奖号");
		return mv; 
	}

	@RequestMapping(value = "/buy/{id:\\d+}.html", method = RequestMethod.GET)
	public ModelAndView payActivity(@PathVariable("id")int id, HttpServletRequest request) throws Exception {
		String buyNumStr = CookieUtil.getCookieValue("buy_num", request);
		if (StringUtil.isBlank(buyNumStr)){
			buyNumStr = request.getParameter("num");
		}

		int num = NumberUtil.safeParseInt(buyNumStr);
		if (num <= 0) {
			num = 1;
		}

		User user = getUser(request);

		ModelAndView mv = new ModelAndView();
		mv.addObject("title", "提交夺宝");

		ProductActivityVo pa = productActivityService.getVoByActivityId(id);
		if(pa == null) {
			mv.setViewName("/common/nodata.html.ftl");
		}else {
			mv.addObject("activity", pa);

			UserDuobaoCoin duobaoCoin = duobaoCoinService.getUserDuobaoCoin(user.getId());

			mv.addObject("duobaoCoin", duobaoCoin);
			mv.addObject("number", num);
			mv.setViewName(prefix + "buy.html.ftl");
		}


		return mv;
	}
}
