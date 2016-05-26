<#import "/lib/util.ftl" as util>
<#include "/common/header.ftl">
<style>
	body{background-color:#f0eff5}
	.ui-txt-info{display: -webkit-box;-webkit-box-orient: vertical;-webkit-box-pack: center;margin-right:36px;font-size:14px;}
	.block{background-color:#fff;position: relative;padding-top:16px;margin-bottom:10px;}
	.ui-tiled {display: -webkit-box;width: 100%;-webkit-box-sizing: border-box;padding-bottom:20px;}
	.ui-tiled .ui-list-thumb{width:40px;height:40px;}
	.ui-btn:before{border:none;}
	.ui-list-thumb .li-kt{background:url(${util.static}images/myincome_01.png) no-repeat;background-size:100%;}
	.ui-list-thumb .li-yt{background:url(${util.static}images/myincome_03.png) no-repeat;background-size:100%;}
	.ui-list-thumb .li-sy{background:url(${util.static}images/myincome_02.png) no-repeat;background-size:100%;}
	.ui-border-t .ui-list-thumb{width:26px;height:26px;margin: 10px 20px 10px 10px;}
	.ui-list-thumb .ul-icon-sw{background:url(${util.static}images/myincome_06.png) no-repeat;background-size:100%;}
	.ui-list-thumb .ul-icon-yq{background:url(${util.static}images/myincome_04.png) no-repeat;background-size:100%;}
	.ui-list-thumb .ul-icon-zf{background:url(${util.static}images/myincome_zf.png) no-repeat;background-size:100%;}
	.ul-icon-encashing {background:url(${util.static}images/myincome_07.png) no-repeat;background-size:100%;}
	.ul-icon-recharge {background:url(${util.static}images/recharge.png) no-repeat;background-size:100%;}
	.ui-list-thumb .ul-icon-other{background:url(${util.static}images/myincome_05.png) no-repeat;background-size:100%;margin-top:12px;}
	.ui-tiled .ui-li-title{font-size:16px;padding:6px 0px;}
	.ui-tiled i{font-size:14px;}
	.ui-tiled .balance{color:#ff8103;}
	.ui-tiled li{-webkit-box-flex: 1;width: 100%;text-align: center;display: -webkit-box;-webkit-box-orient: vertical;-webkit-box-pack: center;-webkit-box-align: center;}
	
	.ui-dialog .ui-dialog-bd{padding-bottom:0px;}
	.ui-dialog .ui-dialog-cnt {text-align:center;}
	.ui-dialog .ui-dialog-balance{font-size:16px;}
	.ui-dialog .ui-dialog-radio{padding-top:20px;text-align: left;}
	.ui-dialog-radio p{padding-left:25%;padding-bottom:6px;}
	.ui-dialog-radio p>span{padding-left:10px;}
	.ui-dialog-bd h1{margin:20px 0px;font-size:22px;font-weight:bold;}
	.ui-dialog-bd .ui-dialog-text{font-size:18px;line-height:34px;margin-bottom:40px;}
	a:link {color: #ff8003}		
	a:visited {color: #ff8003}	
	a:hover {color: #ff8003}	
	a:active {color: #ff8003}

	.bill_box{ background:#fff;}
	.bill_box {display:none;}
	.bill_box.selected {display:block;}
	.bill_box em{display: block;}
	.bill_box .amount li.on{border: 1px solid #ff8003;}
	.bill_box .amount li.on a,.bill_on .amount li.on span{color: #ff8003;}
	.bill_box .amount li.disabled {background:#eee;}
	.bill_box .amount li a{height: 25px; line-height: 25px; margin-top: 12px;}
	.bill_box .amount li span{display: block;}
	.bill_box .amount li i{color:#ff8103;}
	.bill_box .bill_btn a{background: #ff8003;}
	.amount {padding: 15px 15px 0; overflow: hidden; background:#fff; margin:0;}
	.amount li{width: 30%; height: 65px; border: 1px solid #ddd; display: block; float: left; background: #fff; margin-right: 3.4%; margin-bottom: 15px; overflow: hidden;}
	.amount li a{display: block; height: 65px; line-height: 65px; text-align: center; font-size: 18px; color: #aaa;}
	.amount li span{display: none; font-size: 12px; color: #aaa; text-align: center;}
	.amount li:nth-child(3n){margin-right: 0;}
	.bill_btn{margin:0 15px; padding-bottom:15px;}
	.bill_btn a{display: block; background:#ff8003; line-height: 45px; text-align: center; border-radius: 5px; font-size: 17px; color: #fff;}
	.bill_btn.grey a{background:#d2d2d2;}
	.account_balance{padding:15px; border-top:1px solid #dcdcdc; font-size:1em; color:#222;}
	.tishi{padding:0 15px 15px; font-size:0.94em; color:#999;}
	.tishi i{color:#ff0000; font-style:normal; line-height:22px;}
	.tixian_bg{position:fixed; left:0; bottom:0; width:100%;background:#fff;}
	.way{height:3em; margin:0 auto;}
	.way p{width:50%; line-height:2.5em; text-align:center; float:left; font-size:1.1em; color:#000; margin:0; border-left:1px solid #dcdcdc; margin-left:-1px; background:#ececec;}
	.way p.on{background:#fff; color:#ff8003;}
	.account_set {margin:40px 15px;}
	#masked-dialog .strong {color:#ff8003; font-weight:500;}
</style>
<div class="block ui-border-b">
    <ul class="ui-tiled">
        <li>
        	<div class="ui-list-thumb"><span class="li-kt"></span></div>
        	<div class="ui-li-title">可提收入</div>
        	<i class="balance">${util.fen2yuanS(income.balance)}元</i>
    	</li>
        <li><div class="ui-list-thumb"><span class="li-yt"></span></div></span><div class="ui-li-title">已提收入</div><i>${util.fen2yuanS(income.encash_total)}元</i></li>
        <li><div class="ui-list-thumb"><span class="li-sy"></span></div></span><div class="ui-li-title">所有收入</div><i>${util.fen2yuanS(income.income)}元</i></li>
    </ul>
</div>
<#if (income.encashing>0)>
<ul class="ui-list ui-list-one ui-border-tb">
    <li class="ui-border-t">
        <div class="ui-list-thumb">
             <span class="ul-icon-encashing"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">提现中</h4>
        </div>
        <div class="ui-txt-info" style="color:#ff8103">${util.fen2yuanS(income.encashing)}元</div>
    </li>
</ul>
</#if>
<#if (income.recharge>0)>
<ul class="ui-list ui-list-one ui-border-tb">
    <li class="ui-border-t">
        <div class="ui-list-thumb">
             <span class="ul-icon-recharge"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">手机充值中</h4>
        </div>
        <div class="ui-txt-info" style="color:#ff8103">${util.fen2yuanS(income.recharge)}元</div>
    </li>
</ul>
</#if>
<ul class="ui-list ui-list-one ui-list-link ui-border-tb">
    <li class="ui-border-t has-link" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/apps.html">
        <div class="ui-list-thumb">
            <span class="ul-icon-sw"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">限时任务收入</h4>
        </div>
        <div class="ui-txt-info">${util.fen2yuanS(income.task_total)}元</div>
    </li>
     <li class="ui-border-t has-link" data-href="${util.ctx}<#if fromSafari??>/ios/my/articlelist.html<#else>/web/article/article/list.html</#if>">
        <div class="ui-list-thumb">
            <span class="ul-icon-zf"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">转发任务收入</h4>
        </div>
        <div class="ui-txt-info">${util.fen2yuanS(income.article_total)}元</div>
    </li>
    <li class="ui-border-t has-link" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/inviteincome.html">
   		<div class="ui-list-thumb">
             <span class="ul-icon-yq"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">学徒奖励</h4>
        </div>
        <div class="ui-txt-info">${util.fen2yuanS(income.share_total)}元</div>
    </li>
    <li class="ui-border-t has-link" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web</#if>/my/otherincome.html">
        <div class="ui-list-thumb">
             <span class="ul-icon-other"></span>
        </div>
        <div class="ui-list-info">
            <h4 class="ui-nowrap">其它收入</h4>
        </div>
        <div class="ui-txt-info">${util.fen2yuanS(income.other_total)}元</div>
    </li>
</ul>
 <div class="ui-btn-wrap">
    <button class="ui-btn ui-btn-danger" id="withdraw-btn"  style="border-radius:6px;font-size:18px;width:100%;height:46px;background-color:#ff8003;background-image:none;"><#if (income.encashing=0)>提现<#else>正在提现中...</#if></button>
	<#if income.user_id == 33 || income.user_id == 60137 || income.user_id == 263>
		<button class="ui-btn ui-btn-danger" id="recharge-btn"  style="margin-top:18px;border-radius:6px;font-size:18px;width:100%;height:46px;background-color:#ff8003;background-image:none;">手机充值</button>
	</#if>
</div>
<#if userAccount.masked>
	<div class="ui-dialog show" id="masked-dialog">
		<div class="ui-dialog-cnt">
			<div class="ui-dialog-bd">
				<div align="left">
					<p>抱歉，由于您的某些行为已命中秒赚的反作弊系统，被判定为可疑用户，暂时无法提现。</p>
					<p>您可点击下方按钮联系在线客服QQ或手动添加客服微信进行申诉（申诉时，请告知客服您的ID: <span class="strong">${user.user_identity}）</span></p>
					<p>客服微信：<span class="strong">xiaomiaozhushou</span></p>
				</div>
				<div style="padding:10px 0;">
					<button class="ui-btn ui-btn-danger open-app" data-scheme="mqqwpa://im/chat?chat_type=wpa&uin=2815883901&version=1&src_type=web&web_src=">联系QQ客服</button>
				</div>
			</div>
		</div>
	</div>
<#elseif (income.encashing=0)>
<div class="ui-dialog" id="no-mobile-dialog">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1>温馨提示</h1>
            <div class="ui-dialog-text">	
	            您还没有绑定手机号，绑定手机号后才能提现
	            <p id="setting-mobile"><a href="javascript:void(0);" data-href="<#if fromSafari??>/ios<#else>/web</#if>/my/to_bind_mobile.html#withdraw">点击这里设置</a></p>
            </div>
        </div>
    </div>        
</div>
<div class="ui-dialog ui-dialog-tip">
    <div class="ui-dialog-cnt">
        <div class="ui-dialog-bd">
            <h1>温馨提示</h1>
            <div class="ui-dialog-text">	
	            您还没有设置提现账号
	            <p id="setting"><a href="javascript:void(0);" data-href="<#if fromSafari??>/ios/user<#else>/web/my</#if>/withdraw_type.html?withdraw=1">点击这里设置</a></p>
            </div>
        </div>
    </div>        
</div>
<div class="ui-dialog" id="withdraw-dialog">
	<div class="tixian_bg">
		<div class="way">
			<p class="on" data-type="2" data-fee="1"><span>支付宝</span></p>
			<p data-type="1" data-disabled="1"><span>微信</span></p>
		</div>
		<div class="content">
			<div class="bill_box selected">
				<#if userAccount.alipay_account?has_content>
					<ul class="amount">
						<#list enchashStages as stage>
							<#if stage.type == 0 || stage.type == 2>
							<li class=" <#if income.balance lt stage.amount>disabled<#else>enabled</#if>" data-amount="${stage.amount}">
								<a>${util.fen2yuanS(stage.amount)}元</a>
								<span><#if stage.coins gt 0>返<i>${stage.coins}</i>金币<#else>不返金币</#if></span>
							</li>
							</#if>
						</#list>
					</ul>
					<div class="tishi">一个工作日到账，周末及节假日不处理提现业务<br/>
						支付宝扣除手续费1元，实际到账共<i class="drawAmount">${util.fen2yuanS(enchashStages[0].amount - 100)}</i>元<br/>
						返还的金币将在提现成功后打入您的账户
					</div>
				<#else>
					<div class="account_set">您未设置支付宝提现账号，<a href="javascript:;" data-href="<#if fromSafari??>/ios/user<#else>/web/my</#if>/withdraw_account.html?type=2&withdraw=1">立即设置&gt;&gt;</a></div>
				</#if>
			</div>
			<div class="bill_box">
				<ul class="amount">
					<#list enchashStages as stage>
						<#if stage.type == 0 || stage.type == 1>
						<li class=" <#if income.balance lt stage.amount>disabled<#else>enabled</#if>" data-amount="${stage.amount}">
							<a>${util.fen2yuanS(stage.amount)}元</a>
							<span><#if stage.coins gt 0>返<i>${stage.coins}</i>金币<#else>不返金币</#if></span>
						</li>
						</#if>
					</#list>
				</ul>
				<div class="tishi">免手续费，一个工作日到账，周末及节假日不处理提现业务<br/>
					返还的金币将在提现成功后打入您的账户<br/>
					<i>亲爱的用户，微信提现功能升级中，请暂时使用支付宝提现</i>
					<#if !underWeixinLimit><br/><i>抱歉，今日微信提现总额已达上限，请您暂时使用支付宝提现</i></#if>
				</div>
			</div>
		</div>
		<div class="bill_btn" id="save-btn"><a>立即提现</a></div>
	</div>
</div>
<script>
var withdraw_config =  {
	'mobile' : '${user.mobile}',
	"wx_bank_name":"${userAccount.wx_bank_name!""}",
	"alipay_name":"${userAccount.alipay_name!''}",
	"alipay_account":"${userAccount.alipay_account!''}",
	"balance" : parseInt(${income.balance}) || 0,
	"enchash_url" : '${util.ctx}<#if fromSafari??>/ios/user/withdraw_action.html<#else>/api/v1/enchashment/save</#if>'
}
</script>
<script src="${util.static}js/app_withdraw.js"></script>
</#if>
<script src="${util.basejs}" ></script>
<script src="${util.static}frozenjs/1.0.1/frozen.js"></script>
<#include "/common/footer.ftl">