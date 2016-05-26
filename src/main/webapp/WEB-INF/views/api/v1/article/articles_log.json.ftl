<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
		<#if userArticlelist?? && (userArticlelist?size >0)>
			<#list userArticlelist as task>
			{
				"name" : "${(task.transArticleTask.name)!''}",
				"displayDate" : "${task.displayDate}",
				"currentDate" : "${task.currentDate}",
				"date" : "${task.userArticleTask.starttime?string("yyyy年MM月dd日")}",
				"amount" : "${util.fen2yuan(task.rewardAmount!0)}",
				"icon" : "${util.jsonQuote(task.transArticle.defaultImg!"")}"
			}<#if task_index != userArticlelist?size-1>,</#if>
			</#list>
		</#if>
		]
}