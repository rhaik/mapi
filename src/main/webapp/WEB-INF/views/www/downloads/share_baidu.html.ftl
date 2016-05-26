<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<div class="spread">
    <div class="banner"><img src="${util.static}/images/img/banner.png" /></div>
    <h1 class="tit_box">产品简介</h1>
    <div class="spread_txt">
        <p>秒赚大钱，是由北京创力聚点科技有限公司开发的一款服务于千万用户的手机激励赚钱APP。用户花费2-3分钟下载并试玩一个应用即可得到1-2元现金奖励，每天轻轻松松赚零钱！</p>
        <p>北京创力聚点是一家服务于移动应用开发者的互联网公司。专注于移动互联网领域及移动激励广告平台，并发挥巨大影响力。旗下品牌还包括风暴ASO大数据平台，力求为iOS开发者提供最专业的ASO数据。</p>
    </div>
    <#if userFriendIncome??>
        <h1 class="tit_box">网友成绩</h1>
        <div class="score">
        <#list userFriendIncome as uf>
            <#if uf.user??>
            <div class="score_list clearfix" <#if (uf_index >=10)>style="display:none;"</#if>>
                <div class="score_pic"><img width="100%" src="${uf.user.headImg}"></div>
                <div class="score_text">
                    <div class="score_top">
                        <b class="name-icon">${uf.user.uniName!""}</b>
                        <em>${uf.beforMinute}</em>
                    </div>
                    <div class="score_bot">${uf.remarks}</div>
                </div>
            </div>
            </#if>
        </#list>
        </div>
    </#if>
    <div class="wechat"><span><img src="${util.static}/images/img/wechat_sma.png" /></span>客服微信：xiaomiaozhushou</div>
    <h1 class="tit_box">公司简介</h1>
    <div class="site">
        <p>公司名称：北京创力聚点科技有限公司</p>
        <p>地址：北京市海淀区西二旗领秀新硅谷D区33-105</p>
        <p>电话：010-82833270</p>
    </div>
    <div class="posi_bot_btn">
        <a class="money_btn" id="download-btn" href="javascript:;">开始赚钱</a>
    </div>
</div>
<div class="safari_open" style="display:none;">
    <img width="100%" src="${util.ctx}/static/images/download_safari.png" />
</div>
<script src="${util.jquery}"></script>
<script>
    $(function(){
         $('body').addClass('bg_black');
         $('#download-btn').click(function(){
            <#if ios>
                <#if isWeixin>
                $('.safari_open').show();
                <#else>
                location.href = "/www/downloads/safari";
                </#if>
            <#else>
            window.location.href= '${util.ctx}/www/downloads/url';
            </#if>
        });
        $(".safari_open").click(function(){
            $(this).hide();
        });
    });
</script>
<#include "/common/footer.ftl">