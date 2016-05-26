<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<style type="text/css">
	body {background: #fff;}
</style>
<div>
	<div class="shaidan clearfix">
		<em><img src="${user.headImg}" /></em>
		<div class="head_text fl">
			<h1>${user.uniName}</h1>
			<p>${productShare.createtime?string("yyyy-MM-dd HH:mm:ss")}</p>
		</div>
		<div class="head_tetx_right fr">
			<i class="orange">已奖励10金币</i>
			<b class="fr"><img src="${util.static}images/duobao/tishi.png" /></b>
		</div>
	</div>
	<div class="goods_detail">
		<a href="javascript:;" data-href="${util.ctx}/doubao/product/${productActivity.id}.html">(第${productActivity.product_number}期)${productActivity.product_name}</a>
		<p>本期参与：<i class="red">${userBuyNumber!1}</i>人次</p>
		<p>幸运号码：<i class="red">${productActivity.lottery_number}</i></p>
		<p>揭晓时间：${productActivity.lottery_time?string("yyyy-MM-dd HH:mm:ss")}</p>
	</div>
	<div class="goods_detail_text">${productShare.title!""}</div>
	<div class="detail_pic">
		<ul>
			<#list productShare.shareImages as img>
				<li><img src="${img?replace("_thumb", "")}" width="100%" /></li>
			</#list>
		</ul>
	</div>
</div>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<#include "/common/footer.ftl">