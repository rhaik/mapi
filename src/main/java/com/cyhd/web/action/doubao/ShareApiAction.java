package com.cyhd.web.action.doubao;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.util.UserAgentUtil;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.richtext.HtmlUtil;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.ProductShare;
import com.cyhd.service.impl.doubao.OrderProductService;
import com.cyhd.service.impl.doubao.ProductActivityService;
import com.cyhd.service.impl.doubao.ProductShareOrderService;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/doubao/api/share")
public class ShareApiAction extends BaseAction {
	@Resource
	ProductActivityService productActivityService;
	@Resource
	ProductShareOrderService productShareOrderService;
	
	@Resource
	OrderProductService orderProductService;
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;
	
	private static final String prefix = "doubao/api/share/";
	 
	/**
	 * 往期晒单
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/history", method = RequestMethod.GET)
	public ModelAndView historyshare(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "list.json.ftl");
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 1);
		int productId = ServletRequestUtils.getIntParameter(request, "productId", 0);
		
		int start = pageIndex * defaultPageSize;
		mv.addObject("productShare", productShareOrderService.getHistoryShareOrderList(productId, start, defaultPageSize));
		mv.addObject("title", "往期晒单");
		return mv; 
	}
	/**
	 * 我的晒单
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 * @throws CommonException 
	 */
	@RequestMapping(value = "/my", method = RequestMethod.GET)
	public ModelAndView my(HttpServletRequest request, HttpServletResponse response) throws CommonException {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "sharelist.json.ftl");
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 1);
		User u = getUser(request);
		
		int start = pageIndex * defaultPageSize;
		mv.addObject("productShare", productShareOrderService.getMyShareOrderList(u.getId(), start, defaultPageSize));
		mv.addObject("title", "我的晒单");
		return mv; 
	}
	
	/**
	 * 晒单列表
	 * 
	 * @param request
	 * @param response
	 * @return ModelAndView
	 * @throws CommonException 
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list(HttpServletRequest request, HttpServletResponse response) throws CommonException {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "list.json.ftl");
		int pageIndex = ServletRequestUtils.getIntParameter(request, "page", 1);
		
		int start = pageIndex * defaultPageSize;
		mv.addObject("productShare", productShareOrderService.getProductShareList(start, defaultPageSize));
		mv.addObject("title", "晒单");
		return mv; 
	}
	/**
	 * 添加晒单
	 * 
	 * @param request
	 * @return ModelAndView
	 * @throws CommonException 
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "/addshare", method = RequestMethod.POST)
	public ModelAndView addshare(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"upload.json.ftl");
		User u = getUser(request);
		ClientInfo clientInfo = getClientInfo(request);
		
		String title = HtmlUtil.toPlainText(ServletRequestUtils.getStringParameter(request, "title", ""));
		if(StringUtil.isBlank(title)) {
			logger.error("share order title is empty!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "请填写晒单内容！");
			return mv;
		}
		title = URLDecoder.decode(title, "utf-8");

		int orderProductId = ServletRequestUtils.getIntParameter(request, "orderProductId", 0);
		if(orderProductId <= 0) {
			logger.error("share order orderProductId is empty!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "缺少订单商品ID！");
			return mv;
		}

		String imagesStr = null;
		UserAgentUtil.UserAgent userAgent = UserAgentUtil.getUserAgent(request);
		String images = request.getParameter("images");
		if (StringUtil.isNotBlank(images)) {
			images = URLDecoder.decode(images, "utf-8");

			//参数中的图片以逗号分隔
			String[] imageArray = images.split(",");
			List<String> imgList = Arrays.asList(imageArray);

			if (!imgList.isEmpty()) {
				imagesStr = JSONArray.fromObject(imgList).toString();
			}
		}

		if (!userAgent.isInAppView() && StringUtil.isBlank(imagesStr)){
			logger.error("no share image, order:{}", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "请上传晒单图片！");
			return mv;
		}


		OrderProduct op = orderProductService.getOrderProductById(orderProductId);
		if(op == null) {
			logger.error("share order OrderProduct:{} is null!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品不存在！");
			return mv;
		}
		if(op.getUser_id() != u.getId()) {
			logger.error("share order user:{} not OrderProduct!", u.getId());
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您没有可晒单的商品！");
			return mv;
		}
		if(!op.isLottery()) {
			logger.error("share order orderProductId:{} not Lottery!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品没有中奖！");
			return mv;
		}
		if(op.getShare() == 1) {
			logger.error("orderProductId:{} Have shared!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品已经晒单！");
			return mv;
		}

		
		ProductShare productShare = new ProductShare();
		productShare.setOrder_sn(op.getOrder_sn());
		productShare.setCreatetime(new Date());
		productShare.setImages(imagesStr);
		productShare.setOrder_product_id(op.getId());
		productShare.setProduct_activity_id(op.getProduct_activity_id());
		productShare.setProduct_id(op.getProduct_id());
		productShare.setStatus(ProductShare.STATUS_WAIT_AUDIT);
		productShare.setTitle(title);
		productShare.setUser_id(u.getId());
		if(productShareOrderService.add(productShare) && orderProductService.updateShare(op.getId(), op.getUser_id())) {
			productShareOrderService.rewardUserAfterShare(u.getId(), orderProductId,clientInfo.getPlatform());
			this.fillStatus(mv);
		} else {
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "保存晒单失败!");
		}
    	return mv;
	}
	/**
	 * 晒单图片上传
	 * 
	 * @param request
	 * @return ModelAndView
	 * @throws CommonException 
	 * @throws ServletRequestBindingException 
	 */
	@RequestMapping(value = "/upload")
	public ModelAndView upload(@RequestParam(value = "file", required = false) MultipartFile[] files, HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"upload.json.ftl");
		User u = getUser(request);
		
		int orderProductId = ServletRequestUtils.getIntParameter(request, "orderProductId", 0);
		if(orderProductId <= 0) {
			logger.error("share order orderProductId is empty!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "缺少订单商品ID！");
			return mv;
		}
		OrderProduct op = orderProductService.getOrderProductById(orderProductId);
		if(op == null) {
			logger.error("share order OrderProduct:{} is null!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品不存在！");
			return mv;
		}
		if(op.getUser_id() != u.getId()) {
			logger.error("share order user:{} not OrderProduct!", u.getId());
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "您没有可晒单的商品！");
			return mv;
		}
		if(!op.isLottery()) {
			logger.error("share order orderProductId:{} not Lottery!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品没有中奖！");
			return mv;
		}
		if(op.getShare() == 1) {
			logger.error("orderProductId:{} Have shared!", orderProductId);
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "订单商品已经晒单！");
			return mv;
		}
		
		// 判断file数组不能为空并且长度大于0
		ArrayList<String> urls = new ArrayList<String>();
		if (files != null && files.length > 0) {
			// 循环获取file数组中得文件
			for (int i = 0; i < files.length; i++) {
				MultipartFile file = files[i];
				// 保存文件
				String url = productShareOrderService.uploadFileToQiniu(file, u);
				if(!url.isEmpty()) {
					urls.add(url);
				}
			}
		} else {
			logger.error("share order images is empty!");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "请选择上传的晒单图片！");
			return mv;
		}
		mv.addObject("urls", urls);
		return mv;
	}
}
