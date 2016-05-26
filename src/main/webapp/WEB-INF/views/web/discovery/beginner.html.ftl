<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<body>
<style>
 	.ui-border-t {border-top: 1px solid #dcdcdc;background-image:none; padding-right:5em;}
	.ui-btn:before{border:none;}
	.ui-btn{color:#ff8103;border: 1px solid #ff8103;border-radius:6px;padding:0px 8px;font-size:13px;height:auto;line-height:26px;}
	.finish{color: #666666;border: 1px solid #666666;border-radius:6px;}
	.ui-list-thumb {margin: 10px 10px 6px 0px;width:50px;height:50px;margin-bottom:30px;margin-right:20px;}
	.ui-list-thumb div{font-size:13px;padding-top:52px;height:20px;width:60px;}
	.ui-list-info p{font-size:14px;color: #e80010;margin-top:10px;}
    .ui-num{color:#ff8003; font-size:0.75em; padding-right:15px; padding-top:30px;}
    .ui-list-right{margin-right:-5em; float:right}
    .cost {width: 3.75em;line-height: 1.9em;border-radius: 0.4em;margin-top: 0.5em;position: relative;}
    .cost span{font-size:0.8em; text-align:right; color:#666; position:absolute; right:0; width:7em;}
    .cost button{width:100%; font-size:0.75em; color:#666; line-height:1.625em; display:block; text-align:center; background:none; border:1px solid rgb(195, 194, 194); border-radius:0.3em; padding:0; margin-top:30px;}
    .cost.on button{color:#ff8003;border:1px solid #ff8003; border-radius:0.3em;}
</style>
<ul class="ui-list ui-list-active ui-border-tb">
 <li class="ui-border-b"  data-id="4">
        <div class="ui-list-thumb">
            <span style="background-image:url(http://cdn.erbicun.cn/20151026134507qiniu9663_.png);border-radius:10px;"></span>
            <div>收徒赚钱</div>
        </div>
        <div class="ui-list-info">
            <h4>收徒赚钱</h4>
            <p>永久分成：￥200</p>
        </div>
        <button class="ui-btn">去执行</button>
        <span style="font-size:12px; color:#ff8003; margin:60px 15px 0 0; display:block;">
        	${inviteText!''}
        <span>
    </li>
    <#--如果任务还没有完成就显示在上面 完成啦就显示在下面-->
    <#if tranTask??>
    	<#if !tranTask.received>
	    	<ul class="ui-list ui-list-active ui-border-tb">
	 		<li class="ui-border-b"  data-id="t_1">
	        <div class="ui-list-thumb">
	            <span style="background-image:url(http://cdn.erbicun.cn/20151026134507qiniu9663_.png);border-radius:10px;"></span>
	            <div>${tranTask.transArticle.name}</div>
	        </div>
	        <div class="ui-list-info">
	            <h4>${tranTask.transArticleTask.name}</h4>
	            <p>奖励：￥${util.fen2yuan(tranTask.transArticleTask.amount)}</p>
	        </div>
	         <#if tranTask.received>
	        	<button class="ui-btn finish"">已完成</button>
	        <#else>
	        	<button class="ui-btn">去执行</button>
        	</#if>
    </li>
    </#if>
    </#if>
<#list mytasks as task>
	<#if task.appTask.id != 5 || fromSafari??>
    <li class="ui-border-b"  data-id="${task.app.id}" data-encodedId="${task.appTask.encodedId}">
        <div class="ui-list-thumb">
            <span style="background-image:url(${task.app.icon!''});border-radius:10px;"></span>
            <div>${task.app.name}</div>
        </div>
        <div class="ui-list-info">
            <h4>${task.appTask.name}</h4>
            <p>奖励：￥${util.fen2yuan(task.appTask.amount)}</p>
        </div>
        <#if task.status == 2>
        	<button class="ui-btn finish"">已完成</button>
        <#else>
        	<button class="ui-btn">去执行</button>
        </#if>
        <span style="font-size:12px; color:#ff8003; margin:60px 15px 0 0; display:block;">
	       	${task.proStatusText!''}
	   </span>
    </li>
	</#if>
</#list>   
 	<#if tranTask??>
    	<#if tranTask.received>
	    	<ul class="ui-list ui-list-active ui-border-tb">
	 		<li class="ui-border-b"  data-id="t_1">
	        <div class="ui-list-thumb">
	            <span style="background-image:url(http://cdn.erbicun.cn/20151026134507qiniu9663_.png);border-radius:10px;"></span>
	            <div>${tranTask.transArticle.name}</div>
	        </div>
	        <div class="ui-list-info">
	            <h4>${tranTask.transArticleTask.name}</h4>
	            <p>奖励：￥${util.fen2yuan(tranTask.transArticleTask.amount)}</p>
	        </div>
	        <#--新手任务里面是一直都可以转发的-->
	         <#if tranTask.received>
	        	<button class="ui-btn"">已完成</button>
	        <#else>
	        	<button class="ui-btn">去执行</button>
	        </#if>
	    </li>
    </#if>
  </#if>
</ul>
<script>
$(function(){
	$('ul>li').click(function(){
		var id = $(this).attr('data-id');
		var url = '${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/task/' + $(this).attr('data-encodedId');
		if(id == '1' || id == '2'){
			url = '${util.ctx}<#if fromSafari??>/ios<#else>/web/discovery</#if>/share.html';
		}else if (id == '3'){
			url = '${util.ctx}<#if fromSafari??>/ios<#else>/web/discovery</#if>/tasks.html';
		}else if (id == '4'){
			<#if fromSafari??>
			url = '${util.ctx}/ios/share.html';
			<#else>
			url = '${util.ctx}<#if fromSafari??>/static/html/safari_frm.html?pg=</#if>/static/html/apprentice.html';
			</#if>
		}else if(id == '5'){
			url = '${util.ctx}/ios/clip';
		}else if(id == 't_1'){
			url='<#if fromSafari??>${yaoshiScheme!'#'}?action=url&url=</#if>${util.ctx}/web/article/zhuangfa.html';
		}
		
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else if(url){
			window.location.href= url;
		}
	});
});

</script>
</body>
<#include "/common/footer.ftl">