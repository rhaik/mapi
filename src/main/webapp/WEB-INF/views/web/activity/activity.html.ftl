<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<link rel="stylesheet" type="text/css" href="//cdn.bootcss.com/animate.css/3.5.1/animate.min.css">
<style>
	body {background: #ee5857;}
</style>
<!-- 红包 -->
<div class="shake_box yaoiphone">
	<div class="shake_ordinary">
		<!-- 大标题 -->
		<div class="shake_title"><img src="${util.static}images/yaoiphone/title.png" /></div>
		<!-- 开抢时间 -->
		<!-- 今日获奖者 -->
		<div class="start">今日iPhone获奖者：<#if todayMax??>${(todayMax.user.uniName)!''}<#else>暂无</#if></div>
		<div class="start" id="start-hint" style="display:none;"></div>
		<!-- 摇一摇 -->
		<div class="shake_bg">
			<span class="shake_pic animated infinite" id="shake-annim"></span>
		</div>
	</div>

	<!-- 抢到红包 -->
	<div class="get_shake_bg" id="hongbao-content">
		<!-- 抢红包页面 -->
		<div class="get_shake" >
			<i data-shaked="1">×</i>
			<em><img src="${util.static}images/img/logo.png" /></em>
			<h1>秒赚大钱</h1>
			<!-- 抢到红包 -->
			<div class="hasHongbao">
				<p>给你发了一个红包</p>
				<h2>恭喜发财，大吉大利！</h2>
				<span class="open_shake">拆红包</span>
			</div>
			<!-- End 抢到红包 -->
			<!-- 没有抢到红包 -->
			<div class="shoutu_hongbao noHongbao"  >
				<h3>啊哦，iPhone与你擦肩而过</h3>
				<h4>收徒获得更多摇红包机会</h4>
				<div class="btn" id="goto-share"><a>去收徒</a></div>
			</div>
			<!--End 没有抢到红包 -->
		</div>
	</div>
	<!-- 活动介绍 -->
	<div class="get_shake_bg" id="hongbao-desc" data-number="${totalNum}">
		<div class="get_shake search_shake shake_sma">
			<i data-shaked="1">×</i>
			<h1>活动介绍</h1>
			<div class="search_shake_text">
				<p>活动日期：</p>
				<p>${startDate?string("M月d日")}-${endDate?string("M月d日")}</p>
			</div>
			<div class="search_shake_text">
				<p>在活动限定时间内，每收一个徒弟并且打开秒赚大钱app，即可获得5次在首页摇一摇的机会，上不封顶哦</p>
				<p>PS：每天都有一部iPhone6S、5000夺宝币和数千万金币等你拿哦！</p>
			</div>
		</div>
	</div>
	<!-- 拆红包 -->
	<div class="get_shake_bg" id="hongbao-result">
		<div class="get_shake search_shake apart">
			<i data-shaked="1">×</i>
			<h1 class="yellow">恭喜发财，大吉大利</h1>
			<p class="money" id="hongbao-amount">0.10<b>元</b></p>
			<a id="hongbao-info">已存入您的余额，注意查收</a>
			<div class="continue" data-shaked="1">继续摇一摇</div>
		</div>
	</div>
	<!-- 专属红包提示 -->
	<h1 class="sexclusive" id="hongbao-mine"></h1>
	<h3 class="rule"><a href="javascript:;" id="activity-rule">"摇一摇"活动规则</a></h3>
	<div class="shake_exclusive" id="lucky-users">
		<div class="lantern">
			<p>看看大家的手气</p>
		</div>
		<div class="table">
			<table id="hongbao-list"></table>
		</div>
	</div>
	<!-- 活动介绍(没有系统红包时显示) -->
	<div class="jieshao" id="bottom-info" style="display:none;">
		<div class="lantern">
			<p>活动介绍</p>
		</div>
		<div class="search_shake_text">
			<p>活动日期：${startDate?string("M月d日")}-${endDate?string("M月d日")}</p>
		</div>
		<div class="search_shake_text">
			<p>在活动限定时间内，每收一个徒弟并且打开秒赚大钱app，即可获得5次在首页摇一摇的机会，上不封顶哦</p>
			<p>PS：每天都有一部iPhone6S、5000夺宝币和数千万金币等你拿哦！</p>
		</div>
	</div>
</div>
<script>
	var userId = '${user.user_identity}';
</script>
<script src="${util.jquery}"></script>
<script src="${util.static}js/shake_hongbao2.js"></script>
<#include "/common/footer.ftl">	