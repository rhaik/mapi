package com.cyhd.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserIncomeMapper;
import com.cyhd.service.dao.po.TopUser;
import com.cyhd.service.vo.TopUserVo;


@Service
public class UserRankService extends BaseService {

	@Resource
	private UserService userService;
	
	@Resource
	private UserIncomeMapper userIncomeMapper;
	
	private List<TopUser> rankTopUsers = new ArrayList<TopUser>();
	private List<TopUser> monthRankTopUsers = new ArrayList<TopUser>();
	
	private List<TopUserVo> allTopUserCache = new ArrayList<TopUserVo>();
	
	private List<TopUserVo> monthTopUserCache = new ArrayList<TopUserVo>();
	
	private static final int rank_size = 10;
	
	private static final int  size = 10;
	
	private static final String all_tops = "all_tops";
	
	private static String month_tops = "month_top";
	
	private SimpleDateFormat monthFormat = new SimpleDateFormat("yyyyMM");
	
	private volatile boolean loading = false;
	
	private static float[] rates= {0.2f, 0.25f, 0.30f, 0.40f, 0.45f, 0.50f, 0.62f, 0.70f, 0.75f, 0.80f};
	private static int[] income = {};
	/**加上默认的基数*/
	private int base_income = 6000;
	
	public int total_base_income = 980000;
	
	public void reload(){
		if(loading)
			return;
		try{
			logger.info("reload user rank runing.............");
			loading = true;
			List<TopUser> topUsers = userIncomeMapper.getTopUsers(rank_size);
			
			Date time = DateUtil.getNowMonthBeginDate();
			List<TopUser> monthTopUsers = userIncomeMapper.getTimeTopUsers(time, rank_size);
			List<TopUser> realMonthTopUsers = new ArrayList<>();
			for(TopUser topUser:monthTopUsers){
				TopUser topUserTmp = new TopUser();
				topUserTmp.setIncome(topUser.getIncome());
				topUserTmp.setUser_id(topUser.getUser_id());
				realMonthTopUsers.add(topUserTmp);
			}
			List<TopUserVo> allTopUserCache_temp = new ArrayList<TopUserVo>();
			
			float rate = 3.1f;
			
			List<TopUser> topTmp = new ArrayList<TopUser>(rank_size);
			int index = 0;
			for(int i = topUsers.size()-1 ; i >=0 ; i--){
				TopUserVo vo = new TopUserVo();
				int sum = 0;
				int addition = 0;
				//防止前后的差额一样的
				if( i % 2 == 0){
					addition = ((i)*14);
				}else if(i % 3 == 0){
					addition = ((i)*15);
				}else{
					addition = ((i)*16);;
				}
				int income = topUsers.get(rank_size -i-1).getIncome();
				sum = (int)(income *rate) + addition + (int)(total_base_income * (rates[i]));

				vo.setU(userService.getUserById(Constants.defaultUserIds.get(index)));
				vo.setIncome(MoneyUtils.fen2yuanS(sum));
				allTopUserCache_temp.add(vo);
				
				TopUser tmpvo = new TopUser();
				tmpvo.setIncome(sum);
				tmpvo.setUser_id(Constants.defaultUserIds.get(i));
				topTmp.add(tmpvo);
				
				if(allTopUserCache_temp.size() >= size){
					break;
				}
				index++;
			}
			
			List<TopUserVo> monthTopUserCache_temp = new ArrayList<TopUserVo>();
			index = 0;
			int total = monthTopUsers.size();
			List<Integer> topAmounts = new ArrayList<>(10);
			List<Integer> topRankUsers = new ArrayList<>(10);
			for(TopUser tu : monthTopUsers){
				TopUserVo vo = new TopUserVo();
				//替换成默认的用户
				vo.setU(userService.getUserById(Constants.monthUserIds.get(index)));
				tu.setUser_id(vo.getU().getId());
				//收入加上默认基数
				int amount = (int)(tu.getIncome() * rate)+ (int)(base_income *rates[total - index-1]);
				vo.setAmount(amount);
				vo.setIncome(MoneyUtils.fen2yuanS(amount));
				monthTopUserCache_temp.add(vo);
				topAmounts.add(amount);
				topRankUsers.add(vo.getU().getId());
				if(monthTopUserCache_temp.size() >= size){
					break;
				}
				index++;
			}
			this.rankTopUsers = topTmp;
			this.allTopUserCache = allTopUserCache_temp;
			
			//再一次算月的top
			boolean loop = false;
			for(TopUser tu : realMonthTopUsers){
				//如果排行榜里面已经存在那就跳过
				if(monthTopUsers.contains(tu.getUser_id())){
					continue;
				}
				if(loop){
					break;
				}
				//如果比最后的值都要小的话 直接break 
				for(int i = topAmounts.size()-1; i >=0 ; i--){
					if(tu.getIncome() < topAmounts.get(i)){
						if(i >= topAmounts.size()-1){
							loop = true;
							break;
						}
						TopUserVo vo = new TopUserVo();
						vo.setU(userService.getUserById(tu.getUser_id()));
						vo.setIncome(MoneyUtils.fen2yuanS(tu.getIncome()));
						
						monthTopUsers.add(i+1, tu);
						topAmounts.add(i+1,tu.getIncome());
						monthTopUserCache_temp.add(i+1, vo);
						//移除最后一位
						topAmounts.remove(topAmounts.size()-1);
						break;
					}
				}
			}
			
			if(monthTopUserCache_temp.size() > rank_size){
				monthTopUserCache_temp = monthTopUserCache_temp.subList(0, rank_size);
				monthTopUsers = monthTopUsers.subList(0, rank_size);
			}
			this.monthRankTopUsers = monthTopUsers;
			this.monthTopUserCache = monthTopUserCache_temp;
		}finally{
			loading = false;
			logger.info("reload user rank end.............");
		}
	}
	
//	private String getMonthRankName(){
//		return this.month_tops + monthFormat.format(new Date());
//	}
//	
	public List<TopUserVo> getAllTopUsers() {
		return allTopUserCache;
	}

	public List<TopUserVo> getMonthTopUsers() {
		return monthTopUserCache;
	}

	public int getUserAllRank(int userId){
		int rank = -1;
		for(int i = 0; i < rankTopUsers.size(); i ++){
			if(rankTopUsers.get(i).getUser_id() == userId){
				rank = i;
				break;
			}
		}
		return rank;
	}
	
	public int getUserMonthRank(int userId){
		int rank = -1;
		for(int i = 0; i < monthRankTopUsers.size(); i ++){
			if(monthRankTopUsers.get(i).getUser_id() == userId){
				rank = i;
				break;
			}
		}
		return rank;
	}
	
	public static void main(String[] args) {
		List<Integer> rank = new ArrayList<>();
		rank.add(60);
		rank.add(55);
		rank.add(50);
		rank.add(45);
		rank.add(40);
		rank.add(35);
		rank.add(30);
		rank.add(25);
		rank.add(20);
		rank.add(15);
		rank.add(5);
		int size = rank.size();
		System.out.println(rank);
		TreeSet<Integer> rankSet = new TreeSet<>(new Comparator<Integer>(){

			@Override
			public int compare(Integer o1, Integer o2) {
				
				return o2 -o1;
			}
			
		});
		rankSet.addAll(rank);
		List<Integer> tmp = new ArrayList<>();
		tmp.add(46);
		tmp.add(36);
		tmp.add(26);
		tmp.add(16);
		tmp.add(4);
		tmp.add(3);
		rankSet.addAll(tmp);
		
		System.out.println("-----------------");
		System.err.println(rankSet);
		boolean loop = false;
		for(int j:tmp){
			if(loop){
				break;
			}
			for(int i =  rank.size()-1; i > 0; i--){
				if(j < rank.get(i)){
					if(i == rank.size() - 1){
						loop = true;
						break;
					}
					rank.add(i+1, j);
					rank.remove(rank.size()-1);
					break;
				}
			}
		}
		
		System.out.println("rank -> "+rank);
	}
}
