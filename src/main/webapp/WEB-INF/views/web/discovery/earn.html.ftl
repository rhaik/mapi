<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<link rel="stylesheet" type="text/css" href="${util.static}/css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/xianshi.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/style.css">
<style>
	body{background-color:#f0eff5;}
	.bill_box{ background:#fff;}
	.bill_box {display:none;}
	.bill_box.selected {display:block;}
	.bill_box em{display: block;}
	.bill_box .amount li.on{border: 1px solid #ff8003;}
	.bill_box .amount li.on a,.bill_on .amount li.on span{color: #ff8003;}
	.bill_box .amount li.disabled {background:#eee;}
	.bill_box .amount li a{height: 25px; line-height: 25px; margin-top: 12px;}
	.bill_box .amount li span{display: block;}
	.bill_box .amount li i{color:#ff8103;}
	.bill_box .bill_btn a{background: #ff8003;}
	.amount {padding: 15px 15px 0; overflow: hidden; background:#fff; margin:0;}
	.amount li{width: 30%; height: 65px; border: 1px solid #ddd; display: block; float: left; background: #fff; margin-right: 3.4%; margin-bottom: 15px; overflow: hidden;}
	.amount li a{display: block; height: 65px; line-height: 65px; text-align: center; font-size: 18px; color: #aaa;}
	.amount li span{display: none; font-size: 12px; color: #aaa; text-align: center;}
	.amount li:nth-child(3n){margin-right: 0;}
	.bill_btn{margin:0 15px 15px;}
	.bill_btn a{display: block; background:#ff8003; line-height: 45px; text-align: center; font-size: 17px; color: #fff;}
	.bill_btn.grey a{background:#d2d2d2;}
	.account_balance{padding:15px; border-top:1px solid #dcdcdc; font-size:1em; color:#222;}
	.tishi{padding:0 15px 15px; font-size:0.94em; color:#999;}
	.tishi i{color:#ff0000; font-style:normal; line-height:22px;}
	.tixian_bg{position:fixed; left:0; bottom:0; width:100%;background:#fff;}
	.way{height:3em; margin:0 auto;}
	.way p{width:50%; line-height:2.5em; text-align:center; float:left; font-size:1.1em; color:#000; margin:0; border-left:1px solid #dcdcdc; margin-left:-1px; background:#ececec;}
	.way p.on{background:#fff; color:#ff8003;}
	.account_set {margin:40px 15px;}
	.account_set a{color:#ff8003;}
</style>
<div class="income income_box" style="margin-top:0;">
	<p>余额：</p>
	<h1 class="fl">${util.fen2yuanS(balance)!0}<b>元</b></h1>
	<div class="fr">
		<div class="people_info_name clearfix">
			<i><img src="${user.avatar}" /></i>
			<b>${user.uniName}</b>
			<em>ID:${user.user_identity}</em>
		</div>
		<a class="tixian <#if enchashing != 0>enchashing</#if>" id="tixian-btn"><em><#if enchashing != 0>提现处理中<#else/>提现</#if></em></a>
	</div>
	<div class="income_bot">
		<span>今日收入： ${util.fen2yuanS(todayIncome)!0}</span>
		<span>累计收入：${util.fen2yuanS(income)!0} </span>
	</div>
</div>
<div class="content_list content_pt0" id="task-list" data-rate="${shareRate!1}" style="display:none;">
	<#if tasks?has_content>
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
		<#if task.canReceive || task.apping || task.waitingCallback || task.app.id == 96 || task.appTask.valid>
	    <li class="ui-border-t<#if !task.valid  && !task.waitingCallback> ui-li-finish</#if><#if task.app.id==96> friend-task dn</#if>" data-id="${task.appTask.encodedId}" data-pid="${task.app.encodedAppId}" <#if task.app.id==96>style="display:none;"</#if> <#if inAppView>prop-id="${task.app.agreement!''}"  bd-id="${task.app.bundle_id!''}"</#if>  >
	        <div class="ui-list-thumb">
	            <span style="background-image:url(${task.app.icon!''});border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${task.appTask.keywords}<#if (task.requireType > 0)><em class="tip${task.requireType}"></em></#if></h4>
	          	  <#if task.app.id==96> <p class="ui-li-taskimg">永久分成</p><#else><p class="ui-li-taskimg">剩余${task.appTask.leftNumForShow}份</p></#if>
	        </div>
			<#if task.app.id != 96 && task.canReceive && ((task.app.payWay  && task.appTask.amount gt 250) || (!task.app.payWay && task.appTask.amount gt 150) )>
				<div class="markup">
					<p><b>+</b><em>${util.fen2yuan((task.appTask.amount!0)*(shareRate!1))}</em><b>元</b></p>
					<span>限时加价</span>
				</div>
			<#else/>
				<div class="ui-list-right">
					<#if task.canReceive>
						<button class="ui-btn">+ ${util.fen2yuan((task.appTask.amount!0)*(shareRate!1))} 元</button>
					<#elseif task.apping || task.waitingCallback>
						<button class="ui-btn haveing" data-status="${task.userTask.download?string("1","0")}" data-info="${appInfoMap[task.app.bundle_id]}">${task.statusText}</button>
					<#else>
						<button class="ui-btn ui-btn-finish">+ ${util.fen2yuan(task.earned_amount!0)} 元</button>
					</#if>
						<span class="down-message">${task.taskPromptText}</span>
				</div>
			</#if>
	    </li>
		<#else/>
			<#assign  hasMoreTask=true/>
		</#if>
	</#list>

	<#if futureTasks?has_content>
		<li class="ui-border-t no-click" style="border:none;"><div class="yugao"><p>任务预告：精彩任务即将开始</p></div></li>
		<#list futureTasks as task>
			<li class="ui-border-t future-task">
				<div class="ui-list-thumb">
					<span style="background-image:url(${util.static}images/img/future_icon1.png);border-radius:10px;"></span>
				</div>
				<div class="ui-list-info clearfix">
					<h4>${task.appTask.keywords[0]}***<#if (task.requireType > 0)><em class="tip${task.requireType}"></em></#if></h4>
					<div class="ui-li-text">
						<span>${task.appTask.futureTime}</span>
						<p class="ui-li-taskimg">剩余${task.appTask.futureNumForShow}份</p>
					</div>
				</div>
				<#if (task.app.payWay  && task.appTask.amount gt 250) || (!task.app.payWay && task.appTask.amount gt 150)  >
					<div class="markup">
						<p><b>+</b><em>${util.fen2yuan((task.appTask.amount!0)*(shareRate!1))}</em><b>元</b></p>
						<span>限时加价</span>
					</div>
				<#else/>
					<div class="ui-list-right">
						<button class="ui-btn ui-btn-finish">+ ${util.fen2yuan((task.appTask.amount!0)*(shareRate!1))} 元</button>
					</div>
				</#if>
			</li>
		</#list>
	</#if>


	<#if hasMoreTask>
		<li class="ui-border-t no-click" style="border:none;"><div class="jianju"></div></li>
		<#list tasks as task>
			<#if task.canReceive || task.apping || task.waitingCallback || task.app.id == 96 || task.appTask.valid>

			<#else />
			<li class="ui-border-t<#if !task.valid && !task.waitingCallback> ui-li-finish</#if>">
				<div class="ui-list-thumb">
					<span style="background-image:url(${task.app.icon!''});border-radius:10px;"></span>
				</div>
				<div class="ui-list-info">
					<h4>${task.appTask.keywords}<#if (task.requireType > 0)><em class="tip${task.requireType}"></em></#if></h4>
					<#if task.app.id==96> <p class="ui-li-taskimg">永久分成</p><#else><p class="ui-li-taskimg">剩余${task.appTask.leftNumForShow}份</p></#if>
				</div>
				<div class="ui-list-right">
					<#if task.canReceive>
						<button class="ui-btn">+ ${util.fen2yuan((task.appTask.amount!0)*(shareRate!1))} 元</button>
					<#elseif task.apping || task.waitingCallback>
						<button class="ui-btn haveing" data-status="${task.userTask.download?string("1","0")}">${task.statusText}</button>
					<#else>
						<button class="ui-btn ui-btn-finish">+ ${util.fen2yuan(task.earned_amount!0)} 元</button>
					</#if>
					<span class="down-message">${task.taskPromptText}</span>
				</div>
			</li>
			</#if>
		</#list>
	</#if>
	</ul>
	</#if>
</div>
<div class="wrap" id="detail-panel" style="display:none;"></div>
<div class="website">客服微信：xiaomiaozhushou</div>
<div class="ui-dialog" id="accept-dialog">
	<div class="ui-dialog-cnt">
		<div class="ui-dialog-bd">
			<h1>参与任务赢取<i class="reward-amount"></i>元奖励</h1>
			<h2 style="display:none;"></h2>
			<div class="ui-btn-wrap">
				<button class="ui-btn ui-btn-danger">立即参加</button>
			</div>
		</div>
	</div>
</div>
<div class="ui-dialog" id="ui-dialog">
	<div class="ui-dialog-cnt">
		<div class="ui-dialog-bd">
			<h1 id="ui-dialog-title"></h1>
			<div class="ui-btn-wrap">
				<button id="ui-dialog-btn" class="ui-btn ui-btn-danger"></button>
			</div>
		</div>
	</div>
</div>
<div class="ui-dialog" id="welcome-dialog">
	<div class="ui-dialog-cnt">
		<div class="ui-dialog-bd">
			<h3>限时任务改版啦</h3>
			<div class="info-tips">
				<p>1. 告别接任务弹窗，列表中点击即可接任务</p>
				<p>2. 支持放弃任务，自由安排任务时间</p>
				<p>3. 无需长按复制，轻点按钮，直接搜索下载（需升级到最新版钥匙）</p>
			</div>
			<div class="ui-btn-wrap">
				<button class="ui-btn ui-btn-danger">我知道了</button>
			</div>
		</div>
	</div>
</div>
<div class="ui-dialog" id="copy-dialog">
	<div class="ui-dialog-cnt">
		<div class="ui-dialog-bd">
			<h3>复制成功</h3>
			<div class="info-tips">
				<p>请在AppStore搜索框粘贴关键词，搜索并下载</p>
				<p class="paste-img"><img src="${util.static}images/task_paste.png" style="width:100%;"/> </p>
			</div>
			<div class="ui-btn-cont">
				<button class="ui-btn ui-btn-cancel">不再提示</button>
				<button class="ui-btn ui-btn-danger">我知道了</button>
			</div>
		</div>
	</div>
</div>
<div class="ui-dialog" id="withdraw-dialog">
	<div class="tixian_bg">
		<div class="way">
			<p class="on" data-type="2" data-fee="1"><span>支付宝</span></p>
		</div>
		<div class="content">
			<div class="bill_box selected">
				<#if userAccount.alipay_account?has_content>
					<ul class="amount">
						<#list enchashStages as stage>
							<#if stage.type == 0 || stage.type == 2>
								<li class=" <#if balance lt stage.amount>disabled<#else>enabled</#if>" data-amount="${stage.amount}">
									<a>${util.fen2yuan(stage.amount)}元</a>
									<span><#if stage.coins gt 0>返<i>${stage.coins}</i>金币<#else>不返金币</#if></span>
								</li>
							</#if>
						</#list>
					</ul>
					<div class="tishi">一个工作日到账，周末及节假日不处理提现业务<br/>
						支付宝扣除手续费1元，实际到账共<i class="drawAmount">${util.fen2yuan(enchashStages[0].amount - 100)}</i>元<br/>
						返还的金币将在提现成功后打入您的账户
					</div>
				<#else>
					<div class="account_set">您未设置支付宝提现账号，<a data-url="/web/my/withdraw_account.html?type=2&withdraw=1" href="javascript:;">立即设置&gt;&gt;</a></div>
				</#if>
			</div>
		</div>
		<div class="bill_btn" id="save-btn"><a>立即提现</a></div>
	</div>
</div>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<script>
	var userInfo = {
		"wx_bank_name":"${userAccount.wx_bank_name!""}",
		"alipay_name":"${userAccount.alipay_name!''}",
		"alipay_account":"${userAccount.alipay_account!''}",
		"mobile" : "${user.mobile!''}",
		balance: parseInt(${balance})
	}
</script>
<#if inAppView>
<script>var page= "list";</script>
<script src="${util.static}/js/app_xianshi.js"></script>
<#else/>
<script type="text/javascript">
var bm_config = {
	ws: '${websocketAddress}',
	tk: '${user.ticket!""}',
	sm : '${yaoshiScheme!"#"}'
};
</script>
<script type="text/javascript" src="${util.static}js/xianshi.js"></script>
<script type="text/javascript" src="${util.static}js/bridge.js"></script>
</#if>
<#include "/common/footer.ftl">