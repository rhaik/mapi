<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<#if !userAgent.inAppView>
    <div class="tishi_box">
        <a class="prompt" href="javascript:;" data-href="/ios/clip">安装“秒赚入口”，一键进入秒赚大钱<span><img src="${util.static}images/duobao/right_black.png" /></span></a>
    </div>
</#if>
<div class="income income_box">
    <p>余额（元）</p>
    <h1 class="fl" id="user-balance"><#if showNubie?? && showNubie>0<#else />${util.fen2yuanS(balance)!0}</#if></h1>
    <a class="tixian" href="javascript:;" data-href="/ios/user/income.html"><em>立即提现</em></a>
    <div class="income_bot">
        <p id="today-income">今日收入： <#if showNubie?? && showNubie>0<#else />${util.fen2yuanS(todayIncome)!0}</#if> </p>
        <p id="total-income">累计收入：<#if showNubie?? && showNubie>0<#else />${util.fen2yuanS(income)!0}</#if> </p>
    </div>
</div>
<div class="my_content">
    <p class="marquee_text fl" id="tishi-area">
        <i class="marquee_text_ico"><img src="${util.static}images/duobao/tongzhi.png" /></i>
        <span class="marquee" id="msg-panel">恭喜您成为我们的新用户，系统奖</span>
    </p>
    <a class="my_content_box" href="javascript:;" data-href="/ios/user/home.html">
        <em><img src="${util.static}images/img/my_content.png" />我</em>
    </a>
</div>
<div class="my_task">
    <a class="db" href="javascript:;" data-href="/ios/tasks.html" data-show-refresh="1">
        <em><img src="${util.static}images/img/xianshi_icon.png" /></em>
        <div class="my_task_txt">
            <p class="task_title menu_task">限时任务</p>
            <p class="task_tishi">下载体验APP，即可获得任务奖金！</p>
        </div>
        <i><img width="100%" src="${util.static}images/img/right_orange.png" /></i>
    </a>
</div>
<div class="my_task">
    <a class="db" href="javascript:;" data-href="/ios/share.html">
        <em><img src="${util.static}images/img/shoutu_icon.png" /></em>
        <div class="my_task_txt">
            <p class="task_title">收徒</p>
            <p class="task_tishi">徒弟做任务师父可获丰厚分成！</p>
        </div>
        <i><img width="100%" src="${util.static}images/img/right_orange.png" /></i>
    </a>
</div>
<div class="main clearfix">
    <#list homeMenuList as menu>
    <div class="main_list">
        <a href="javascript:;" data-href="<#if menu.link?length = 0><#elseif menu.link?index_of('/web') = 0>/ios${menu.link}<#else>${menu.link}</#if>" class="menu${menu.id}">
            <img src="<#if menu.logo?contains('://')>${menu.logo}<#else>${util.static}images/home/${menu.logo}.png</#if>" />
        </a>
        <span>${menu.title}</span>
    </div>
    </#list>
</div>
<div class="site-info">
    <p>秒赚大钱</p>
    <p>miaozhuandaqian.com</p>
</div>
<div class="ui-dialog" id="ui-dialog">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1 id="ui-dialog-title"></h1>
            <div class="ui-btn-wrap">
                <button id="ui-dialog-btn" class="ui-btn ui-btn-danger" style="color:#fff;;width:100%;font-size:18px;margin:20px 0px 0px 0px;;height:40px;background-color:#ff8003;background-image:none;"></button>
            </div>
        </div>
    </div>
</div>
<#if showNubie?? && showNubie>
    <!-- 新人红包弹框 -->
    <div class="bg dn" id="nubie-dialog">
        <!-- 未拆开红包 -->
        <div class="first_box <#if hasInvite><#else>dn</#if>" id='first_reward_top_id'>
            <div class="first_top posi_r">
                <em class="db"><img src="${util.static}images/img/first_top.png" /></em>
                <em class="buckle"><img src="${util.static}images/img/buckle.png" /></em>
            </div>
            <h1 class="first_title">恭喜您，获得新人红包！</h1>
            <div class="first_btn" id="first-btn"><a>马上领取</a></div>
        </div>
        
        <!-- 拆开红包 -->
        <div class="first_reward dn" id='first_reward_id'>
            <div class="hongbao_opne"><img width="100%" src="${util.static}images/img/hongbao_opne.png" /></div>
            <div class="first_reward_bg">
                <p id='reward_message'>${util.fen2yuanS(nubieAmount)}<i>元</i></p>
                <h1 class="first_title">别捉急，这仅仅是开始！满10元可提现。更多现金任务等你来领！快去看看吧！ </h1>
                <div id="begin" class="first_btn"><a>马上赚钱</a></div>
            </div>
        </div>
    </div>
   <!-- 师徒奖励 -->
	<div class="bg <#if hasInvite>dn<#else></#if>">
		<div class="mentor_id" id='invite_mentor_id'>
			<h1>请填写邀请码</h1>
			<b class="dn">×</b>
			<p><input type="text" id="invite_code_inp"/></p>
			<div class="mentor_text" id='invite_mentor_text'>新用户有邀请码可获得<i class="red">2</i>元师徒奖励；没有邀请码获得<i class="red">1.5</i>元奖励 </div>
			<div class="mentor_btn">
				<a class="fl" id='no_invite_code'>没有邀请码</a>
				<a class="on fr" id='has_invite_code'>已填写</a>
			</div>
		</div>
	</div>
<#else/>
    <div class="bg" style="display:none;" id="shoutu-dialog">
        <div class="shoutu_box">
            <div class="shoutu_box_img posi_r">
                <img src="${util.static}images/img/shoutu_popup_bg.png" />
                <div class="colse_box">
                    <span>×</span>
                    <em></em>
                </div>
            </div>
            <a href="javascript:;" class="goto-share">马上去收徒</a>
        </div>
    </div>
</#if>
<style id="keyFrames"></style>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<script type="text/javascript">
var today_income='${util.fen2yuanS(todayIncome)!0}';
var total_income = '${util.fen2yuanS(income)!0}';
var user_balance='${util.fen2yuanS(balance)!0}';
</script>
<#if !userAgent.inAppView>
<script type="text/javascript">
var bm_config = {
    ws: '${websocketAddress}',
    tk: '${user.ticket!""}',
    sm : '${yaoshiScheme!"#"}'
};
</script>
<script type="text/javascript" src="${util.static}js/bridge.js"></script>
</#if>
<script src="${util.basejs}"></script>
<script type="text/javascript" src="${util.static}js/index.js"></script>
<#include "/common/footer.ftl"/>