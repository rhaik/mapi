package com.cyhd.web.action.doubao;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.TimePeriod;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.ProductActivity;
import com.cyhd.service.impl.doubao.OrderProductService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.impl.doubao.ProductShareOrderService;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.vo.doubao.ProductActivityVo;
import com.cyhd.web.common.BaseAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Controller
@RequestMapping("/doubao/api/common")
public class IndexApiAction extends BaseAction {

	@Resource
	ProductActivityService productActivityService;

	@Resource
	ProductShareOrderService productShareOrderService;

	@Resource
	private OrderProductService orderProductService;

	private static final String prefix = "doubao/api/index/";

	/**
	 * 获取夺宝活动列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView apps(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		int type = ServletRequestUtils.getIntParameter(request, "type", ProductActivity.TYPE_DEFAULT);
		
		mv.setViewName(prefix + "index.json.ftl");
		int start = pageIndex * defaultPageSize;
		mv.addObject("productList", productActivityService.getProductActivityByType(type, start, defaultPageSize));
		mv.addObject("title", "夺宝列表");
		return mv; 
	}

	/**
	 * 获取最近待开奖的活动
	 * @return
	 */
	@RequestMapping(value = "/annoucingList", method = RequestMethod.GET)
	public ModelAndView getLatestAnnouncing(HttpServletRequest request){
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "index.json.ftl");
		mv.addObject("productList", productActivityService.getLatestAnnouncingList());
		mv.addObject("title", "夺宝列表");
		return mv;
	}
	
	/**
	 * 夺宝活动的夺宝记录
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/buyhistory/{id:\\d+}", method = RequestMethod.GET)
	public ModelAndView buyhistory(@PathVariable("id")int activityId, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "buyhistory.json.ftl");
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		
		int start = pageIndex * defaultPageSize;
		mv.addObject("productList", orderProductService.getOrderProductByActivityId(activityId, start, defaultPageSize));
		mv.addObject("title", "商品购买记录列表");
		return mv; 
	}
	
	/**
	 * 查询商品是否已揭晓成功
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/announced/{id:\\d+}", method = RequestMethod.GET)
	public ModelAndView announced(@PathVariable("id")int id, HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "announced.json.ftl");
		
		ProductActivityVo product = productActivityService.getVoByActivityId(id);

		//揭晓成功后才返回商品信息
		if(product.getProductActivity().isResult()) {
			mv.addObject("product", product);
		}
		mv.addObject("title", "揭晓商品");
		return mv; 
	}
	
	/**
	 * 往期揭晓
	 * 
	 * @param request
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/history/{id:\\d+}", method = RequestMethod.GET)
    public ModelAndView history(@PathVariable("id")int id, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "history.json.ftl");
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
		
		int start = pageIndex * defaultPageSize;
		mv.addObject("productActivity", productActivityService.getHistoryOrderActivityList(id, start, defaultPageSize));
		mv.addObject("title", "往期揭晓");
		return mv; 
	}


	/**
	 * 获取最新中奖的用户及商品
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/latestLottery", method = RequestMethod.GET)
	public ModelAndView getLatestLottery(HttpServletRequest request) throws Exception {
		User user = getUser(request);

		ModelAndView mv = new ModelAndView(prefix + "latestlottery.json.ftl");
		List<ProductActivityVo> voList = productActivityService.getLatestLotteryList();
		if (voList.size() > 0){
			ProductActivityVo lottery = voList.get(ThreadLocalRandom.current().nextInt(voList.size()));
			mv.addObject("lottery", lottery);
			mv.addObject("lottery_time", TimePeriod.beforeForSubscription(lottery.getProductActivity().getLottery_time()));
		}
		mv.addObject("user", user);
		fillStatus(mv);
		return mv;
	}


	/**
	 * 检查用户最新中奖的情况
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/myLottery", method = RequestMethod.GET)
	public ModelAndView getMyLottery(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView(prefix + "mylottery.json.ftl");

		String lastTime = CookieUtil.getCookieValue("last_lottery_time", request);
		if (StringUtil.isBlank(lastTime)){
			lastTime = request.getParameter("lastTime");
		}

		long lastLotteryTime = NumberUtil.safeParseLong(lastTime);
		Date lastLotteryDate = null;
		if (lastLotteryTime > 0) {
			lastLotteryDate = new Date(lastLotteryTime);
		}

		User user = getUser(request);
		ProductActivity myLatestLottery = productActivityService.getUserLatestLottery(user.getId(), lastLotteryDate);

		mv.addObject("user", user);
		mv.addObject("myLottery", myLatestLottery);
		fillStatus(mv);
		return mv;
	}
}
