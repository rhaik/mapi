<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<link rel="stylesheet" type="text/css" href="${util.swipercss}">
<#if banners?has_content>
<div class="swiper-container">
	<div class="swiper-wrapper">
		<#list banners as banner>
			<div class="swiper-slide" data-href="${banner.url}"><img src="${banner.image}"></div>
		</#list>
	</div>
	<!-- Add Pagination -->
	<div class="swiper-pagination"></div>
</div>
</#if>
<div class="nav">
	<div class="nav_list" data-href="${util.ctx}/doubao/my/index.html">
		<em><img src="${util.static}images/duobao/ico1.png" /></em>
		<span>个人中心</span>
	</div>
	<div class="nav_list" data-href="<#if fromSafari??>${util.static}/html/safari_frm.html?pg=</#if>${util.static}html/duobao/Problem.html">
		<em><img src="${util.static}images/duobao/ico2.png" /></em>
		<span>常见问题</span>
	</div>
</div>
<div class="notice" id="lottery-notice" style="display:none;"><em><img width="100%" src="${util.static}images/duobao/tongzhi.png" /></em><span id="lottery-msg"></span></div>
<div class="goods_list clearfix" id="announce-list" style="display:none;"></div>
<div class="goods_list clearfix scrollWindow" id="activity-list" data-url="${util.ctx}/doubao/api/common/list" data-template="activity-template">
	<#list productList as p>
	<div class="goods">
		<a href="javascript:;" data-href="${util.ctx}/doubao/product/${p.productActivity.id}.html">
			<em><img src="${p.product.thumb}" /></em>
			<h1>${p.productActivity.product_name}</h1>
			<p>开奖进度  <i>${(p.productActivity.buy_number/p.productActivity.number*100)?string('#.#')}%</i></p>
			<div class="bar_big"><div class="bar_sma" style="width:${p.productActivity.buy_number/p.productActivity.number*100}%"></div></div>
		</a>
	</div>
	</#list>
</div>
<div class="bottom" id="loading-panel" style="display:none;">
	<span><img src="${util.static}images/loading.gif" /></span>
	<p>正在加载中...</p>
</div>
<div class="popup_bg dn" id="lottery-popup">
	<div class="close">×</div>
	<div class="popup_win">
		<p></p>
		<h1>中奖啦</h1>
	</div>
	<a class="see" href="javascript:;"><img src="${util.static}images/duobao/look.png" /></a>
</div>
<script id="activity-template" type="text/x-handlebars-template">
	{{#data}}
	<div class="goods">
		<a href="javascript:;" data-href="${util.ctx}/doubao/product/{{id}}.html">
			<em><img src="{{thumb}}" /></em>
			<h1>{{product_name}}</h1>
			<p>开奖进度  <i>{{progress}}%</i></p>
			<div class="bar_big"><div class="bar_sma" style="width:{{progress}}%"></div></div>
		</a>
	</div>
	{{/data}}
</script>
<script id="announce-template" type="text/x-handlebars-template">
<div class="goods {{^lotteryTimes}}leastRemain{{/lotteryTimes}}" id="announce-pid-{{id}}" data-status="{{status}}">
	<a href="javascript:;" data-href="${util.ctx}/doubao/product/{{id}}.html">
		<em><img src="{{thumb}}" /></em>
		<h1>(第{{product_number}}期){{product_name}}</h1>
		{{#if lotteryTimes}}
		<div class="times">
			<i><img width="100%" src="${util.static}images/duobao/time.png"></i>
			<div class="countdown" data-end="{{lotteryTimes}}">
				<span class="mini">10</span><span>:</span><span class="sec">22</span><span>:</span><span class="hm">147</span>
			</div>
		</div>
		{{/if}}
		{{#unless lotteryTimes}}
		<p>开奖进度  <i>{{progress}}%</i></p>
		<div class="bar_big"><div class="bar_sma" style="width:{{progress}}%"></div></div>
		{{/unless}}
	</a>
	<div class="text_tishi">{{#lotteryTimes}}即将揭晓{{/lotteryTimes}}{{^lotteryTimes}}即将抢完{{/lotteryTimes}}</div>
</div>
</script>
<script src="${util.jquery}"></script>
<script src="${util.swiperjs}"></script>
<script src="${util.handlebars}"></script>
<script type="text/javascript" src="${util.static}js/countdown.js"></script>
<script src="${util.static}js/duobao.js"></script>
<script src="${util.static}js/duobao_home.js"></script>
<#include "/common/footer.ftl">