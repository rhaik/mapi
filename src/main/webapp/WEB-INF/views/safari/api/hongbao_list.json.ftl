<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if hongbaoList?? && (hongbaoList?size gt 0)>
	  <#list hongbaoList as p>
	  {
		  "uniName" : "${util.jsonQuote(p.user.uniName)}",
	      "time" : "${p.userDrawLog.timeText}",
		  "timestamp" : ${p.userDrawLog.createtime?long},
		  "hongbao":  "${util.jsonQuote(p.userDrawLog.reason)}"
	  }<#if p_index != hongbaoList?size-1>,</#if>
	  </#list>
	</#if>
    ]
}