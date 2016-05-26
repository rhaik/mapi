<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if lotteryList?? && (lotteryList?size > 0)>
	  <#list lotteryList as lottery>
	  {
	    "number" : "${lottery.number}",
		"createtime" : "${lottery.create_time?string("MM-dd HH:mm")}"
	  }<#if lottery_index != lotteryList?size-1>,</#if>
	  </#list>
	</#if>
    ]
}