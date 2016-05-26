<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	.ui-border-box{background:#eee; border-bottom:1px solid #dcdcdc; overflow:hidden; padding:0 1.1em; display:block;}
	.ui-border-t{padding:0 5em; overflow:hidden; padding-top:0.625em; padding-bottom:0.625em;border:none;} 
	.ui-list-thumb { width: 3.75em;height: 3.75em;margin: 0 0 0 -5em;display: block;float: left;}
	.ui-list-thumb span{width:3.75em; height:3.75em; display:block; background-size:100%;}
	.ui-list-info{float:left; padding-right:0;}
	.ui-list-info h4 {width: 100%; height:1.5em; line-height:1.5em; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp: 1; -webkit-box-orient:vertical; font-size:1em; color:#000; padding-top:0.3em; margin-bottom: 0.5em; font-weight:normal;}
	.ui-list-info>p{padding-top:2px;padding-left:20px;color:#666666;}
	.ui-li-taskimg {font-size: 0.8em; color: #666;display: block; margin-top: 0;line-height: 1.3em; background:url(/static/images/task_04.png) left center no-repeat;background-size:14px 15px;height:auto;}
	.ui-list-right{margin-right:-5em; float:right}
	.cost {width: 3.75em;line-height: 1.9em;border-radius: 0.4em;margin-top: 0.5em;position: relative;}
	.cost span{font-size:0.8em; text-align:right; color:#666; position:absolute; right:0; width:7.5em;}
	.cost button{width:100%; font-size:0.75em; color:#666; line-height:1.625em; display:block; text-align:center; background:none; border:1px solid rgb(195, 194, 194); border-radius:0.3em; padding:0;}
	.content .on button{color:#ff8003;border:1px solid #ff8003; border-radius:0.3em;}
	.content .on span{font-size:0.8em;color:#ff8003;}
	.content .on .ing{float:right; background:#ff8003; margin-top:0.4em;}
	.ing_txt button{background:#ddd; border-radius:0.4em;}
	.tishi{padding:0 4%; width:92%; height:2em; line-height:2em; background:#feab58; font-size:0.8em; font-weight:bold; color:#fff; position:fixed; top:0; left:0; z-index:10;}
	.tishi em{width:2em; display:block; float:right;}
	.ui-list{margin-top:1.5em;}
</style>
 <link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<div class="content">
	<#if hint_text??>
		<div class="tishi">${hint_text}<em><img src="${util.static}images/img/mune.png" /></em></div>
	</#if>
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
	<a class="ui-border-box <#if task.valid>bg_f</#if>">
	    <li class="ui-border-t<#if !task.valid> ui-li-finish</#if>" data-id="${task.transArticleTask.encodedId}" data_transArticleId="${task.transArticle.encodedId}">
	        <div class="ui-list-thumb">
	            <span style="background:url(${(task.transArticle.defaultImg)!''})0 0 / 100% 100% no-repeat; border-radius:10px;"></span>
	        </div>
	        <div class="ui-list-info">
	            <h4>${(task.transArticleTask.name)!''}</h4>
	          	  <p class="ui-li-taskimg">剩余${(task.transArticleTask.leftNumForShow)!'0'}份</p>
	        </div>
	        <div class="ui-list-right">
	       	 	<div class="cost <#if task.valid>on<#else>ing_txt</#if>">
	        	<#if task.canReceive>
	        		<button >最高${util.fen2yuan(task.transArticleTask.maxAmount)}元</button>
	        	<#elseif task.processing>
	        		<button>进行中</button>
	        	<#else>
	        		<button>
	        			<#if task.rewardAmount gt 0>
	        				+${util.fen2yuan((task.rewardAmount)!0)}元
	        			<#else>	
	        				最高${util.fen2yuan(task.transArticleTask.maxAmount)}元
	        			</#if>
	        		 </button>
	        	</#if>
	        	<span>
	        		${task.taskPromptText}
	    	    </span>
	            </div>
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
		if(typeof MiJSBridge=="object") {
			MiJSBridge.on("onRefreshPage", function(){ location.reload(); } );
			if(having_count == 0){
				MiJSBridge.call('clearTask');
			}
		}
	}
	
	$('a>li').click(function(e){
		//if($(this).hasClass('ui-li-finish')) return ;
		var url = '${util.ctx}/web/article/' + $(this).attr('data-id');
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else {
			window.location.href= url;
		}
	});
});

</script>
<#include "/common/footer.ftl">