<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="amount" id="recharge-amount">
	<ul class="clearfix">
		<#list allowedAmount as amount>
			<li data-amount="${amount.amount}" data-pay="${amount.payAmount}">
				<a><div class="box"><span>${amount.amount}个</span><em>支付${amount.payAmount}元</em><#if amount.reserved gt 0><i class="promotion">省${amount.reserved?string(".#")}元</i></#if></div></a>
			</li>
		</#list>
	</ul>
</div>
<div class="money">
	拥有夺宝币：<i class="red">${util.fen2yuan(duobaoCoin.balance)}</i> 个
</div>
<div class="money">
	可兑换余额：<i class="red">${util.fen2yuanS(availBalance)}</i> 元
</div>
<div class="cz_tishi">1元人民币兑换1个夺宝币<br/>夺宝币不支持提现，兑换后只能参与一元夺宝<br/>目前仅支持使用余额兑换夺宝币<#if income.encash_total lte 0>。为保证账户安全，未提过现用户的可兑换余额比实际余额少2元</#if></div>
<div class="button" id="exchange-btn">
	<a>立即兑换</a>
</div>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
$(function(){
	var balance = ${util.fen2yuanS(availBalance)};
	$("#recharge-amount li").click(function(){
		$(this).addClass("on").siblings().removeClass("on");
	}).eq(0).addClass('on');
	$('#exchange-btn').click(function(){
		var amount = parseInt($('#recharge-amount li.on').data('amount')) || 0;
		if(amount <= 0) {
			return myAlert('兑换数量错误');
		}
		var pay = parseFloat($('#recharge-amount li.on').data('pay')) || 0;
		if (pay > balance){
			return myAlert('可兑换余额不足');
		}
		doExchange(amount);
	});

	function doExchange(amount){
		$.showTips('正在兑换...', true);
		myPost('${util.ctx}/doubao/api/my/recharge', { amount : amount}, function(resp) {
			$.hideTips();
			if(resp.code){
				myAlert(resp.message);
			}else {
				myAlert('兑换成功', function(){ location.reload();});
			}
		});
	}
});
</script>
<#include "/common/footer.ftl"/>