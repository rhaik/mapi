<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	.ui-border-box{background:#eee; border-bottom:1px solid #dcdcdc; overflow:hidden; padding:0 1.1em; display:block;}
	.ui-border-t{padding:0 5em; overflow:hidden; padding-top:0.625em; padding-bottom:0.625em;border:none; position:relative;} 
	.ui-list-thumb { width: 3.75em;height: 3.75em;margin: 0 0 0 -5em;display: block;float: left;}
	.ui-list-thumb span{width:3.75em; height:3.75em; display:block; background-size:100%;}
	.ui-list-info{float:left; padding-right:0;}
	.ui-list-info h4 {width: 100%; height:1.5em; line-height:1.5em; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp: 1; -webkit-box-orient:vertical; font-size:1em; color:#000; padding-top:0.3em; font-weight:normal;}
	.ui-list-info span{font-size:13px; color:#666; display:block; line-height:18px;}
	.ui-list-info>p{padding-top:2px;padding-left:20px;color:#666666;}
	.ui-li-taskimg {font-size: 0.8em; color: #666;display: block; margin-top: 0;line-height: 1.3em; background:url(/static/images/task_04.png) left center no-repeat;background-size:14px 15px;height:auto;}
	.ui-list-right{margin-right:-5em; float:right}
	.ui-list button.ui-btn{right:0; border:1px solid #ff8003; color:#ff8003;}
	.cost {width: 3.75em;line-height: 1.9em;border-radius: 0.4em;margin-top: 0.5em;position: relative;}
	.cost span{font-size:0.8em; text-align:right; color:#666; position:absolute; right:0; width:7.5em;}
	.cost button{width:100%; font-size:0.75em; color:#666; line-height:1.625em; display:block; text-align:center; background:none; border:1px solid rgb(195, 194, 194); border-radius:0.3em; padding:0;}
	.content .on button{color:#ff8003;border:1px solid #ff8003; border-radius:0.3em;}
	.content .on span{font-size:0.8em;color:#ff8003;}
	.content .on .ing{float:right; background:#ff8003; margin-top:0.4em;}
	.ing_txt button{background:#ddd; border-radius:0.4em;}
	.tishi{padding:0 4%; width:92%; height:2em; line-height:2em; background:#feab58; font-size:0.8em; font-weight:bold; color:#fff; position:fixed; top:0; left:0; z-index:10;}
	.tishi em{width:2em; display:block; float:right;}
	.bar_top{padding-left:20px; line-height:30px; font-size:12px; color:#666;}
</style>
 <link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<div class="content">
	<#if hint_text??>
		<div class="tishi">${hint_text}<em><img src="${util.static}images/img/mune.png" /></em></div>
	</#if>
	<div class="bar_top">本周排行榜</div>
	<ul class="ui-list ui-border-tb">
	<#list games as task>
	<a class="ui-border-box">
	    <li class="ui-border-t" data-id="${task.encodedId}" data-platform="${task.platform}" data-url="${task.url}" data-bundle="${task.bundle_id!''}">
	        <div class="ui-list-thumb">
	           <span style="width:60px; height:60px; overflow:hidden; border-radius:10px;"><img src="${(task.icon)!''}"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${(task.name)!''}</h4>
	            <#--
	          		<span>${(task.player_num)}人在玩</span>
	          	-->
	          	<span>${task.title!''}</span>
	        </div>
	        <div class="ui-list-right">
        		<button class="ui-btn">
        			<#if task.platform == 3>
        				打开
        			<#else>	
        				下载
        			</#if>
        		 </button>
	        	<span>
	    	    </span>
	        </div>
       	 	
	    </li>
	  </a>
	</#list>
	</ul>
</div>
<script src="${util.jquery}"></script>
<script>
$(function(){
	if(window.MiJSBridge){//window.MiJSBridge对象不为空，可以直接调用
	     onMiJSBridgeReady();
	}else{
	     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
	}
	
	function onMiJSBridgeReady(){
		$('a>li').each(function(index){
			var paltform = $(this).attr("data-platform");
			if(paltform == 3){
				return;
			}
			var bundle=$(this).attr("data-bundle");
			if(!bundle||bundle.length==0){
				return ;
			}
			var that = this;
			if(typeof MiJSBridge !="undefined"){
				MiJSBridge.call('checkApp', {protocol: '', bundle:bundle}, function(ret){
					if(ret.installed){
						$(that).addClass("installed");
						$(that).find(".ui-btn").html("打开");
					}
				});
			 }
		});
	}
	
	$('a>li').click(function(e){
		if($(this).hasClass('installed')){
			MiJSBridge.call('launchApp', {protocol: '', bundle:$(this).attr("data-bundle")});
			return ;
		} 
		var url =  $(this).attr("data-url");
		var paltform = $(this).attr("data-platform");
		if(paltform == 1){
			url = '${util.ctx}/web/game/detail/' + $(this).attr('data-id');
		}
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else {
			window.location.href= url;
		}
	});
});

</script>
<#include "/common/footer.ftl">