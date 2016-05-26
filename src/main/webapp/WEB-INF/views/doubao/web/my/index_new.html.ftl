<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="personal">
	<#if !(user.mobile?has_content)>
		<a class="personal_top" href="javascript:;" id="setting-mobile">
			<span><img src="${util.static}images/duobao/notice.png" /></span>
			<p>为了保证账户安全和及时获得中奖消息，请绑定您的手机号</p>
			<em><img src="${util.static}images/duobao/right_black.png" /></em>
		</a>
	</#if>
	<div class="people">
		<a class="head" href="javascript:;" id="edit-info"><img src="${user.headImg}" /><em>编辑信息</em></a>
		<p>${user.uniName}</p>
		<span>夺宝币：<i class="orange">${util.fen2yuan(duobaoCoin.balance)}</i>个</span>
		<a href="javascript:;" id="exchange-btn">去兑换</a>
	</div>
	<div class="personal_list">
		<a class="clearfix" href="javascript:;" data-href="${util.ctx}/doubao/my/lotteryrecord.html">
			<span><img src="${util.static}images/duobao/icon_01.png" /></span>
			<p>夺宝记录</p>
			<em><img src="${util.static}images/img/right_ico.png" /></em>
		</a>
		<a class="clearfix" href="javascript:;" data-href="${util.ctx}/doubao/my/winrecord.html">
			<span><img src="${util.static}images/duobao/icon_06.png" /></span>
			<p>中奖纪录</p>
			<em><img src="${util.static}images/img/right_ico.png" /></em>
		</a>
		<a class="clearfix" href="javascript:;" data-href="${util.ctx}/doubao/share/my.html">
			<span><img src="${util.static}images/duobao/icon_02.png" /></span>
			<p>我的晒单</p>
			<em><img src="${util.static}images/img/right_ico.png" /></em>
		</a>
	</div>
	<div class="personal_list">
		<a class="clearfix" href="javascript:;" data-href="${util.ctx}/doubao/my/addressList.html?toEdit=1">
			<span><img src="${util.static}images/duobao/icon_03.png" /></span>
			<p>我的收货地址</p>
			<em><img src="${util.static}images/img/right_ico.png" /></em>
		</a>
	</div>
	<div class="personal_list">
		<a class="clearfix" href="javascript:;"  data-href="<#if fromSafari??>${util.static}/html/safari_frm.html?pg=</#if>${util.static}html/duobao/serve.html">
			<span><img src="${util.static}images/duobao/icon_04.png" /></span>
			<p>服务协议</p>
			<em><img src="${util.static}images/img/right_ico.png" /></em>
		</a>
		<a class="clearfix" href="javascript:;"  data-href="<#if fromSafari??>${util.static}/html/safari_frm.html?pg=</#if>${util.static}html/duobao/guarantee.html">
			<span><img src="${util.static}images/duobao/icon_05.png" /></span>
			<p>消费者保障声明</p>
			<em><img src="${util.static}images/img/right_ico.png" /></em>
		</a>
	</div>
	<div class="tishi_bot">所有夺宝活动与苹果公司（Apple Inc.）无关</div>
</div>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
	$(function(){
		var needRefresh = false;
		$('#exchange-btn').click(function(){
			$('body').attr('data-refresh', 1);
			toUrl('${util.ctx}/doubao/my/recharge.html');
		});
		$('#setting-mobile').click(function(){
			$('body').attr('data-refresh', 1);
			if(typeof MiJSBridge=="object") {
				MiJSBridge.call('bindMobile');
			}else{
				toUrl('${util.ctx}/ios/my/to_bind_mobile.html#withdraw');
			}
		});
		$('#edit-info').click(function(){
			$('body').attr('data-refresh', 1);
			toUrl('${util.ctx}/doubao/my/editinfor.html');
		});
	});
</script>
<#include "/common/footer.ftl" />