<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 <#import "/lib/util.ftl" as util>
 <#include "/doubao/common/header.ftl">
 <style type="text/css">
	.pay_num{padding:0.625em 0.9em; background:#fff;}
	.pay_num p{font-size:1em; color:#000; line-height:1.6em;}
	.pay_num p span{color:#e60012;}
	.balance{background:#fff; padding:0 0.9em; margin:0.9em 0; line-height:3.125em; font-size:1em; color:#000;}
	.pay_choose{width:0.94em; height:0.94em; display:block; float:right; margin-top:1.1em;background:url(${util.static}images/duobao/choose.png) no-repeat; background-size:100%;}
	.pay_choose img:active{opacity:0;}
	.pay_text{padding:0.3em 0 0.3em 0.9em; background:#fff; margin-top:0.9em;}
	.pay_text p{font-size:0.875em; color:#666; padding-right:4em; height:2em; line-height:2em; border-bottom:1px solid #dcdcdc; overflow:hidden; text-overflow:ellipsis; display:-webkit-box; -webkit-line-clamp: 1; -webkit-box-orient:vertical; position:relative;}
	.pay_text p:last-child{border:none;}
	.pay_text p span{position:absolute; right:0.625em; top:0;}
	.posi_btn{width:100%; background:#fff; position:fixed; bottom:0; border-top:1px solid #dcdcdc;}
	.posi_btn a{margin:0.625em 0.9em; height:2.8em; line-height:2.8em; display:block; font-size:1em; color:#fff; background:#ff8003; border-radius:0.3em; text-align:center;}
	.success_bg{width:100%; height:100%; background:rgba(0,0,0,.5); position:fixed; top:0; left:0; z-index:9999;}
	.success{width:19.375em; height:11.25em; border-radius:0.5em; background:#fff; position:absolute; top:50%; left:50%; margin:-5.62em 0 0 -9.687em;}
	.success h1{font-size:1em; color:#000; padding:3.5em 0 2.5em; text-align:center; font-weight:normal;}
	.success a{width:16.875em; height:2.5em; line-height:2.5em; margin:0 auto; display:block; font-size:0.9em; color:#fff; text-align:center; background:#ff8003; border-radius:0.3em;}
</style>
<!-- header -->
<header class="header">
	<a class="back" onclick="myClose();"><img width="100%" src="${util.static}images/duobao/back_btn.png" /></a>
	<span>支付</span>
</header>
<!-- 中间内容 -->
<div class="pt pb">
	<div class="pay_num" data-payAmount="${total}">
		<p>商品数量&nbsp;&nbsp;&nbsp; ${productNumber}件</p>
		<p>应付金额&nbsp;&nbsp;&nbsp; <span>${util.fen2yuan(total)}元</span></p>
	</div>
	<div class="balance" data-balance="${userIncome.balance}">
		<span>余额支付（账户余额${util.fen2yuan(userIncome.balance)}元）</span>
		<a class="pay_choose"><img src="${util.static}images/duobao/choose_btn.png" /></a>
	</div>
	
	<div class="pay_text">
		<#list cartList as cart>
		<p>${cart.productActivity.product_name} <span class="fr">${cart.cart.number}人次</span></p>
		 </#list>
	</div>
</div>
<!-- footer -->
<div class="posi_btn">
	<a>确认支付</a>
</div>
<div class="success_bg dn">
	<div class="success">
		<h1>购买成功</h1>
		<a href="javascript:void(0);" onclick="myClose();toUrl('${util.ctx}/doubao/my/lotteryrecord.html')">返回列表</a>
	</div>
</div>
<script>
$(function(){ 
	$('.posi_btn>a').one('click',function(){
		save(this);
	});
	function save(o) { 
		$(o).text('提交中...');
		var payAmount = parseInt($('.pay_num').attr("data-payAmount"));
		var balance = parseInt($('.balance').attr("data-balance"));
		if(balance < payAmount) {
			myAlert("您的余额不足");
			return ;
		} 
		$.ajax({
		   type: "POST",
		   url: "${util.ctx}/api/v1/doubao/order/sumbitOrder",
		   dataType:'json',
		   success:function(r){
		   		if(r.code == 0){
		   			$('.success_bg').show();
		   		} else {
		   			$(o).text('确认支付');
		   			$(o).one('click', function(){save(o)});
		   			myAlert(r.message);
		   		}
		   }
		});
	}
});
</script>
</body> 
</html> 