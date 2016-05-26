<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"id" : ${user.user_identity!0},
   	"openid" : "${user.openid!0}",
   	"area" : "${user.area!''}",
    "name":"${util.jsonQuote(user.name!'')}",
    "avatar":"${util.jsonQuote(user.avatar!'')}",
    "mobile":"${user.mobile!''}"
}