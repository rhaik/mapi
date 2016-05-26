<#import "/lib/util.ftl" as util>
<#include "/common/headerNews.ftl">
	<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<#if datas?? && (datas?size>0)>
<#list datas?keys as key>
<div class="tudi_date">
	<span>${key}</span>
	<#assign userInviteList=datas[key]>
	<span class="fr">有效徒弟数<i class="orange">${(userInviteList?size)!0}人</i></span>
</div>
<div class="bg_f clearfix">
<#list userInviteList as userInviteVo>
	<div class="tudi_list"><p><img src="${(userInviteVo.user.headImg)!''}" />${(userInviteVo.user.uniName)!''}</p></div>
</#list>
</div>
</#list>
<#else>
	亲您还没有有效收徒数据 &nbsp;&nbsp;<a href='#' onclick='window.history.go(-1);'>去收徒</a>
</#if>
<#include "/common/footer.ftl">