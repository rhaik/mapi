<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<#if fromSafari?? && !fromWeixin>
	<style>
		#wrapper {margin-top:44px;}
		.nav_box {top:44px;}
	</style>
</#if>
<div class="record_main" id="record-container">
	<div class="record_con" data-url="${util.ctx}/doubao/api/my/lotteryrecord?type=10" data-template="record-template-3">
		<#if type ==10 && orderList??>
			<#list orderList as od>
				<a class="detail_goods clearfix" data-href="${util.ctx}/doubao/my/lotterydetail/${od.orderProduct.id}.html" data-pid="${od.productActivity.id}">
					<div class="goods_pic"><img src="${od.product.thumb}" /></div>
					<div class="goods_text">
						<h1>(第${od.productActivity.product_number}期)${od.productActivity.product_name}</h1>
						<p><span>中奖时间：${od.productActivity.lottery_time?string("MM-dd HH:mm")}</span></p>
						<p class="clearfix">
							<span class="fl">购买人次：<i class="orange">${od.orderProduct.number}</i></span>
							<em class="look_over fr" data-number="${od.orderProduct.number}">查看夺宝号&gt;</em>
						</p>
					</div>
					<#if od.orderProduct.shipping_status gt 0 && od.orderProduct.shippingDesc?length gt 0>
						<span class="tishi_pic">${od.orderProduct.shippingDesc}</span>
						<#if od.orderProduct.shipping_status == 4 && od.orderProduct.share == 1><em class="finish"></em></#if>
					</#if>
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
<script id="record-template-3" type="text/x-handlebars-template">
	{{#data}}
	<a class="detail_goods clearfix" data-href="${util.ctx}/doubao/my/lotterydetail/{{id}}.html" data-pid="{{productActivityId}}">
		<div class="goods_pic"><img src="{{thumb}}" /></div>
		<div class="goods_text">
			<h1>(第{{product_number}}期){{product_name}}</h1>
			<p><span>中奖时间：{{lottery_time}}</span></p>
			<p class="clearfix">
				<span class="fl">购买人次：<i class="orange">{{opnumber}}</i></span>
				<em class="look_over fr" data-number="{{opnumber}}">查看夺宝号&gt;</em>
			</p>
		</div>
		{{#shipping_desc}}<span class="tishi_pic">{{this}}</span>{{/shipping_desc}}
		{{#finished}}<em class="finish"></em>{{/finished}}
	</a>
	{{/data}}
</script>
<script src="${util.zepto}"></script>
<script src="${util.handlebars}"></script>
<script src="${util.static}js/duobao.js"></script>
<script type="text/javascript">

	$(function(){
			//下拉加载数据
			$('#record-container').children().initWindowScroll();
			
			$('#record-container').on('click', 'div', function(){//
				$(this).data('href') && toUrl($(this).data('href'));
			}).on('click', '.look_over', function(){//查看夺宝号弹出框
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