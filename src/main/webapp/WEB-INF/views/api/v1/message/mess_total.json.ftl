<#import "/lib/util.ftl" as util>
{
	"sys_total":${sys_total!0},
	"sys_message":{
		<#if sysMessage??>
			"id" : ${sysMessage.id!0},
			"title" : "${util.jsonQuote(sysMessage.title!"")}",
			"type" : ${sysMessage.type},
			"create_time" : "${sysMessage.create_time?string('yyyy-MM-dd HH:mm')}",
			"timestamp" : "${sysMessage.timestamp}",
			"thumb" : "${sysMessage.thumb!''}",
			"description" : "${util.jsonQuote(sysMessage.description!'')}",
			"content" : "${util.jsonQuote(sysMessage.content!'')}",
			"url":"${util.jsonQuote(sysMessage.target_url!'')}"
		</#if>
	},
	"app_total":${app_total!0},
	"app_message":{
		<#if appMessage??>
			"id" : ${appMessage.id},
			"app_icon" : "${appMessage.app_icon}",
			"app_name" : "${appMessage.app_name}",
			"agreement" : "${appMessage.agreement!''}",
			"finish_time" : "${appMessage.finish_time?string('yyyy-MM-dd HH:mm:ss')}",
			"task_description" : "${util.jsonQuote(appMessage.task_description!'')}",
			"trial_time" : "${appMessage.trial_time}",
			"keyword" : "${appMessage.keyword}",
			"type" : ${appMessage.status},
			"create_time" : "${appMessage.create_time?string('yyyy-MM-dd HH:mm')}",
			"timestamp" : "${appMessage.timestamp}",
			"amount" : ${util.fen2yuan(appMessage.amount)}
		</#if>
	},
	"friend_total":${friend_total!0},
	"friend_message":{
		<#if friendMessage??>
			"id" : ${friendMessage.id},
			"app_name" : "${friendMessage.app_name}",
			"friend_avater" : "${friendMessage.friend_avater!''}",
			"friend_name" : "${util.jsonQuote(friendMessage.friend_name!'')}",
			"friend_amount" : "${util.fen2yuan(friendMessage.friend_amount)}",
			"create_time" : "${friendMessage.create_time?string('yyyy-MM-dd HH:mm')}",
			"timestamp" : "${friendMessage.timestamp}",
			"amount" : ${util.fen2yuan(friendMessage.amount)},
			"level" : ${friendMessage.friend_level}
		</#if>
	}

}