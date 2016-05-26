<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"total" : ${total},
	"has_more" : ${ismore},
	"data" : [
		<#if messages?? && (messages?size > 0)>
				<#list messages as m>
				{
					"id" : ${m.id},
					"title" : "${util.jsonQuote(m.title)}",
					"time" : ${m.time},
					"content" : "${util.jsonQuote(m.content!'')}"
				}<#if m_index != messages?size -1>,</#if>
				</#list>
			</#if>
	]
}