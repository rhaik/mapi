package com.cyhd.service.dao.po.doubao;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.common.util.TimePeriod;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.impl.doubao.ThirdShishicaiService;

public class ProductActivity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private int product_id;
	private int activity_rule_id;
	private String product_name; //商品名称
	private String product_lable; //副标题
	private int product_number;  //当前商品期数
	private int price;	//一次购买的单价，一元夺宝默认是1元，为100
	private int	vendor_price;  //商品的实际采购价格
	private int presell; //是否预售

	private Date start_time;  //开始时间
	private Date end_time; 		//结束时间
	private Date createtime; 	//创建时间
	private Date updatetime; 	//更新时间
	private Date expire_time;	//设置为过期的时间

	private int number; 	//所需购买数量
	private int buy_number; //用户已购买的数量
	private int min_buy_number;  //用户一次最少购买的数量，默认是1
	private Date finish_time;
	private int status;
	private int lottery_user; //夺宝成功的用户信息
	private int lottery_number; //夺宝成功的夺宝号
	private Date lottery_time; //夺宝成功的夺宝时间
	private int lottery_order_product; //夺宝成功的订单商品信息
	private long lottery_order; //对应的订单信息
	private int lottery_buy_number; //中奖用户的夺宝次数
	private int shishicai;  //对应哪一期的时时彩
	private int refund;			//是否已退款
	private Date refund_time;	//退款时间
	private int history_order;	//生成夺宝订单的时间
	private Date history_time;

	//活动列表排序类型
	public static final int TYPE_DEFAULT = 0; //进行中活动，按添加时间逆序
	public static final int TYPE_ANNOUNCING = 1; //揭晓中
	public static final int TYPE_LEASTRESIDUAL = 2; //最少
	public static final int TYPE_MAXPRICE = 3; //价高
	public static final int TYPE_MINPRICE = 4; //价低

	//活动的状态
	public static final int STATUS_NOSTART = 0; //未开始
	public static final int STATUS_DOING = 1; //进行中
	public static final int STATUS_ANNOUNCED = 2; //揭晓中
	public static final int STATUS_RESULT= 3; //已揭晓
	public static final int STATUS_EXPIRED= 5; //已过期
	
	public boolean isActivityTime(){
		Date now = new Date();
		return (now.after(start_time) && now.before(end_time));
	}
	public boolean isDoing(){
		return this.status == STATUS_DOING;
	}
	
	public boolean isDoingAnnounced(){
		Date now = new Date();
		return (this.status == ProductActivity.STATUS_DOING || this.status == ProductActivity.STATUS_ANNOUNCED) && (now.after(start_time) && now.before(end_time));
	}
	public boolean isAnnounced() {
		return this.status == ProductActivity.STATUS_ANNOUNCED;
	}
	public boolean isResult() {
		return this.status == ProductActivity.STATUS_RESULT;
	}

	public Date getLotteryTimes() {
		if (this.getShishicai() == 0) {
			return new Date(this.finish_time.getTime() + Constants.PRODUCT_ANNOUNCED_TIME);
		}else {
			Date shishicaiDate = ThirdShishicaiService.getShishicaiTime(this.getShishicai());
			return new Date(shishicaiDate.getTime() + Constants.minutes_millis * 5);
		}
	}
	public boolean isHaveHistoryOrder() {
		return history_order > 0;
	}
	/**
	 * 是否满员
	 * 
	 * @return
	 */
	public boolean isFull() {
		return this.getBuy_number() >= this.getNumber();
	}
	
	public boolean isCanBuyNumber(int number) {
		return (this.getBuy_number() + number) <= this.getNumber();
	}
	/**
	 * 是否允许购买最小数
	 * @return
	 */
	public boolean isSatisfyBuyMinNumber(int number) {
		return number >= this.getMin_buy_number();
	}
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public int getProduct_id() {
		return product_id;
	}


	public void setProduct_id(int product_id) {
		this.product_id = product_id;
	}

	public int getActivity_rule_id() {
		return activity_rule_id;
	}


	public void setActivity_rule_id(int activity_rule_id) {
		this.activity_rule_id = activity_rule_id;
	}

	public String getProduct_name() {
		return product_name;
	}


	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}


	public int getProduct_number() {
		return product_number;
	}


	public void setProduct_number(int product_number) {
		this.product_number = product_number;
	}
	
	public String getProduct_lable() {
		return product_lable;
	}
	public void setProduct_lable(String product_lable) {
		this.product_lable = product_lable;
	}

	public int getPrice() {
		return price;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}

	public int getPresell() {
		return presell;
	}


	public void setPresell(int presell) {
		this.presell = presell;
	}
	
	public Date getStart_time() {
		return start_time;
	}


	public void setStart_time(Date start_time) {
		this.start_time = start_time;
	}


	public Date getEnd_time() {
		return end_time;
	}


	public void setEnd_time(Date end_time) {
		this.end_time = end_time;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public Date getUpdatetime() {
		return updatetime;
	}


	public void setUpdatetime(Date updatetime) {
		this.updatetime = updatetime;
	}


	public int getBuy_number() {
		return buy_number;
	}


	public void setBuy_number(int buy_number) {
		this.buy_number = buy_number;
	}
	
	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public int getMin_buy_number() {
		return min_buy_number;
	}

	public void setMin_buy_number(int min_buy_number) {
		this.min_buy_number = min_buy_number;
	}

	public Date getFinish_time() {
		return finish_time;
	}


	public void setFinish_time(Date finish_time) {
		this.finish_time = finish_time;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public int getLottery_user() {
		return lottery_user;
	}


	public void setLottery_user(int lottery_user) {
		this.lottery_user = lottery_user;
	}


	public int getLottery_number() {
		return lottery_number;
	}


	public void setLottery_number(int lottery_number) {
		this.lottery_number = lottery_number;
	}


	public Date getLottery_time() {
		return lottery_time;
	}


	public void setLottery_time(Date lottery_time) {
		this.lottery_time = lottery_time;
	}


	public int getLottery_order_product() {
		return lottery_order_product;
	}


	public void setLottery_order_product(int lottery_order_product) {
		this.lottery_order_product = lottery_order_product;
	}
	public long getLottery_order() {
		return lottery_order;
	}


	public void setLottery_order(long lottery_order) {
		this.lottery_order = lottery_order;
	}
	
	public int getShishicai() {
		return shishicai;
	}
	public void setShishicai(int shishicai) {
		this.shishicai = shishicai;
	}

	public boolean isAllowPublished() {
		return price == buy_number;
	}
	

	public Date getExpire_time() {
		return expire_time;
	}
	public void setExpire_time(Date expire_time) {
		this.expire_time = expire_time;
	}
	public int getRefund() {
		return refund;
	}
	public void setRefund(int refund) {
		this.refund = refund;
	}
	public Date getRefund_time() {
		return refund_time;
	}
	public void setRefund_time(Date refund_time) {
		this.refund_time = refund_time;
	}
	
	public int getHistory_order() {
		return history_order;
	}
	public void setHistory_order(int history_order) {
		this.history_order = history_order;
	}
	public Date getHistory_time() {
		return history_time;
	}
	public void setHistory_time(Date history_time) {
		this.history_time = history_time;
	}


	public int getVendor_price() {
		return vendor_price;
	}

	public void setVendor_price(int vendor_price) {
		this.vendor_price = vendor_price;
	}

	public int getLottery_buy_number() {
		return lottery_buy_number;
	}

	public void setLottery_buy_number(int lottery_buy_number) {
		this.lottery_buy_number = lottery_buy_number;
	}

	/**
	 * 获取开始到现在有几分钟
	 * @return
	 */
	public String getBeforMinute() {
		return TimePeriod.beforeForSubscription(this.getLottery_time().getTime());
//		long total =  current.getTime() - this.userIncomeLog.getOperator_time().getTime();
//		int Minute = (int) (total / (60 *1000));
//		return Minute > 0 ? Minute : 1;
	}


	@Override
	public String toString() {
		return "ProductActivity{" +
				"id=" + id +
				", product_name='" + product_name + '\'' +
				", createtime=" + createtime +
				", number=" + number +
				", buy_number=" + buy_number +
				", status=" + status +
				", end_time=" + end_time +
				", lottery_user=" + lottery_user +
				", lottery_number=" + lottery_number +
				", refund=" + refund +
				", lottery_order_product=" + lottery_order_product +
				'}';
	}
}
