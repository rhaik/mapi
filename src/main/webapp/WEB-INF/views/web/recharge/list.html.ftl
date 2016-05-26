<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	.ui-list-info>span{line-height:30px;font-size:14px;margin:20px 0px;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;}
	.ui-list-date span{font-size:14px;margin-left:14px;color:#777777;}
	.ui-list-info>span{font-size:15px;}
	.ui-list-right{position:relative;font-size:12px;margin:10px 0px;margin-right:16px;line-height: 16px;text-align: right;}
	.ui-list-right .amount{text-align:right;color:#666666}
	.status-2{color:green;}
	.status-3,.status-6{color:red;}
	.status-1{color:blue;}
</style>
<ul class="ui-list ui-border-tb">
	<#list recharge as u>
		<#if u.displayDate>
			<li class="ui-list-date" style="margin-left:0px;">
				<span>${u.recharge.createtime?string("yyyy年MM月dd日")}</span>
			</li>
		</#if>
		<li class="<#if !u.displayDate>ui-border-t</#if>">
			<div class="ui-list-info">
				<span>${u.recharge.mobilephone}</span>
			</div>
			<div class="ui-list-right">
				<p class="amount">${util.fen2yuan(u.recharge.pay_amount!0)}元</p>
				<span class="status-${u.recharge.status}">${u.statusText}</span>
			</div>
		</li>
	</#list>    
</ul>
<#include "/common/footer.ftl">