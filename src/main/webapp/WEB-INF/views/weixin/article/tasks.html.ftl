<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style>
  .ui-border-box{border-bottom:1px solid #dcdcdc; overflow:hidden; padding:0 1.1em; display:block;}
  .ui-border-t{padding:0 5em; overflow:hidden; padding-top:0.9em; padding-bottom:0.9em;border:none;}
  .ui-list-thumb { width: 3.75em;height: 3.75em;margin: 0 0 0 -5em;display: block;float: left;}
  .ui-list-thumb span{width:3.75em; height:3.75em; display:block; background-size:100%;}
  .ui-list-info{float:left; padding-right:0;}
  .ui-list-info h4 {width: 100%; height:1.5em; line-height:1.5em; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp: 1; -webkit-box-orient:vertical; font-size:1em; color:#000; padding-top:0.3em; margin-bottom: 0.5em; font-weight:normal;}
  .ui-list-info>p{padding-top:2px;padding-left:20px;color:#666666;}
  .ui-li-taskimg {font-size: 0.8em; color: #666;display: block; margin-top: 0;line-height: 1.3em; background:url(${util.static}images/weixin/task_04.png) left center no-repeat;background-size:14px 15px;height:auto;}
  .ui-list-right{margin-right:-5em; float:right}
  .cost {width: 3.75em;line-height: 1.9em;border-radius: 0.4em;margin-top: 0.5em;position: relative;}
  .cost span{font-size:0.8em; text-align:right; color:#666; position:absolute; right:0; width:7em;}
  .cost button{width:100%; font-size:0.75em; color:#666; line-height:1.625em; display:block; text-align:center; background:none; border:1px solid rgb(195, 194, 194); border-radius:0.3em; padding:0;}
  .content .on button{color:#ff8003;border:1px solid #ff8003; border-radius:0.3em;}
  .content .on span{font-size:0.7em;color:#ff8003;}
  .content .on .ing{float:right; background:#ff8003; margin-top:0.4em;}
  .ing_txt button{background:#ddd; border-radius:0.4em;}
  .new .cost{margin-top:1.3em;}
  .tip_bg{width:100%; height:100%; position:fixed; top:0; left:0; background:rgba(0,0,0,.5);}
  .tip{width:92%; padding:0 4%; position:absolute; top:1.4em; left:0; z-index:2;}
  .tip span{width:1.4em; height:1.4em; line-height:1.4em; text-align:center; display:block; background:#2ca0fd; border-radius:1.5em; float:right;}
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
	<ul class="ui-list ui-border-tb">
	<#list tasks as task>
	<a class="ui-border-box <#if task.valid>bg_f</#if>">
	    <li class="ui-border-t<#if !task.valid> ui-li-finish</#if>" data-id="${task.transArticleTask.encodedId}">
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
	        		<button >最高${util.fen2yuanS(task.transArticleTask.maxAmount)}元</button>
	        	<#elseif task.processing>
	        		<button>进行中</button>
	        	<#else>
	        		<button>+ 
	        			<#if task.rewardAmount gt 0>
	        				${util.fen2yuanS((task.rewardAmount)!0)}
	        			<#else>	
	        				${util.fen2yuanS((task.transArticleTask.amount)!0)}
	        			</#if>
	        		 元</button>
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
<div class="tip_bg" id="tips">
  <div class="tip clearfix">
    <span id="tips-close"><a>×</a></span>
    <div class="line"></div>
    <div class="tishi">
      <p>点击文章标题进入详情页，将文章分享至朋友圈，好友阅读即可获得收入。阅读人数越多收入越高，满10元就可以在个人中心提现啦！奖励如下：</p>
      <div class="tab">
        <table>
          <tr>
            <td width="40%">达到阅读人数</td>
            <td width="15%">1</td>
            <td width="15%">5</td>
            <td width="15%">20</td>
            <td width="15%">80</td>
          </tr>
          <tr>
            <td width="40%">共获得奖励</td>
            <td width="15%">0.1</td>
            <td width="15%">0.5</td>
            <td width="15%">0.6</td>
            <td width="15%">3</td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</div>
<script src="${util.zepto}"></script>
<script>
$(function(){
	
	$('a>li').click(function(e){
		//if($(this).hasClass('ui-li-finish')) return ;
		var url = '${util.ctx}/weixin/article/detail?aid=' + $(this).attr('data-id');
		window.location.href= url;
	});

	$('#tips-close').click(function(){
		$('#tips').hide();
	});
});

</script>
<#include "/common/footer.ftl">