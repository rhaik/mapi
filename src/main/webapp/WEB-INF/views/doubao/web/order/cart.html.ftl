<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
 <#import "/lib/util.ftl" as util>
 <#include "/doubao/common/header.ftl">
<style type="text/css">
	.goods{background:#fff; padding:10px 20px; padding-left:6.4em; border-bottom:1px solid #dcdcdc; overflow:hidden; position:relative;}
	.goods_pic{width:4.4em; height:4.4em; overflow:hidden; margin-left:-5.5em; float:left;}
	.good_right{width:100%;}
	.goods_name{height:2.2em; line-height:2.2em; font-size:0.9em; color:#000; background:#fff; border-bottom:1px solid #dcdcdc; padding:0 0.9em;}
	.goods_name a{color:#444; float:right;}
	.num_tit p{font-size:0.75em; color:#888; margin:0 1.25em 0.3em 0;}
	.num_tit p:last-child{margin-right:0;}
	.detailedlist{padding-right:2.2em;}
	.col_red{color:#e60012;}
	.ipt_box{margin-top:0.5em;}
	.choose_btn{border:1px solid #999;}
	.choose_btn span{width:1.5em; height:1.5em; line-height:1.5em; text-align:center; background:none; border:none; vertical-align:middle; outline:none; display:block; float:left; text-align:center;}
	.choose_btn input.number{width:100%; height:1.5em; border:none; overflow:hidden; border-radius:0; text-align:center; outline:none;}
	.end{width:1.7em; height:1.7em; line-height:1.7em; text-align:center; border-radius:1.7em; overflow:hidden;}
	.end a{font-size:0.68em; height:100%; color:#fff; display:block; background:#ff8003;}
	.tishi{width:100%; font-size:0.75em; color:#888; margin-top:0.6em; display:inline-block;}
	.end_on a{display:block; height:100%; background:#81d02b url(/static/images/duobao/end.png) no-repeat; background-size:100%;}
	.balance{position:fixed; bottom:0; left:0; width:100%; background:#fff; height:3.8em; border-top:1px solid #b5b5b5;}
	.sum{margin-left:0.625em; line-height:3.8em; font-size:1em; color:#000;}
	.sum i{color:#e60012; font-style:normal;}
	.sum_btn{width:5em; height:2.2em; line-height:2.2em; background:#ff8003; border-radius:0.3em; margin:1.2em 0.625em 0 0; font-size:0.9em; color:#fff; text-align:center;}
	.del{width:3em; height: 8.4em; line-height:2em; overflow:hidden; background:#e60012; position:absolute; right:-3em; top:0;}
	.del em{width:1.5em; margin:0 auto; text-align:center; display:block; padding:1.8em 0; font-size:1.2em; color:#fff; font-style:normal;}
	.ui-btn{float:left; width:1.56em; height:1.56em; overflow:hidden; position:relative;}
	.ui-btn input{width:1.56em; height:1.56em; background:none; border:none; display:none; position:absolute; top:0; left:0;}
	.ui-shadow-inset{width:2.4em; padding:0 0.45em; height:1.56em; border:none; float:left; outline:none; border-left:1px solid #999; border-right:1px solid #999;}
</style>
<header class="header">
	<a class="back" onclick="myClose();"><img width="100%" src="${util.static}images/duobao/back_btn.png" /></a>
	<span>清单</span>
</header>
<div class="pb">
	<!-- 中间内容 -->
	<div class="content_box">
		<!-- 清单 -->
		<form id="form" name="checkOrder" action="${util.ctx}/doubao/order/pay" method="post">
		<div class="main">
			<#list cartList as cart>
			<div class="main_box">
				<div class="goods_name">茉莉蔻超值日夜修护体验套装东方<a>删除</a></div>
				<div class="goods" data-id="${cart.productActivity.id}">
					<div class="goods_pic fl"><img width="100%" src="${cart.product.thumb}" /></div>				
					<div class="good_right fl">
						<div class="goods_name">(第${cart.productActivity.product_number}期) ${cart.productActivity.product_name}</div>
						<div class="num_tit clearfix">
							<p class="fl">总需：<span>${cart.productActivity.number}</span></p>
							<p class="fl">剩余：<span class="col_red">${cart.productActivity.number-cart.productActivity.buy_number}</span>人次</p>
						</div>
						<div class="ipt_box clearfix">
							<div class="choose_btn fl">
								<span class="lost">－</span><input class="number" name="product_${cart.cart.product_id}" min=1 max="${cart.productActivity.number-cart.productActivity.buy_number}" current="${cart.cart.number}" type="number" value="${cart.cart.number}" /><span class="add">＋</span>
							</div>
							<#if cart.all><div class="end fr end_on"><a></a></div><#else><div class="end fr"><a>包尾</a></div></#if>
							<#if (cart.msg?? && cart.msg?length gt 1)><div class="tishi" style="color:#ff8003">${cart.msg}</div><#else><div class="tishi">奖品最新一期正在进行中</#if></div>
						</div>
					</div>
				</div>
			</div>
			</#list>
		</div>
		</form>
	</div>
</div>
<div class="balance">
	<div class="sum fl">总计：<i>¥0</i></div>
	<div class="sum_btn fr"><a href="javascript:void(0);" onclick="document.getElementById('form').submit();">结算</a></div>
</div>
<!-- footer -->
<script type="text/javascript" src="${util.static}js/jquery.mobile.min.js"></script>
<script type="text/javascript">
$(function(){
	$('.ui-loader').remove();
	$('.number').change(function(event){
		event.stopPropagation();
		var value = $(this).parents('.ipt_box').find(".end a");
		if(parseInt($(this).val()) >= parseInt($(this).attr("max"))) {
			$(this).val($(this).attr("max"));
			selectMaxValue(value);
		} else {
			selectInputValue(value);
		}
		calcTotal();
	});
	$(".lost").click(function(event) {
		event.stopPropagation();
		var val = parseInt($(this).next().find('.number').val());
		if(val > 1){
			$(this).next().find('.number').val(val-1);
			calcTotal();
		}
		if((val-1) != $(this).next().find('.number').attr("max")) {
			selectInputValue($(this).parents('.ipt_box').find(".end a"));
		}
	});
	$(".add").click(function(event){
		event.stopPropagation();
		var val = parseInt($(this).prev().find('.number').val());
		var max = parseInt($(this).prev().find('.number').attr("max"));
		if(val < max){
			val = val+1;
			$(this).prev().find('.number').val(val);
			calcTotal();
		} 
		if(max == val) {
			selectMaxValue($(this).parents('.ipt_box').find(".end a"));
		}
	});
	$(".end a").click(function(event){
		event.stopPropagation();
		var number = $(this).parents('.ipt_box').find('.number');
		 if($(this).html() == '') {
		 	selectInputValue(this);
		 	number.val(number.attr("current"));
		 } else {
			selectMaxValue(this);
			number.val(number.attr("max"));
		}
		calcTotal();
	});
	$('.goods_name a').click(function(){
		$(this).parents('.main_box').empty();
	})

	selectMaxValue = function(tthis) {
		$(tthis).html("");
		$(tthis).parent().addClass("end_on");
		$(tthis).parents('.ipt_box').find(".tishi").css({"color":"#ff8003"})
		$(tthis).parents('.ipt_box').find(".tishi").html("购买人次自动调整为包尾人次，确认订单后 获得包尾特权");
	};
	selectInputValue = function(tthis){
		$(tthis).html("包尾");
	 	$(tthis).parent().removeClass("end_on");
	 	$(tthis).parents('.ipt_box').find(".tishi").html("奖品最新一期正在进行中");
	 	$(tthis).parents('.ipt_box').find(".tishi").css({"color":"#000"})
	};
	calcTotal = function() {
		var total = 0;
		$('.goods').each(function(){
			total += parseInt($(this).find('.number').val());
		});
		$('.sum>i').html("¥"+total);
	};
	calcTotal();
	
	$("input").click(function(event){
		event.stopPropagation();
	});
	/*$(".goods").on("swipeleft",function(){
		$(this).stop().animate({
			marginLeft:"-3em",
			paddingRight:"4em"
		})
		$(this).find(".del").stop().animate({
			right:0
		})
		$(this).addClass('on');
	})*/
	$('.del').click(function(event){
		event.stopPropagation();
		var goods = $(this).parents('.goods')
		var product_activity_id = $(this).attr('data-id');
		$.ajax({
		   type: "POST",
		   data:{product_activity_id:product_activity_id},
		   url: "${util.ctx}/api/v1/doubao/order/cart/delete",
		   dataType:'json',
		   success:function(r) {
		   		if(r.code == 0) {
		   			goods.remove();
		   			calcTotal();
		   		} else {
		   			myAlert(r.message);
		   		}
		   }
		});
	});
	$(".goods").on("click",function(){
		if($(this).hasClass("on")){
			$(this).stop().animate({
				marginLeft:"0",
				paddingRight:"20px"
			})
			$(this).find(".del").stop().animate({
				right:"-3em"
			})
			$(this).removeClass('on')
		}else{
			var id = $(this).attr('data-id');
			toUrl('${util.ctx}/doubao/product/'+id);
		}
	})
});
</script>
</body> 
</html> 