<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if orderList?? && (orderList?size > 0)>
	  <#list orderList as p>
	  {
	  	"id" : "${p.orderProduct.id}",
	  	"productActivityId" : "${p.productActivity.id}",
		"opnumber" : "${p.orderProduct.number}",
		"number" : "${p.productActivity.number}",
		"product_name" : "${util.jsonQuote(p.productActivity.product_name)}",
		"product_number" : "${p.productActivity.product_number}",
		"thumb" : "${p.product.thumb}",
	    <#if p.orderProduct.lottery>
		"shipping_status" : "${p.orderProduct.shipping_status}",
		"status" : "${p.orderProduct.status}",
		<#if p.orderProduct.shipping_status gt 1>
			"shipping_desc": "${p.orderProduct.shippingDesc}",
			"finished" : <#if p.orderProduct.shipping_status == 4 && p.orderProduct.share == 1>true<#else>false</#if>,
		</#if>
		</#if>
		<#if p.productActivity.status==3>
		"headImg" : "${util.jsonQuote(p.user.headImg)}",
		"uniName" : "${util.jsonQuote(p.user.uniName)}",
		"hideMobile" : "${p.user.hideMobile}",
		"lottery_number" : "${p.productActivity.lottery_number}",
		"lottery_time" : "${p.productActivity.lottery_time?string("MM-dd HH:mm")}",
		</#if>
		<#if p.productActivity.status==5>
			"expire_time" : "${p.productActivity.expire_time?string("MM-dd HH:mm")}",
		</#if>
		"createtime" : "${p.orderProduct.createtime?string("MM-dd HH:mm")}"
	  }<#if p_index != orderList?size-1>,</#if>
	  </#list>
	</#if>
    ]
}