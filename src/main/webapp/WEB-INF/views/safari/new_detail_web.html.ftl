<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<link rel="stylesheet" type="text/css" href="${util.static}/css/xianshi.css">
<style>
	body{background-color:#fff;}
</style>
<div class="wrap">
	<div class="head_top clearfix">
		<div class="head_top_pic fl"><img src="${task.app.icon!''}" /></div>
		<div class="head_text fl">
			<h1>${task.appTask.keywords}</h1>
			<p><em><img width="100%" src="${util.static}images/task_11.png" /></em>${task.app.download_size!0}M&nbsp;&nbsp;&nbsp;&nbsp;<em><img width="100%" src="${util.static}images/task_12.png" /></em>剩余时间:${task.expireTime}分钟</p>
		</div>
		<div class="title"><em>+</em><b>${util.fen2yuan(task.appTask.amount!0)}</b><i>元</i></div>
	</div>
	<div class="step">
		<h1>任务步骤:</h1>
		<#if !task.appTask.directDownload>
		<p>1、长按虚线框，拷贝关键词</p>
		<p>2、在AppStore中粘贴并搜索</p>
		<p>3、找到图标对应的应用(在第<i>${task.appTask.current_rank}</i>个左右)并下载安装</p>
		<p><i>4、${task.appTask.description}</i></p>
		<#else>
			<p>1、 点击“下载安装”按钮，从苹果AppStore免费下载安装该应用</p>
			<p><i>2、${task.appTask.description}</i></p>
		</#if>
	</div>
	<#if !task.appTask.directDownload>
	<div class="icon" id="keyword_icon">
		<em>长按复制（必须）<br/>自动跳转到App Store</em>
		<i><img src="${util.static}images/img/bot.png" /></i>
		<div id="copy-area"><div id="keyword"><span id="word">${task.appTask.keywords}</span></div></div>
	</div>
	</#if>
	<#if task.appTask.directDownload>
	<div class="btn">
		<a href="javascript:;" id="btn-download">下载安装</a>
	</div>
	</#if>
	<div id='btn-open' style="display:none;" class="disabled"></div>
</div>
<div class="ui-dialog ui-dialog-accpet">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1>参与任务赢取<i>${util.fen2yuan(task.appTask.amount!0)}</i>元奖励</h1>
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
            <div class="info-tips">您已在${task.userTask.finishtime?string("yyyy-MM-dd")}日完成了此任务，<#if task.userTask.reward>并获得了<i>${util.fen2yuan(task.appTask.amount!0)}</i>元奖励。<#else>正在审核中。</#if></div>
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
	!isApping && $(".ui-dialog-accpet").dialog("show");
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
				$(".ui-dialog-accpet").remove();

				isApping = true;
				clearInterval(timeInterval);
				timeInterval = setInterval(checkIsInstall, 10000);

				<#if task.appTask.directDownload>
			   	 	showDialog('接收任务成功！该任务为直接下载任务，点击“确定”将打开苹果AppStore免费下载该应用', '确定', toDownload);
				<#else>
					showDialog('接收任务成功，请在60分钟完成任务。(下载完成后，必须在此页点击“立即打开试玩”才有效！)');
				</#if>
			}
		});
	}
};
$(function(){
	
	$('.tips-icon').click(function(){	//提示
		$(".ui-dialog-info").dialog("show");
	});
	$('#ui-dialog-yes').click(function(){$(".ui-dialog-info").dialog("hide")});
	$('#keyword,#word').longTap(function(){
		selectText($('#word')[0]);
	})
	$('#keyword').on('copy', function (event) {
		openApp("https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=");
	});

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

	$('#btn-download').click(toDownload);
});

function toDownload(){
	<#if task.appTask.directDownload>
		location.href = '${task.app.description!''}';
	</#if>
}

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