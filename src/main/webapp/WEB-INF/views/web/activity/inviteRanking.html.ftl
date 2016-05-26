<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/swiper.min.css">
	<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
	<script src="${util.static}js/swiper.min.js"></script>
<div class="wrap_shoutu">
	<div class="banner_top"><img src="${util.static}images/img/banner1.png" /></div>
	<div class="shoutu_top posi_r">
	<#if rankList?? && (rankList?size >2)>
		<div class="top1">
			<i class="name_box"><span style="background-image:url(http://7xkdr1.com1.z0.glb.clouddn.com/1f380.png)" class="emojicon-m">
			</span>${(rankList[1].user.uniName)!''}<span style="background-image:url(http://7xkdr1.com1.z0.glb.clouddn.com/1f380.png)" class="emojicon-m"></span></i>
			<h1>当日收徒：${(rankList[1].inviteNum)!0}人</h1>
			<h2><em><img src="${util.static}images/img/hongbao_ico.png" /></em>200元红包</h2>
			<p class="top_ranking">
				<i><img src="${(rankList[1].user.headImg)!''}" /></i>
				<span><img src="${util.static}images/img/top02.png"></span>
			</p>
		</div>
		<div class="top2">
			<i class="name_box">${(rankList[0].user.uniName)!''}</i>
			<h1>当日收徒：${(rankList[0].inviteNum)!0}人</h1>
			<h2><em><img src="${util.static}images/img/hongbao_ico.png" /></em>300元红包</h2>
			<p class="top_ranking">
				<i><img src="${(rankList[0].user.headImg)!''}" /></i>
				<span><img src="${util.static}images/img/top01.png"></span>
			</p>
		</div>
		<div class="top3">
			<i class="name_box"><span style="background-image:url(http://7xkdr1.com1.z0.glb.clouddn.com/1f380.png)" class="emojicon-m"></span>
		${(rankList[2].user.uniName)!''}<span style="background-image:url(http://7xkdr1.com1.z0.glb.clouddn.com/1f380.png)" class="emojicon-m"></span></i>
			<h1>当日收徒：${(rankList[2].inviteNum)!0}人</h1>
			<h2><em><img src="${util.static}images/img/hongbao_ico.png" /></em>100元红包</h2>
			<p class="top_ranking">
				<i><img src="${(rankList[2].user.headImg)!''}" /></i>
				<span><img src="${util.static}images/img/top03.png"></span>
			</p>
		</div>
		</#if>
	</div>
	<div class="list_date">
		<h1>榜单日期：${rankDay!''}<a class="active_btn fr" href="#rule_id">活动规则</a></h1>
	</div>
	<div class="list_ranking_box">
		<div class="rabking_title"><span>第4-30名</span></div>
		<div class="list_ranking">
		<#if rankList??>
			<#list rankList as rankVO >
			<#if (rankVO_index > 2) >
			<div class="list_place clearfix">
				<div class="list_place_head fl"><img src="${(rankVO.user.headImg)!''}" /></div>
				<div class="list_place_text fl">
					<h1>${(rankVO.user.uniName)!''}</h1>
					<h2>当日收徒：${(rankVO.inviteNum)!''}人 <i class="red">${(rankVO.hongBaoMessage)!''}红包</i></h2>
				</div>
				<div class="list_place_num fr">${rankVO_index +1}</div>
			</div>
			</#if>
			</#list>
		</#if>
		</div>
	</div>
	<div class="list_ranking_box list_ranking_table" id="rule_id">
		<div class="rabking_title"><span>游戏规则</span></div>
		<div class="ranking_rule">1、新徒弟完成一个限时任务即认定为有效徒弟，每天有效收徒最高的前30名用户可以荣进“全民收徒财神榜”，登上日榜即可领取红包和金币，榜单每天24:00更新。</div>
		<div class="ranking_rule">2、红包奖励如下：</div>
		<div class="reward">
			<table>
				<tr>
					<td>第1名</td>
					<td>第2名</td>
					<td>第3名</td>
					<td>第4名</td>
				</tr>
				<tr>
					<td>300元</td>
					<td>200元</td>
					<td>100元</td>
					<td>60元</td>
				</tr>

				<tr>
					<td>第5名</td>
					<td>第6-10名</td>
					<td>第11-20名</td>
					<td>第21-30名</td>
				</tr>

				<tr>
					<td>40元</td>
					<td>30元</td>
					<td>20元</td>
					<td>1W金币</td>
				</tr>
			</table>
		</div>
		<div class="ranking_rule">3、红包发放时间：每天24:00发放</div>
		<div class="ranking_rule">4、活动期间：原有的收徒奖励制度（“新徒弟完成前5个限时任务，每个任务师傅分成1元”和“收徒越多，任务单价越高”）暂停，现为：徒弟做任务师傅分成10%。</div>
		<div class="ranking_rule">5、最终解释权归秒赚大钱所有</div>
	</div>
	<div class="share_btn"><a class="active_btn" id='shareBtn'>告诉好友</a></div>
	<#if userVo??>
	<div class="people_infor_box">
		<div class="people_infor">
			<div class="list_place clearfix">
				<div class="list_place_head fl"><img src="${(userVo.user.headImg)!''}" /></div>
				<div class="list_place_text fl">
					<h1>${(userVo.user.uniName)!''}</h1>
					<h2>当日收徒：${(userVo.inviteNum)!0}人</h2>
				</div>
				<div class="list_place_btn fr" id='go_invite'>去收徒</div>
				<div class="list_place_num fr">${(userVo.rankMessage)!''}</div>
			</div>
		</div>
	</div>
	</#if>
</div>
<script src="${util.jquery}"></script>
<script type="text/javascript">
	$(function(){
		$('#go_invite').click(function(){
			var url = '/ios/share.html';
			<#if isInAppView>
			url='/web/discovery/share.html';
			</#if>
			location.href = url;
		});
	});
</script>
<script type="text/javascript">
var shareUrl = 'http://wx.hongjie68888.cn/www/downloads/share/<#if user??>${user.user_identity}<#else>0</#if>';
</script>
<script type="text/javascript">
	if(window.MiJSBridge){//window.MiJSBridge对象不为空，可以直接调用
	     onMiJSBridgeReady();
	}else{
	     document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
	}

	function onMiJSBridgeReady(){
	    <#if isInAppView>
	    	$('#shareBtn').click();
	    </#if>
	}

	$('#shareBtn').click(function(){
    	<#if isInAppView>
    	if(typeof MiJSBridge == 'object')
    		MiJSBridge.call('share', 
    			{title:"轻松点一点，越玩越有钱", image:"https://m.miaozhuandaqian.com/static/images/logo.png", content:"下载试用App即可挣钱，每天半小时，轻松月挣几百元零花钱，你也可以", url:shareUrl},
    				 function(ret){
    				 	console.log(ret);
    				 }
    		);
    	<#else>
    		location.href = "${yaoshiScheme!'#'}?action=url&url=/web/activity/invite.html#shareBtn";
    	</#if>
})
</script>
<#include "/common/footer.ftl">	