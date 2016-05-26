<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{-webkit-user-select:initial;}
	.content{background-color:#f0eff5;}
	.ui-list-function .ui-list-info{padding-right:0px;}
	.ui-list {margin-bottom: 12px;}
	.ui-list>li{margin:auto;}
	.ui-list-thumb{width:60px;height:60px;margin: 10px 14px;}
	.ui-list-info .ui-li-description{padding:8px 0px 0px 0px;line-height:20px;}
	.ui-list-info>h4 {font-size: 16px;}
	.ui-li-fileszie,.ui-li－second{font-size:13px;background:url(${util.static}images/task_11.png) left center no-repeat;color:#666666;background-size:16px 15px;padding-left:18px;}
	.ui-list-info .ui-li-taskimg{margin-left:6px;padding-left:16px;color:#666666;font-size:13px;}
	.ui-li-taskimg{background:url(${util.static}images/task_12.png) left center no-repeat;background-size:13px 13px;height:auto;}
	.ui-li-taskimg span{color:#ff8003;padding:0px 2px;}
	.ui-btn{color:#ff8103;font-size:12px;line-height:26px;height:26px;width:64px;padding:0px;}
	.ui-btn:before{border: 1px solid #ff8103;border-radius:10px;}
	.ui-li-first{height:30px;margin:8px 20px 0px 20px;padding-top:12px;padding-left:24px;background:url(${util.static}images/task_13.png) left center no-repeat;background-size:16px 18px;}
	.ui-li-bz{margin-left:20px;margin-bottom:12px;}
	.ui-li－second{height:30px;margin:8px 20px 0px 20px;padding-top:12px;padding-left:24px;}
	.ui-list-center{  padding-top: 10px;display: -webkit-box;margin:0 auto;text-align: center;}
	.ui-list-center span{line-height:60px;height:60px;}
	.ui-list-center .ui-list-icon{margin-right: 10px;width:60px;border-radius: 12px;text-align: center; display: block;background-repeat: no-repeat; background-size: 60px 60px;}
	.ui-li-icon i{color:#ff8003;padding:0px 2px;font-size:16px;}
	.ui-li－thrid{height:30px;margin:8px 20px 0px 20px;padding-top:12px;padding-left:24px;background:url(${util.static}images/task_15.png) left center no-repeat;background-size:16px 18px;}
	
	.tips{height:40px;background-color:#aaaaaa;text-align:center;}
	.tips p{padding-left:30px;color:#fff;font-size:16px;line-height:26px;display:inline;}
	.tips p>span{color:#ff8003;padding:0px 2px;}
	.tips .ui-list-thumb{width:35px;height:35px;margin:10px 12px;}
	.ui-list-thumb .tips-icon{background:url(${util.static}images/task_10.png) no-repeat;background-size:100%;width:22px;height:22px;}
	
	.ui-dialog .ui-dialog-bd{padding-bottom:10px;}
	.ui-dialog .ui-dialog-cnt {text-align:center;}
	.ui-dialog-bd i{color:#ff8003;padding:0px 2px;}
	.ui-dialog-bd h3{font-size:18px;}
	.ui-dialog-bd h2{font-size:12px; margin-top:10px; color:#F52929;}
	.ui-btn-wrap{padding:0px 0px 14px 0px;}
	.info-tips{line-height:24px;font-size:14px;text-align:left;margin:16px 0px;}
</style>
<div class="content">
	<ul class="ui-list ui-list-function ui-border-b" pid="${task.app.process_name!''}">
	 	<li class="tips">
	        <div class="ui-list-info">
	            <p>完成奖励<span>${util.fen2yuan((task.appTask.amount!0)+(task.appTask.amount!0)*(shareRate!0))}</span>元<#if shareRate?? && (shareRate gt 0)>,含<span>${util.fen2yuan((task.appTask.amount!0)*(shareRate!0))}</span>元奖励</#if></p>
	        </div>
	        <div class="ui-list-thumb"><span class="tips-icon"></span></div>
        </li>
        <li style="background-color: #f0eff5;height:14px;"></li>
	    <li class="ui-border-tb">
	        <div class="ui-list-thumb">
	            <span style="background-image:url(${task.app.icon!''});border-radius:12px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${task.appTask.keywords}</h4>
	            <div class="ui-li-description">
	            <span class="ui-li-fileszie">${task.app.download_size!0} M</span>
	           	<#if task.userTask!=null && task.userTask.completed>
	           		<span class="ui-li-taskimg">任务完成，成功获得奖励<span>${util.fen2yuanS((task.appTask.amount!0)+(task.appTask.amount!0)*(shareRate!0))}</span>元</span>
           		<#else>
	           		<span class="ui-li-taskimg">请在<span>${task.expireTime}分钟</span>内完成任务</span>
           		</#if>
           		</div>
	        </div>
        </li>
	</ul>
	<ul class="ui-list ui-border-tb">
		<#if task.appTask.directDownload>
			<li>
				<div class="ui-list-info">
					<p class="ui-li-first">第一步</p>
				</div>
				<div class="ui-list-right">
					<button class="ui-btn" id="btn-download">下载安装</button>
				</div>
			</li>
			<li>
				<div class="ui-list-info"><div class="ui-li-bz">点击“下载安装”按钮，从苹果AppStore免费下载安装该应用</div></div>
			</li>
		<#else>
			<li>
				<div class="ui-list-info">
					<p class="ui-li-first">第一步</p>
				</div>
				<div class="ui-list-right">
					<button class="ui-btn" id="btn-copy">复制</button>
				</div>
			</li>
			<li class="ui-border-b">
				<div class="ui-list-info"><div class="ui-li-bz">复制关键词 “${task.appTask.keywords}”</div></div>
			</li>

			<li>
				<div class="ui-list-info">
					<p class="ui-li－second">第二步</p>
				</div>
				<div class="ui-list-right">
					<button class="ui-btn" id="btn-seach">搜索下载</button>
				</div>
			</li>
			<li>
				<div class="ui-list-info">
					<div class="ui-li-bz">到AppStore搜索“${task.appTask.keywords}”，找到该图标并下载</div>
					<p style="text-align:center;"><img src="${util.static}images/task_14.png" alt="搜索" style="width:60%;"/></p>
				</div>
			</li>
			<li>
				<div class="ui-list-center">
					<span class="ui-list-icon" style="background-image:url(${task.app.icon!''});"></span><span>图标在第<i>${task.appTask.current_rank}</i>个左右</span>
				</div>
			</li>
		</#if>
        <li class="ui-border-t" style="margin-top:12px;">
	        <div class="ui-list-info">
	            <p class="ui-li－thrid"><#if task.appTask.directDownload>第二步<#else>第三步</#if></p>
	        </div>
	        <div class="ui-list-right">
	        	<button class="ui-btn disabled" id="btn-open">打开试玩</button>
	        </div>
	    </li>
	     <li class="ui-border-b">
	    	<div class="ui-list-info"><div class="ui-li-bz">${task.appTask.description}</div></div>
	    </li>
	  </ul>
</div>
<div class="ui-dialog ui-dialog-accpet">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1>参与任务赢取<i>${util.fen2yuanS((task.appTask.amount!0)+(task.appTask.amount!0)*(shareRate!0))}</i>元奖励</h1>
            <#if (task.requireType > 0)>
            	<h2><#if (task.requireType == 1)>【需付费下载】<#elseif (task.requireType == 2)>【请务必按第三步的要求完成任务】</#if></h2>
            </#if>
            <div class="ui-btn-wrap">
			    <button id="accpet" class="ui-btn ui-btn-danger" style="color:#fff;;width:100%;font-size:18px;margin:20px 0px 0px 0px;;height:40px;background-color:#ff8003;background-image:none;">立即参加</button>
			</div>
        </div>
    </div>        
</div>

<div class="ui-dialog ui-dialog-info">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h3>温馨提示</h3>
			 <div class="info-tips">
				 <p>1.您的身份＝唯一微信+唯一设备号+唯一手机号+唯一苹果账号</p>
				 <p>2.不能使用他人的苹果账号下载</p>
				 <p>3.试用完成，发放奖励需等待几分钟，请耐心等待</p>
			 </div>
            <div class="ui-btn-wrap">
			    <button id="ui-dialog-yes" class="ui-btn ui-btn-danger" style="color:#fff;width:100%;font-size:18px;height:40px;background-color:#ff8003;background-image:none;">确定</button>
			</div>
        </div>
    </div>
</div>
<#if task.userTask!=null && task.userTask.completed>
<div class="ui-dialog ui-dialog-finish">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h3>已完成</h3>
            <div class="info-tips">您已在${task.userTask.finishtime?string("yyyy-MM-dd")}日完成了此任务，<#if task.userTask.reward>并获得了<i>${util.fen2yuanS(task.earned_amount!0)}</i>元奖励。<#else>正在审核中。</#if></div>
            <div class="ui-btn-wrap">
			    <button id="invites" class="ui-btn ui-btn-danger" data-url="${util.ctx}/web/discovery/share.html" style="color:#fff;width:100%;font-size:18px;margin-bottom:12px;height:40px;background-color:#ff8003;background-image:none;">邀请好友试用</button>
				<button id="myincome" class="ui-btn ui-btn-danger" data-url="${util.ctx}/web/my/income.html" style="color:#fff;width:100%;font-size:18px;height:40px;background-color:#ff8003;background-image:none;">查询我的收入</button>
			</div>
        </div>
    </div>        
</div>
</#if>
<div class="ui-dialog ui-dialog-status">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h3>${task.statusText}</h3>
            <div class="ui-btn-wrap" style="margin-top:16px;">
				<button id="reopen-app" class="ui-btn ui-btn-danger" style="display:none;color:#fff;;width:100%;font-size:18px;margin-bottom:10px;height:40px;background-color:#ff8003;background-image:none;">继续打开试玩</button>
         		<button id="task-list"  data-url="${util.ctx}/web/discovery/tasks.html"  class="ui-btn ui-btn-danger" style="color:#fff;width:100%;font-size:18px;height:40px;background-color:#ff8003;background-image:none;">返回限时任务</button>
        	</div>
        </div>
    </div>        
</div>

<div class="ui-dialog ui-dialog-open">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
        	<h1>点击按钮打开试玩(必须)</h1>
			<div class="info-tips">首次打开会比较慢，请耐心等待</div>
            <div class="ui-btn-wrap">
			    <button id="open-app" class="ui-btn ui-btn-danger" style="color:#fff;;width:100%;font-size:18px;margin:20px 0px 0px 0px;;height:40px;background-color:#ff8003;background-image:none;">立即打开试玩</button>
				<button id="finish-task" class="ui-btn ui-btn-danger" style="color:#fff;;width:100%;font-size:18px;height:40px;background-color:#ff8003;background-image:none;margin-top:8px;">检查完成状态</button>
			</div>
        </div>
    </div>        
</div>

<script>
$(function(){
	var received = false;
	//未接过任务
	<#if task.canReceive>
		$(".ui-dialog-accpet").dialog("show");
	<#elseif (task.userTask!=null && task.userTask.reward gt 0)>
		$('.ui-dialog-finish').dialog("show");
	<#elseif task.apping || task.waitingCallback>
		received = true;
		checkIsInstall();
		timeInterval = setInterval(checkIsInstall, 5000);
	<#else>
		$(".ui-dialog-status").dialog("show");
	</#if>

	var timeInterval;
	function checkIsInstall() {
		if($('.disabled').length == 0){
			if(timeInterval){
				clearInterval(timeInterval);
			}
			return;
		}
		if(typeof MiJSBridge == "object") {
			MiJSBridge.call('checkApp', {protocol: '${task.app.agreement}', bundle : '${task.app.bundle_id!""}'}, function(ret){
				if(ret.installed) {
					$('.disabled').removeClass("disabled");
					$('#btn-open').click(function(){
						MiJSBridge.call('launchApp', {protocol: '${task.app.agreement}', bundle : '${task.app.bundle_id!""}'});
					});
					if(received){
						$('.ui-dialog-open').dialog("show");
					}
					clearInterval(timeInterval);
				}
			});
		}
	}

	if(window.MiJSBridge){	//window.MiJSBridge对象不为空，可以直接调用
	     onMiJSBridgeReady();
	}else{
	     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
	}

	function onMiJSBridgeReady(){
		MiJSBridge.call('startDetect');
		checkIsInstall();
		if($('.disabled').length > 0){
			timeInterval = setInterval(checkIsInstall,3000);
		}

		//接收任务
		$('#accpet').click(function(){
			<#if iosVersion gte 9.0>
			if(typeof MiJSBridge == "object") {
				MiJSBridge.call('isAppSupported', {protocol:'${task.app.agreement}', bundle : '${task.app.bundle_id!""}'}, function(ret){
					if(ret.result == 'ok'){
						doReceiveTask();
					}else{
						showTips('当前版本低，请在“我”->“设置”->“检测新版本”，更新最新版本！');
					}
				});
			}
			<#else>
			doReceiveTask();
			</#if>
		});

		//复制
		$('#btn-copy').click(function(){
			if(typeof MiJSBridge == "object")
				MiJSBridge.call('copy', {message: '${task.appTask.keywords}'},function(){MiJSBridge.call('toast', {message: '复制成功'});});
		});
		$('#btn-seach').click(function(){
			if(typeof MiJSBridge == "object") MiJSBridge.call('appSearch');
		});

		$('#task-list').click(function(){
			if(typeof MiJSBridge=="object") MiJSBridge.call('close');
			else window.location.href=  $(this).attr('data-url');
		});
		$('#myincome,#invites').click(function(){
			var url = $(this).attr('data-url');
			if(typeof MiJSBridge=="object") {
				MiJSBridge.call("open", {url: url});
			} else {
				window.location.href= url;
			}
		});
		$('#open-app,#reopen-app').click(function(){
			MiJSBridge.call('launchApp', {protocol: '${task.app.agreement}', bundle : '${task.app.bundle_id!""}'});
		});

		$('#btn-download').click(function(){
			<#if task.appTask.directDownload>
			MiJSBridge.call('launchApp', {protocol: '${task.app.description!''}', bundle:''});
			</#if>
		});
	}


	function doReceiveTask(){
		$.ajax({
		   type: "POST",
		   url: "${util.ctx}/api/v1/ah/ha",
		   data: {task:"${task.appTask.encodedId!0}"},
		   dataType:'json',
		   success: function(r){
		   		if(r.code != 0) {
			   		showTips(r.message);
			    } else {
			    	$(".ui-dialog").dialog("hide");
			   	 	received = true;
			   	 	clearInterval(timeInterval);
			   	 	timeInterval = setInterval(checkIsInstall, 3000);

					<#if task.appTask.directDownload>
					MiJSBridge.call('checkApp', {protocol: '${task.app.agreement}', bundle : '${task.app.bundle_id!""}'});
			   	 	showTips('接收任务成功！该任务为直接下载任务，点击“确定”将打开苹果AppStore免费下载该应用', function(){
			   	 		$('#btn-download').click();
			   	 	});
			   	 	<#else>
			   	 	showTips('接收任务成功，请在'+r.expireTime+'分钟完成任务。(下载完成后，必须在此页点击“立即打开试玩”才有效！)');
			   	 	</#if>
			   	}
		   },
	    	error: function(r) {
            	 showTips('出错了，请稍后再试吧！');
           }
		});
	}
	$('.tips-icon').click(function(){	//提示
		$(".ui-dialog-info").dialog("show");
	});
	$('#ui-dialog-yes').click(function(){$(".ui-dialog-info").dialog("hide")});

	$('#finish-task').click(function(){
		$.getJSON('/api/v1/task/check/${task.appTask.encodedId!0}', function(data){
			showTips(data.desc, function(){  data.status <= 0 && location.reload(); });
		});
	});

	function showTips(message, callback) {
		if(typeof MiJSBridge=="object") {
			 MiJSBridge.call('alert', {title: message}, function(){ callback && callback(); });
		} else {
			var el=Zepto.tips({
		        content:message,
		        stayTime:2000,
		        type:"info"
		    })
		    el.on("tips:hide",function(){
		    	callback && callback();
		        console.log("tips hide");
		    })
		}
	}
});
</script>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<#include "/common/footer.ftl">