<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5;}
	.ui-border-tb {border-top: #e0e0e0 1px solid;border-bottom: #e0e0e0 1px solid;background-image: none;}
	.ui-border-t {border-top: 1px solid #dcdcdc;background-image:none;}
	.content{padding-top:2.75em;}
	.ui-list>li{margin:auto;}
	.ui-list-thumb {width:60px;height:60px;margin: 10px 18px;}
	.ui-list-info>h4{font-size:16px;margin-bottom:6px;}
	.ui-list-info>p{padding-top:2px;padding-left:20px;color:#666666;}
	.ui-li-taskimg{background:url(${util.static}images/task_04.png) left center no-repeat;background-size:14px 15px;height:auto;}
	.ui-list-right{margin-right:16px;}
	.ui-btn:before{border: none;font-weight:400;}
	.ui-list .ui-btn{right:16px;top:inherit;margin-top:20px;}
	.ui-btn{color:#ff8103;width:60px;height:26px;font-size:12px;line-height:24px;border: 1px solid #ff8103;border-radius:5px;padding:0px;}
	.haveing{background-color: #ff8003;color:#ffffff;background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0.5,#ff8003),to(#ff8003));}
	.ui-btn-finish,.ui-li-finish{background-color:#eeeeee;background-image: -webkit-gradient(linear,left top,left bottom,color-stop(0.5,#eeeeee),to(#eeeeee));}
	.ui-btn-finish{color: #666666;border: 1px solid #666666;border-radius:5px;}
	.down-message{position:relative;top:46px;color:#666666;font-size:12px;}

	.tishi_box{width: 100%; height:2.75em; position:fixed; top:0px; left:0; z-index:10;}
	.active span{font-size:0.9em; color:#fff; text-decoration: underline;}
	.prompt span{font-size:0.9em; color:#fff;}
	.active{height:2.75em; line-height:2.75em; background:#fd2351; position:absolute; top:0; left:0; z-index:2; width:100%; text-indent:0.5em;}
	.active a{float:right; font-size:1.5em; padding-right:0.3em; cursor:pointer; text-align:center; color:#fff;}
	.prompt{height:2.75em; line-height:2.75em; background:#ff8003; position:absolute; top:0; left:0; width:100%; text-indent:0.5em;}
	.prompt img{float:right; width:1.75em; padding-top:0.5em; display:block; padding-right:0.5em;}

	.ui-list-info h4 em{width:1.875em; height:0.75em; display:inline-block; margin-left:0.625em; vertical-align:middle;}
	.ui-list-info h4 em.tip1{background:url(${util.static}images/img/tip1.png) 0 0 / 100% 100% no-repeat;}
	.ui-list-info h4 em.tip2{background:url(${util.static}images/img/tip2.png) 0 0 / 100% 100% no-repeat;}

	.ui-li-text{overflow: hidden;color:#666;}
	.ui-li-text span{display: block; float: left;}
	.ui-li-text .ui-li-taskimg{float: left; margin-left:0.625em; padding-left:20px;}
	.yugao{background: #f0eff5; width: 100%; padding:0.94em 0; margin-top: -1px;}
	.yugao p{margin:0 0.94em; border: 1px dashed #c9c9c9; color: #c9c9c9; height:2.34em; line-height:2.34em; text-align:center;}
	.jianju{width:100%; height:0.625em; background: #f0eff5; margin-top: -1px;}
	.markup{width:5.3em; height:3.4em; background:url(${util.static}images/img/jj_bg.png) no-repeat; border-radius:0.2em; background-size:100%; margin:15px 15px 0 0;}
	.markup p{text-align:center;}
	.markup p b{font-weight:normal; font-size:0.75em; color:#fff;}
	.markup p em{font-style:normal; font-size:1.32em; color:#fff;}
	.markup span{width:100%; display:block; font-size:0.75em; line-height:2.5em; color:#fff; text-align:center;}
	@media screen and (max-width:320px){.ui-li-text{font-size:0.8em;}}
</style>
<div class="content">
	<#if hint_text??>
		<div class="tishi_box">
			<div class="prompt"><span>${hint_text}</span><img src="${util.static}images/img/mune.png" /></div>
		</div>
	</#if>
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
		<#if task.canReceive || task.apping || task.waitingCallback || task.app.id == 96 || task.appTask.valid>
	    <li class="ui-border-t<#if !task.valid && !task.waitingCallback> ui-li-finish</#if>" prop-id="${task.app.agreement!''}" bd-id="${task.app.bundle_id!''}" data-id="${task.appTask.encodedId}" data_appId="${task.app.encodedAppId}">
	        <div class="ui-list-thumb">
	            <span style="background-image:url(${task.app.icon!''});border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${task.appTask.keywords}<#if (task.requireType > 0)><em class="tip${task.requireType}"></em></#if></h4>
	          	  <#if task.app.id==96> <p class="ui-li-taskimg">永久分成</p><#else><p class="ui-li-taskimg">剩余${task.appTask.leftNumForShow}份</p></#if>
	        </div>
			<#if task.app.id != 96 && task.canReceive && ((task.app.payWay  && task.appTask.amount gt 250) || (!task.app.payWay && task.appTask.amount gt 150) )>
				<div class="markup">
					<p><b>+</b><em>${util.fen2yuan(((task.appTask.amount)*(shareRate!1))!0)}</em><b>元</b></p>
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
		<#else>
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
<script>
$(function(){
	if(window.MiJSBridge){//window.MiJSBridge对象不为空，可以直接调用
	     onMiJSBridgeReady();
	}else{
	     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
	}
	
	function onMiJSBridgeReady(){
		MiJSBridge.call('startDetect');
		var appid = [];
		var agreement =[];
		setTimeout(function(){
			if(appid.length > 0){
				$.ajax({type: "POST",url: "${util.ctx}/api/v1/task/repost_installed",data:{appid:appid,agreement:agreement},
					success: function(r){
						console.debug("success response"+r);
					},
		   			error: function(xhr) {
						console.debug("error response"+xhr);
		   			}
				});
			 }	
		},3000);
		
		var having_count=0;
		
		$('ul>li').each(function(index){
			if($(this).hasClass('ui-li-finish'))
				return;
			var tthis = this;
			var haveing = $(this).find('.haveing');
			var prop = $(tthis).attr('prop-id');
			var bd = $(tthis).attr('bd-id');
			if(haveing.length > 0) {
				if(haveing.attr('data-status') == "0") {
					MiJSBridge.call('checkApp', {protocol: prop, bundle : bd}, function(ret){ 
						if(ret.installed) {
							$(tthis).find(".down-message").html("等待试用完成");
						}
					});
				}
				having_count=having_count+1;
				return ;
			}
			MiJSBridge.call('checkApp', {protocol: prop, bundle : bd}, function(ret){
				if(ret.installed) {
					appid.push($(tthis).attr("data_appId"));
					agreement.push(prop);
					$(tthis).addClass("ui-li-finish").find('.ui-btn').addClass('ui-btn-finish');
					$(tthis).find('.ui-li-taskimg').html("剩余0份");
					$(tthis).find(".down-message").html("任务已被抢光");
					$(tthis).remove();
					$('.ui-list').append(tthis);
				}
			});
		});
		
		if(typeof MiJSBridge=="object") {
			MiJSBridge.on("onRefreshPage", function(){ location.reload(); } );
			if(having_count == 0){
				MiJSBridge.call('clearTask');
			}
		}
	}
	
	$('ul>li').click(function(e){
		if($(this).hasClass('no-click')) return ;
		var url = '${util.ctx}/web/task/' + $(this).attr('data-id');
		if('abcd.money.e' == $(this).attr('bd-id')){
			url='/static/html/apprentice.html';
		}
		if($(this).hasClass('ui-li-finish')){
			 showTips($.trim($(this).find('.down-message').html()));
			return ;
		}
		openUrl(url);
	});

	$('#dec-panel span').click(function(){
		openUrl('https://mp.weixin.qq.com/s?__biz=MjM5MDI5NDkwMQ==&mid=402441901&idx=1&sn=3ac82e5242b818947ee5ebb71ef699c2&scene=0');
	});

	$('#dec-panel a').click(function(){
		$('#dec-panel').hide();
	});

	function openUrl(url){
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else {
			window.location.href= url;
		}
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
</script>
<#include "/common/footer.ftl">