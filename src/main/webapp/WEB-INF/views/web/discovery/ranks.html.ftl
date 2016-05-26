<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5}
	.ui-tab{background-color:#f0eff5;}
	.ui-list{margin-bottom:0px;}
	<#if fromSafari?? && !fromWeixin>.ui-tab-nav {top:44px;}</#if>
	.ui-tab-nav li{font-size:16px;}
	.ui-tab-nav li.current {color: #fff;border:1px #0080fd solid;background-color:#0080fd}
	.ui-tab-content{padding:8px 0px;overflow:auto;margin-bottom:129px;}
	.ui-avatar-thumb{width:43px;height:40px;padding:10px 10px 0px 0px;margin-left:-10px;}
	.ui-avatar-thumb img{width:43px;height:40px;}
	.ui-thumb-text {width:40px;font-size:18px;padding:16px 0px 0px 14px;}
	.ui-list-thumb{margin:6px 10 6px 0px;width:40px;height:40px;}
	.ui-list-thumb img{border-radius:8px;}
	.ui-list-info>p {width: 120px;overflow: hidden;white-space: nowrap;font-size:15px;line-height: 24px;}
	.ui-list-info span{width:20px;height:20px;background-size:100%;background-repeat:no-repeat;display:inline-block;}
	.li-amount{padding-top:4px;font-size:14px;}
	.li-amount span{color:#e40112;}
	.ui-list-action{margin-top:-10px;font-size: 15px;color:#e40112;}
	.ui-badge-wrap{margin:0 auto;text-align:center;}
	
	.ui-btn-group, .ui-btn-group-tiled{display:inline;}
	.ui-list .ui-li-myrank{margin:0px 6px;}
	.ui-li-rank{width:50%;padding:6px 0px;}
	.ui-li-rank:first-child{border-right:1px solid #e0e0e0;}
	.ui-li-rank p{font-size:16px;line-height:26px;}
	
</style>
<div class="ui-tab">
	<ul class="ui-tab-nav ui-border-b">
        <li  class="current">本月排行</li>
        <li>总排行</li>
    </ul>
    <ul class="ui-tab-content">
        <li class="current">
        	<ul class="ui-list ui-border-tb">
        	<#list monthTopUsers as user>
			    <li class="ui-border-t">
			    	<#if user_index == 0>
			           <div class="ui-avatar-thumb"><img src="${util.static}/images/rank_01.png" /></div>
			        <#elseif user_index == 1>
			        	<div class="ui-avatar-thumb"><img src="${util.static}/images/rank_02.png" /></div>
			        <#elseif user_index == 2>
			        	<div class="ui-avatar-thumb"><img src="${util.static}/images/rank_03.png" /></div>
			        <#else>
			        	<div class="ui-avatar-thumb ui-thumb-text">${user_index+1}</div>
			        </#if>
			        <div class="ui-list-thumb">
			            <span style="background-image:url(${user.u.headImg});width:40px;height:40px;border-radius:10px;"></span>
			        </div>
			        <div class="ui-list-info">
			            <p>${user.u.uniName!'用户'}</p>
			        </div>
			        <div class="ui-list-action ui-txt-warning">￥${user.income}</div>
			    </li>
			</#list>
			</ul>
        </li>
        <li>
        	<ul class="ui-list ui-border-tb">
			   <#list topUsers as user>
			    <li class="ui-border-t">
			    	<#if user_index == 0>
			           <div class="ui-avatar-thumb"><img src="${util.static}/images/rank_01.png" /></div>
			        <#elseif user_index == 1>
			        	<div class="ui-avatar-thumb"><img src="${util.static}/images/rank_02.png" /></div>
			        <#elseif user_index == 2>
			        	<div class="ui-avatar-thumb"><img src="${util.static}/images/rank_03.png" /></div>
			        <#else>
			        	<div class="ui-avatar-thumb ui-thumb-text">${user_index+1}</div>
			        </#if>
			        <div class="ui-list-thumb">
			            <span style="background-image:url(${user.u.headImg!'${util.static}/images/rank_avatar.png'});width:44px;height:44px;border-radius:10px;"></span>
			        </div>
			        <div class="ui-list-info">
			            <p>${user.u.uniName!"用户"}</p>
			        </div>
			        <div class="ui-list-action ui-txt-warning">￥${user.income}</div>
			    </li>
			</#list>
			</ul>
        </li>
    </ul>
</div>
<div class="ui-btn-group ui-btn-group-bottom">
	<ul id="sharePage" class="ui-list ui-border-t" data-url="">
	    <li class="ui-border-t ui-list-item-link">
	        <div class="ui-list-thumb" style="width:50px;height:50px;padding-right:14px;">
	            <img src="${user.headImg!'${util.static}/images/rank_avatar.png'}" />
	        </div>
	        <div class="ui-list-info">
	            <h4>${user.uniName!'我'}</h4>
					<div class="li-amount">
						总收入：<span>￥<#if userIncome??>${util.fen2yuan(userIncome.income)}<#else>0</#if></span>
					</div>
			</div>
	    </li>
	    <li class="ui-border-t ui-li-myrank">
	    	<div class="ui-li-rank">
		        <div class="ui-badge-wrap">我的本月排名</div>
		        <p class="ui-badge-wrap">
		        	<#if (monthRank >=0)>${monthRank+1}<#else>暂未上榜</#if>
		        </p>
	        </div>
	        <div class="ui-li-rank">
	        	<div class="ui-badge-wrap">我的总排名</div>
		        <p class="ui-badge-wrap">
		        	<#if (allRank >=0)>${allRank+1}<#else>暂未上榜</#if>
		        </p>
	        </div>
	    </li>
	</ul>
</div>
<script src="${util.static}/frozenjs/1.0.1/frozen.js"></script> 
<script>
$(function(){
    var tab = new fz.Scroll('.ui-tab', {
        role: 'tab',
        autoplay: false,
    });

    tab.on('beforeScrollStart', function(fromIndex, toIndex) {
        console.log(fromIndex, toIndex)
    });

    tab.on('scrollEnd', function() {
        console.log('end')
    });
    $('.ui-list-item-link').click(function(){
    	var url="<#if fromSafari??>/ios/user/income.html<#else/>${util.ctx}/web/my/income.html</#if>";
		if(typeof MiJSBridge=="object") {
			MiJSBridge.call("open", {url: url});
		} else {
			window.location.href= url;
		}
	});
});
</script>
<#include "/common/footer.ftl">