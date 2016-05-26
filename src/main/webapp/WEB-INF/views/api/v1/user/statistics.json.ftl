<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
   	"currentMonth" : "${currentMonth!0}",
	"lastMonthAmount" : "${lastMonthAmount!0}",
	"balance" : "${balance!0}",
	"todayAppAmount" : "${todayAppAmount!0}",
	"todayFriendAmount" : "${todayFriendAmount!0}",
	"sevenDayAppAmount" : "${sevenDayAppAmount!0}",
	"sevenDayFriendAmount" : "${sevenDayFriendAmount!0}",
	"thirtyDayAppAmount" : "${thirtyDayAppAmount!0}",
	"thirtyDayFriendAmount" : "${thirtyDayFriendAmount!0}"
}