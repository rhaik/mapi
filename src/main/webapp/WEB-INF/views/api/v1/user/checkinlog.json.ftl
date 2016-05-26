<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if checkinLog?? && (checkinLog?size > 0)>
				<#list checkinLog as t>
				{
					"checkin_time" : "${t.checkin_time?string("yyyy年MM月dd日")}",
					"income" : "${t.income!0}"
				}<#if t_index != checkinLog?size -1>,</#if>
				</#list>
			</#if>
	]
}