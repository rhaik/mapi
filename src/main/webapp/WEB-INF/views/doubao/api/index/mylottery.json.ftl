<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">
	<#if lottery??>, "lottery": {
		"id" : "${lottery.productActivity.id}",
		"product_name" : "${util.jsonQuote(lottery.productActivity.product_name)}",
		"headImg" : "${util.jsonQuote(lottery.user.headImg)}",
		"uniName" : "${util.jsonQuote(lottery.user.uniName)}",
		"hideMobile" : "${lottery.user.hideMobile}",
		"lottery_number" : "${lottery.productActivity.lottery_number}",
		"lottery_time" : "${lottery_time}"
	  }</#if>
}