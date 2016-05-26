<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"areachannel" : "${areachannel}",
	"data" : [
		<#if recharge?? && (recharge?size > 0)>
				<#list recharge as u>
				{
					"id" : ${u.id},
					"value" : "${u.value}",
					"pay_amount" : "${u.pay_amount}",
					"payamount" : "${u.payAmountYuan}"
				}<#if u_index != recharge?size -1>,</#if>
				</#list>
			</#if>
	]
}