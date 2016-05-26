package com.cyhd.service.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// 退款工具类
public class DepositUtil {

	public static Map<String, String> alipayDepositFail = new HashMap<String, String>() ;
	static {
		alipayDepositFail.put("ACCOUN_NAME_NOT_MATCH", "支付宝姓名与支付宝账号不匹配") ;
		alipayDepositFail.put("ERROR_OTHER_NOT_REALNAMED", "支付宝账户尚未实名认证，无法收款") ;
		alipayDepositFail.put("ILLEGAL_USER_STATUS", "支付宝账户尚未实名认证，无法收款") ;
		alipayDepositFail.put("RECEIVE_USER_NOT_EXIST", "收款账户不存在") ;
	}
	
	public static Map<String, String> umpayDepositFail = new HashMap<String, String>() ;
	static {
		umpayDepositFail.put("商户未开通该产品", "收款账户不能为信用卡") ;
		umpayDepositFail.put("您输入的证件号、姓名或手机号有误(6630021)", "银行卡号或开户人姓名错误") ;
		umpayDepositFail.put("内部错误", "暂不支持提现到#{bankname}的储蓄卡") ;
		umpayDepositFail.put("您输入的卡号无效，请确认后输入(6130002)", "银行卡号错误") ;
		umpayDepositFail.put("账户属性与账号不匹配或卡bin不存在", "卡号有误或收款账号为对公账号（仅支持个人银行账号）") ;
	}
	
	private static final String bankname = "#{bankname}" ;
	
	public static String getReason(String reason, String bankname) {
		reason = DepositUtil.umpayDepositFail.get(reason) ;
		return reason.replace(DepositUtil.bankname, bankname);
	}
	
	public static Set<String> umpayDepositFailForTransferAgain = new HashSet<String>() ;
	static {
		umpayDepositFailForTransferAgain.add("对不起，该分行的该交易已经关闭，请稍候再做! 付款银行暂时关闭") ;
		umpayDepositFailForTransferAgain.add("无可用的支付通道 付款银行暂时关闭") ;
		umpayDepositFailForTransferAgain.add("连接服务器失败(12030)（如果该笔交易为帐务交易，则处理结果不确定，请先核对明细！）") ;
		umpayDepositFailForTransferAgain.add("支付超时") ;
		umpayDepositFailForTransferAgain.add("保存付款到银行卡请求前数据出错") ;
		umpayDepositFailForTransferAgain.add("余额不足，详情咨询4006125880") ;
		umpayDepositFailForTransferAgain.add("支付失败") ;
		umpayDepositFailForTransferAgain.add("交易失败") ;
		umpayDepositFailForTransferAgain.add("交易流水插入失败(01Z2002)") ;
		umpayDepositFailForTransferAgain.add("IO请求数据错误") ;
	}
}
