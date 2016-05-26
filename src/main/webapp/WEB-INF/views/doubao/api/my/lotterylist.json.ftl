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
		"shipping_status" : "${p.orderProduct.shipping_status}",
		"lottery_number" : "${p.productActivity.lottery_number}",
	    "lottery_time" : "${p.productActivity.lottery_time?string("MM-dd HH:mm:ss")}",
		"share": "${p.orderProduct.share}",
		"orderProductLottery" : [
			<#if p.orderProductLottery?? && (p.orderProductLottery?size > 0)>
	 		 <#list p.orderProductLottery as opl>
	 		 	{
	 		 		"number" : "${opl.number}"
	 		 	}<#if opl_index != p.orderProductLottery?size-1>,</#if>
			 </#list>
			</#if>
		],
	    "createtime" : "${p.orderProduct.createtime?string("MM-dd HH:mm")}"
	  }<#if p_index != orderList?size-1>,</#if>
	  </#list>
	</#if>
    ]
}