<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style> 
body{background-color:#f0eff5}
.ui-page{margin-top:14px;}
.ui-list-text>li{padding:6px 25px 6px 0;margin-left:0px;}
.ui-list-action{font-size:14px;right:0px;color: gray;}
.ui-list-info .ui-li-title{font-size: 15px;}
.ui-list-info .ui-li-date{font-size:12px;}

.ui-list>li{margin:0px 18px;}
</style>
<ul class="ui-list ui-list-text ui-border-tb" id="fpmxList" data-role="listview">
	<#list userIncomeDetail as u>
		<li class="ui-border-b">
			<div class="ui-list-info">
				<p class="ui-li-title">任务《${u.userIncomeLog.remarks!""}》</p>
				<p class="ui-li-date">${u.userIncomeLog.operator_time?string("yyyy-MM-dd HH:mm:ss")}</p>
			</div>
			<div class="ui-list-action">${u.userIncomeLog.amountYuan}元</div>
		</li>
	</#list>    
</ul>
<script>
$(function(){
 	//回调处理
	$(document).bind("pageinit", function() {
		isInit=0;
		serverURL = "${util.ctx}/api/v1/user/inviteincome/detail?from_user=${from_user!0}"; //服务器地址
		startNum = 0;//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 )	$('#pullUp').hide();
		callbackReviceData = function(datas){
			var result = '';
		    for (var i = 0; i < datas.data.length; i++) {
		        result += '<li class="ui-border-b">'
						+'<div class="ui-list-info">'
						+'	<p class="ui-li-title">任务《'+ datas.data[i].taskName + '》成功</p>'
						+'	<p class="ui-li-date">'+datas.data[i].operator_time+'</p>'
						+'</div>'
						+'<div class="ui-list-action">'+datas.data[i].amount+'元</div>'
					+'</li>';
		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">