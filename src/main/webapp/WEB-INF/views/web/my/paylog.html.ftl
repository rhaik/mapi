<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5}
	.ui-txt-info{display: -webkit-box;-webkit-box-orient: vertical;-webkit-box-pack: center;margin-right:36px;font-size:14px;}
	.ui-btn:before{border:none;}
	.ui-border-t .ui-list-thumb{width:26px;height:26px;margin: 10px 20px 10px 10px;}
	.ul-icon-cash {background:url(${util.static}images/enchashment_cash.png) no-repeat;background-size:100%;}
	.ul-icon-recharge {background:url(${util.static}images/recharge.png) no-repeat;background-size:100%;}
	  
	a:link {color: #ff8003}		
	a:visited {color: #ff8003}	
	a:hover {color: #ff8003}	
	a:active {color: #ff8003}	
</style>
<ul class="ui-list ui-list-one ui-list-link ui-border-tb">
    <li class="ui-border-t" data-url="${util.ctx}/web/my/enchashmentlist.html">
        <div class="ui-list-thumb">
            <span class="ul-icon-cash"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">提现记录</h4>
        </div>
        <div class="ui-txt-info">${util.fen2yuanS(income.encash_total)}元</div>
    </li>
	<!--
    <li class="ui-border-t" data-url="${util.ctx}/web/recharge/list.html">
   		<div class="ui-list-thumb">
             <span class="ul-icon-recharge"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">手机充值记录</h4>
        </div>
        <div class="ui-txt-info">${util.fen2yuan(income.recharge_total)}元</div>
    </li>
    -->
</ul>
<script>
$(function(){
	$('.ui-list-link>li').click(function(){
		var url = $(this).attr('data-url') + "";
		if($(this).attr('data-url')==null) return ;
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else {
			window.location.href= url;
		}
	});
});
</script> 
<#include "/common/footer.ftl">