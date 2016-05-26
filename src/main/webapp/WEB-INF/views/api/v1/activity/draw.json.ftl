<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":{
		"amount":${util.fen2yuan(amount!0)},
		<#if userDraw??>
			"total_times":${userDraw.total_times},
			"balance_times":${userDraw.balance_times},	
			"incre_times":${userDraw.incre_times}
		</#if>
	}
}