<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if productList?? && (productList?size > 0)>
	  <#list productList as p>
	  {
	  	"number" : "${p.orderProduct.number}",
		"ip_area" : "${p.orderProduct.ip_area}",
		"hideIp" : "${p.orderProduct.hideIp}",
		"uniName" : "${util.jsonQuote(p.user.uniName)}",
		"headImg" : "${util.jsonQuote(p.user.headImg)}",
		"createdate" : "${p.orderProduct.createtime?string("yyyy-MM-dd")}",
		"createtime" : "${p.orderProduct.createtime?string("yyyy-MM-dd HH:mm:ss")}"
	  }<#if p_index != productList?size-1>,</#if>
	  </#list>
	</#if>
    ]
}