<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<#if fromSafari?? && !fromWeixin>
<style>
#wrapper {margin-top:84px;}
.nav_box {top:44px;}
</style>
</#if>
<div class="nav_box">
	<ul class="wid3 clearfix" id="nav-list">
		<li <#if type==1>class="on"</#if> >进行中</li>
		<li <#if type==3>class="on"</#if> >未中奖</li>
		<li <#if type==5>class="on"</#if> >已过期</li>
	</ul>
</div>
<div class="record_main" id="record-container">
	<div class="record_con <#if type!=1>dn</#if>" data-url="${util.ctx}/doubao/api/my/lotteryrecord?type=1" data-template="record-template-1" <#if type!=1>data-page="0"</#if>>
		<#if type ==1 && orderList??>
		<#list orderList as od>
		<a class="detail_goods clearfix" data-href="${util.ctx}/doubao/product/${od.productActivity.id}.html" data-pid="${od.productActivity.id}">
			<div class="goods_pic"><img src="${od.product.thumb}" /></div>
			<div class="goods_text">
				<h1>(第${od.productActivity.product_number}期)${od.productActivity.product_name}</h1>
				<p><span>购买时间：${od.orderProduct.createtime?string("MM-dd HH:mm")}</span></p>
				<p class="clearfix">
					<span class="fl">购买人次：<i class="orange">${od.orderProduct.number}</i></span>
					<em class="look_over fr" data-number="${od.orderProduct.number}">查看夺宝号&gt;</em>
				</p>
			</div>
		</a>
		</#list>
		</#if>
	</div>
	<div class="record_con <#if type!=3>dn</#if>" data-url="${util.ctx}/doubao/api/my/lotteryrecord?type=3" data-template="record-template-2" <#if type!=3>data-page="0"</#if> >
		<#if type ==3 && orderList??>
			<#list orderList as od>
			<a class="announce" data-href="${util.ctx}/doubao/product/${od.productActivity.id}.html" data-pid="${od.productActivity.id}">
				<div class="detail_goods clearfix">
					<div class="goods_pic"><img src="${od.product.thumb}" /></div>
					<div class="goods_text">
						<h1><em class="over">已揭晓</em>(第${od.productActivity.product_number}期)${od.productActivity.product_name}</h1>
						<p class="clearfix"><span class="fl">购买时间：${od.orderProduct.createtime?string("MM-dd HH:mm")}</span><span class="fr">购买人次：<i class="orange">${od.orderProduct.number}</i></span></p>
						<p><span>购买时间：${od.orderProduct.createtime?string("MM-dd HH:mm")}</span></p>
						<p class="clearfix">
							<span class="fl">购买人次：<i class="orange">${od.orderProduct.number}</i></span>
							<em class="look_over fr" data-number="${od.orderProduct.number}">查看夺宝号&gt;</em>
						</p>
					</div>
				</div>
				<div class="winning">
					<div class="win_head"><img src="${od.user.headImg}"></div>
					<div class="win_txt">
						<p>中奖用户：${od.user.uniName}</p>
						<p>手机号码：${od.user.hideMobile}</p>
						<p>参与人次：${od.productActivity.lottery_number}</p>
						<p>揭晓时间：${od.productActivity.lottery_time?string("MM-dd HH:mm:ss")}</p>
					</div>
				</div>
			</a>
			</#list>
		</#if>
	</div>
	<div class="record_con <#if type!=5>dn</#if>" data-url="${util.ctx}/doubao/api/my/lotteryrecord?type=5" data-template="record-template-3" <#if type!=5>data-page="0"</#if>>
		<#if type ==5 && orderList??>
			<#list orderList as od>
				<a class="detail_goods clearfix" data-href="${util.ctx}/doubao/product/${od.orderProduct.id}.html" data-pid="${od.productActivity.id}">
					<div class="goods_pic"><span><img src="${od.product.thumb}" /></span></div>
					<div class="goods_text">
						<h1>(第${od.productActivity.product_number}期)${od.productActivity.product_name}</h1>
						<p><span>过期时间：${od.productActivity.expire_time?string("MM-dd HH:mm")}</span></p>
						<p class="clearfix"><span class="fl">购买人次：<i class="orange">${od.orderProduct.number}</i></span></p>
					</div>
					<em class="finish"></em>
				</a>
			</#list>
		</#if>
	</div>
</div>
<div class="bottom" id="loading-panel" style="display:none;">
	<span><img src="${util.static}images/loading.gif" /></span>
	<p>正在加载中</p>
</div>
<div class="num_bg dn" id="duobao-number-dialog">
	<div class="num_box">
		<h1>查看夺宝号<span class="close"  href="javascript:;"><img src="${util.static}images/sign/no.png" /></span></h1>
		<h2>本期购买<i class="orange" id="duobao-times">1</i>人次</h2>
		<ul id="duobao-numbers"></ul>
	</div>
</div>
<script id="record-template-1" type="text/x-handlebars-template">
	{{#data}}
	<a class="detail_goods clearfix" data-href="${util.ctx}/doubao/product/{{productActivityId}}.html" data-pid="{{productActivityId}}">
		<div class="goods_pic"><img src="{{thumb}}" /></div>
		<div class="goods_text">
			<h1>(第{{product_number}}期){{product_name}}</h1>
			<p><span>购买时间：{{createtime}}</span></p>
			<p class="clearfix">
				<span class="fl">购买人次：<i class="orange">{{opnumber}}</i></span>
				<em class="look_over fr" data-number="{{opnumber}}">查看夺宝号&gt;</em>
			</p>
		</div>
	</a>
	{{/data}}
</script>
<script id="record-template-2" type="text/x-handlebars-template">
	{{#data}}
	<a class="announce" data-href="${util.ctx}/doubao/product/{{productActivityId}}.html" data-pid="{{productActivityId}}">
	<div class="detail_goods clearfix">
		<div class="goods_pic"><img src="{{thumb}}" /></div>
		<div class="goods_text">
			<h1><em class="over">已揭晓</em>(第{{product_number}}期){{product_name}}</h1>
			<p><span>购买时间：{{createtime}}</span></p>
			<p class="clearfix">
				<span class="fl">购买人次：<i class="orange">{{opnumber}}</i></span>
				<em class="look_over fr" data-number="{{opnumber}}">查看夺宝号&gt;</em>
			</p>
		</div>
	</div>
	<div class="winning">
		<div class="win_head"><img src="{{headImg}}"></div>
		<div class="win_txt">
			<p>中奖用户：{{{uniName}}}</p>
			<p>手机号码：{{hideMobile}}</p>
			<p>参与人次：{{lottery_number}}</p>
			<p>揭晓时间：{{lottery_time}}</p>
		</div>
	</div>
	</a>
	{{/data}}
</script>
<script id="record-template-3" type="text/x-handlebars-template">
	{{#data}}
	<a class="detail_goods clearfix" data-href="${util.ctx}/doubao/product/{{productActivityId}}.html" data-pid="{{productActivityId}}">
		<div class="goods_pic"><span><img src="{{thumb}}" /></span></div>
		<div class="goods_text">
			<h1>(第{{product_number}}期){{product_name}}</h1>
			<p><span>过期时间：{{expire_time}}</span></p>
			<p class="clearfix"><span class="fl">购买人次：<i class="orange">{{opnumber}}</i></span></p>
		</div>
		<em class="finish"></em>
	</a>
	{{/data}}
</script>
<script src="${util.zepto}"></script>
<script src="${util.handlebars}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
	$(function(){
		$('#nav-list li').click(function(){
			$(this).addClass('on').siblings().removeClass('on');
			var index = $(this).index();
			var $container = $('#record-container').children().eq(index);
			$container.initWindowScroll().removeClass('dn').siblings().addClass('dn');
		});
		$('#record-container').on('click', 'div', function(){
			$(this).data('href') && toUrl($(this).data('href'));
		}).on('click', '.look_over', function(){
			var actId = $(this).closest('a').data('pid');
			if(actId){
				$('#duobao-times').text($(this).data('number'));
				$('#duobao-numbers').empty();
				$('#duobao-number-dialog').removeClass('dn');
				getDuobaoNumbers(actId);
			}
			return false;
		});

		$('#duobao-number-dialog .close').click(function(){
			$('#duobao-number-dialog').addClass('dn');
		});

		$('#record-container').children().not('.dn').initWindowScroll();

		function getDuobaoNumbers(actId){
			$.ajax({
			   type: "GET",
			   data: {activityId:actId},
			   url: "${util.ctx}/doubao/api/my/lotteryNumber",
			   dataType:'json',
			   success:function(r){
					if(r.code==0) {
						if(r.data){
							var items = $.map(r.data, function(item, index){ return '<li>' + item.number + '</li>';}).join(' ');
							$('#duobao-numbers').append(items);
						}
					} else {
						myAlert(r.message);
					}
			   }
			});
		}
	});
</script>
<#include "/common/footer.ftl">