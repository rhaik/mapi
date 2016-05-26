<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<#if ualist?? && ualist?has_content>
	<#list ualist as addr>
		<div class="address_box clearfix <#if !toEdit><#if addressId gt 0><#if addr.id == addressId>on</#if><#elseif addr.default>on</#if></#if>" data-aid="${addr.id}" data-name="${addr.name}" data-mobile="${addr.mobile}" data-address="${addr.address}">
			<!-- 收货信息 -->
			<div class="address fl">
				<p>收货人：${addr.name}<span class="fr">${addr.mobile}</span></p>
				<p><#if addr.default><b>[默认]</b></#if>收货地址：${addr.address}</p>
			</div>
			<span class="right_push"><img width="100%" src="${util.static}images/duobao/select_address.png" /></span>
		</div>
	</#list>
<#else>
	<div class="kong clearfix">
		<span><img src="${util.static}images/duobao/kong.png" /></span>
		<p>您尚未添加收货地址哦~赶紧点击右上角添加吧</p>
	</div>
</#if>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
$(function(){
	$('header a.right_btn').show().attr('href', 'javascript:;').click(function(){
		$('body').attr('data-refresh', 1);
		toUrl('${util.ctx}/doubao/my/address.html');
	}).find('img').attr('src', '${util.static}images/duobao/add_btn.png');

	$('.address_box').click(function(){
		var aid = $(this).data('aid');
		<#if toEdit>
		toUrl('${util.ctx}/doubao/my/address.html?addressId=' + aid);
		<#else>
		$(this).addClass('on').siblings().removeClass('on');
		$.cookie('addrList_selected_id', aid, 300);
		$.cookie('addrList_address_content', JSON.stringify({name: $(this).data('name'), mobile: $(this).data('mobile'), address: $(this).data('address')}) ,300);
		myClose();
		</#if>
	});
});
</script>
<#include "/common/footer.ftl"/>