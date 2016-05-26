<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div>
	<div class="address_write clearfix" <#if orderProduct.shipping_status == 1>id="choose-address"</#if> >
	<#if ua??>
		<em class="address_logo"><img width="100%" src="${util.static}images/duobao/site.png"></em>
		<div class="address fl">
			<p>收货人：${ua.name}<em class="fr">${ua.mobile}</em></p>
			<p>收货地址：${ua.address}</p>
		</div>
		<div class="right_btn"><img src="${util.static}images/img/right_ico.png" /></div>
	<#else>
		<em class="address_logo"><img width="100%" src="${util.static}images/duobao/site.png"></em>
		<div class="address fl">
			<span>＋点击选择收货地址</span>
		</div>
	</#if>
	</div>

	<!-- 已发货后才显示 -->
	<#if orderProduct.shipping_time??>
	<div class="delivered dn">
		<p>运单编号：${orderProduct.shipping_sn}</p>
		<p>承运来源：${orderProduct.shipping}</p>
	</div>
	</#if>
	<div class="mt20">
		<div class="wares clearfix">
			<em class="wares_pic fl"><span><img src="${productActivity.product.thumb}"></span></em>
			<div class="wares_txt fl">
				<h1>(第${productActivity.productActivity.product_number}期)${productActivity.productActivity.product_name}</h1>
				<p>本期总需夺宝次数：${productActivity.productActivity.number} 次</p>
			</div>
		</div>
		<div class="join">本期参与<a>${productActivity.productActivity.lottery_buy_number}人次</a></div>
		<div class="message" id="contact-us"><em><img src="${util.static}images/duobao/message.png" /></em>联系我们</div>
	</div>
	<div class="mt20 infor">
		<p>中奖夺宝号：${productActivity.productActivity.lottery_number}</p>
		<p>中奖时间：${productActivity.productActivity.lottery_time?string("MM-dd HH:mm:ss")}</p>
		<p>订单编号：${orderProduct.order_sn}</p>
	</div>
</div>
<#if orderProduct.shipping_status == 1>
<div class="btn_big">
	<a class="btn <#if !ua??>btn_grey</#if>" id="confirm-btn">确定收货地址</a>
</div>
</#if>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
$(function(){
	var addressId=<#if ua??>${ua.id}<#else>0</#if>;
	$('#choose-address').click(function(){
		toUrl('${util.ctx}/doubao/my/addressList.html?addressId=' + addressId);
	});

	$('#contact-us').click(function(){
		if(typeof MiJSBridge=="object") {
			myAlert('请拨打秒赚大钱客服电话：01057225923');
		}else{
			myConfirm('确定联系秒赚大钱客服吗？确定后将拨打客服电话。', function(){
				location.href = 'tel:01057225923'
			});
		}
	});

	$('#confirm-btn').click(function(){
		if($(this).hasClass('btn_grey'))return;
		addressId > 0 ||  myAlert('请先选择收货地址');
		myConfirm('确定使用该收货地址吗？确定后将无法修改收货地址。', function(){
			doConfirmAddress();
	 	});
	});

	$.onPageShowAgain = function(event){
		var addrId = parseInt($.cookie('addrList_selected_id')) || 0;
		var addrContent = $.cookie('addrList_address_content');

		if(addrId > 0 && addrContent && addrId != addressId){
			addressId = addrId;
			var selAddr = JSON.parse(addrContent);
			$('#choose-address .address').html('<p>收货人：' + selAddr.name + '<em class="fr">' + selAddr.mobile + '</em></p><p>收货地址：' +  selAddr.address + '</p>');
			!$('#choose-address .right_btn').size() && $('#choose-address').append('<div class="right_btn"><img src="${util.static}images/img/right_ico.png" /></div>');
			$('#confirm-btn').removeClass('btn_grey');
		}
	};

	function doConfirmAddress(){
		myPost('${util.ctx}/doubao/api/my/saveConsignee', {orderProductId: ${orderProduct.id}, addressid: addressId }, function(resp) {
			if(resp.code){
				myAlert(resp.message);
			}else {
				myAlert('确认收货地址成功');
				setTimeout(function(){location.reload();}, 200);
			}
		});
	}
});
</script>
<#include "/common/footer.ftl">