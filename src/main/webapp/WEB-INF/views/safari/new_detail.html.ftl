<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
 <link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
 <link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<style>
	.ui-dialog{z-index:9;}
	.ui-dialog .ui-dialog-bd{padding-bottom:10px;}
	.ui-dialog .ui-dialog-cnt {text-align:center;}
	.ui-dialog-bd i{color:#ff8003;padding:0px 2px;}
	.ui-dialog-bd h3{font-size:18px;}
	.ui-dialog-bd h2{font-size:12px; margin-top:10px; color:#F52929;}
	.ui-btn-wrap{padding:0px 0px 14px 0px; margin-top:10px;}
	.info-tips {line-height:24px;font-size:14px;text-align:left;margin:16px 0px;}
	#copy-area {text-align: center; margin:10px 0;-webkit-touch-callout: none;}
	#keyword {display: inline-block; color: #272727; background-color: #fff; font-size: 1.8em; border: 1px dashed #aaa; padding:10px 10px 5px 10px; margin:10px;-webkit-user-select:initial}
	#keyword span { display:block; border:1px solid #fff;}
</style>
<div>
<div class="tishi_top">额外奖励0.5元<em><img width="100%" src="${util.static}images/img/mune.png" /></em></div>
	<div class="head_top clearfix">
		<div class="head_top_pic fl"><img src="${task.app.icon!''}" /></div>
		<div class="head_text fl">
			<h1>${task.appTask.keywords}</h1>
			<p><em><img src="${util.static}images/task_11.png" /></em>${task.app.download_size!0}M</p>
			 	<#if task.userTask!=null && task.userTask.completed>
	           		<p>任务完成，成功获得奖励<i class="orange">${util.fen2yuanS(task.appTask.amount!0)}</i>元</p>
           		<#else>
	           		<p>请在<i class="orange">${task.expireTime}分钟</i>内完成任务</p>
           		</#if>
		</div>
		<div class="header_text"><em>+</em><b>${util.fen2yuanS(task.appTask.amount!0)}</b><em>元</em></div>
	</div>
		<#if task.appTask.directDownload>
		<div class="step_box">
			<h1><em><img src="${util.static}images/task_13.png" /></em>第一步<a class="step_btn col" id="btn-download">下载安装</a></h1>
			<p>点击“下载安装”从苹果AppStore免费下载安装应用</p>
		</div>
		<#else>
			<div class="step bg_f">
				<div class="step_box">
					<h1><em><img src="${util.static}images/task_13.png" /></em>第一步</h1>
					<p>长按虚线框，复制关键词“${task.appTask.keywords}”</p>
					<div id="copy-area">
						<div id="keyword">
							<span id="word">${task.appTask.keywords}</span>
						</div>
					</div>
				</div>
				<div class="step_box">
					<h1><em><img src="${util.static}images/task_11.png" /></em>第二步<a class="step_btn col" id="btn-seach">搜索下载</a></h1>
					<p>在AppStore粘贴搜索“${task.appTask.keywords}”，找到应用图标 在第${task.appTask.current_rank}名左右</p>
					<em><img src="${util.static}images/task_14.png" /></em>
				</div>
		</#if>
		<div class="step_box">
			<h1><em><img src="${util.static}images/task_15.png" /></em><#if task.appTask.directDownload>第二步<#else>第三步</#if>
			<a class="step_btn ui-btn disabled" id="btn-open">打开试玩</a></h1>
			<p>${task.appTask.description}</p>
		</div>
	</div>
<div class="ui-dialog ui-dialog-accpet">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1>参与任务赢取<i>${util.fen2yuanS(task.appTask.amount!0)}</i>元奖励</h1>
            <#if (task.requireType > 0)>
            	<h2><#if (task.requireType == 1)>【需付费下载】<#elseif (task.requireType == 2)>【请按第三步要求完成任务】</#if></h2>
            </#if>
            <div class="ui-btn-wrap">
			    <button id="accpet" class="ui-btn ui-btn-danger">立即参加</button>
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
			    <button id="ui-dialog-yes" class="ui-btn ui-btn-danger">确定</button>
			</div>
        </div>
    </div>
</div>
<#if task.userTask!=null && task.userTask.completed>
<div class="ui-dialog ui-dialog-finish">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h3>已完成</h3>
            <div class="info-tips">您已在${task.userTask.finishtime?string("yyyy-MM-dd")}日完成了此任务，<#if task.userTask.reward>并获得了<i>${util.fen2yuanS(task.appTask.amount!0)}</i>元奖励。<#else>正在审核中。</#if></div>
            <div class="ui-btn-wrap">
				<button id="myincome" class="ui-btn ui-btn-danger" data-url="${yaoshiScheme!'#'}?action=url&url=/web/my/income.html">查询我的收入</button>
				<button class="ui-btn ui-btn-danger goBack" style="margin-top:8px;">返回限时任务</button>
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
				<button id="reopen-app" class="ui-btn ui-btn-danger" style="display:none;margin-bottom:8px;">继续打开试玩</button>
				<button class="ui-btn ui-btn-danger goBack">返回限时任务</button>
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
			    <button id="open-app" class="ui-btn ui-btn-danger">立即打开试玩</button>
				<button id="finish-task" class="ui-btn ui-btn-danger" style="margin-top:8px;">检查完成状态</button>
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
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<script type="text/javascript">
function showTips(message) {
	var el=Zepto.tips({
		content:message,
		stayTime:3000,
		type:"info"
	})
	el.on("tips:hide",function(){
		console.log("tips hide");
	})
}

<#if fromWeixin>
function openApp(info){
	MiWSBridge.call('openApp', {info: info});
}
</#if>

var timeInterval;
var isApping = false;

$(function(){
//未接过任务
<#if task.canReceive>
	$(".ui-dialog-accpet").dialog("show");
<#elseif (task.userTask!=null && task.userTask.reward gt 0)>
	$('.ui-dialog-finish').dialog("show");
<#elseif task.apping || task.waitingCallback>
	isApping = true;
<#else>
	$(".ui-dialog-status").dialog("show");
</#if>
});

function onWSReady(){
	if(!MiWSBridge.isOpen) return;

	//未接过任务
	<#if task.canReceive>
		$(".ui-dialog-accpet").dialog("show");
		checkIsInstall(true);
	<#elseif (task.userTask!=null && task.userTask.reward gt 0)>
		$('.ui-dialog-finish').dialog("show");
	<#elseif task.apping || task.waitingCallback>
		isApping = true;
		checkIsInstall();
		timeInterval = setInterval(checkIsInstall, 5000);
	<#else>
		$(".ui-dialog-status").dialog("show");
	</#if>


	function checkIsInstall(isFirstCheck) {
		if(!$('#btn-open').hasClass('disabled')){
			clearInterval(timeInterval);
			return;
		}

		MiWSBridge.call('checkApp', {info:'${info!""}', pid: '${task.app.encodedAppId}'}, function(ret){
			if(ret.installed) {
				$('#btn-open').removeClass("disabled");
				$('#btn-open').click(function(){
					$('#open-app').click();
				});

				if(isApping){
					$('.ui-dialog-open').dialog("show");
				}else{
					showDialog('您已安装过该应用', "返回限时任务", function(){history.back();});
					$(".ui-dialog-accpet").dialog("hide");
				}
				clearInterval(timeInterval);
			}else if(!isApping){
				$(".ui-dialog-accpet").dialog("show");
			}
		});
	}
	
	//接收任务
	$('#accpet').click(function(){
		doReceiveTask();
	});

	function doReceiveTask(){
		MiWSBridge.call('task', {task:"${task.appTask.encodedId!0}", info:'${info!""}', pid: '${task.app.encodedAppId}'}, function(r){
			if(r.code != 0) {
				showDialog(r.message);
			} else {
				$(".ui-dialog").dialog("hide");

				isApping = true;
				clearInterval(timeInterval);
				timeInterval = setInterval(checkIsInstall, 10000);

				<#if task.appTask.directDownload>
			   	 	showDialog('接收任务成功！该任务为直接下载任务，点击“确定”将打开苹果AppStore免费下载该应用', '确定', function(){
			   	 		$('#btn-download').click();
			   	 	});
				<#else>
					showDialog('接收任务成功，请在60分钟完成任务。(下载完成后，必须在此页点击“立即打开试玩”才有效！)');
				</#if>
			}
		});
	}
};

$(function(){
	$('#btn-seach').click(function(){
		openApp("https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=");
	});

	$('#keyword').longTap(function(){
		selectText($('#word')[0]);
	});
	$('.tips-icon').click(function(){	//提示
		$(".ui-dialog-info").dialog("show");
	});
	$('#ui-dialog-yes').click(function(){$(".ui-dialog-info").dialog("hide")});

	$('button.goBack').click(function(){
		history.back();
	});

	$('#myincome,#invites').click(function(){
		var url = $(this).attr('data-url');
		openApp(url);
	});

	$('#reopen-app,#open-app').click(function(){
		<#if task.app.agreement?starts_with("NES_")>
		MiWSBridge.call('openApp', {info:'${info!""}'});
		<#else>
		openApp('${task.app.agreement!"#"}');
		</#if>
		MiWSBridge.call('launch', {info:'${info!""}'});
	});

	$('#finish-task').click(function(){
		$.getJSON('/ios/api/task/check/${task.appTask.encodedId!0}', function(data){
			showDialog(data.desc, '确定', function(){ data.status <= 0 && location.reload(); });
		});
	});

	$('#btn-download').click(function(){
		<#if task.appTask.directDownload>
			location.href = '${task.app.description!''}';
		</#if>
	});
});

function selectText(text){
 var doc = document,
		range,
		selection;
	if (doc.body.createTextRange) {
		range = document.body.createTextRange();
		range.moveToElementText(text);
		range.select();
	} else if (window.getSelection) {
		selection = window.getSelection();
		range = document.createRange();
		range.selectNodeContents(text);
		selection.removeAllRanges();
		selection.addRange(range);
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
<script src="${util.static}js/zepto-touch.min.js"></script>
<script type="text/javascript" src="${util.static}js/bridge.js"></script>
<#include "/common/footer.ftl">