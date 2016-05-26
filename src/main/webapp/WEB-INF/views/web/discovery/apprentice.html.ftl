<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
<style>
	.tishi{margin:1.3em 0.93em; border:1px dashed #ff8001;}
	.tishi p{font-size:0.875em; color: #ff8001; padding:0.83em;}
	.tishi .activity {color:red; }
	.tishi i {color:red; font-weight:bold; font-style:normal; font-size: 1.5em;}
	.reward{background:#fff; overflow:hidden;}
	.total{margin:1.2em 2.1em;}
	.total h1{font-size:2.25em; color:#ff8001; text-align:center;}
	.total p{font-size:1em; color:#000; text-align:center; line-height:1.5em;}
	.list_box{margin:1em 0;}
	.list{width:50%; border-left:1px solid #ebeaea; margin-left: -1px;}
	.list_top b{display:block; font-size:1.875em; color:#ff8001; text-align:center;}
	.list_bot{margin-top:0.4em;}
	.list_bot span{font-size:0.93em; line-height:1.9em; color:#444; display: block; text-align: center;}
	.list_bot span a{width:1.7em; height:1.9em; background:url(${util.static}images/img/icon.png) no-repeat; background-size:20.6em 20.6em; display:inline-block; vertical-align:bottom;}
	.list_bot span a.icon1{background-position:-2.5em 0;}
	.list_bot span a.icon2{background-position: -8.9em 0.1em;}
	.today{font-size:0.875em; color:#666; padding: 0 1.1em; line-height:2.8em; border-top:1px solid #dcdcdc;}
	.today i.orange{color:#ff8001; font-style:normal; margin:0 0.2em;}

	.entry_box{background:#fff; margin-top:1.1em;}
	.entry{margin-left:1.1em; height:3em; line-height:3em; border-bottom:1px solid #dcdcdc;}
	.entry a{display: block;}
	.entry span{font-size:0.9em; color:#000;}
	.entry b{width:0.6em; height:1em; display:block; background:url(${util.static}images/img/icon.png) 0 -2.9em / 20.6em 20.6em no-repeat; position:absolute; top:50%; right:1.2em; margin-top:-0.5em;}
	.entry:last-child{border:none;}

	.posi_btn{width:100%; background:#fff; position:fixed; bottom:0; left:0;}
	.posi_btn a{height:2.5em; line-height:2.5em; margin:0.625em; text-align:center; background:#ff8001; display:block; border-radius:0.3em;}

	.popup_bg{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0; display:none;}
	.select_box{width:14em; height:10em; overflow:hidden; background:#fff; border-radius:0.5em; padding:1.56em 2.2em; position:fixed; top:50%; left:50%; margin:-10.2em 0 0 -9em;}
	.select_box h1{font-size:0.875em; font-weight:normal; text-align:center;}
	.select_box span{width:1em; height:1em; border-radius:1em; overflow:hidden; background:#bfbfbf; text-align:center; color:#fff; font-size:1.2em; position:absolute; top:0.5em; right:0.5em; display:block;}
	.select_btn a{height:2.8rem; line-height:2.8rem; text-align:center; display:block; margin:1em auto 0; background:#ff8003; border-radius:0.3em; font-size:1.25rem;}
</style>
	<div style="padding-top:1px;padding-bottom:65px;">
		<div class="tishi">
			<p>我的邀请码：<i>${user.user_identity}</i></p>
			<p>提示：徒弟完成<#if isIOS>限时、转发、金币任务<#else/>转发任务、快速任务，</#if>甚至签到， 即可获得10%的分成收益。</p>
<#if isIOS><p class="activity">限时活动：新徒弟完成前<i>5个</i>限时任务，每个任务为师傅分成<i>1元</i></p></#if>
		</div>
		<div class="reward">
			<div class="list_box posi_r clearfix">
				<div class="list fl">
					<div class="list_top"><b>${userFriendCount!0}</b></div>
					<div class="list_bot"><span><a class="icon1"></a>徒弟人数</span></div>
				</div>
				<div class="list fl">
					<div class="list_top"><b>${util.fen2yuan(userIncome.share_total)}</b></div>
					<div class="list_bot"><span><a class="icon2"></a>累计徒弟奖励</span></div>
				</div>
			</div>
			<div class="entry_box today">
				<div class="entry posi_r"><a><span>今日收徒<i>${todayInviteCount!0}</i>人</span></a></div>
				<!--
				<div class="entry posi_r"><a href='<#if fromSafari>/ios/activity/effectiveList.html<#else>/web/activity/effectiveList.html</#if>'><span>有效徒弟列表</span><b></b></a></div>
				-->
			</div>
		</div>

		<div class="entry_box">
			<div class="entry posi_r" onclick="go('${util.ctx}<#if fromSafari??>/static/html/safari_frm.html?pg=</#if>/static/html/ApprenticeReason.html')"><span>为什么要收徒？</span><a></a></div>
			<div class="entry posi_r" onclick="go('${util.ctx}<#if fromSafari??>/static/html/safari_frm.html?pg=</#if>/static/html/ApprenticeWay.html')"><span>如何收取更多徒弟？</span><a></a></div>
		</div>
	</div>
	<div class="posi_btn">
		<a href="javascript:void(0);" id="friendBtn">去收徒</a>
	</div>
<#if isIOS>
	<div class="popup_bg" id="share-dialog">
		<div class="ui-dialog" >
			<div class="select_box">
				<h1>收徒方式多样化，快去收徒赚钱吧</h1>
				<span class="close">×</span>
				<div class="select_btn">
					<a href="javascript:;" onclick="goStatic('${util.ctx}/static/html/shoutu_saoyisao.html?u=${user.user_identity}');">扫一扫收徒</a>
					<a href="javascript:;" onclick="goStatic('${util.ctx}/static/html/shoutu_lianjie.html?u=${user.user_identity}');">收徒链接</a>
				</div>
			</div>
		</div>
	</div>
</#if>
<script src="${util.zepto}"></script>
<script type="text/javascript">
   function go(url) {
   		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else {
			window.location.href= url;
		}
   }

   function goStatic(url){
   		<#if fromSafari??>
   		url = '/static/html/safari_frm.html?pg=' + encodeURIComponent(url);
   		</#if>
   		go(url);
   }

   $(function(){
		$('#friendBtn').click(function(){
			<#if fromSafari??>
			$('#share-dialog').show();
			<#else>
			goStatic('/static/html/apprentice.html');
			</#if>
		});
		$('#share-dialog .close').click(function(){$('#share-dialog').hide();});
   });
   
</script>
<#include "/common/footer.ftl">