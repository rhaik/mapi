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
					"task_id":"${idEncoder.encode(m.task_id!0)}",
					"task_description" : "${util.jsonQuote(m.task_description!'')}",
					"create_time" : "<#if m.create_time??>${m.create_time?string('yyyy-MM-dd HH:mm')}</#if>",
					"view_num":${m.view_num},
					"task_name":"${(m.task_name)!''}",
					"expired_time": "<#if m.expired_time??> ${m.expired_time?string('yyyy-MM-dd HH:mm')}</#if>",
					"amount" : ${util.fen2yuan(m.amount)},
					"extra_info" : "${util.jsonQuote((m.extra_info)!'')}",
					"type":${m.type},
					"timestamp":"${m.timestamp}"
				}<#if m_index != messages?size -1>,</#if>
				</#list>
			</#if>
	]
}