<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"total" : ${total},
	"has_more" : ${ismore},
	"data" : [
		<#if messages?? && (messages?size > 0)>
				<#list messages as m>
				{
					"id" : ${m.sort_time},
					"db_id" : ${m.id},
					"title" : "${util.jsonQuote(m.title)}",
					"type" : ${m.type},
					"create_time" : "${m.create_time?string('yyyy-MM-dd HH:mm')}",
					"timestamp" : "${m.timestamp}",
					"thumb" : "${m.thumb!''}",
					"description" : "${util.jsonQuote(m.description!'')}",
					"content" : "${util.jsonQuote(m.content!'')}",
					"url":"${util.jsonQuote(m.target_url!'')}"
				}<#if m_index != messages?size -1>,</#if>
				</#list>
			</#if>
	]
}