package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Resource;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.util.IdEncoder;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserArticleTaskMapper;
import com.cyhd.service.dao.po.Account;
import com.cyhd.service.dao.po.TransArticle;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserArticleMessage;
import com.cyhd.service.dao.po.UserArticleTask;
import com.cyhd.service.vo.UserArticleTaskVo;

@Service
public class UserArticleTaskService extends BaseService {

	@Resource  
	private UserArticleTaskMapper userArticleTaskMapper;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource
	private IdMakerService idMakerService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private BeginnerService beginnerService;
	
	@Resource
	private UserService userService;
	
	private volatile boolean loading = false;
	
	//private ConcurrentLRUCache<Integer, UserArticleTask> lruCache = new ConcurrentLRUCache<Integer, UserArticleTask>(row, col, false);
	
//	@Resource(name=RedisUtil.NAME_SELF)
//	private IJedisDao userarticleCache;

	public long addTask(int article_id,int user_id,int article_task_id,String idfa,String did,int clientType,int wx_account_id, String ip){
		//TODO 检查是不是分享过 
		//TODO 检查还可以分享么 任务过期没有
		//TODO 任务达到上限没有
		
		TransArticleTask transArticleTask = transArticleTaskService.getTransArticleTask(article_task_id);
		if(transArticleTask.isValid() == false){
			return -1;
		}
		
		UserArticleTask userArticleTask = userArticleTaskMapper.getUserArticleTask(user_id, article_task_id);
		if(userArticleTask != null){
			String wx_accounts = userArticleTask.getWx_account_id();
			String[] ids =  wx_accounts.split(",");
			for(String id:ids){
				if(id.equals(wx_accounts)){
					return userArticleTask.getId();
				}
			}
			//没有过 改微信公众号 入库
			boolean ret =  userArticleTaskMapper.addWX_account_id(userArticleTask.getId(),wx_account_id) > 0;
			if(ret){
				//TODO 分享成功后的一些操作  
				//
			}
			return userArticleTask.getId();
		}
		
		Date now = new Date();
		userArticleTask = new UserArticleTask();
		userArticleTask.setArticle_id(transArticleTask.getArticle_id());
		userArticleTask.setArticle_task_id(article_task_id);
		userArticleTask.setSharetime(now);
		userArticleTask.setClient_type(clientType);
		userArticleTask.setDid(did);
		userArticleTask.setIdfa(idfa);
		userArticleTask.setExpiretime(transArticleTask.getEnd_time());
		userArticleTask.setStarttime(now);
		userArticleTask.setId(idMakerService.getTimedId());
		userArticleTask.setWx_account_id(wx_account_id+"");
		userArticleTask.setUser_id(user_id);
		userArticleTask.setStatus(UserArticleTask.STATUS_INIT);
		userArticleTask.setUser_ip(ip);
		
		boolean ret = false;
		if(article_id < 10){
			ret = transArticleTaskService.updateReceiveNumBySysTask(article_task_id);
		}else{
			ret = transArticleTaskService.updateReceiveNum(article_task_id);
		}
		//小于10 的留作系统任务
		if(ret){
			//响应的任务减一
			if(userArticleTaskMapper.addTask(userArticleTask ) > 0){
				//系统任务
				if(transArticleTask.getArticle_id() < 10){
					beginnerService.addTranArticleTaskReward(userService.getUserById(user_id), transArticleTask);
					return userArticleTask.getId();
				}
				
				String amount_des = "";
				if(transArticleTask.getRaward_type_zero() == 0){
					amount_des = Integer.toString(transArticleTask.getAmount());
				}else{
					amount_des = MoneyUtils.fen2yuanS(transArticleTask.getRaward_type_zero())+"~"+MoneyUtils.fen2yuanS(transArticleTask.getMaxAmount());
				}
				//分享成功后的一些操作 第一次才发 
				userMessageService.addFirstTranArticleMessage(user_id, article_task_id, userArticleTask.getId(), clientType, transArticleTask.getAmount(), transArticleTask.getView_num(),
					transArticleTask.getName(),userArticleTask.getExpiretime(),UserArticleMessage.MESS_TYPE_START,amount_des);
				return userArticleTask.getId();
			}else{
				return -1;
			}
		}
		return -1;
	}
	
	public List<UserArticleTaskVo> getTasks(int user_id, int type){
		List<UserArticleTaskVo> tasks = transArticleTaskService.getValidUserArticleTaskVos(type);
		if(tasks == null || tasks.isEmpty() == true){
			return  new ArrayList<UserArticleTaskVo>();
		}
		
		List<Integer> articleIds = new ArrayList<Integer>();
		
		for(UserArticleTaskVo vo:tasks){
			articleIds.add(vo.getTransArticle().getId());
		}
		
		List<UserArticleTask> userTasks = userArticleTaskMapper.getUserArticleTasksByArticleIds(user_id, articleIds);
		
		List<UserArticleTaskVo> receiveds = new ArrayList<UserArticleTaskVo>();
		List<UserArticleTaskVo> canReceives = new ArrayList<UserArticleTaskVo>();
		List<UserArticleTaskVo> others = new ArrayList<UserArticleTaskVo>();
		List<UserArticleTaskVo> reward = new ArrayList<UserArticleTaskVo>();
		
		for(UserArticleTaskVo vo:tasks){
			UserArticleTask task = fillArticleTask(user_id,vo.getTransArticle().getId(),userTasks);
			
			vo.setUserArticleTask(task);
			
			if(vo.isCanReceive()){
				canReceives.add(vo);
			}else if(vo.isProcessing()){
				vo.setReceived(true);
				receiveds.add(vo);
			}else if(vo.isCompleted()){
				reward.add(vo);
			}else{
				others.add(vo);
			}
		}
		canReceives.addAll(receiveds);
		canReceives.addAll(reward);
		canReceives.addAll(others);
		return canReceives;
	}

//	public List<UserArticleTask> getUserAllTask(int user_id,List<Integer> taskIds){
//		return userArticleTaskMapper.getUserArticleTasksByArticleIds(user_id, taskIds);
//	}
	
	public UserArticleTask getArticleTask(int userId,int taskId){
		return userArticleTaskMapper.getUserArticleTask(userId, taskId);
	}

	public UserArticleTask getTaskByArticleId(int userId, int articleId){
		return userArticleTaskMapper.getUserArticleTaskByArticleId(userId, articleId);
	}

	public UserArticleTask getUserArticleTaskById(long id){
		return userArticleTaskMapper.getUserArticleTaskById(id);
	}

	/**
	 * 添加一个阅读人数
	 * @param user_id
	 * @param task_id
	 * @return
	 */
	public boolean addViewNum(int user_id,long task_id){
		return userArticleTaskMapper.addViewNum(task_id, user_id) > 0;
	}
	
	public boolean onReward(long id,int userId){
		boolean ret = userArticleTaskMapper.finishTaskAndReward(id, userId) > 0;
		if(ret ){
			//缓存中不需要他这么多数据 作显示用
			//addUserFinshTaskToCache(userArticleTask);
		}
		return ret;
	}
	private UserArticleTask fillArticleTask(int user_id, int articleId,List<UserArticleTask> articleTasks) {
		if(articleTasks == null || articleTasks.isEmpty() == true){
			return null;
		}
		for(UserArticleTask task:articleTasks){
			if(task.getUser_id() == user_id && articleId== task.getArticle_id()){
				return task;
			}
		}
		return null;
	}
	
	public int getFinshTotal(int user_id){
		return userArticleTaskMapper.getUserArticleTaskTotal(user_id);
	}
	/**
	 * 加入redis
	 */
	private void addUserFinshTaskToCache(List<UserArticleTask> data){
		Map<Object, Double> scoreMembers = new HashMap<Object, Double>(data.size());
		for(UserArticleTask task:data){
			double j = task.getId();
			scoreMembers.put(task, j);
		}
		try {
			//userarticleCache.zaddObj(RedisUtil.builUserArticleLogKey(data.get(0).getUser_id()), scoreMembers );
		} catch (Exception e) {
			logger.info("添加用户完成入redis:{}",e);
		}
	}
	private void addUserFinshTaskToCache(UserArticleTask data){
		Map<Object, Double> scoreMembers = new HashMap<Object, Double>(1);
		double j = data.getId();
		scoreMembers.put(data, j);
		try {
			//userarticleCache.zaddObj(RedisUtil.builUserArticleLogKey(data.getUser_id()), scoreMembers );
		} catch (Exception e) {
			logger.info("添加用户完成入redis:{}",e);
		}
	}
	private List<UserArticleTask> selectUserFinishTaskLogs(int user_id,int start,int size){
		Set<Object> cacheData = null;
		List<UserArticleTask> data = null;
		//放不放去cache 
		try {
			//cacheData = userarticleCache.zrevrangeObj(RedisUtil.builUserArticleLogKey(user_id), start, start+size-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		if(cacheData == null || cacheData.isEmpty()){
			data =  userArticleTaskMapper.getUserFinishTaskLogs(user_id, start, size);
			if(data != null && data.isEmpty() == false){
				//放入cache
				//addUserFinshTaskToCache(data );
			}
		}
		return data;
	}
	public List<UserArticleTaskVo> getUserArtricleLog(int user_id,int start,int size){
		List<UserArticleTaskVo> voList = new ArrayList<UserArticleTaskVo>();
		String showDate = "";
		List<UserArticleTask> data = this.selectUserFinishTaskLogs(user_id, start, size);
		for(UserArticleTask task:data){
			UserArticleTaskVo e = new UserArticleTaskVo();
			String tmp = DateUtil.format(task.getStarttime(), "yyyyMMdd");
			if(showDate.isEmpty()){
				showDate = tmp;
				e.setDisplayDate(true);
			}else if(showDate.equals(tmp) == false){
				if(voList.size() > 0){
					showDate = tmp;
					e.setDisplayDate(true);
				}
			}
			e.setUserArticleTask(task);
			e.setTransArticleTask(transArticleTaskService.getTransArticleTask(task.getArticle_task_id()));
			e.setTransArticle(transArticleTaskService.getTransArticle(task.getArticle_id()));
			voList.add(e );
		}
		return voList;
	}
	
	public List<UserArticleTask> getUserArticleTasksToCloseAccount(int taskId, long start,int size){
		return userArticleTaskMapper.getUserArticleTasksToCloseAccount(taskId, start, size);
	}

	public int setExpired(long taskId){
		return userArticleTaskMapper.setExpired(taskId);
	}

	public void NotifyNearExpireTask(){
		if(loading){
			return;
		}
		loading = true;
		logger.info("通知转发任务快过期的通知开始");
		int expiredTime = (Constants.ARTICLE_DEFAULT_EXPRIE -10*60);
		TransArticleTask task = null;
		List<UserArticleTask> list =  null;
		int start = 0;
		int size = 100;
		while(true){
			try{
				list =  userArticleTaskMapper.getExpireTask(expiredTime,start,size);
				if(list == null || list.isEmpty()){
					break;
				}
				int userId = 0;
				for(UserArticleTask userArticleTask:list){
					try{
						userId = userArticleTask.getUser_id();
						task = transArticleTaskService.getTransArticleTask(userArticleTask.getArticle_task_id());
						//只通知你一次 
						if(userArticleTaskMapper.updateWillExpire(userArticleTask.getId(), userId) > 0){
							userMessageService.notifyTranArticleMessage(userId, task.getName(),
									task.getAmount(), userArticleTask.getView_num(), userArticleTask.getClient_type(), UserArticleMessage.MESS_TYPE_WILL_EXPRIED,task.getId(),userArticleTask.getId());
							if(logger.isDebugEnabled()){
								logger.debug("通知用户，任务快过期:{}",userId);
							}
						}
					}catch(Exception e){
						logger.info("转发任务中,通知,userid：{}，cause by:{}",userId,e);
					}
				}
				if(list.size() < size){
					break;
				}
				start +=size;
				}catch(Exception e){
					logger.error("转发任务中,获取数据出现异常:{}",e);
				}
		}
		loading = false;
	}
	
	private UserArticleTaskVo assemblay(UserArticleTask task ,String showDate,List<UserArticleTaskVo> voList ){
		UserArticleTaskVo e = new UserArticleTaskVo();
		String tmp = DateUtil.format(task.getStarttime(), "yyyyMMdd");
		if(showDate.isEmpty()){
			showDate = tmp;
			e.setDisplayDate(true);
		}else if(showDate.equals(tmp) == false){
			if(voList.size() > 0){
				showDate = tmp;
				e.setDisplayDate(true);
			}
		}
		e.setUserArticleTask(task);
		e.setTransArticleTask(transArticleTaskService.getTransArticleTask(task.getArticle_task_id()));
		e.setTransArticle(transArticleTaskService.getTransArticle(task.getArticle_id()));
		return e;
	}

	/**
	 * 获取用户的转发任务VO对象
	 * @param userId
	 * @param taskId
	 * @return
	 */
	public UserArticleTaskVo getUserArticleTaskVo(int userId, int taskId){
		UserArticleTaskVo vo = new UserArticleTaskVo();
		TransArticleTask task = transArticleTaskService.getTransArticleTask(taskId);
		vo.setTransArticleTask(task);
		TransArticle article = transArticleTaskService.getTransArticle(task.getArticle_id());

		//微信文章才获取阅读数
		if (task.getType() == TransArticleTask.TYPE_WEIXIN) {
			int readNum = transArticleTaskService.getReadNum(article.getId());
			article.setRealReadNum(readNum);
		}
		vo.setTransArticle(article);

		if (userId > 0) {
			vo.setUserArticleTask(getTaskByArticleId(userId, task.getArticle_id()));
		}
		return vo;
	}

	/**
	 * 获取加密后的页面地址
	 * @return
	 */
	public static String getShareUrl(User u, Account wxAccount, UserArticleTaskVo vo){
		String wxId = IdEncoder.encode(wxAccount.getId());
		String host = wxAccount.getHost();

		if(!host.endsWith("/")){
			host = host + "/";
		}

		String aid = IdEncoder.encode(vo.getTransArticle().getId());
		String tid = vo.getTransArticleTask().getEncodedId();

		StringBuilder sharePraameters = new StringBuilder(320);
		sharePraameters.append(host);

		sharePraameters.append("wec" + getRandomValue() + "/chc"+ getRandomValue() +"/").append(tid).append("?");

		//这里的u_id是用户标识
		sharePraameters.append("u_id=").append(u.getUser_identity());
		sharePraameters.append("&a_id=").append(aid);

		//如果是微信里的分享文章，文章地址是在这里拼好的
		String articleUrl = vo.getTransArticle().getUrl();
		if (vo.getTransArticleTask().getType() == TransArticleTask.TYPE_WEIXIN){
			articleUrl = host + "open/article/" + tid;
		}

		//测试中发现总体加密 导致失败 ,所以只对分享地址加密 防止地址被恶意使用
		sharePraameters.append("&url=").append(AesCryptUtil.encrypt(articleUrl, Constants.ARTICLE_AES_PASSWORD));
		sharePraameters.append("&w_id=").append(wxId);

		return sharePraameters.toString();
	}
	
	public String getShareUrlByNewUserTask(User u,Account wxAccount,UserArticleTaskVo vo){
		String wxId = IdEncoder.encode(wxAccount.getId());
		String host = wxAccount.getHost();

		if(!host.endsWith("/")){
			host = host + "/";
		}
		StringBuilder sharePraameters = new StringBuilder(320);
		sharePraameters.append(host);
		sharePraameters.append(vo.getTransArticle().getUrl());
		sharePraameters.append("?invite=").append(AesCryptUtil.encryptWithCaseInsensitive(String.valueOf(u.getUser_identity()),Constants.ARTICLE_AES_PASSWORD));
		sharePraameters.append("&wxId=").append(wxId);
		return sharePraameters.toString();
	}
	
	static Random rand = new Random();
	
	private static int getRandomValue(){
		return 100000 + rand.nextInt(99999999);
	}

	/**
	 * 获取分享的页面数据
	 * @param u
	 * @param wxAccount
	 * @param vo
	 * @return
	 */
	public static String getShareData(User u, Account wxAccount, UserArticleTaskVo vo){
		//将关键参数拼接加密 之后解密比对 不匹配 不处理
		StringBuilder sb = new StringBuilder(500);
		String a_id = IdEncoder.encode(vo.getTransArticle().getId());

		sb.append("u_id=").append(IdEncoder.encode(u.getId())).append("&");
		sb.append("u_iden=").append(u.getUser_identity()).append("&");
		sb.append("t_id=").append(vo.getTransArticleTask().getEncodedId()).append("&");
		sb.append("a_id=").append(a_id).append("&");
		sb.append("w_id=").append(IdEncoder.encode(wxAccount.getId()));

		//将数据段加密  不使用 客户端一样的工具类
		return AesCryptUtil.encrypt(sb.toString(), Constants.ARTICLE_AES_PASSWORD);
	}

	public UserArticleTask getUserLastTask(int userId){
		return userArticleTaskMapper.getUserLasttask(userId);
	}

	/**
	 * 加密用户的userTaskId
	 * @param userTaskId
	 * @return
	 */
	public static String encyptUserTaskId(String host, long userTaskId){
		return AesCryptUtil.encryptWithCaseInsensitive(getHostSign(host) + new Random().nextInt(100) + "-" + userTaskId, Constants.ARTICLE_AES_PASSWORD);
	}

	/**
	 * 微信分享链接中用户任务id加密
	 * @param userTaskId
	 * @return
	 */
	public static String encyptViewTaskId(String host, long userTaskId){
		return AesCryptUtil.encryptWithCaseInsensitive(getHostSign(host) + new Random().nextInt(100) + "-" + userTaskId, Constants.ARTICLE_VIEW_PASSWORD);
	}


	/**
	 * 解析用户的用户任务id
	 * @param encypt
	 * @return
	 */
	public static long decryptUserTaskId(String host, String encypt){
		return decrypeTaskId(host, encypt, Constants.ARTICLE_AES_PASSWORD);
	}

	/**
	 * 解析微信分享后的用户任务id
	 * @param encypt
	 * @return
	 */
	public static long decryptViewTaskId(String host, String encypt){
		return decrypeTaskId(host, encypt, Constants.ARTICLE_VIEW_PASSWORD);
	}

	/**
	 * 根据host做解密，解密后的字符串必须以host的标签开头
	 * @param host
	 * @param encypt
	 * @param pass
	 * @return
	 */
	private static long decrypeTaskId(String host, String encypt, String pass){
		String decypted = AesCryptUtil.decryptWithCaseInsensitive(encypt, pass);

		if (decypted != null && decypted.startsWith(getHostSign(host)) && decypted.indexOf('-') > 0){
			return NumberUtil.safeParseLong(decypted.substring(decypted.indexOf('-') + 1));
		}
		return 0;
	}

	/**
	 * 获取Host的标签
	 * @param host
	 * @return
	 */
	private static String getHostSign(String host){
		if (host == null){
			return "";
		}
		host = host.toLowerCase().replace("http://", "").replace("https://", "").replaceAll("\\.", "");

		if (host.length() < 5){
			return host;
		}

		StringBuilder sign = new StringBuilder();
		sign.append(host.charAt((host.length() - 1) / 2));
		sign.append(host.charAt((host.length() - 3) / 2));
		sign.append(host.charAt((host.length() - 5 ) / 2));
		sign.append(host.charAt(host.length() -1));

		return sign.toString();
	}

	public boolean setOpened(int userId, int articleId) {
		return userArticleTaskMapper.setArticleOpened(userId, articleId) > 0;
	}
}

class TaskUserId{
	
	private int userId;
	private int articleId;
	private int accountId;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	
	
}
