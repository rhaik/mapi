<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}/css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/style.css">
<div class="center_box">
    <div class="apprentice_box">
        <div class="apprentice_tip">
        	<#if isIOS><h1>限时活动：新徒弟完成前<i>5个</i>限时任务，每个任务为师傅分成<i>1元</i></h1></#if>
            <p><i class="deep_red">•</i> 徒弟完成<#if isIOS>限时、转发、金币任务<#else/>转发任务、快速任务，</#if>甚至签到， 即可获得<i class="deep_red">10%</i>的分成收益</p>
            <h2>我的邀请码：<b>${user.user_identity}</b></h2>
        </div>
    </div>
    <div class="bg_f clearfix" id='apprentice_nav_list_id'>
	        <div class="apprentice_nav_list">
    			<a href="javascript:;" data-href='${util.ctx}/ios/user/invites.html'>
	            	<span>${todayInviteCount!0}</span>
	            	<p>今日收徒</p>
	      		</a>
	        </div>
        <div class="apprentice_nav_list">
          	<a href="javascript:;" data-href='${util.ctx}/ios/user/invites.html'>
            	<span>${userFriendCount!0}</span>
            	<p>徒弟人数</p>
            </a>
        </div>
        <div class="apprentice_nav_list">
            <a href="javascript:;" data-href='${util.ctx}/ios/my/inviteincome.html'>
            	<span>${util.fen2yuanS(userIncome.share_total)}</span>
            	<p>累计收徒奖励</p>
           	</a>
        </div>
    </div>
    <div class="center_list">
        <div class="apprentice_top_way"><b></b>收徒方式<i>收徒方式多样化，快去收徒赚钱吧↓</i></div>
    </div>
    <div class="apprentice_entry_box">
        <div class="apprentice_entry">
            <a href="javascript:;" data-href="${util.ctx}/static/html/shoutu_saoyisao.html?u=${user.user_identity}">
                <em><img src="${util.static}images/img/shoutu01.png" /></em>
                <span>扫一扫收徒</span>
            </a>
        </div>
        <div class="apprentice_entry">
            <a href="javascript:;" data-href="${util.ctx}/static/html/shoutu_lianjie.html?u=${user.user_identity}">
                <em><img src="${util.static}images/img/shoutu02.png" /></em>
                <span>收徒链接</span>
            </a>
        </div>
        <div class="apprentice_entry">
            <a href="javascript:;" data-href="${util.ctx}/ios/user/show_order_image.html">
                <em><img src="${util.static}images/img/shoutu03.png" /></em>
                <span>晒单收徒</span>
            </a>
        </div>
    </div>
    <div class="center_list apprentice_list apprentice_way_list">
        <div class="apprentice_top_way"><b></b>收徒攻略</div>
        <a href="javascript:;" data-href="${util.ctx}/static/html/ApprenticeReason.html">为什么要收徒？<em><img src="${util.static}images/img/right_ico.png" /></em></a>
        <a href="javascript:;" data-href="${util.ctx}/static/html/ApprenticeWay.html">如何收取更多徒弟？<em><img src="${util.static}images/img/right_ico.png" /></em></a>
    </div>
</div>
<script src="${util.zepto}"></script>
<script src="${util.basejs}"></script>
<#include "/common/footer.ftl" />