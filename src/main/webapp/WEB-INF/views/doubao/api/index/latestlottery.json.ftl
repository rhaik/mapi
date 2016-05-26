<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">
	<#if lottery??>, "lottery": {
		"id" : "${lottery.productActivity.id}",
		"product_name" : "${util.jsonQuote(lottery.productActivity.product_name)}",
		"product_number" : "${lottery.productActivity.product_number}",
		"headImg" : "${util.jsonQuote(lottery.user.headImg)}",
		"uniName" : "${util.jsonQuote(lottery.user.uniName)}",
		"hideMobile" : "${lottery.user.hideMobile}",
		"lottery_number" : "${lottery.productActivity.lottery_number}",
		"lottery_time" : "${lottery_time}"<#if user.id == lottery.user.id>,
		"is_mine" : true,
		"lottery_timestamp" : ${lottery.productActivity.lottery_time?long}
		</#if>
	  }</#if>
}