<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"update": ${update},
	"force_update" : ${forceUpdate}
    <#if entitled>,"entitled" : true</#if>
    <#if home_url>,"home_url" : "${home_url}"</#if>
	<#if version??>,
    "version": "${util.jsonQuote(version.version!"")}",
    "url": "${util.jsonQuote(version.url!"")}",
    "content": "${util.jsonQuote(version.content!"")}"
    </#if>
    <#if iosVersion??>,
    "version": "${util.jsonQuote(iosVersion.version!"")}",
    "url": "${util.jsonQuote(download_url!"")}"
    </#if>
}