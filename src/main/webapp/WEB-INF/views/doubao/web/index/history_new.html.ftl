<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<!-- 中间内容 -->
<div class="past scrollWindow" id="history-list" data-url="${util.ctx}/doubao/api/common/history/${productId}" data-template="history-template">
	 <#list productActivityHistory as p>
	 <div class="past_list">
		 <h1>第${p.productActivity.product_number}期（揭晓时间：${p.productActivity.lottery_time?string("yyyy-MM-dd HH:mm:ss")}）</h1>
		 <a class="past_infor" href="javascript:;" data-href="${util.ctx}/doubao/product/${p.productActivity.id}.html">
			 <div class="past_pic"><img src="${p.user.headImg}" /></div>
			 <div class="past_txt">
				 <p>中奖用户：${p.user.uniName}</p>
				 <p>幸运号码：${p.productActivity.lottery_number!1}</p>
				 <p>本期参与：${p.productActivity.lottery_buy_number}人次</p>
			 </div>
			 <div class="right_icon"><img src="${util.static}images/img/right_ico.png" /></div>
		 </a>
	 </div>
	 </#list>
</div>
<div class="bottom" id="loading-panel" style="display:none;">
	<span><img src="${util.static}images/loading.gif" /></span>
	<p>正在加载中</p>
</div>
<script id="history-template" type="text/x-handlebars-template">
	{{#data}}
	<div class="past_list">
		<h1>第{{product_number}}期（揭晓时间：{{lottery_time}}）</h1>
		<a class="past_infor"  href="javascript:;" data-href="${util.ctx}/doubao/product/{{id}}.html">
			<div class="past_pic"><img src="{{headImg}}" /></div>
			<div class="past_txt">
				<p>中奖用户：{{{uniName}}}</p>
				<p>幸运号码：{{lottery_number}}</p>
				<p>本期参与：{{number}}人次</p>
			</div>
			<div class="right_icon"><img src="${util.static}images/img/right_ico.png" /></div>
		</a>
	</div>
	{{/data}}
</script>
<script src="${util.zepto}"></script>
<script src="${util.handlebars}"></script>
<script src="${util.static}js/duobao.js"></script>
<#include "/common/footer.ftl">