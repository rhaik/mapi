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
.ui-li-fileszie,.ui-li－second{font-size:13px;background:url(${util.static}images/img/task_16.png) left center no-repeat;color:#666666;background-size:16px 15px;padding-left:18px;}
.ui-list-info .ui-li-taskimg{margin-left:6px;padding-left:16px;color:#666666;font-size:13px;}
.ui-li-taskimg{background:url(${util.static}images/task_12.png) left center no-repeat;background-size:13px 13px;height:auto;}
.ui-li-taskimg span{color:#ff8003;padding:0px 2px;}
	.ui-btn{color:#ff8103;font-size:12px;line-height:26px;height:26px;width:64px;padding:0px;}
	.ui-btn:before{border: 1px solid #ff8103;border-radius:10px;}
	.ui-li-first{height:30px;margin:8px 20px 0px 20px;padding-top:12px;padding-left:24px;background:url(${util.static}images/task_13.png) left center no-repeat;background-size:16px 18px;}
	.ui-li-bz{margin-left:20px;margin-bottom:12px;}
	.ui-li－second{height:30px;margin:8px 20px 0px 20px;padding-top:12px;padding-left:24px;}
	.ui-list-center{  padding-top: 10px;display: -webkit-box;margin:0 auto;text-align: center;}
	.ui-list-center span{width:60px; display:block; margin:0 auto 5px;}
	.ui-list-center .ui-list-icon{width:60px;border-radius: 12px;text-align: center; display: block;background-repeat: no-repeat; background-size: 60px 60px;}
	.ui-li-icon i{color:#ff8003;padding:0px 2px;font-size:16px;}
	.ui-li－thrid{height:30px;margin:8px 20px 0px 20px;padding-top:12px;padding-left:24px;background:url(${util.static}images/img/task_17.png) left center no-repeat;background-size:16px 18px;}
	.ui-list>li.ui-list-img{width:150px; height:160px;}
	.ui-list-img img{width:150px; height:121px; margin:20px auto;}
	
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

	.tip_bg{width:100%; height:100%; position:fixed; top:0; left:0; background:rgba(0,0,0,.5); z-index:2;}
	.tip{width:92%; padding:0 4%; position:absolute; top:1.4em; left:0; z-index:2;}
	.tip span{width:1.4em; height:1.4em; line-height:1.4em; text-align:center; display:block; background:#2ca0fd; border-radius:1.5em; float:right; margin:-9px 11px 0 0;}
	.tip span a{font-size:1.6em; color:#fff;}
	.tishi{background:#2ca0fd; width:100%; padding:0.9em 0; margin-top:4.6em; border-radius:0.5em; color:#fff;}
	.tishi p{padding:0 1.25em 0.78em; line-height:1.4em;}
	.tab{margin:0 1.25em; border:1px solid #fff;}
	.tab table{width:100%; border-collapse:collapse;}
	.tab tr{border-bottom:1px solid #fff;}
	.tab tr:last-child{border-bottom:none;}
	.tab tr td{text-align:center; border-right:1px solid #fff; padding:0.3em 0;}
	.tab tr td:last-child{border-right:none;}
	.line{width:2px; height:3.3em; background:#2ca0fd; float:right; margin:1.3em -0.7em 0 0;}
</style>
<div class="content">
	<ul class="ui-list ui-list-function ui-border-b" >
	 	<li class="tips">
	        <div class="ui-list-info">
	            <p>完成最高赢取奖励<span>${util.fen2yuan(task.transArticleTask.maxAmount!0)}</span>元</p>
	        </div>
	        <div class="ui-list-thumb"><span class="tips-icon"></span></div>
        </li>
        <li style="background-color: #f0eff5;height:14px;"></li>
	    <li class="ui-border-tb">
	        <div class="ui-list-thumb">
	            <span style="background-image:url(${task.transArticle.img!''});border-radius:12px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${(task.transArticleTask.name)!''}</h4>
	            
	        </div>
        </li>
	</ul>

	<ul class="ui-list ui-border-tb">
		<li>
	        <div class="ui-list-info">
	            <p class="ui-li-first">第一步</p>
	        </div>
	        <div class="ui-list-right">
	        	<button class="ui-btn" id="btn-copy">复制链接</button>
	        </div>
	    </li>
	    <li class="ui-border-b">
	    	<div class="ui-list-info"><div class="ui-li-bz">复制文章链接</div></div>
	    </li>
	    
	    <li>
	        <div class="ui-list-info">
	            <p class="ui-li－second">第二步</p>
	        </div>
	        <div class="ui-list-right">
	        	<button class="ui-btn" id="btn-copy-zgh">复制公众号</button>
	        </div>
	    </li>
	    <li>
	    	<div class="ui-list-info">
	    		<#--这里的写死在 下面js中的复制-->
	    		<div class="ui-li-bz">手动找到微信中公众号：“ <span> shuimu444 </span> ” </div>
	    	</div>
	    </li>
	    <li>
	        <div class="ui-list-center">
	            <span class="ui-list-icon" style="background-image:url(${util.static}images/img/shuimu.jpg);"><span style="padding-top:70px;">水木读书</span></span>
	        </div>
	        <div class="ui-list-right">
	        	<button class="ui-btn btn-seach">打开微信</button>
	        </div>
        </li>

        <li class="ui-border-t" style="margin-top:12px;">
	        <div class="ui-list-info">
	            <p class="ui-li－thrid">第三步</p>
	        </div>
	        <div class="ui-list-right">
	        	<button class="ui-btn btn-seach" >打开微信</button>
	        </div>
	    </li>
	     <li class="ui-border-b">
	    	<div class="ui-list-info"><div class="ui-li-bz">将复制的链接发送到水木读书公众号，然后在公众号 内打开链接，并点击文章右上角分享到朋友圈</div></div>
	    </li>
	    <li class="ui-list-img">
	    	<img src="${util.static}images/img/list_img.png" />
	    </li>
	</ul>
</div>

<div class="tip_bg">
	<div class="tip clearfix">
		<span><a>×</a></span>
		<div class="line"></div>
		<div class="tishi">
			<p>点击文章标题进入，并将内容分享至朋友圈，好友阅读即可获得收入，阅读人数越多则收入越高哦！奖励如下：</p>
			<div class="tab">
				<table>
					<tr>
						<td width="40%">达到阅读人数</td>
						<#if task.transArticleTask.raward_type_zero gt 0>
						    <td width="15%">1</td>
						</#if>
						<td width="15%">5</td>
						<#if task.transArticleTask.raward_type_two gt 0>
							<td width="15%">20</td>
						</#if>
						<#if task.transArticleTask.raward_type_three gt 0>
							<td width="15%">80</td>
						</#if>
					</tr>
					<tr>
						<td width="40%">共获得奖励</td>
						<#if task.transArticleTask.raward_type_zero gt 0>
							<td width="15%">${util.fen2yuan(task.transArticleTask.raward_type_zero)}</td>
						</#if>
						<td width="15%">${util.fen2yuan(task.transArticleTask.amount)}</td>
						<#if task.transArticleTask.raward_type_two gt 0>
							<td width="15%">${util.fen2yuan(task.transArticleTask.raward_type_two)}</td>
						</#if>
						<#if task.transArticleTask.raward_type_three gt 0>
							<td width="15%">${util.fen2yuan(task.transArticleTask.raward_type_three)}</td>
						</#if>
					</tr>
				</table>
			</div>
		</div>
	</div>
</div>

<div class="ui-dialog ui-dialog-accpet">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1>参与任务最多赢取<i>${util.fen2yuan(task.transArticleTask.maxAmount!0)}</i>元奖励</h1>
            <div class="ui-btn-wrap">
			    <button id="accpet" class="ui-btn ui-btn-danger" style="color:#fff;;width:100%;font-size:18px;margin:20px 0px 0px 0px;;height:40px;background-color:#ff8003;background-image:none;">立即参加</button>
			</div>
        </div>
    </div>        
</div>

<#if task.userArticleTask!=null && task.userArticleTask.completed>
<div class="ui-dialog ui-dialog-finish">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h3>已完成</h3>
            <div class="info-tips">您已在${task.userArticleTask.finishtime?string("yyyy-MM-dd")}日完成了此任务，<#if task.userArticleTask.reward>并获得了<i>${util.fen2yuan(task.rewardAmount!0)}</i>元奖励。<#else>正在审核中。</#if></div>
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
            <h3>${task.taskPromptText}</h3>
            <div class="ui-btn-wrap" style="margin-top:16px;">
         		<button id="task-list"  data-url="${util.ctx}/web/article/tasks.html"  class="ui-btn ui-btn-danger" style="color:#fff;width:100%;font-size:18px;height:40px;background-color:#ff8003;background-image:none;">返回转发任务</button>
        	</div>
        </div>
    </div>        
</div>

<script>
$(function(){
	//未接过任务
	<#if task.canReceive>
		$(".ui-dialog-accpet").dialog("show");
	<#elseif (task.userArticleTask!=null && task.userArticleTask.reward gt 0)>	//已发放奖励
		$('.ui-dialog-finish').dialog("show");
	<#elseif (task.userArticleTask!=null && task.userArticleTask.completed)>	//已完成任务，但是未发放奖励
		$(".ui-dialog-status").dialog("show");
	<#elseif !task.processing>
		$(".ui-dialog-status").dialog("show");
	</#if>
	if(window.MiJSBridge){	//window.MiJSBridge对象不为空，可以直接调用
	     onMiJSBridgeReady();
	}else{
	     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
	}
	function onMiJSBridgeReady(){
	}
	var copy_data="${shareUrl!''}";
	//复制
	$('#btn-copy').click(function(){
		if(typeof MiJSBridge == "object")
			MiJSBridge.call('copy', {message: copy_data},function(){MiJSBridge.call('toast', {message: '复制成功'});});
	});
	
	$(".btn-seach").click(function(){
		if(typeof MiJSBridge == "object") MiJSBridge.call('launchApp', {protocol: 'weixin://', bundle:'com.tencent.xin'});
	});
	$("#btn-copy-zgh").click(function(){
		if(typeof MiJSBridge == "object") MiJSBridge.call('copy', {message: 'shuimu444'},function(){MiJSBridge.call('toast', {message: '复制成功'});});
	});
	$("#accpet").click(function(){
		doReceiveTask();
	});
	//接收任务
	function doReceiveTask(){
		if(typeof _MiJS != 'undefined' && _MiJS.os.android){
							MiJSBridge.call('ajax', { url: '/web/article/article/share_success', data: {'se_id':'${se_id!''}','d_id':'${_data!''}'}, type:'POST'
								, success:function(r){
		   							onShareSuccess(r);
		   						}
		   				});
			}else{
				$.ajax({
		   			type: "POST",
		   			url: '/web/article/article/share_success',
		   			data: {'se_id':'${se_id!''}','d_id':'${_data!''}'},
		   			dataType:'json',
		   			success:function(r){
		   				onShareSuccess(r);
		   			}
				});
		}
	}
	
	function onShareSuccess(rest){
		$(".ui-dialog").dialog("hide");
		if(rest ){
			if(rest.shareUrl){
				copy_data=rest.shareUrl;
				showTips("接收任务成功,请按要求完成任务");
			}else{
				showTips(rest.message);
			}
		}
	}
	
	$('.tips-icon').click(function(){	//提示
		$(".ui-dialog-info").dialog("show");
	});
	$('#ui-dialog-yes').click(function(){$(".ui-dialog-info").dialog("hide")});
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


});
</script>
<script>
	$('.tip_bg').hide();
	$('.ui-list-thumb span').click(function(){
		$('.tip_bg').show();
	})
	$('.tip span').click(function(){
		$('.tip_bg').hide();
	})
</script>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<#include "/common/footer.ftl">