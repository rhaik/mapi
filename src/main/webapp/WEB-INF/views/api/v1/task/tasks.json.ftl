<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"tasks":[
	<#if tasks?? && (tasks?size > 0)>
	  <#list tasks as task>
	  {
		"id" : ${task.userTask.id!0},
	   	"app_name" : "${task.app.name!''}",
	   	"process_name" : "${task.app.process_name!''}",
	   	"app_protocol" : "${task.app.agreement!''}",
	   	"app_bundle" : "${task.app.bundle_id!''}"
	  }<#if task_index != tasks?size -1>,</#if>
	  </#list>
	</#if>
    ]
}