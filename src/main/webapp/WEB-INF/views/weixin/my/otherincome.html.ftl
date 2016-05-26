<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5;}
	.ui-list{margin-top:16px;}
	.ui-list-info h4{font-size: 16px; margin:10px 0px;}
</style>
<ul class="ui-list ui-border-tb">
<#list otherIncome as u>
	<li class="ui-border-t">
        <div class="ui-list-info">
            <h4>${u.remarks}</h4>
        </div>
        <div class="ui-list-action">${util.fen2yuanS(u.amount!0)}元</div>
    </li>
</#list>   
</ul>
<#include "/weixin/wx_share.ftl">
<#include "/common/footer.ftl">