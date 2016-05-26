<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if exchangeLog?? && (exchangeLog?size > 0)>
				<#list exchangeLog as u>
				{
					"rmb" : "${util.fen2yuan(u.rmb!0)}",
					"createtime" : "${u.createtime?string("yyyy-MM-dd HH:mm:ss")}"
				}<#if u_index != exchangeLog?size -1>,</#if>
				</#list>
			</#if>
	]
}