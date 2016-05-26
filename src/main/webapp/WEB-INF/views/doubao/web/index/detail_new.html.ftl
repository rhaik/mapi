<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<!-- 中间内容 -->

<div class="pb_btn">
	<!-- 中间内容 -->
	<div class="prize_box">
		<a class="prize_pic" href="javascript:;" data-href="${util.ctx}/doubao/product/content-${product.productActivity.product_id}.html">
			<span><img src="${product.product.thumb}" /></span>
			<p class="look">查看商品详情(建议WiFi下查看)</p>
		</a>
		<div class="states">
			<#if product.productActivity.status==3>
				<!-- 已揭晓 -->
				<!-- 显示倒计时时底下立即夺宝变为确认支付 -->
				<div class="prize_infor">
					<h1><em class="over">已揭晓</em>(第${product.productActivity.product_number}期)${product.productActivity.product_name}</h1>
					<div class="soon_see clearfix">
						<span class="fl">中奖号</span>
						<div class="countdown fl">
							<span class="mini">${product.productActivity.lottery_number}</span>
						</div>
						<a href="javascript:;" data-href="${util.ctx}/doubao/productcalc/${product.productActivity.id}.html" class="fr">查看计算详情<em><img src="${util.static}images/img/right_ico.png"></em></a>
					</div>
					<div class="winning">
						<div class="win_head"><img src="${product.user.headImg}"></div>
						<div class="win_txt">
							<p>中奖用户：${product.user.uniName}</p>
							<p>手机号码：${product.user.hideMobile}</p>
							<p>参与人次：${product.productActivity.lottery_buy_number}</p>
							<p>揭晓时间：${product.productActivity.beforMinute}</p>
						</div>
					</div>
					<#if userBuy gt 0><div class="not_join duobao_num" data-number="${userBuy}">已参与${userBuy}人次，点击查看夺宝号&gt;&gt;</div></#if>
				</div>
			<#elseif product.productActivity.status==2>
				<!-- 揭晓中 -->
				<!-- 显示倒计时时底下立即夺宝变为确认支付 -->
				<div class="prize_infor">
					<h1><em class="ing">揭晓中</em>(第${product.productActivity.product_number}期)${product.productActivity.product_name}</h1>
					<div class="soon_see clearfix">
						<span class="fl">倒计时</span>
						<div class="countdown fl" id="fnTimeCountDown" data-end="${product.productActivity.lotteryTimes?string('yyyy/MM/dd HH:mm:ss')}" >
							<span class="mini">10</span><span>分</span><span class="sec">00</span><span>秒</span><span class="hm">147</span>
						</div>
						<a href="javascript:;" data-href="${util.ctx}/doubao/productcalc/${product.productActivity.id}.html" class="fr">查看计算详情<em><img src="${util.static}images/img/right_ico.png"></em></a>
					</div>
					<#if userBuy gt 0><div class="not_join duobao_num" data-number="${userBuy}">已参与${userBuy}人次，点击查看夺宝号&gt;&gt;</div></#if>
				</div>
			<#else>
				<!-- 进行中 -->
				<div class="prize_infor">
					<!-- on是进行中，over是已揭晓，ing是揭晓中 -->
					<h1><em class="on">进行中</em>(第${product.productActivity.product_number}期)${product.productActivity.product_name}</h1>
					<div class="surplus">
						<div class="bar_big"><div class="bar_sma" style="width:${product.productActivity.buy_number/product.productActivity.number*100}%"></div></div>
						<div class="num_tit clearfix">
							<p class="fl">总需：${product.productActivity.number}</p>
							<p class="fr">剩余：<span>${product.productActivity.number-product.productActivity.buy_number}</span></p>
						</div>
					</div>
					<#if userBuy gt 0><div class="not_join duobao_num" data-number="${userBuy}">已参与${userBuy}人次，点击查看夺宝号&gt;&gt;</div>
					<#else/> <div class="not_join">您还没有参加，赶快参加吧</div>
					</#if>
				</div>
			</#if>
			</div>
		</div>

	<!-- 列表 -->
	<div class="prize_list">
		<a href="javascript:;" data-href="${util.ctx}/doubao/history/${product.productActivity.product_id}.html">往期揭晓<span><img src="${util.static}images/img/right_ico.png" /></span></a>
		<a href="javascript:;" data-href="${util.ctx}/doubao/share/history/${product.productActivity.product_id}.html">往期晒单<span><img src="${util.static}images/img/right_ico.png" /></span></a>
	</div>

	<!-- 参与记录 -->
	<div class="record_box">
		<span class="join_record">参与记录</span>
		<div class="record_num scrollWindow" data-url="${util.ctx}/doubao/api/common/buyhistory/${product.productActivity.id}" data-template="record-template">
			<#list orderProductList as op>
			<div class="record_list">
				<div class="record_img"><img src="${op.user.headImg}" /></div>
				<div class="record_txt">
					<h1>${op.user.uniName}</h1>
					<p>${op.orderProduct.ip_area} IP:${op.orderProduct.hideIp}</p>
					<p>参与${op.orderProduct.number}人次 ${op.orderProduct.createtime?string("yyyy-MM-dd HH:mm:ss")}</p>
				</div>
			</div>
			</#list>
		</div>
	</div>

	<div class="bottom" id="loading-panel" style="display:none;">
		<span><img src="${util.static}images/loading.gif" /></span>
		<p>正在加载中</p>
	</div>
	<!-- 底部 -->
	<div class="foot_btn" id="duobao-btn" >
		<#if product.productActivity.status != 1>
			<a href="javascript:;" data-href="${util.ctx}/doubao/latest/${product.productActivity.product_id}.html?latest=1">前往最新一期</a>
		<#else/>
			<a href="javascript:;"><#if userBuy == 0>立即夺宝<#else/>追加夺宝</#if></a>
		</#if>
	</div>
</div>

<div class="num_bg dn" id="duobao-number-dialog">
	<div class="num_box">
		<h1>查看夺宝号<span class="close"  href="javascript:;"><img src="${util.static}images/sign/no.png" /></span></h1>
		<h2>本期购买<i class="orange" id="duobao-times">1</i>人次</h2>
		<ul id="duobao-numbers"></ul>
	</div>
</div>
<script id="record-template" type="text/x-handlebars-template">
	{{#data}}
	<div class="record_list">
		<div class="record_img"><img src="{{headImg}}" /></div>
		<div class="record_txt">
			<h1>{{{uniName}}}</h1>
			<p>{{ip_area}} IP:{{hideIp}}</p>
			<p>参与{{number}}人次 {{createtime}}</p>
		</div>
	</div>
	{{/data}}
</script>

<script src="${util.zepto}"></script>
<script src="${util.handlebars}"></script>
<script src="${util.static}js/duobao.js"></script>

<#if product.productActivity.status == 1>
<div class="popup_bg dn" id="buy-popup">
	<div class="number_box fixed_bot">
		<h1>调整购买人次</h1>
		<div class="number_ipt clearfix">
			<a class="jian">－</a>
			<div class="text_num"><input value="1" type="tel" data-max="${product.productActivity.number-product.productActivity.buy_number}" data-min="1"/></div>
			<a class="jia">＋</a>
		</div>
		<div class="popup_bot">
			<div class="bot_btn on" id="buy-confirm-btn"><a>确定</a></div>
			<div class="bot_btn" id="buy-cancel-btn"><a>取消</a></div>
		</div>
	</div>
</div>
<script type="text/javascript">
	$(function(){
		var $input = $(".text_num input");
		var maxBuy = parseInt($input.data('max')) || 5, minBuy = parseInt($input.data('min')) || 1;
		$("a.jia").click(function(){
			var value= parseInt($input.val()) || 1;
			value = (value == maxBuy? maxBuy : value + 1);
			$(".text_num input").val(value);
		})
		$("a.jian").click(function(){
			var value= parseInt($input.val()) || 1;
			value = (value == minBuy? minBuy : value - 1);
			$input.val(value);
		});

		$input.change(function(){
			var value= parseInt($input.val()) || 1;
			value = value < minBuy? minBuy : (value > maxBuy? maxBuy : value);
			$input.val(value);
		});

		$('#duobao-btn').click(function(){
			<#if !user.mobile?has_content>
				$.showDialog('', '你还没有绑定手机号，请先绑定手机号', {sureText: '去绑定', sureCallback:
					function(){
					$('body').attr('data-refresh', 1); $.hideDialog();
					if(typeof MiJSBridge=="object") {
						MiJSBridge.call('bindMobile');
					}else{
						toUrl('${util.ctx}/ios/my/to_bind_mobile.html#withdraw');
					}}
				});
			<#elseif duobaoCoin.balance lt 1>
				$.showDialog('', '你的夺宝币不足，请先兑换夺宝币', {sureText: '去兑换', sureCallback:
					function(){ $('body').attr('data-refresh', 1);  $.hideDialog(); toUrl('${util.ctx}/doubao/my/recharge.html');}
				});
			<#else>
				$('#buy-popup').removeClass('dn');
			</#if>
		});

		$('#buy-cancel-btn').click(function(){
			$('#buy-popup').addClass('dn');
		});

		$('#buy-confirm-btn').click(function(){
			$('#buy-popup').addClass('dn');
			var value = parseInt($input.val()) || 1;
			doDuobao(value);
		});

		function doDuobao(num){
			$('body').attr('data-refresh', 1);
			$.cookie('buy_num', num, 600);
			<#if ua.inAppView>
			toUrl('${util.ctx}/doubao/buy/${product.productActivity.id}.html?num=' + num);
			<#else>
			toUrl('${util.ctx}/doubao/buy/${product.productActivity.id}.html');
			</#if>
		}
	});
</script>
</#if>

<#if product.productActivity.status==2>
<script id="lottery-template" type="text/x-handlebars-template">
	<h1><em class="over">已揭晓</em>(第${product.productActivity.product_number}期)${product.productActivity.product_name}</h1>
	<div class="soon_see clearfix">
		<span class="fl">中奖号</span>
		<div class="countdown fl">
			<span class="mini">{{lottery_number}}</span>
		</div>
		<a href="javascript:;" data-href="${util.ctx}/doubao/productcalc/${product.productActivity.id}.html" class="fr">查看计算详情<em><img src="${util.static}images/img/right_ico.png"></em></a>
	</div>
	<div class="winning">
		<div class="win_head"><img src="{{headImg}}"></div>
		<div class="win_txt">
			<p>中奖用户：{{{uniName}}}</p>
			<p>手机号码：{{mobile}}</p>
			<p>参与人次：{{number}}</p>
			<p>揭晓时间：{{beforMinute}}</p>
		</div>
	</div>
</script>
<script type="text/javascript" src="${util.static}js/countdown.js"></script> 
<script type="text/javascript">
	var announcedTimeId;
	$("#fnTimeCountDown").countDown(checkAnnounce);

	function checkAnnounce(){
		$.ajax({
		   type: "GET",
		   url: "${util.ctx}/doubao/api/common/announced/${product.productActivity.id}",
		   dataType:'json',
		   success:function(r){
		   		if(r.code==0 && r.product) {
		   			var html = handlebars['lottery-template'](r.product);
		   			var duobaoNum = $('div.prize_infor .duobao_num').clone();
					$('div.prize_infor').html(html).append(duobaoNum);

					clearTimeout(announcedTimeId);
		   		} else {
		   			$('.soon_see>.fl').html('正在揭晓中，请稍后...');
		   			$('.soon_see>div').remove();
		   			announcedTimeId = setTimeout(checkAnnounce, 10*1000);
		   		}
		   }
		});
	}
</script>
</#if>
<script>
	$(function(){
		$('#duobao-number-dialog .close').click(function(){
			$('#duobao-number-dialog').addClass('dn');
		});
		$('div.prize_infor').on('click', '.duobao_num', function(){
			$('#duobao-times').text($(this).data('number'));
			$('#duobao-numbers').empty();
			$('#duobao-number-dialog').removeClass('dn');
			getDuobaoNumbers(${product.productActivity.id});
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