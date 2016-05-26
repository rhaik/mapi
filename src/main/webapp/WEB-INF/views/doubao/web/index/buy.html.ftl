<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="pb">
	<!-- 支付内容 -->
	<div class="detail_box">
		<div class="detail_goods clearfix">
			<div class="goods_pic"><img src="${activity.product.thumb}" /></div>
			<div class="goods_text">
				<h1>${activity.productActivity.product_name}</h1>
				<p class="clearfix"><span class="fl">第${activity.productActivity.product_number}期</span><span class="fr">购买人次：${number} 人次</span></p>
			</div>
		</div>
		<div class="pay">需支付：<i class="red">${number}</i>夺宝币</div>
	</div>
	<div class="balance">
		<span>夺宝币支付</span>
		<b>（拥有夺宝币：${util.fen2yuan(duobaoCoin.balance)}个）</b>
		<span class="fr">个</span>
		<i class="red fr">${number}</i>
	</div>
	<div class="xieyi" id="xieyi-panel">
		<em class="on"></em>
		<span>我已认真阅读并接受<a href="javascript:;" data-href="<#if fromSafari??>${util.static}/html/safari_frm.html?pg=</#if>${util.static}html/duobao/serve.html">秒赚大钱一元夺宝服务协议</a></span>
	</div>
</div>
<div class="posi_btn" id="submit-btn">
	<a>提交夺宝</a>
</div>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
$(function(){
	$('#submit-btn').click(function(){
		doDuobao();
	});

	$("#xieyi-panel em").click(function(){
		$(this).toggleClass("on");
	})

	function doDuobao(num){
		if(!$("#xieyi-panel em").hasClass('on')) {
			return myAlert('您需要先阅读并接受秒赚大钱一元夺宝服务协议，才能提交夺宝');
		}
		$.showTips('正在提交夺宝...');
		myPost("${util.ctx}/doubao/api/order/sumbitOrder", {activityId:${activity.productActivity.id}, number: ${number}}, function(r){
			$.hideTips();
			if(r.code==0) {
				$.cookie('refreshedDuobaoHome', 0, 0);
				$.showDialog('恭喜您成功参与夺宝', '请耐心等待开奖，获奖后会有的短信通知您。', {
					sureText: '继续夺宝', sureCallback: function(){myClose();},
					cancelText: '夺宝记录', cancelCallback : function(){toUrl('${util.ctx}/doubao/my/lotteryrecord.html');}
				});
			} else {
				myAlert(r.message);
			}
	    });
	}

	<#if (number * 100) gt duobaoCoin.balance>
		$.showDialog('', '您的夺宝币不足，请先兑换更多夺宝币或调整夺宝次数', {
			sureText: '去兑换', sureCallback: function(){ $('body').attr('data-refresh', 1);  $.hideDialog(); toUrl('${util.ctx}/doubao/my/recharge.html');},
			cancelText: '修改夺宝数', cancelCallback : function(){myClose();}
		});
	</#if>
});
</script>
<#include "/common/footer.ftl">