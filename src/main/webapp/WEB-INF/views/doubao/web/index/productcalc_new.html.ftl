<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/duobao.css" />
<!-- 中间内容 -->
<div>
	<div class="formula"><img src="${util.static}images/duobao/formula.png" /></div>
	<div class="num">
		<p><em>数值A=</em>截止该奖品开奖前时间点前10条全站参与记录</p>
		<p><em>=</em><span style="padding-left:6px;">${total}</span><a class="history">查看</a></p>
	</div>
	<div class="num_txt">
		<table width="100%">
			<tr>
				<th width="33%">夺宝时间</th>
				<th width="32%">夺宝号</th>
				<th width="35%">用户账号</th>
			</tr>
			<#list orderHistory as oh>
			<tr>
				<td>${oh.order_time}</td>
				<td class="num_col">${oh.time_value}</td>
				<td><a>${oh.uniUserName}</a></td>
			</tr>
			</#list> 
		</table>
	</div>
	<div class="num">
		<p><em>数值B=</em>最近一期中国福利彩票“老时时彩”的开奖结果</p>
		<p><em>=</em><span><#if shishicai>${shishicai}<#else>正在等待开奖</#if>(第${productActivity.shishicai}期)</span><a class="find" href="javascript:;" data-href="http://data.shishicai.cn/cqssc/haoma/${productActivity.lotteryTimes?string('yyyy-MM-dd')}/">开奖查询</a></p>
	</div>
	<div class="results">计算结果：<#if productActivity.status==3>${productActivity.lottery_number}<#else>等待揭晓</#if></div>
	<div class="careful">注：最后一个夺宝号认购时间距离“老时时彩”最近下一期开奖大于24小时，默认“老时时彩”开奖结果为00000;如遇福彩中心通讯故障，无法获取“老时时彩”开奖结果，最后一个号码分配时间距离故障时间大于24小时，亦默认“老时时彩”开奖结果为00000.</div>
</div>
<script src="${util.zepto}"></script>
<script src="${util.static}js/duobao.js"></script>
<script type="text/javascript">
	$('.history').click(function(){
		$('.num_txt').toggle();
	})
</script>
</body> 
</html> 