<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">
	<#if product??>,
	"product":
	  {
	  	"id" : "${product.productActivity.id}", 
	  	"headImg" : "${util.jsonQuote(product.user.headImg)}",
		"uniName" : "${util.jsonQuote(product.user.uniName)}",
		"mobile" : "${product.user.hideMobile}",
		"lottery_number" : "${product.productActivity.lottery_number}",
		"number" : ${product.orderProduct.number},
		"beforMinute" : "${product.productActivity.beforMinute}"
	  } 
	</#if>
}