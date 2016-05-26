<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if userFriends?? && (userFriends?size > 0)>
				<#list userFriends as u>
				{
					"name" : "${util.jsonQuote(u.friend.uniName)}",
					"avatar" : "${u.friend.headImg}",
					"displayDate" : "${u.displayDate}",
					"currentDate" : "${u.invi_time?string("yyyy年MM月dd日")}",
					"invi_time" : "${u.invi_time?string("HH:mm")}"
				}<#if u_index != userFriends?size -1>,</#if>
				</#list>
			</#if>
	]
}