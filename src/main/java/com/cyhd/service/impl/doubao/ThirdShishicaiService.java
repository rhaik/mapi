package com.cyhd.service.impl.doubao;

 

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

import javax.annotation.Resource;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.structure.LRUCache;
import com.mongodb.util.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.dao.db.mapper.doubao.ThridShishicaiMapper;
import com.cyhd.service.dao.po.doubao.ThridShishicai;
import com.cyhd.service.impl.BaseService;


@Service
public class ThirdShishicaiService extends BaseService {

	private static Logger log = LoggerFactory.getLogger("shishicai");

	//默认开奖的延迟时间，目前是10分钟，也就是说达到开奖人数后，最快10分钟后才开奖，所以要延迟5分钟
	public final static int LOTTERY_DELAY = 5; //分钟

	//时时彩第一期，从00:05分点开始, 01:55结束，总共23期
	private final static int SHISHICAI_FIRST_HOR = 2;

	//时时彩上午开始的时间，10点钟开始，第一期为23期
	private final static int SHISHICAI_DAY_HOUR = 10;

	//时时彩晚上十点开始，每5分钟一期
	private final static int SHISHICAI_NIGHT_HOUR = 22;

	@Resource
	private ThridShishicaiMapper thridShishicaiMapper;


	/**
	 * 缓存10条时时彩的开奖时间，避免每次都计算
	 */
	private static LRUCache<Integer, Date> shishicaiDateCache = new LRUCache<>(5, 10);


	/**
	 * 抓取时时彩票数据，使用163和百度双保险
	 * @throws IOException 
	 */
	public void fetchShishicaiData() {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();

		//每天的前6分钟，需要获取一次前一天的
		if (calendar.get(Calendar.HOUR_OF_DAY) == 0 && calendar.get(Calendar.MINUTE) < 6){
			date = DateUtil.addDate(calendar.getTime(), -1);
		}

		Map<Integer, String> netMap = get163Shishicai(date);
		Map<Integer, String> baiduMap = getBaiduShishicai(date);

		//以163的数据为准，同时和百度的简单校验一下，如果不正确，暂时只打印日志
		for (int period : baiduMap.keySet()){
			String netValue = netMap.get(period);
			String baiduValue = baiduMap.get(period);

			if (StringUtil.isNotBlank(netValue) && StringUtil.isNotBlank(baiduValue) && !netValue.equals(baiduValue)){
				log.warn("shishicai lottery value error, period={}, 163={}, baidu={}", period, netValue, baiduValue);
			}

			//如果网易的数据不包含该期或者该期的数据为空，则用百度的数据
			if (!netMap.containsKey(period) || StringUtil.isBlank(netValue)){
				netMap.put(period, baiduValue);
			}
		}

		log.info("requested shishicai, result:{}", netMap);
		if(netMap.size() > 0) {
			this.saveToDB(netMap);
		}
	}


	/**
	 * 从网易彩票获取时时彩信息
	 */
	protected Map<Integer, String> get163Shishicai(Date date){
		Map<Integer,String> map = new HashMap<Integer,String>();

		String dateStr = DateUtil.format(date, "yyyyMMdd");
		String url = "http://caipiao.163.com/award/cqssc/"+dateStr+".html";
		try{
			Document doc = Jsoup.connect(url)
					.header("Referer", "http://caipiao.163.com/")
					.userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4")
					.timeout(5000)
					.get();

			Elements awardList = doc.select(".start");
			for (Element el : awardList) {
				//号码
				String number = el.attr("data-win-number");
				//期数
				String dataPeriod = el.attr("data-period");
				if(dataPeriod.isEmpty()) continue;

				int period = NumberUtil.safeParseInt(dataPeriod);
				if(number == null || number.isEmpty()) {
					map.put(period, "");
				} else {
					map.put(period, number.replaceAll(" ", ""));
				}
			}


			log.info("request 163 shishicai, url:{} result:{}", url, map.toString());

		} catch(Exception e) {
			log.error("request 163 shishicai, url:{} error:{}", url, e);
		}

		return map;
	}

	/**
	 * 从百度获取时时彩
	 * @return
	 */
	protected Map<Integer, String> getBaiduShishicai(Date date){
		Map<Integer,String> map = new HashMap<Integer,String>();

		String dateStr = DateUtil.format(date, "yyyy-MM-dd");
		String url = "http://baidu.lecai.com/lottery/draw/sorts/ajax_get_draw_data.php?lottery_type=200&date=" + dateStr;
		try{
			Connection.Response response= Jsoup.connect(url)
					.header("Referer", "http://baidu.lecai.com/lottery/draw/sorts/cqssc.php")
					.header("X-Requested-With", "XMLHttpRequest")
					.header("Accept", "application/json, text/javascript, */*; q=0.01")
					.userAgent("Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4")
					.timeout(5000)
					.ignoreContentType(true)
					.execute();

			String body = response.body();

			/*
			 * {
				"code": 0,
				"message": "",
				"data": {
					"rows": 58,
					"data": [
						{
							"phasetype": "200",
							"phase": "20160106058",
							"time_draw": "2016-01-06 15:41:00",
							"result": {
								"result": [
									{
										"key": "ball",
										"data": [
											"3",
											"5",
											"1",
											"5",
											"2"
										]
									}
								]
							},
							"ext": {
								"ten": "<span class=\"orange\">大</span>单",
								"unit": "小<span class=\"orange\">双</span>",
								"last": "组六"
							}
						},
						{
			 */
			JSONObject jsonObject = JSONObject.fromObject(body);
			if (jsonObject != null && jsonObject.optInt("code") == 0){
				JSONObject data = jsonObject.getJSONObject("data");
				if (data != null && data.has("data")){
					JSONArray dataArray = data.getJSONArray("data");

					for (int i = 0, len = dataArray.size(); i < len; ++ i){
						JSONObject item = dataArray.getJSONObject(i);

						//解析期数，去掉前面的20
						String periodStr = item.optString("phase");
						if (periodStr.startsWith("20")) {
							periodStr = periodStr.substring(2);
						}
						int period = NumberUtil.safeParseInt(periodStr);

						//解析中奖的号码
						StringBuilder sb = new StringBuilder();
						if (item.has("result") ){
							JSONObject result = item.getJSONObject("result");
							if (result != null && result.has("result")){
								JSONArray resultArray = result.getJSONArray("result");
								if (resultArray != null && resultArray.size() > 0){
									JSONObject dataObj = resultArray.getJSONObject(0);
									if (dataObj != null && dataObj.has("data")){
										JSONArray lotteryArray = dataObj.getJSONArray("data");
										for (int j = 0, size = lotteryArray.size(); j < size; j ++){
											sb.append(lotteryArray.getString(j));
										}
									}
								}

							}
						}
						if (period > 0){
							map.put(period, sb.toString());
						}
					}
				}
			}

			log.info("request baidu shishicai, url:{} result:{}", url, map);
		} catch(Exception e) {
			log.error("request url:{} error:{}", url, e);
		}

		return map;
	}

	/**
	 * 根据期数获取中奖号码
	 * @return
	 */
	public ThridShishicai getByPeriods(int periods) {
		return thridShishicaiMapper.getByPeriods(periods);
	}

	/**
	 * 获取最新一期未开奖的
	 * @return
	 */
	public ThridShishicai getNolotteryPeriods() {
		return thridShishicaiMapper.getNolotteryPeriods();
	}
	
	/**
	 * 获取今天所有的时时彩数据
	 * @return
	 */
	public HashMap<Integer, String> getList() {
		List<ThridShishicai> tssc = thridShishicaiMapper.getList();
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		for(ThridShishicai ts:tssc) {
			map.put(ts.getPeriods(), ts.getLottery_number());
		}
		return map;
	}

	/**
	 * 添加时时彩数据
	 * 
	 * @param id
	 * @return
	 */
	public boolean add(ThridShishicai shishicai) {
		return thridShishicaiMapper.add(shishicai) > 0;
	} 


	/**
	 * 更新时时彩数据
	 * 
	 * @return
	 */
	public boolean updateLotteryNumber(int periods, String lotteryNumber) {
		return thridShishicaiMapper.updateLotteryNumber(periods, lotteryNumber) > 0;
	} 

	/**
	 * 保存时时彩数据
	 * @param map
	 */
	public void saveToDB(Map<Integer,String> map) {
		for(Map.Entry<Integer,String> m : map.entrySet()) {
			ThridShishicai shishicai = new ThridShishicai();
			shishicai.setPeriods(m.getKey());
			shishicai.setLottery_number(m.getValue());
			shishicai.setPeriods(m.getKey());
			shishicai.setLottery_time(getShishicaiTime(m.getKey()));
			this.add(shishicai);
		}
	}

	/**
	 * 根据当前时间，获取最近一期待开奖的时时彩的期号<br/>
	 * 10:00-22:00（72期）10分钟一期，22:00-02:00（48期）5分钟一期<br/>
	 * @return
	 */
	public static int getNextPeriod() {
		return getShishicaiPeriod(GenerateDateUtil.getCurrentDate()) + 1;
	}


	/**
	 * 根据时时彩的period，获取其开奖时间
	 * @return
	 */
	public static Date getShishicaiTime(int period){
		Date shishicaiTime = shishicaiDateCache.get(period);
		if (shishicaiTime != null){
			return shishicaiTime;
		}


		String periodStr = String.valueOf(period);
		Calendar calendar = Calendar.getInstance();
		calendar.clear();

		int year = NumberUtil.safeParseInt("20" + periodStr.substring(0,2));
		calendar.set(Calendar.YEAR, year);

		int month = NumberUtil.safeParseInt(periodStr.substring(2,4));
		calendar.set(Calendar.MONTH, month -1);

		int day = NumberUtil.safeParseInt(periodStr.substring(4,6));
		calendar.set(Calendar.DATE, day);

		int seq = NumberUtil.safeParseInt(periodStr.substring(6));
		int minOfDay = 0;
		if (seq >= 96){
			minOfDay = 60 * SHISHICAI_NIGHT_HOUR + (seq - 96) * 5;
		}else if (seq >= 24){
			minOfDay = 60 * SHISHICAI_DAY_HOUR + (seq - 24) * 10;
		}else {
			minOfDay = seq * 5;
		}

		calendar.set(Calendar.HOUR_OF_DAY, minOfDay / 60);
		calendar.set(Calendar.MINUTE, minOfDay % 60);

		shishicaiTime = calendar.getTime();

		//加入缓存
		shishicaiDateCache.put(period, shishicaiTime);

		return shishicaiTime;
	}

	/**
	 * 根据给定时间，获取最近一期已开奖的时时彩的期号<br/>
	 * 10:00-22:00（72期）10分钟一期，22:00-02:00（48期）5分钟一期<br/>
	 * 期号：yyMMdd001这种，例如160106001
	 * @param date
	 * @return
	 */
	public static int getShishicaiPeriod(Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);

		int period = 1;
		int minOfDay = hour * 60 + min;

		//晚上5分钟一期
		if (hour >= SHISHICAI_NIGHT_HOUR){ //晚上22点开出第96期
			period = 96 + (minOfDay - (SHISHICAI_NIGHT_HOUR * 60)) / 5;
		}else if (hour >= SHISHICAI_DAY_HOUR){ //10点到22点之间的，10点钟开第24期
			period = 24 + (minOfDay - (SHISHICAI_DAY_HOUR * 60)) / 10;
		}else if (hour >= SHISHICAI_FIRST_HOR) { //都等到10点钟开启
			period = 23;
		}else { //hour < SHISHICAI_FIRST_HOR
			period = minOfDay / 5;
		}

		String dateStr = DateUtil.format(date, "yyMMdd");
		String periodStr = String.format("%s%03d", dateStr, period);

		return NumberUtil.safeParseInt(periodStr);
	}
}
