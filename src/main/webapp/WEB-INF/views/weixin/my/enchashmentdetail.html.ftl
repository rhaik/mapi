<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	.ui-form label{font-size:14px}
	.content{margin-left:105px;font-size:14px} 
</style>
<div class="ui-form ui-border-b">
	<div style="height: 30px; margin: 0 auto; padding:15px 0px;font-size:16px;text-align: center;">
		提现详情
	</div>
	<div class="ui-form-item ui-border-t">
		<label for="#">金额：</label> 
		<span class="content">${userEnchashment.amount/100}元</span>
	</div>
	<div class="ui-form-item ui-border-t">
		<label for="#">时间：</label> 
		<span class="content">${userEnchashment.mention_time?string("yyyy-MM-dd HH:mm:ss")}</span>
	</div>
	<div class="ui-form-item ui-border-t">
		<label for="#">提现信息：</label> 
		<span class="content">${userEnchashment.typeText}</span>
	</div>
	<div class="ui-form-item ui-border-t">
		<label for="#">提现状态：</label> 
		<span class="content">${userEnchashment.statusText}</span>
	</div>
	<#if userEnchashment.status==4 || userEnchashment.status==6> 
	<div class="ui-form-item ui-border-t">
		<label for="#">原因：</label> 
		<span class="content">${userEnchashment.reason}</span>
	</div>
	</#if>
</div>
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">