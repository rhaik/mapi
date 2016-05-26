<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
	<ul class="ui-list ui-list-link ui-border-tb">
	
    <li class="ui-border-t" data-url="${util.ctx}/web/discovery/tasks.html">
        <div class="ui-list-thumb">
            <span  style="background-image:url(${util.static}/images/found_01.png)"></span>
        </div>
        <div class="ui-list-info">
            <h4>应用试用</h4>
        </div>
    </li>
    <li class="ui-border-t" data-url="${util.ctx}/web/discovery/ranks.html">
        <div class="ui-list-thumb">
           <span style="background-image:url(${util.static}/images/found_02.png)"></span>
        </div>
        <div class="ui-list-info">
            <h4>收入排行榜</h4>
        </div>
    </li>
    <li class="ui-border-t" data-url="${util.ctx}/web/discovery/share.html">
        <div class="ui-list-thumb">
            <span  style="background-image:url(${util.static}/images/found_03.png)"></span>
        </div>
        <div class="ui-list-info">
            <h4>邀请好友</h4>
        </div>
    </li>
   
</ul>
<script>
$(function(){
	$('.ui-border-t').click(function(){
		window.location.href= $(this).attr('data-url');
	});
});
</script>
<#include "/common/footer.ftl">