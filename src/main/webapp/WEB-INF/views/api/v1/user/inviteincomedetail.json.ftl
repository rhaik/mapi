<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if userIncomeDetail?? && (userIncomeDetail?size > 0)>
				<#list userIncomeDetail as u>
				{
					"taskName" : "${u.userIncomeLog.remarks!''}",
					"amount" : "${u.userIncomeLog.amountYuan}",
					"operator_time" : "${u.userIncomeLog.operator_time?string("yyyy-MM-dd HH:mm:ss")}"
				}<#if u_index != userIncomeDetail?size -1>,</#if>
				</#list>
			</#if>
	]
}