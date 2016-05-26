	<#import "/lib/util.ftl" as util>
<#include "/common/scroll_header.ftl">
<style>
	.ui-list-action{color:#666666;}
	.ui-list-action>span{color:#ff8003;}
	.ui-list-date{line-height:26px;background-color:#f0eff5;} 
	.ui-list-info>h4 {margin:12px 0px;}
	.ui-list-date span{font-size:14px;margin-left:14px;color:#777777}
</style>
	<ul class="ui-list ui-border-tb" id="fpmxList" data-role="listview">
	<#list checkInLog as log>
		<li class="ui-border-t" data-date="${log.checkin_time?string("yyyy年MM月dd日")}">
			<div class="ui-list-info">
				<h4>${log.checkin_time?string("yyyy年MM月dd日")}</h4>
			</div>
			<div class="ui-list-action"><span>${log.income!0}</span> 金币</div>
		</li>
	</#list>
	</ul>
<script>
$(function(){
	$(document).bind("pageinit", function() {
		isInit=0;
		serverURL = "${util.ctx}<#if fromSafari??>/ios/api<#else>/api/v1/user</#if>/checkinlog"; //服务器地址
		startNum = 0;			//当前页
		count = ${totalPage!0}; //总页数
		if(count <=1 ) $('#pullUp').hide();

		console.log("开始数：" + startNum + ',总页数:' + count);
		//回调处理
		callbackReviceData = function(datas){
			if(!datas|| datas.data.length == 0){
				console.info("not data");
				return ;
			}
			var result = '';
		    for (var i = 0 , len = datas.data.length; i < len; ++ i) {
				var log = datas.data[i];

				result += '<li class="ui-border-t" data-date="' + log.checkin_time +'">' +
						 '<div class="ui-list-info">' +
						 '<h4>' + log.checkin_time + '</h4>' +
						 '</div><div class="ui-list-action"><span>' + log.income + '</span> 金币</div>' +
						 '</li>';

		    }
		    return result;
		}
	});
 });
 </script>
<#include "/common/scroll_footer.ftl">