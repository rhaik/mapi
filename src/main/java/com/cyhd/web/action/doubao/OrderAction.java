package com.cyhd.web.action.doubao;


/*
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.ProductActivity;
import com.cyhd.service.impl.UserIncomeService;
import com.cyhd.service.impl.doubao.OrderService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.impl.doubao.ProductShareOrderService;
import com.cyhd.web.common.BaseAction;

@Controller
@RequestMapping("/doubao/order")
public class OrderAction extends BaseAction {
	@Resource
	UserIncomeService userIncomeService;
	
	@Resource
	OrderService orderService;
	
	@Resource
	ProductShareOrderService productShareOrderService;
	
	@Resource
	private ProductActivityService productActivityService; 
	
	private static final String prefix = "doubao/web/order/";

	/**
	 * 购物车
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	@RequestMapping(value = "/cart")
	public ModelAndView cart(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "cart.html.ftl");
		User u = getUser(request);
		List<CartVo> list = orderService.getCartList(u.getId());
		if(list!=null && list.size() > 0) {
			for(CartVo  cart: list) { 
				//当前活动商品是否进行中
				if(!cart.getProductActivity().isActivityTime()) {
					cart.setMsg("商品["+cart.getProductActivity().getProduct_name()+"]不在活动范围！");
					continue;
				}
				int number = cart.getCart().getNumber();
				
				//当前活动商品已满员
				if(cart.getProductActivity().isFull()) {
					ProductActivity pa = productActivityService.buildNextPeriod(cart.getProductActivity());
					if(pa != null) {
						orderService.updateCartNumber(u.getId(), pa.getProduct_id(), pa.getId(), number);
						cart.getCart().setNumber(number);
						cart.setProductActivity(pa);
						cart.setMsg("商品已过期，自动更新为第"+pa.getProduct_number()+"期");
					} else {
						cart.setMsg("商品已过期");
					}
				} 
				int currentMax = cart.getProductActivity().getNumber() - cart.getProductActivity().getBuy_number();
				if( number > currentMax) {
					orderService.updateCartNumber(u.getId(), cart.getProduct().getId(), cart.getProductActivity().getId(), currentMax);
					cart.getCart().setNumber(currentMax);
					cart.setMsg("商品库存不足，只能购买"+currentMax+"份");
					cart.setAll(true);
				} else if(number == currentMax) {
					cart.setMsg("购买人次自动调整为包尾人次，确认订单后 获得包尾特权");
					cart.setAll(true);
				} 
				
			}
			mv.addObject("cartList", list);
		} else {
			
			mv.addObject("info", "宝贝");
			mv.addObject("description", "抢购商品");
			mv.setViewName("/doubao/common/nodata.html.ftl");
		}
		mv.addObject("title", "清单");
		return mv; 
	}
	
	/**
	 * 商品结算页面
	 * 
	 * @param request
	 * @param response
	 * @return

	@RequestMapping(value = "/pay", method = RequestMethod.POST)
    public ModelAndView pay(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "pay.html.ftl");
		User u = getUser(request);
		
		
		List<CartVo> cartList = orderService.getCartList(u.getId());
		
		if(cartList !=null && cartList.size() >0) {
			int total = 0;
			for(CartVo  cart: cartList) { 
				//旧购买数
				int number = cart.getCart().getNumber();
				//当前最大能购买数
				int currentMax = cart.getProductActivity().getNumber() - cart.getProductActivity().getBuy_number();
				//新购买数
				int newBuyNumber = ServletRequestUtils.getIntParameter(request, "product_"+cart.getCart().getProduct_id());
				if(newBuyNumber < 1) {
					newBuyNumber = number;
				}
				if(newBuyNumber > currentMax) {
					newBuyNumber = currentMax;
				}
				if(newBuyNumber != number && newBuyNumber <= currentMax) {
					//当前活动商品已满员
					if(cart.getProductActivity().isFull()) {
						ProductActivity pa = productActivityService.buildNextPeriod(cart.getProductActivity());
						if(pa != null) {
							orderService.updateCartNumber(u.getId(), pa.getProduct_id(), pa.getId(), newBuyNumber);
							cart.getCart().setNumber(newBuyNumber);
							cart.setProductActivity(pa);
						}
					} else {
						orderService.updateCartNumber(u.getId(), cart.getProduct().getId(), cart.getProductActivity().getId(), newBuyNumber);
						cart.getCart().setNumber(newBuyNumber);
					} 
				} 
				total+= cart.getCart().getNumber() * cart.getProductActivity().getSingle_price();
			}
			mv.addObject("userIncome",  userIncomeService.getUserIncome(u.getId()));
			mv.addObject("cartList",  cartList);
			mv.addObject("total",  total);
			mv.addObject("productNumber",  cartList.size());
		} else {
			mv.addObject("info", "宝贝");
			mv.addObject("description", "夺宝任务");
			mv.setViewName("/doubao/common/nodata.html.ftl");
		}
		 
		mv.addObject("title", "支付页面");
		return mv; 
	}


}
 */