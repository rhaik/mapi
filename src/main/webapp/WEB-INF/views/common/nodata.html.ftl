<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style type="text/css">
	body{background:#fff;}
	.record_ico{width:5.24em; height:4.5em; margin: <#if showHeader>5<#else>2.62</#if>em auto 0.75em; background:url(${util.static}images/img/record_ico.png) no-repeat; background-size:100% 100%;}
	.con_text p{width:100%; text-align:center; font-size:1.2em; color:#bfbfbf; padding-top:0.75em;}
	.internet_ico{width:6.93em; height:4.2em; margin:2.62em auto 0.75em;}
</style>
<div class="record">
	<div class="record_ico"></div>
	<div class="con_text">
		<p>现在还没有${info!title}</p>
		<#if description??><p>赶快去${description!info}吧</p></#if>
	</div>
</div>
<script>
	if(window.MiJSBridge){
	     onMiJSBridgeReady();
	}else{
	     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
	}
	
	function onMiJSBridgeReady(){
		<#if tasks??>
		MiJSBridge.call('clearTask');
		</#if>
	}
</script>
<#include "/common/footer.ftl">