<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if productShare?? && (productShare?size > 0)>
	  <#list productShare as share>
	  {
	  	"id" : "${share.productShare.id}",
		"headImg" : "${share.user.headImg}",
		"uniName" : "${util.jsonQuote(share.user.uniName)}",
		"title" : "${share.productShare.title!""}",
		"createtime" : "${share.productShare.createtime?string("yyyy-MM-dd HH:mm:ss")}",
		"shareImages" : ${share.productShare.images}
	  }<#if share_index != productShare?size-1>,</#if>
	  </#list>
	</#if>
    ]
}