<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"income" : "${income!0}",
	"balance":"${balance}",
	"todyIncome" : "${todyIncome!0}",
	"todyInvite" : "${todyInvite!0}",
	"hasCheckIn" : ${hasCheckIn!0},
	"lastAppTaskId":"${(appTask.id)!0}",
	"lastArticleTaskId":${(lastArticleTask.id)!0}
}