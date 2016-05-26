<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}/css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/style.css">
<#if source == 1>
	<#assign name="金币" />
<#else/>
	<#assign name="积分" />
</#if>
<div class="coin_wrap" id="coin-wrap" data-name="${name}">
	<h1>总${name}:</h1>
	<div class="coin_exchange">
		<em>${income.balance}</em>
		<span>兑换${name}：1000${name}=1元</span>
	</div>
	<p><input type="tel" placeholder="输入${name}数量" id="amount-input"/></p>
</div>
<div class="coin_btn" id="exchange-btn"><a>确定兑换</a></div>
<div class="center_list apprentice_list apprentice_way_list">
	<a href="javascript:;" data-href="<#if fromSafari??>/ios<#else>/web</#if>/my/thridpartlog.html?source=${source}">${name}任务记录<em><img src="${util.static}images/img/right_ico.png" /></em></a>
	<#if source == 1><a href="javascript:;" data-href="<#if fromSafari??>/ios<#else>/web/my</#if>/checkinlog.html">签到金币记录<em><img src="${util.static}images/img/right_ico.png" /></em></a></#if>
	<a href="javascript:;" data-href="<#if fromSafari??>/ios<#else>/web</#if>/my/exchangelog.html?source=${source}">${name}兑换记录<em><img src="${util.static}images/img/right_ico.png" /></em></a>
</div>
<script>
	var score_config = {
		balance: ${income.balance},
		source: ${source},
		api : '<#if fromSafari??>/ios/wxapi<#else>/api/v1</#if>/integral/exchange'
	};
</script>
<script src="${util.zepto}"></script>
<script src="${util.basejs}"></script>
<script src="${util.exchangejs}"></script>
<#include "/common/footer.ftl">