package com.cyhd.web.action.doubao;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.cyhd.service.impl.QiniuService;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.ClientInfoUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.ProductShare;
import com.cyhd.service.impl.doubao.OrderProductService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.impl.doubao.ProductShareOrderService;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/doubao/share")
public class ShareAction extends BaseAction {
	@Resource
	ProductActivityService productActivityService;
	
	@Resource
	OrderProductService orderProductService;
	
	@Resource
	ProductShareOrderService productShareOrderService;

	@Resource
	QiniuService qiniuService;

	// 安卓app内支持上传图片的最低版本
	private static int MIN_APP_UPLOAD_VERSION = VersionUtil.getVersionCode("1.9.0");
	
	private static final String prefix = "doubao/web/share/";

	/**
	 * 商品往期晒单列表页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/history/{id:\\d+}.html", method = RequestMethod.GET)
    public ModelAndView historyshare(@PathVariable("id")int productId, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		 
		int total = productShareOrderService.countProductShareListByProductId(productId);
		mv.addObject("productId", productId);
		if(total > 0) {
			mv.setViewName(prefix + "list_new.html.ftl");
			int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
			int start = pageIndex * defaultPageSize;
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;

			mv.addObject("isProduct", true);
			mv.addObject("totalPage",totalPage);
			mv.addObject("productShare", productShareOrderService.getHistoryShareOrderList(productId, start, defaultPageSize));
		} else {
			mv.addObject("description", "参与活动");
			mv.setViewName("/common/nodata.html.ftl");
		}
		mv.addObject("title", "往期晒单");
		return mv;
	}

	/**
	 * 我的晒单列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/my.html", method = RequestMethod.GET)
    public ModelAndView my(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		User u = getUser(request);
		
		int total = productShareOrderService.countMyProductShare(u.getId());
		if(total > 0) {
			mv.setViewName(prefix + "list_new.html.ftl");
			int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
			int start = pageIndex * defaultPageSize;
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;

			mv.addObject("isMine", true);
			mv.addObject("totalPage",totalPage);
			mv.addObject("productShare", productShareOrderService.getMyShareOrderList(u.getId(), start, defaultPageSize));
		} else {
			mv.addObject("description", "参与活动");
			mv.setViewName("/common/nodata.html.ftl");
		}
		mv.addObject("back", true);
		mv.addObject("title", "我的晒单");
		return mv; 
	}
	/**
	 * 所有晒单列表
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/list.html", method = RequestMethod.GET)
    public ModelAndView list(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		int total = productShareOrderService.countShare();
		if(total > 0) {
			mv.setViewName(prefix + "list_new.html.ftl");
			int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 0);
			int start = pageIndex * defaultPageSize;
			int totalPage =  (total  +  defaultPageSize  - 1) / defaultPageSize;
			mv.addObject("totalPage",totalPage);
			mv.addObject("productShare", productShareOrderService.getProductShareList(start, defaultPageSize));
		} else {
			mv.addObject("description", "参与活动");
			mv.setViewName("/common/nodata.html.ftl");
		}
		mv.addObject("title", "晒单");
		return mv; 
	}

	/**
	 * 晒单详情页
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/{id:\\w+}.html", method = RequestMethod.GET)
    public ModelAndView share(@PathVariable("id")int id, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "detail.html.ftl");
		//Integer id = IdEncoder.decode(encodedId);
		//if(id == null) {
		//	throw ErrorCode.getParameterErrorException("参数错误!");
		//}
		User u = getUser(request);
		ProductShare productShare = productShareOrderService.getProductShare(id);
		mv.addObject("user", userService.getUserById(productShare.getUser_id()));
		mv.addObject("productShare", productShare);
		mv.addObject("productActivity", productActivityService.getProductActivityById(productShare.getProduct_activity_id()));
		mv.addObject("orderProduct", orderProductService.getOrderProductById(productShare.getOrder_product_id()));
		mv.addObject("userBuyNumber", orderProductService.countUserBuy(productShare.getUser_id(), productShare.getProduct_activity_id()));
		mv.addObject("title", "晒单详情");
		return mv; 
	}
	
	
	/**
	 * 添加晒单
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws CommonException 
	 */
	@RequestMapping(value = "/addshare.html", method = RequestMethod.GET)
    public ModelAndView add( HttpServletRequest request) throws CommonException {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "addshare.html.ftl");

		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		mv.addObject("ua", userAgent);

		User u = getUser(request);

		int orderProductId = ServletRequestUtils.getIntParameter(request, "orderProductId", 0);
		mv.addObject("orderProductId", orderProductId);
		mv.addObject("qiniuToken", qiniuService.getUploadToken());
		mv.addObject("user", u);

		boolean isShowUpload = false;
		if (userAgent.isInAppView()) {
			ClientInfo clientInfo = ClientInfoUtil.getClientInfo(request);
			String version = clientInfo.getAppVer();
			if (VersionUtil.getVersionCode(version) >= MIN_APP_UPLOAD_VERSION) {
				isShowUpload = true;
			}
		}else {
			isShowUpload = true;
		}
		mv.addObject("isShowUpload", isShowUpload);


		logger.error("orderProductId:{}", orderProductId);
		if(orderProductId <= 0) {
			logger.error("share order orderProductId is empty!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "缺少订单商品ID！");
			return mv;
		}
		OrderProduct op = orderProductService.getOrderProductById(orderProductId);
		if(op == null) {
			logger.error("share order OrderProduct:{} is null!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您的订单商品不存在！");
			return mv;
		}
		if(op.getUser_id() != u.getId()) {
			logger.error("share order user:{} not OrderProduct!", u.getId());
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您没有可晒单的商品！");
			return mv;
		}
		if(!op.isLottery()) {
			logger.error("share order orderProductId:{} not Lottery!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您的订单商品没有中奖！");
			return mv;
		}
		if(op.getShare() == 1) {
			logger.error("orderProductId:{} Have shared!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您的订单商品已经晒单！");
			return mv;
		}

		mv.addObject("title", "晒单");
		return mv; 
	}
}
