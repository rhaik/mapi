<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"success":true,
	"data" : [
		<#if tasklog?? && (tasklog?size > 0)>
				<#list tasklog as t>
				{
					"name" : "${t.ad_name!''}",
					"currentDate" : "${t.createtime?string("yyyy年MM月dd日")}",
					"displayDate" : "${t.displayDate}",
					"points" : "${t.points!0}"
				}<#if t_index != tasklog?size -1>,</#if>
				</#list>
			</#if>
	]
}