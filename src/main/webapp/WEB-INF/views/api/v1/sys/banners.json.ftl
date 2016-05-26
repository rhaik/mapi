<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
    "banners": [
	  	<#if banners?? && (banners?size>0)>
	    	<#list banners as b>
	    	{
		    	"id": ${b.id},
		        "image" : "${util.jsonQuote((b.image)!"")}",
		        "url" : "${util.jsonQuote((b.url)!"")}"
	    	}
	    	<#if b_index != banners?size - 1>,</#if>
    		</#list>
    	</#if>
     ]
}