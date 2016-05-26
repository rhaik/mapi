<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
    "exchange": 
	    	{
		    	"user_id": ${(log.user_id)!0},
		        "exchange_num" : "${(log.integral)!0}",
		        "rmb":"${util.fen2yuan((log.rmb)!0)}",
		        "remark":"${(log.remark)!""}"
	    	}
	
}