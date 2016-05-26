<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<div class="android_box">
<#if ualist?? && ualist?has_content>
	<#list ualist as addr>
		<div class="android_address clearfix">
			<div class="address_box clearfix <#if !toEdit><#if addressId gt 0><#if addr.id == addressId>on</#if><#elseif addr.default>on</#if></#if>" data-aid="${addr.id}" data-name="${addr.name}" data-mobile="${addr.mobile}" data-address="${addr.address}">
				<!-- 收货信息 -->
				<div class="address fl">
					<p>收货人：${addr.name}<span class="fr">${addr.mobile}</span></p>
					<p><#if addr.default><b>[默认]</b></#if>收货地址：${addr.address}</p>
				</div>
				<#if toEdit>
					<div class="android_btn fr" data-aid="${addr.id}">
						<a class="android_edit_btn"><span><img src="${util.static}images/duobao/edit.png" /></span>编辑</a>
						<a class="android_del_btn"><span><img src="${util.static}images/duobao/delete.png" /></span>删除</a>
					</div>
				</#if>
				<span class="right_push"><img width="100%" src="${util.static}images/duobao/select_address.png" /></span>
			</div>
		</div>
	</#list>
<#else>
	<div class="android_kong">
		<span><img src="${util.static}images/duobao/kong_an.png" /></span>
		<p>您还没哟收货地址哦！</p>
	</div>
</#if>
<div class="bottom_btn">
	<a href="javascript:;" id="add-address">添加收货地址</a>
</div>
</div>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script>
$(function(){
	$('#add-address').click(function(){
		$('body').attr('data-refresh', 1);
		toUrl('${util.ctx}/doubao/my/address.html');
	});

<#if toEdit>
	$('.android_edit_btn').click(function(){
		$('body').attr('data-refresh', 1);
		var aid = $(this).parent().data('aid');
		toUrl('${util.ctx}/doubao/my/address.html?addressId=' + aid);
	});

	$('.android_del_btn').click(function(){
		var addressId = $(this).parent().data('aid');
		if(addressId >0 && confirm('确定删除该地址吗？') ) {
			myPost("${util.ctx}/doubao/api/my/deleteUserAddress", {addressId:addressId},function(r){
				if(r.code==0) {
					 myAlert("删除成功!");
					 setTimeout(function(){location.reload();}, 200);
				} else {
					 myAlert(r.message);
				}
			});
		}
	});
<#else>
	$('.address_box').click(function(){
		var aid = $(this).data('aid');
		$(this).addClass('on').siblings().removeClass('on');
		$.cookie('addrList_selected_id', aid, 300);
		$.cookie('addrList_address_content', JSON.stringify({name: $(this).data('name'), mobile: $(this).data('mobile'), address: $(this).data('address')}) ,300);
		myClose();
	});
</#if>

});
</script>
<#include "/common/footer.ftl"/>