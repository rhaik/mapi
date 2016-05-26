<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if productShare?? && (productShare?size > 0)>
	  <#list productShare as share>
	  {
	  	"id" : "${share.productShare.id}",
	    "product_number" : ${share.productActivity.product_number},
	    "product_name" : "${util.jsonQuote(share.productActivity.product_name!'')}",
		"headImg" : "${share.user.headImg}",
		"uniName" : "${util.jsonQuote(share.user.uniName)}",
		"title" : "${util.jsonQuote(share.productShare.title!"")}",
		"createtime" : "${share.productShare.createtime?string("MM-dd HH:mm:ss")}",
		"shareImages" : ${share.productShare.images}
	  }<#if share_index != productShare?size-1>,</#if>
	  </#list>
	</#if>
    ]
}