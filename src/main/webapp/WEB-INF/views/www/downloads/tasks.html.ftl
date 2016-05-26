<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5;}
	.ui-border-tb {border-top: #e0e0e0 1px solid;border-bottom: #e0e0e0 1px solid;background-image: none;}
	.ui-border-t {border-top: 1px solid #dcdcdc;background-image:none;}
	.ui-list{margin-top:10px;}
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
	.tishi{padding:0 4%; width:92%; height:2em; line-height:2em; background:#feab58; font-size:0.8em; font-weight:bold; color:red; position:fixed; top:0; left:0; z-index:10;}
	.tishi em{width:2em; display:block; float:right; height:2em; overflow:hidden;}
	.popup_bg{width:100%; height:100%; position:fixed; top:0; left:0; background:rgba(0,0,0,.5); z-index:9;}
	.popup_box{margin:30% 35px; background:#fff; border-radius:10px; padding-bottom:30px;}
	.loding_top{padding:15px 20px; overflow:hidden;}
	.lode_text span{font-size:18px; color:#000; margin-top:13px; display:block;} 
	.erect{padding:8px 0; text-align:center; font-size:14px; color:#000; border-top:1px solid #c1c1c1; border-bottom:1px solid #c1c1c1;}
	.Install_btn a.on{background:#ff8003;}
	.serch_box{width:100%; height:100%; position:fixed; top:0; left:0; background:#7f7f7f; z-index:9;}
	.serch_box img{padding-top:20px;} 
	
	.lode_icon{width:70px; height:70px; margin-right:10px; float:left;}
	.lode_text p{font-size:13px; color:#000; margin-top:4px;} 
	.Install_btn a{margin:20px 35px 0; height:40px; line-height:40px; border-radius:5px; font-size:16px; color:#fff; background:#c1c1c1; display:block; text-align:center;}

	.safari_open{width:100%; height:100%; position:fixed; left:0; top:0; background:rgba(0,0,0,0.6); z-index:19; text-align:right;}
	.safari_open img{margin:5px 10px 0px; }
</style>
<div class="content">
	<#if ios><div class="tishi">如果APP闪退，请删除并重新下载秒赚大钱！<em><img width="100%" src="${util.static}images/img/mune.png" /></em></div></#if>
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
	    <li class="ui-border-t">
	        <div class="ui-list-thumb">
	            <span style="background-image:url(${task.app.icon!''});border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${task.appTask.keywords}</h4>
	          	  <p class="ui-li-taskimg">剩余${task.appTask.total_task}份</p>
	        </div>
	        <div class="ui-list-right">
	        	<button class="ui-btn">+ ${util.fen2yuanS(task.appTask.amount!0)} 元</button>
       	 	</div>
	    </li>
	</#list>
	</ul>
</div>

<div id="popup_div">
<#if isWeixin>
	<div class="safari_open">
		<img  style="width:50%;" src="${util.ctx}/static/images/wx_download.png" />
	</div>
<#else>
	<div class="popup_bg">
		<div class="popup_box">
			<div class="loding_top">
				<div class="lode_icon fl"><img width="100%" src="${util.ctx}/static/images/img/logo.png" /></div>
				<div class="lode_text fl">
					<span>秒赚大钱</span>
					<p>官方苹果手机赚钱神器</p>
				</div>
			</div>
			<div class="erect">请按步骤完成安装</div>
			<div class="Install_btn">
				<a class="on" id="install-btn">安装并打开秒赚钥匙</a>
				<a id="open-btn">已安装，请直接打开</a>
			</div>
		</div>
	</div>
</#if>
</div>

<script>
$(function(){
	$(".safari_open").click(function(){
		$(this).hide();
	});

	$('ul>li').click(function(e){
	<#if isWeixin>
		$(".safari_open").show();
	<#else>
		$('.popup_bg').show();
	</#if>
	});


	$("#install-btn").click(function(){
		location.href = "/www/downloads/safari";
	});
	$("#open-btn").click(function(){
		location.href = '${yaoshiScheme!""}';
	});
});

</script>
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">