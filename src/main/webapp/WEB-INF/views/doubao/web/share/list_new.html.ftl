<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="sd_box scrollWindow" id="share-list" data-url="${util.ctx}/doubao/api/share/<#if isProduct>history?productId=${productId}<#elseif isMine>my<#else>list</#if>" data-template="share-template">
	<#list productShare as share>
	<a class="sd_list" href="javascript:;" data-href="${util.ctx}/doubao/share/${share.productShare.id}.html">
		<div class="sd_tit">
			<span><img src="${share.user.headImg}" /></span>
			<div class="sd_text fl">
				<h1>${share.user.uniName}</h1>
				<p>第${share.productActivity.product_number}期   ${share.productActivity.product_name}</p>
			</div>
			<div class="sd_text fr">
				<i>已奖励10金币</i>
				<p>${share.productShare.createtime?string("MM-dd HH:mm:ss")}</p>
			</div>
		</div>
		<p class="evaluate">${share.productShare.title!""}</p>
		<div class="sd_pic">
			<ul class="sd_pic_box">
				<#list share.productShare.shareImages as img>
				<li><img src="${img}" width="100%" height="100%" /></li>
				</#list>
			</ul>
		</div>
	</a>
	</#list>
</div>
<div class="bottom" id="loading-panel" style="display:none;">
	<span><img src="${util.static}images/loading.gif" /></span>
	<p>正在加载中</p>
</div>
<script id="share-template" type="text/x-handlebars-template">
	{{#data}}
	<a class="sd_list" href="javascript:;" data-href="${util.ctx}/doubao/share/{{id}}">
		<div class="sd_tit">
			<span><img src="{{headImg}}" /></span>
			<div class="sd_text fl">
				<h1>{{{uniName}}}</h1>
				<p>第{{product_number}}期   {{product_name}}</p>
			</div>
			<div class="sd_text fr">
				<i>已奖励10金币</i>
				<p>{{createtime}}</p>
			</div>
		</div>
		<p class="evaluate">{{title}}</p>
		<div class="sd_pic">
			<ul class="sd_pic_box">
				{{#shareImages}}
				<li><img src="{{this}}" width="100%" height="100%" /></li>
				{{/shareImages}}
			</ul>
		</div>
	</a>
	{{/data}}
</script>
<script src="${util.jquery}"></script>
<script src="${util.handlebars}"></script>
<script src="${util.static}js/duobao.js"></script>
<#include "/common/footer.ftl">