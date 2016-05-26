<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
    "menus": [
	  	<#if homePageMenus?? && (homePageMenus?size>0)>
	    	<#list homePageMenus as b>
	    	{
	    		"id" : ${b.id},
		        "logo" : "${util.jsonQuote((b.logo)!"")}",
		        "title" : "${util.jsonQuote((b.title)!"")}",
		        "link":"${util.jsonQuote((b.link)!"")}",
		        "index":"${b.cindex}"
	    	}
	    	<#if b_index != homePageMenus?size - 1>,</#if>
    		</#list>
    	</#if>
     ]
}