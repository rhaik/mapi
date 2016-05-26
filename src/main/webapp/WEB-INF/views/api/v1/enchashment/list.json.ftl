<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data" : [
		<#if userEnchashments?? && (userEnchashments?size > 0)>
				<#list userEnchashments as u>
				{
					"id" : ${u.userEnchashment.id},
					"account" : "${u.userEnchashment.account}",
					"displayDate" : "${u.displayDate}",
					"mention_time": "${u.userEnchashment.mention_time?string('yyyy年MM月dd日')}",
					"typeText": "${u.userEnchashment.typeText}",
					"statusText" : "${util.jsonQuote(u.userEnchashment.statusText)}",
					"status" : "${u.userEnchashment.status}",
					"amount" : ${util.fen2yuan(u.userEnchashment.amount)}
				}<#if u_index != userEnchashments?size -1>,</#if>
				</#list>
			</#if>
	]
}