<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl">,
	"data":[
	<#if games??>
		<#list games as game>
			<#include "/api/v1/game/template-game.json.ftl" />
			<#if game_index != games?size -1>,</#if>
		</#list>
	</#if>
		]
}