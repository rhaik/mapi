<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<link rel="stylesheet" type="text/css" href="${util.static}/css/xianshi.css">
<style>
	body{background-color:#f0eff5;}
</style>
<#if hint_text??>
	<div class="tishi_box">
		<div class="prompt"><span>${hint_text}</span><img src="${util.static}images/img/mune.png" /></div>
	</div>
</#if>
<div class="content_list">
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
		<#if task.canReceive || task.apping || task.waitingCallback || task.app.id == 96 || task.appTask.valid>
	    <li class="ui-border-t<#if !task.valid  && !task.waitingCallback> ui-li-finish</#if><#if task.app.id==96> friend-task</#if>" data-id="${task.appTask.encodedId}" data-appId="${task.app.encodedAppId}">
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
						<button class="ui-btn haveing" data-status="${task.userTask.download?string("1","0")}">${task.statusText}</button>
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
			<li class="ui-border-t no-click">
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
			<li class="ui-border-t<#if !task.valid && !task.waitingCallback> ui-li-finish</#if>" prop-id="${task.app.agreement!''}" bd-id="${task.app.bundle_id!''}" data-id="${task.appTask.encodedId}" data_appId="${task.app.encodedAppId}">
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
<script>
$(function(){
	$('ul>li').click(function(e){
		if($(this).hasClass('no-click')) return ;
		var url = '${util.ctx}/ios/task/' + $(this).data('id');
		if($(this).hasClass('friend-task')){
			<#if fromSafari??>
			url = '${util.ctx}/ios/share.html';
			<#else>
			url='<#if fromSafari??>/static/html/safari_frm.html?pg=</#if>/static/html/apprentice.html';
			</#if>
		}
		if($(this).hasClass('ui-li-finish')){
			 showTips($.trim($(this).find('.down-message').html()));
			return ;
		}
		window.location.href = url;
	});

	$('#dec-panel span').click(function(){
		location.href = 'https://mp.weixin.qq.com/s?__biz=MjM5MDI5NDkwMQ==&mid=402441901&idx=1&sn=3ac82e5242b818947ee5ebb71ef699c2&scene=0';
	});

	$('#dec-panel a').click(function(){
		$('#dec-panel').hide();
	});
});
	function showTips(message) {
		if(typeof MiJSBridge=="object") {
			 MiJSBridge.call('alert', {title: message}, function(){});
		} else {
			var el=Zepto.tips({
		        content:message,
		        stayTime:2000,
		        type:"info"
		    })
		    el.on("tips:hide",function(){
		        console.log("tips hide");
		    })
		}
	}
</script>
<script type="text/javascript">
var bm_config = {
	ws: '${websocketAddress}',
	tk: '${user.ticket!""}',
	sm : '${yaoshiScheme!"#"}'
};
</script>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<script type="text/javascript" src="${util.static}js/bridge.js"></script>
<#include "/common/footer.ftl">