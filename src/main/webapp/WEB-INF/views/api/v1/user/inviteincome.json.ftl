<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if userIncome?? && (userIncome?size > 0)>
				<#list userIncome as u>
				{
					"name" : "${util.jsonQuote(u.fromUser.uniName)}",
					"amount" : "${u.userIncomeLog.amountYuan}",
					"headImg" :  "${util.jsonQuote(u.fromUser.headImg)}",
					"displayDate" : "${u.displayDate}",
					"from_user" : "${u.userIncomeLog.from_user}",
					"currentDate" : "${u.userIncomeLog.operator_time?string("yyyy年MM月dd日")}"
				}<#if u_index != userIncome?size -1>,</#if>
				</#list>
			</#if>
	]
}