<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<link rel="stylesheet" type="text/css" href="${util.static}/css/xianshi.css">
<style>
	body{background-color:#f0eff5;}
</style>
<#if hint_text??>
	<div class="tishi_box" id="tishi-box" style="display:none;">
		<div class="prompt"><span>${hint_text}</span><img src="${util.static}images/img/mune.png" /></div>
	</div>
</#if>
<div class="content_list" id="task-list" data-rate="${shareRate!1}" style="display:none;">
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
		<#if task.canReceive || task.apping || task.waitingCallback || task.app.id == 96 || task.appTask.valid>
	    <li class="ui-border-t<#if !task.valid  && !task.waitingCallback> ui-li-finish</#if><#if task.app.id==96> friend-task</#if>" data-id="${task.appTask.encodedId}" data-pid="${task.app.encodedAppId}"  <#if inAppView>prop-id="${task.app.agreement!''}"  bd-id="${task.app.bundle_id!''}"</#if>  >
	        <div class="ui-list-thumb">
	            <span style="background-image:url(${task.app.icon!''});border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info" data-restnum="${task.appTask.leftNumForShow}">
	            <h4>${task.appTask.keywords}<#if (task.requireType > 0)><em class="tip${task.requireType}"></em></#if></h4>
	          	  <#if task.app.id==96> <p class="ui-li-taskimg">永久分成</p><#else><p class="ui-li-taskimg">剩余${task.appTask.leftNumForShow}份</p></#if>
	        </div>
			<#if task.app.id != 96 && task.canReceive && ((task.app.payWay  && task.appTask.amount gt 250) || (!task.app.payWay && task.appTask.amount gt 150) )>
				<div class="markup">
					<p><b>+</b><em>${util.fen2yuanS((task.appTask.amount!0)*(shareRate!1))}</em><b>元</b></p>
					<span>限时加价</span>
				</div>
			<#else/>
				<div class="ui-list-right">
					<#if task.canReceive>
						<button class="ui-btn">+ ${util.fen2yuanS((task.appTask.amount!0)*(shareRate!1))} 元</button>
					<#elseif task.apping || task.waitingCallback>
						<button class="ui-btn haveing" data-status="${task.userTask.download?string("1","0")}" data-info="${appInfoMap[task.app.bundle_id]}">${task.statusText}</button>
					<#else>
						<button class="ui-btn ui-btn-finish">+ ${util.fen2yuanS(task.earned_amount!0)} 元</button>
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
						<p><b>+</b><em>${util.fen2yuanS((task.appTask.amount!0)*(shareRate!1))}</em><b>元</b></p>
						<span>限时加价</span>
					</div>
				<#else/>
					<div class="ui-list-right">
						<button class="ui-btn ui-btn-finish">+ ${util.fen2yuanS((task.appTask.amount!0)*(shareRate!1))} 元</button>
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
						<button class="ui-btn">+ ${util.fen2yuanS((task.appTask.amount!0)*(shareRate!1))} 元</button>
					<#elseif task.apping || task.waitingCallback>
						<button class="ui-btn haveing" data-status="${task.userTask.download?string("1","0")}">${task.statusText}</button>
					<#else>
						<button class="ui-btn ui-btn-finish">+ ${util.fen2yuanS(task.earned_amount!0)} 元</button>
					</#if>
					<span class="down-message">${task.taskPromptText}</span>
				</div>
			</li>
			</#if>
		</#list>
	</#if>
	</ul>
</div>
<div class="wrap" id="detail-panel" style="display:none;"></div>
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
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
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