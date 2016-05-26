<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	<#if showInfo>
	"pid" : "${app.encodedAppId}",
	"tid" : "${appTask.encodedId}",
	"info" : "${appInfo}",
	"keyword": "${util.jsonQuote(appTask.keywords)}",
	"rank" : ${appTask.current_rank},
	"download_size" : ${app.download_size},
	"amount" : ${appTask.amount},
	"icon" : "${app.icon}",
	"description" : "${util.jsonQuote(appTask.description)}",
	"agreement" : "${app.agreement}",
	"remain_time" : ${remainTime},
	"paid" : ${app.payWay?string("1", "0")},
	"require_type" : ${appTask.require_type},
	<#if app.process_name?starts_with("NES_")>"need_open" : 1,</#if>
	<#if appTask.directDownload>"download_url" : "${util.jsonQuote(app.description)}",</#if>
	</#if>
	"status" : ${status!1},
	"desc" : "${message!''}"
}