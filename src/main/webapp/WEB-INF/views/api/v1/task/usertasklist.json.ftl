<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if usertasks?? && (usertasks?size > 0)>
				<#list usertasks as u>
				{
					"id" : ${u.userTask.id},
					"name" : "${u.appTask.name}",
					"displayDate" : "${u.displayDate}",
					"currentDate" : "${u.currentDate}",
					"date" : "${u.userTask.starttime?string("yyyy年MM月dd日")}",
					"statusText" : "${util.jsonQuote(u.statusText)}",
					"amount" : "${util.fen2yuan(u.earned_amount!0)}",
					"icon" : "${util.jsonQuote(u.app.icon!'')}"
				}<#if u_index != usertasks?size -1>,</#if>
				</#list>
			</#if>
	]
}