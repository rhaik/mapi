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
					"app_icon" : "${m.app_icon}",
					"app_name" : "${m.app_name}",
					"agreement" : "${m.agreement}",
					"bundle" : "${m.bundle_id!''}",
					"task_description" : "${util.jsonQuote(m.task_description!'')}",
					"trial_time" : "${m.trial_time}",
					"keyword" : "${m.keyword}",
					"type" : ${m.status},
					"task" : "${m.encodedTaskId}",
					"create_time" : "${m.create_time?string('yyyy-MM-dd HH:mm')}",
					"timestamp" : "${m.timestamp}",
					"expired_time": "${m.expiredTimestamp}",
					"amount" : ${util.fen2yuan(m.amount)},
					"extra_info" : "${util.jsonQuote(m.extra_info!'')}"
				}<#if m_index != messages?size -1>,</#if>
				</#list>
			</#if>
	]
}