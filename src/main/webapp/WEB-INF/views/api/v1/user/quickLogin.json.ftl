<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"state":${state},
	"mobile":"${mobile!''}",
	"ticket":"${ticket!''}",
	"user" : {
    	<#if user??>
    	"id" : ${user.user_identity!0},
    	"openid" : "${user.openid!0}",
        "name":"${util.jsonQuote(user.rawName!'')}",
        "avatar":"${util.jsonQuote(user.headImg!'')}",
        "mobile":"${user.mobile!''}",
        "is_new": false
        </#if>
    }
}