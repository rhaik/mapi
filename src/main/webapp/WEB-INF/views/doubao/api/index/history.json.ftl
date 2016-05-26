<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if productActivity?? && (productActivity?size > 0)>
	  <#list productActivity as p>
	  {
	  	"id" : "${p.productActivity.id}",
		"product_number" : "${p.productActivity.product_number}",
		"uniName" : "${util.jsonQuote(p.user.uniName)}",
		"headImg" : "${util.jsonQuote(p.user.headImg)}",
		"number" : "${p.productActivity.lottery_buy_number!1}",
		"lottery_number" : "${p.productActivity.lottery_number}",
		"lottery_time" : "${p.productActivity.lottery_time?string("yyyy-MM-dd HH:mm:ss")}"
	  }<#if p_index != productActivity?size-1>,</#if>
	  </#list>
	</#if>
    ]
}