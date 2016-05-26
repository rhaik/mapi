<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="delivery">
	<h1>中奖状态</h1>
	<div class="state_box">
		<div class="delivery_text" id="right-steps">
			<div class="state">
				<span>夺宝成功</span>
				<em>${productActivity.productActivity.lottery_time?string("yyyy-MM-dd HH:mm:ss")}</em>
			</div>

			<div class="state <#if !orderProduct.consignee_time??>on</#if>" >
				<#if orderProduct.consignee_time??>
					<span>确认收货地址</span>
					<em>${orderProduct.consignee_time?string("yyyy-MM-dd HH:mm:ss")}</em>
				<#else />
					<span>等待确认收货地址</span>
					<em></em>
				</#if>
			</div>
			<#assign finished=orderProduct.consignee_time?? />

			<div class="state <#if finished && !orderProduct.shipping_time??>on</#if>">
				<#if orderProduct.shipping_time??>
					<span>奖品已发货</span>
					<em>${orderProduct.shipping_time?string("yyyy-MM-dd HH:mm:ss")}</em>
				<#else />
					<span>等待发货中</span>
					<em></em>
				</#if>
			</div>
			<#assign finished=(finished && orderProduct.shipping_time??) />

			<div class="state <#if finished && !orderProduct.sign_time??>on</#if>">
				<#if orderProduct.sign_time??>
					<#assign finished=true />
					<span>确认收货</span>
					<em>${orderProduct.sign_time?string("yyyy-MM-dd HH:mm:ss")}</em>
				<#else/>
					<span>确认收货</span>
					<#if finished>
						<b id="confirm-delivery">确认收货</b>
					</#if>
				</#if>
			</div>
			<#assign finished=(finished && orderProduct.sign_time??) />

			<div class="state <#if finished && orderProduct.share == 0> on </#if>" >
				<#if orderProduct.share gt 0>
					<span>晒单</span>
					<em><#if productShare??>${productShare.createtime?string("yyyy-MM-dd HH:mm:ss")}</#if></em>
					<i>奖励10个金币</i>
				<#else />
					<span>晒单</span>
					<#if finished><b id="share-btn">立即晒单</b></#if>
					<i>奖励10个金币</i>
				</#if>
			</div>
			<#assign finished=(finished && orderProduct.share gt 0) />

			<div class="state">
				<span>完成</span>
				<em><#if finished && productShare??>${productShare.createtime?string("yyyy-MM-dd HH:mm:ss")}</#if></em>
			</div>
		</div>
		<div class="delivery_left">
			<div class="step_box"></div>
			<div class="step_yuan" id="left-steps">
				<span></span>
				<span></span>
				<span></span>
				<span></span>
				<span></span>
				<span></span>
			</div>
		</div>
	</div>
</div>


<!-- 中奖物品 -->
<div class="shangpin wuliu_xinxi">
	<div class="winning">
		<div class="win_txt">
			<p>物流状态：<i class="green"><#if orderProduct.shipping_status==4 >已收货<#elseif  orderProduct.shipping_status==3 />运输中 <#else/>待发货</#if></i></p>
			<p>承运来源：${orderProduct.shipping}</p>
			<p>运单编号：${orderProduct.shipping_sn}</p>
		</div>
		<div class="goods_text">
			<p class="clearfix"><span class="fl">收货人：${orderProduct.consignee}</span><span class="fr">${orderProduct.consignee_mobile}</span></p>
			<h1>收货地址：${orderProduct.address}</h1>
		</div>
	</div>
</div>
<div class="shangpin">
	<a class="detail_goods clearfix" href="javascript:;" data-href="${util.ctx}/doubao/product/${productActivity.productActivity.id}.html">
		<div class="goods_pic"><img src="${productActivity.product.thumb}"></div>
		<div class="goods_text">
			<h1>(第${productActivity.productActivity.product_number}期)${productActivity.productActivity.product_name}</h1>
			<p class="clearfix"><span class="fl">揭晓时间：${productActivity.productActivity.lottery_time?string("MM-dd HH:mm")}</span><span class="fr">购买人次：<i class="orange">${productActivity.productActivity.lottery_buy_number}</i></span></p>
		</div>
	</a>
	<div class="winning num_waybill">
		<div class="win_txt">
			<p>订单编号：<i class="orange">${orderProduct.order_sn}</i></p>
			<p>中奖夺宝号：<i class="orange">${productActivity.productActivity.lottery_number}</i></p>
		</div>
	</div>
</div>

<#if orderProduct.shipping_status == 4 && orderProduct.share == 0>
	<!-- 收货完成 -->
	<div class="popup_bg">
		<div class="number_box">
			<h1>收货完成</h1>
			<h3>晒单有礼，赶快去晒单吧</h3>
			<div class="popup_bot">
				<div class="bot_btn on" id="dialog-share-btn"><a>确定</a></div>
				<div class="bot_btn" id="dialog-cancel-btn"><a>取消</a></div>
			</div>
		</div>
	</div>
</#if>

<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
$(function(){
	$('#left-steps>span').eq($('#right-steps>div.on').index()).addClass('on');

	$('#dialog-cancel-btn').click(function(){
		$(this).parents('.popup_bg').hide();
	});

	$('#dialog-share-btn, #share-btn').click(function(){
		$('body').attr('data-refresh', 1);
		$(this).parents('.popup_bg').hide();

		setTimeout(function(){toUrl('${util.ctx}/doubao/share/addshare.html?orderProductId=${orderProduct.id}');}, 100);
	});

	$('#confirm-delivery').click(function(){
		doConfirmDelivery();
	});

	function doConfirmDelivery(){
		myPost('${util.ctx}/doubao/api/my/confirmGoods', {orderProductId: ${orderProduct.id}}, function(resp) {
			if(resp.code){
				myAlert(resp.message);
			}else {
				myAlert('确认收货成功');
				setTimeout(function(){location.reload();}, 200);
			}
		});
	}
});
</script>
<#include "/common/footer.ftl">