<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if productList?? && (productList?size > 0)>
	  <#list productList as p>
	  {
	  	"id" : "${p.productActivity.id}",
		"thumb" : "${util.jsonQuote(p.product.thumb)}",
		"product_name" : "${util.jsonQuote(p.productActivity.product_name)}",
		"product_number":"${p.productActivity.product_number}",
		"price" : "${util.fen2yuan(p.productActivity.price)}",
		"status" : "${p.productActivity.status}",
		<#if p.productActivity.status == 3>
		"uniName" : "${util.jsonQuote(p.user.uniName)}",
		"opnumber" : "${p.orderProduct.number}",
		"beforMinute" : "${p.productActivity.beforMinute}",
		"lottery_time" : "${p.productActivity.lottery_time?string("yyyy-MM-dd HH:mm:ss")}",
		</#if>
		<#if p.productActivity.status == 2>
		"lotteryTimes" : "${p.productActivity.lotteryTimes?string("yyyy/MM/dd HH:mm:ss")}",
		</#if>
		"number" : "${p.productActivity.number}",
		"buy_number" : "${p.productActivity.buy_number}",
	    "progress" : ${(p.productActivity.buy_number/p.productActivity.number*100)?string('#.#')}
	  }<#if p_index != productList?size-1>,</#if>
	  </#list>
	</#if>
    ]
}