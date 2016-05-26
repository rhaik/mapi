<#import "/lib/util.ftl" as util>
{
"success":"<#if ret_code==0>true<#else>false</#if>",                   
"message":"${util.jsonQuote(ret_message!"OK")}"
}