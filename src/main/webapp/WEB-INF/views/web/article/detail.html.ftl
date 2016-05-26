<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
 <link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<style type="text/css">
		table{border-collapse:collapse;border-spacing:0;}
		.header{width:100%; height:3.3em; text-align:center; line-height:3.3em; background:#19191f; position:relative; z-index:2;}
		.header span{font-size:1.3em; color:#fff;}
		.header a{position:absolute; top:0; left:0.75em; height:3em; line-height:3em; font-size:1.1em; color:#fff; background:url(${util.static}images/img/left_icon.png) 0.2em 0.85em / 0.82em 1.4em no-repeat; padding-left:1.2em;}
		.footer{width:100%; position:fixed; bottom:0; left:0; background:rgba(0,0,0,.85); z-index:2;}
		.foot_left{width:11em; height:2.625em; overflow:hidden; margin:0.9em 0 0.75em 0.9em;}
		.foot_left p{font-size:0.75em; color:#fff; line-height:2em;}
		.btn{width:6.5em; height:2.3em; line-height:2.3em; background:#ff8003; border-radius:0.3em; margin-top:1.2em; margin-right:0.9em;}
		.btn a{font-size:0.9em; display:block; text-align:center;}
		.tishi_btn{width:2.2em; height:2.2em; display:block; background:#ff8003; border-radius:2.2em; position:fixed; top:1.2em; right:0.9em;}
		.tishi_bg{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0;}
		.tishi_con{width:18em; height:12em; background:#fff; border-radius:0.8em; position:fixed; top:50%; left:50%; margin:-6em 0 0 -9em;}
		.tishi_close{width:2.2em; height:2.2em; line-height:2.2em; text-align:center; background:#d4d4d4; position:fixed; top:1.2em; right:0.9em; border-radius:2.2em; z-index:9;}
		.tishi_close a{color:#ff8003; font-size:2em; }
		.tishi_con table tr td{font-size:0.75em; padding:0.75em 0; text-align:center; color:#000;}
		.tishi_con table tr:first-child{border-bottom:1px solid #dcdcdc; color:#000;}
		.tishi_con table tr:first-child td:last-child{color:#000;}
		.tishi_con table tr:first-child td{text-align:center; font-size:0.9em;}
		.tishi_con table tr td:last-child{color:#e60012;}
		.popup_bg{width:100%; height:100%; position:fixed; top:0; left:0; background:rgba(0,0,0,.2);}
		.popup_box{width:18.7em; height:15.8em; position:fixed; top:50%; left:50%; margin:-7.9em 0 0 -9.35em; background:#fff; border-radius:0.3em;}
		.popup_box h1{font-size:1.3em; color:#000; text-align:center; font-weight:normal; margin:1em 0 0.5em;}
		.popup_box p{padding:0.5em 2em; font-size:1.04em; color:#333; text-align:left; line-height:1.5em;}
		.popup_box .button{width:15em; height:2.25em; line-height:2.25em; text-align:center; border-radius:0.3em; margin:1em auto; background:#ff8003;}
		.popup_box .button a{font-size:1.12em; display:block;}
		#iframe {overflow-y:scroll !important; overflow-x:hidden !important; overflow:hidden; height:100%; min-width: 100%; width: 10px; *width: 100%; border:none;}
</style>
<#assign shareShow='0'>
<#assign task_title="">
<#assign task_des="">
<#if vo.userArticleTask?? >
	<#if vo.userArticleTask.reward>
		<#assign shareShow='1'>
		<#assign task_title="已获得奖励">
		<#assign task_des="您已完成任务,并获得奖励">
	<#elseif vo.expired>
		<#if vo.rewardAmount gt 0>
			<#assign shareShow='1'>
			<#assign task_title="任务已结束">
			<#assign task_des="等待发放奖励">
		<#else>
			<#assign shareShow='1'>
			<#assign task_title="任务已过期">
		</#if>
	<#else>
		<#assign shareShow='2'>
	</#if>
<#elseif vo.expired>
	<#assign shareShow='1'>
	<#assign task_title="任务已过期">
	<#assign task_des="您的任务已过期！">
<#elseif !vo.transArticleTask.hasLeftTasks>
	<#assign shareShow='1'>
	<#assign task_title="任务无剩余">
	<#assign task_des="任务已经被抢光">
</#if>

<!-- header -->
<!-- 菜单按钮 -->
<div class="tishi_box">
	<div class="tishi_btn"><img src="${util.static}images/img/mune.png"></div>
	<div class="tishi_bg">
		<div class="tishi_close"><a>×</a></div>
		<div class="tishi_con">
			<table width="100%">
				<tr>
					<td width="50%">达到阅读人数</td>
					<td width="50%">共获得奖励(元)</td>
				</tr>
				<#if vo.transArticleTask.raward_type_zero gt 0>
				<tr>
					<td>1</td>
					<td>${util.fen2yuan(vo.transArticleTask.raward_type_zero)}</td>
				</tr>
				</#if>
				<tr>
					<td>5</td>
					<td>${util.fen2yuan(vo.transArticleTask.amount)}</td>
				</tr>
				<#if vo.transArticleTask.raward_type_two gt 0>
				<tr>
					<td>20</td>
					<td>${util.fen2yuan(vo.transArticleTask.raward_type_two)}</td>
				</tr>
				</#if>
				<#if vo.transArticleTask.raward_type_three gt 0>
				<tr>
					<td>80</td>
					<td>${util.fen2yuan(vo.transArticleTask.raward_type_three)}</td>
				</tr>
				</#if>
			</table>
		</div>
	</div>
</div>

<!-- 中间内容 -->
<div class="con">
		<iframe id="iframe" src="${(vo.transArticle.url)!''}" frameBorder="0" width="100%" scrolling="yes"></iframe>
</div>
	<div class="popup_bg <#if shareShow!=1>dn</#if>">
		<!-- 弹框 -->
		<div class="popup_box">
			<h1>${task_title}</h1>
			<p>${task_des}</p>
			<div class="button"><a href="javascript:;" data-href="close">返回任务列表</a></div>
			<div class="button"><a href="javascript:;" data-href="/web/my/income.html">查询我的收入</a></div>
		</div>
	</div>
<#if shareShow!=1>
<!-- 底部 -->
<footer class="footer clearfix">
	<div class="foot_left fl">
		<p>分享成功后，将在<#if vo.transArticleTask ??>${vo.transArticleTask.end_time?string('MM月dd日HH:mm')}</#if>根据阅读人数统一发放奖励 </p>
	</div>
	<div class="btn fr">
		<a onclick="forwardFrends()">转发到朋友圈</a>
	</div>
</footer>

</#if>
<script type="text/javascript">

	$(".tishi_btn").click(function(){
		$(".tishi_bg").toggle();
	})
	$(".tishi_close").click(function(){
		$(".tishi_bg").toggle();
	})
	
	var hei=$(window).height();
	$("#iframe").css({"height":hei});

	$(".mune span").click(function(){
		$(".mune_box").toggle();
		$(".mune a").toggle()
	});
	
	var hei=$(window).height();
	$("#iframe").css({"height":hei});

	$(function(){
		$('a').click(function(){
			var href = $(this).data('href');
			if(href && typeof MiJSBridge !="undefined"){
				if(href == 'close'){
					MiJSBridge.call('close');
				}else{
					MiJSBridge.call('open', {url: href});
				}
			}
		});
	});
				
	function forwardFrends(){
		if(typeof MiJSBridge !="undefined"){
			MiJSBridge.call('log', {msg: '进去分享'})
			<#if vo.expired>
				MiJSBridge.call('confirm', {title: '任务已过期-本次分享没有奖励-你确定分享'},
				 function(result){ 
					if(result.index == 1){
						doShare();
					}
				});
			<#else>
				reviceCheck();
			</#if>
		}
	}

	function doShare(){
		var imgUrl='${vo.transArticle.defaultImg}';
		MiJSBridge.call('weiXinShare', {title:"${util.jsonQuote(vo.transArticleTask.name)!''}", content:"${util.jsonQuote(vo.transArticleTask.name)!''}", image:imgUrl, url:'${shareUrl!''}'},
				function(ret){
			 		MiJSBridge.call('log', {msg: "分享的Response:"+ret})
			 		var flag = 'true';
			 		<#if !vo.expired>
			 			if(typeof _MiJS != 'undefined' && _MiJS.os.android){
							MiJSBridge.call('ajax', { url: '/web/article/article/share_success', data: {'se_id':'${se_id!''}','d_id':'${_data!''}'}, type:'POST'}
								, onShareSuccess);
						}else{
							$.ajax({
				   			type: "POST",
				   			url: '/web/article/article/share_success',
				   			data: {'se_id':'${se_id!''}','d_id':'${_data!''}'},
				   			dataType:'json',
				   			success:onShareSuccess
						});
					}
					</#if>
			 	});
	}

	function onShareSuccess(){
		MiJSBridge.call("open", {url: '/web/article/success/${vo.transArticleTask.encodedId}'});
		MiJSBridge.call('close');
	}

	function reviceCheck(){
		if(typeof _MiJS != 'undefined' && _MiJS.os.android){
			MiJSBridge.call('ajax', { url: '/web/article/article/revice',data: {'se_id':'${se_id!''}','d_id':'${_data!''}'}, type:'POST'}
				,function(result){
    				 executeResu(result);
				});
		}else{
			$.ajax({
				type: "POST",
				url: '/web/article/article/revice',
				data: {'se_id':'${se_id!''}','d_id':'${_data!''}'},
				dataType:'json',
				success:function(ret){
					 executeResu(ret);
				}
			});
		}
	}
	function executeResu(result){
		if(result.code == '0'){
			MiJSBridge.call('log', {msg: "校验通过"});
    		doShare();
    	}else{
    		MiJSBridge.call('log', {msg: "校验未通过"});
    		MiJSBridge.call('alert', {message:result.message}, function(){ console.log('OK');});
    	}
	}
</script>
<#include "/common/footer.ftl">