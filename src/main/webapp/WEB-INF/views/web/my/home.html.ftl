<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}/css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/style.css">
<div class="center_box">
	<div class="center_top clearfix">
		<div class="center_top_left clearfix fl">
			<div class="center_top_head fl"><img src="${user.avatar}" /></div>
			<div class="center_top_name fr">
				<h1>${user.uniName}</h1>
				<h2>ID：${user.user_identity}</h2>
			</div>
		</div>
		<div class="center_top_number fr has-link" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/to_bind_mobile.html">
			<em><img src="${util.static}images/img/mobile_icon.png" /></em>
			<span><#if user.mobile?has_content>${user.mobile}<#else/>未绑定手机号</#if></span>
			<i><img src="${util.static}images/duobao/right_black.png" /></i>
		</div>
	</div>
	<div class="center_list">
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/apps.html"><span><img src="${util.static}images/myincome_06.png" /></span>限时任务记录<em><img src="${util.static}images/img/right_ico.png" /></em></a>
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios/my/articlelist.html<#else>/web/article/article/list.html</#if>"><span><img src="${util.static}images/myincome_zf.png" /></span>转发任务记录<em><img src="${util.static}images/img/right_ico.png" /></em></a>
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/invites.html"><span><img src="${util.static}images/myincome_04.png" /></span>收徒记录<em><img src="${util.static}images/img/right_ico.png" /></em></a>
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/enchashments.html"><span><img src="${util.static}images/myincome_12.png" /></span>提现记录<em><img src="${util.static}images/img/right_ico.png" /></em></a>
	</div>
	<div class="center_list">
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios/user<#else>/web/my</#if>/income.html"><span><img src="${util.static}images/myincome_13.png" /></span>我的收入<em><img src="${util.static}images/img/right_ico.png" /></em></a>
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/goldcoin.html"><span><img src="${util.static}images/myincome_11.png" /></span>我的金币<em><img src="${util.static}images/img/right_ico.png" /></em></a>
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/giftscore.html"><span><img src="${util.static}images/img/my_income_score.png" /></span>我的积分<em><img src="${util.static}images/img/right_ico.png" /></em></a>
	</div>
	<div class="center_list">
		<a href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios/user<#else>/web/my</#if>/withdraw_type.html"><span><img src="${util.static}images/myincome_10.png" /></span>提现账号设置<em><img src="${util.static}images/img/right_ico.png" /></em></a>
	</div>
</div>
<script src="${util.zepto}"></script>
<script src="${util.basejs}"></script>
<#include "/common/footer.ftl">