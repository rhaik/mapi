package com.cyhd.service.impl;

import com.cyhd.common.util.job.AsyncJob;
import com.cyhd.common.util.job.JobHandler;
import com.cyhd.service.dao.po.TransArticleTask;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserArticleTask;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class UserViewArticleRecordService extends BaseService {

	//第一上报任务的已过期不用记录
	//点击和入库数据的速率不匹配用中间件
	//查看过该文章的不用记录 即不用加入中间件
	//分享的用户可能使用不同的微信公众号分享 所以要记录他分享的是哪个
	
	@Resource
	private ArticleViewLogService articleViewLogService;
	
	@Resource
	private UserArticleTaskService userArticleTaskService;
	
	@Resource
	private TransArticleTaskService transArticleTaskService;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	@Resource
	private UserMessageService userMessageService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserFriendService userFriendService;
	
	private AsyncJob<ArticleJobBean> executeJob = new AsyncJob<ArticleJobBean>("article-job", new JobHandler<ArticleJobBean>() {
		@Override
		public boolean handle(ArticleJobBean t) {
			return execute(t);
		}
	}, 4);


	/**
	 * 记录微信用户阅读信息
	 * @param user_identity
	 * @param article_task_id
	 * @param articleId
	 * @param viewUnionId
	 * @param accountId
	 * @param view_openid
	 */
	public void record(int user_identity,int article_task_id,int articleId, String viewUnionId,int accountId,String view_openid, String ip){
		User u = userService.getUserByIdentifyId(user_identity);

		//自己阅读不记录
		String taskUnionId = u.getUnionid();
		if(taskUnionId.equals(viewUnionId)){
			return ;
		}

		if (viewUnionId.equals(u.getOpenid())){
			return;
		}

		UserArticleTask userArticleTask = userArticleTaskService.getArticleTask(u.getId(), article_task_id);

		//用户的任务是否过期
		if(userArticleTask == null || userArticleTask.getArticle_id() != articleId){
			logger.error("用户的任务Id:{} 和所转发文章Id:{} 不同:view_openid:{}",article_task_id,articleId,view_openid);
			return ;
		}
		if(userArticleTask.getArticle_id() < 10 && userArticleTask.getView_num() > 2){
			logger.info("系统任务不用记录太多:userId:{},taskId:{}",u.getId(),article_task_id);
			return ;
		}
		record(userArticleTask, viewUnionId, accountId, view_openid, ip);
	}
	/**
	 * 记录的总入口
	 * @param article_id 转发任务的文章ID
	 * @param unionId 
	 */
	public void record(UserArticleTask userArticleTask, String viewUnionId, int accountId, String view_openid, String ip){
		if (userArticleTask == null){
			logger.warn("记录文章浏览：用户任务为null");
			return;
		}

		//看过该文章
		if(articleViewLogService.isViewed(userArticleTask.getArticle_id(), viewUnionId, ip)){
			logger.warn("unionid:{} (openid:{}) is viewed,article_id:{}, ip:{}", viewUnionId, view_openid, userArticleTask.getArticle_id(), ip);
			return ;
		}

		//检查任务是否过期
		TransArticleTask transArticleTask = transArticleTaskService.getTransArticleTask(userArticleTask.getArticle_task_id());
		if(transArticleTask == null || transArticleTask.isExpired()){
			logger.warn("记录文章浏览：任务无效或者任务已过期, taskId:{}, ip:{}", userArticleTask.getArticle_task_id(), ip);
			return ;
		}

		ArticleJobBean t = new ArticleJobBean();
		t.setAccountId(accountId);
		t.setViewUnionId(viewUnionId);
		t.setTask_id(transArticleTask.getId());
		t.setArticle_id(userArticleTask.getArticle_id());
		t.setCurrent(new Date());
		t.setUser_id(userArticleTask.getUser_id());
		t.setView_openid(view_openid);
		t.setIp(ip);
		t.setOpened(userArticleTask.getOpened());
		try {
			executeJob.put(t );
		} catch (InterruptedException e) {
			logger.error("put into job ,bean:{},cause by:{}",t,e);
		}
	}


	//TODO 此处有bug 我跪啦  这个并发怎么处理 
	private boolean execute(ArticleJobBean bean){
		Date current = bean.getCurrent();
		logger.info("转发记录入库开始:{}",bean);
		int article_id = bean.getArticle_id();
		int task_user_id = bean.getUser_id();
		String view_unionid = bean.getViewUnionId();
		String task_user_unionid = bean.getTaskUnionId();
		int article_task_id = bean.getTask_id();
		try{//有的操作数据库有限制 所以有可能会出现异常
			int insert = articleViewLogService.insert(article_id , task_user_id , task_user_unionid, view_unionid, current,bean.getView_openid(), bean.getIp());
			boolean insertLog = insert > 0 ;
			logger.info("插入访问者日志:{}",insertLog);
			if(insertLog == false){
				return false;
			}

			//是首次打开，只插入记录，不增加阅读人数
			if (bean.getOpened() == 0){
				logger.warn("用户分享的文章第一次被打开，不记录阅读数, articleId:{}, taskId:{}, userId:{}, openId:{}", bean.getArticle_id(), bean.getTask_id(), bean.getUser_id(), view_unionid);
				userArticleTaskService.setOpened(bean.getUser_id(), bean.getArticle_id());

				return false;
			}else {
				//给任务数加1
				if (userArticleTaskService.addViewNum(task_user_id, article_task_id) == false) {
					logger.error("增加阅读人,task_user_id:{},article_task_id:{},view_unionid:{}", task_user_id, article_task_id, view_unionid);
					return false;
				}
				logger.info("增加阅读人数Ok");
			}
			return true;
			
		}catch(Exception e){
			logger.error("处理转发任务中出现异常:userId:{},article_task_id:{},cause by:{}",task_user_id,article_task_id,e);
		}
		return false;
	}
}

class ArticleJobBean {
	
	private int user_id;
	private String viewUnionId;
	private int task_id;
	private String taskUnionId = "";
	private int accountId;
	private int article_id;
	//防止job的延迟 所以时间是加入job的时间
	private Date current;
	private String view_openid = "";
	private String ip;
	private int opened;
	
	public int getArticle_id() {
		return article_id;
	}
	public void setArticle_id(int article_id) {
		this.article_id = article_id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getViewUnionId() {
		return viewUnionId;
	}
	public void setViewUnionId(String viewUnionId) {
		this.viewUnionId = viewUnionId;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public String getTaskUnionId() {
		return taskUnionId;
	}
	public void setTaskUnionId(String taskUnionId) {
		this.taskUnionId = taskUnionId;
	}
	public int getAccountId() {
		return accountId;
	}
	public void setAccountId(int accountId) {
		this.accountId = accountId;
	}
	public void setOpened(int flag){this.opened = flag;}
	public int getOpened() {return opened;}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(100);
		sb.append( "ArticleJobBean [article_id=" ).append( article_id ).append( ", viewUnionId="
		).append( viewUnionId ).append( ", task_id=" ).append( task_id ).append( ", taskUnionId="
		).append( taskUnionId ).append( ", accountId=" ).append( accountId ).append(",view_openid=").append(view_openid).append( "]");
		return sb.toString();
	}
	public Date getCurrent() {
		return current;
	}
	public void setCurrent(Date current) {
		this.current = current;
	}
	public String getView_openid() {
		return view_openid;
	}
	public void setView_openid(String view_openid) {
		this.view_openid = view_openid;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}