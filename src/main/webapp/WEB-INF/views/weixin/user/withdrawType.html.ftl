<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
<!--
body{background-color:#f0eff5}
-->
</style>
<div style="margin-top:10px">
<ul class="ui-list ui-list-text ui-list-active ui-list-link ui-border-tb">
	<li class="ui-border-t" data-url="<#if fromSafari??>/ios<#else>/weixin</#if>/user/withdraw_account.html?type=1" style="display:none;">
		<p>微信</p>
	</li>
	<li class="ui-border-t" data-url="<#if fromSafari??>/ios<#else>/weixin</#if>/user/withdraw_account.html?type=2">
		<p>支付宝</p>
	</li>
</ul>
</div>
<script>
window.onpageshow = function(event){
	if(typeof MiJSBridge=="undefined" && event.persisted && (location.href.indexOf('#withdraw') > 0 || location.href.indexOf('withdraw=1') > 0) ){
		history.back();
	}
};
$(function(){
	$('li').click(function(){
		var url = $(this).data('url');
		if(url){
			if(location.href.indexOf('#withdraw') > 0){
				url = url + '#withdraw';
			}else if(location.href.indexOf('withdraw=1') > 0){
				url = url + (url.indexOf('?') > 0 ? '&' : '?') + 'withdraw=1';
			}
			if(typeof MiJSBridge=="object") {
				url = url.replace('/weixin/user', '/web/my');
			}
			window.location.href= url;
		}
	});
});
</script>
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">
