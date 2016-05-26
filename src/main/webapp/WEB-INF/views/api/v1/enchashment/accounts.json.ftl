<#import "/lib/util.ftl" as util>
{
	<#include "/common/status.json.ftl"><#if ue??>,
	"wx_name":"${ue.wx_bank_name!''}",
	"alipay_name":"${ue.alipay_name!''}",
	"alipay_account":"${ue.alipay_account!''}"
	</#if>
}