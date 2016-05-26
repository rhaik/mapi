<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{ background:#f0eff5;}
	table{border-collapse:collapse;border-spacing:0;}
	.header{width:100%; height:3.3em; text-align:center; line-height:3.3em; background:#19191f; position:relative; z-index:2;}
	.header span{font-size:1.3em; color:#fff;}
	.header a{position:absolute; top:0; left:0.75em; height:3em; line-height:3em; font-size:1.1em; color:#fff; background:url(${util.static}images/img/left_icon.png) 0.2em 0.85em / 0.82em 1.4em no-repeat; padding-left:1.2em;}
	.tishi_btn{width:2.2em; height:2.2em; display:block; background:#ff8003; border-radius:2.2em; position:fixed; top:3.9em; right:0.9em;}
	.con_txt{background:#fff; padding:1.875em 0; text-align:center;-webkit-user-select: initial;}
	.con_txt h1,.con_txt p{font-weight:normal; font-size:0.875em; color:#444; line-height:1.5em;}
	.con_txt p a{font-size:1em; color:#444;}
	.success{margin:0.9em 0.9em 1.25em; overflow:hidden;}
	.success_main{margin:0 1.09em 0 0.9em;}
	.success_left{width:0.9em; height:3.75em; margin-left:-0.9em;}
	.success_con{width:100%; background:#2ca0fd; height:3.75em;}
	.success_con h1{font-size:1em; color:#fff; font-weight:normal; text-align:center; margin:0.5em 0 0.4em;}
	.success_con p{font-size:0.72em; color:#fff; text-align:center;}
	.success_right{width:1.09em; height:3.75em; margin-right:-1.09em;}
	.reward{height:7.2em;}
	.reward .success_left,.reward .success_con,.reward .success_right{height:7.2em;}
	.tab{margin:0 0.4em 0 0.9em; border:1px solid #fff;}
	.tab tr{border-bottom:1px solid #fff;}
	.tab tr:last-child{border:none;}
	.tab td{border-right:1px solid #ddd; padding:0.4em 0; font-size:0.875em; color:#fff; text-align:center;}
	.tab td:last-child{border:none;}
</style>
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<div class="con">
	<div class="search">
		<div class="success">
			<div class="success_main">
				<div class="success_left fl"><img height="100%" src="${util.static}images/left_sma.png" /></div>
				<div class="success_con fl">
					<h1>分享成功，等待不同小伙伴阅读</h1>
					<p>在${task.end_time?string('MM月dd日HH:mm')}前，按要求完成任务即可获得奖励</p>
				</div>
				<div class="success_right fl"><img height="100%" src="${util.static}images/right_sma.png" /></div>
			</div>
		</div>
		<div class="success reward">
			<div class="success_main">
				<div class="success_left fl"><img height="100%" src="${util.static}images/left_big.png" /></div>
				<div class="success_con fl">
					<h1>奖励机制</h1>
					<div class="tab">
						<table width="100%">
							<tr>
								<td width="40%">达到阅读人数</td>
								<#if task.raward_type_zero gt 0><td width="15%">1</td></#if>
								<td width="15%">5</td>
								<#if task.raward_type_two gt 0><td width="15%">20</td></#if>
								<#if task.raward_type_three gt 0><td width="15%">80</td></#if>
							</tr>
							<tr>
								<td width="40%">共获得奖励(元)</td>
								<#if task.raward_type_zero gt 0><td width="15%">0.1</td></#if>
								<td width="15%">${util.fen2yuan(task.amount)}</td>
								<#if task.raward_type_two gt 0><td width="15%">${util.fen2yuan(task.raward_type_two)}</td></#if>
								<#if task.raward_type_three gt 0><td width="15%">${util.fen2yuan(task.raward_type_three)}</td></#if>
							</tr>
						</table>
					</div>
				</div>
				<div class="success_right fl"><img height="100%" src="${util.static}images/right_big.png" /></div>
			</div>
		</div>
	</div>
	<div class="con_txt">
		<h1>更多任务请关注我们的公众号</h1>
		<p><span>【 la6680 】</span></p>
	</div>
	<!-- 
	<div class="con_pic"><img src="${util.static}images/pic2.jpg" /></div>
	<div class="con_pic"><img src="${util.static}images/pic3.jpg" /></div>
	<div class="con_pic"><img src="${util.static}images/pic4.jpg" /></div>
	<div class="con_pic"><img src="${util.static}images/pic5.jpg" /></div>
	 -->
</div>
<#include "/common/footer.ftl">