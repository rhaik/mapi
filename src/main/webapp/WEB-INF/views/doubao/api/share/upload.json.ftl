<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"uploads":[
	<#if urls?? && (urls?size > 0)>
	  <#list urls as u>
	  {
		"url" : "${u}"
	  }<#if u_index != urls?size -1>,</#if>
	  </#list>
	</#if>
    ]
}