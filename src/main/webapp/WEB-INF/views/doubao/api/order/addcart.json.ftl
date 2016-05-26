<#import "/lib/util.ftl" as util>
{
"code":${ret_code!0},                   
"message":"${util.jsonQuote(ret_message)}"
<#if count?? && count==1>,"count":"${count}"</#if>
}