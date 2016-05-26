<#import "/lib/util.ftl" as util/>
<#include "/common/headerNews.ftl">
    <style type="text/css">
        body{background:#ee5857;}
        .con_top h1{font-size:1.2em; color:#fff; font-weight:normal; text-align:center; padding-top:1.6em;}
        .con_top h1 em{color:#fffb91; font-style:normal;}
        .con_top p{font-size:0.875em; color:#fffb91; text-align:center; padding:0.5em 0 0.93em;}
        .total{margin:0 10%; padding-bottom:1.25em;}
        .head_ico{width:2.875em; border-radius:3em; overflow:hidden; display:block; float:left;}
        .total_day{height:1.875em; line-height:1.875em; background:#fff; color:#ee5857; padding:0 0.93em; margin:0.35em 0 0 0.5em; float:left; border-radius:1em;}
        .total_day em{width:0.375em; display:block; float:right; padding-left:0.5em;}
        .border{width:100%; margin:-3px 0 -2px;}
        .main_box{background:#fff; padding:2em 0;}
        .main{margin:0 6%; border:1px solid #ee5857; border-radius:0.2em; position:relative;}
        .tab{font-size:0.875em; padding:1.7em 0 1.3em;}
        .tab tr:first-child td:last-child{color:#000;}
        .tab tr td{font-size:1em; color:#000; text-align:center; padding:0.5em 0;}
        .tab tr td:last-child{color:#ee5857;}
        .tab tr td em{font-style:normal; color:#ee5857;}
        .titlt{width:14.6em; height:2.2em; position:absolute; top:-0.9em; left:50%; margin-left:-7.3em; background:#ee5857; border-radius:0.3em;}
        .titlt span{margin:0.3em 0.2em; height:1.5em; line-height:1.5em;text-align:center; border:1px dashed #fff; display:block; border-radius:0.3em;}
        .titlt span em{font-style:normal; color:#fff;}
        .con_bot{padding:1.25em 0.78em;}
        .con_bot h1{font-size:1.25em; color:#fff; font-weight:normal;}
        .con_bot p{font-size:1em; color:#fff; line-height:1.5em;}
        .con_bot p.col_y{color:#fffb91;}
    </style>
    <div class="content">
        <div class="con_top">
            <h1>今日签到成功，奖励<em>${(todayStage.amount * checkinRate)?round}</em><#if checkinRate gt 1><em>(${checkinRate}倍)</em></#if>金币</h1>
            <p>已连续签到${days}天<#if nextStageDays gt 0>，再连续签到${nextStageDays}天即可获得更多奖励</#if></p>
            <div class="total clearfix">
                <div class="head_ico"><img src="${userInfo.avatar!''}" /></div>
                <a class="total_day" href="javascript:;" data-href="${util.ctx}<#if fromSafari??>/ios<#else>/web/my</#if>/checkinlog.html">累计奖励：${checkInStat.total_income}金币<em><img src="${util.static}images/img/right_red.png" /></em></a>
            </div>
        </div>
        <div class="border"><img width="100%" src="${util.static}images/img/bor_top.png" /></div>
        <div class="main_box">
            <div class="main">
                <table class="tab" width="100%">
                    <tr>
                        <td width="50%">连续签到天数</td>
                        <td width="50%">签到奖励金币<#if checkinRate gt 1><em>(${checkinRate}倍)</em></#if></td>
                    </tr>
                    <#list checkInStages as stage>
                        <tr>
                            <td>${stage.minDays}</td>
                            <td>${(stage.amount * checkinRate)?round}</td>
                        </tr>
                    </#list>
                </table>
                <div class="titlt">
                    <span><a>奖励规则</a></span>
                </div>
            </div>
        </div>
        <div class="border"><img width="100%" src="${util.static}images/img/bor_bot.png" /></div>
        <div class="con_bot">
            <h1>签到规则</h1>
            <p>1.连续签到中断后，将重新计算连续天数</p>
            <p>2.签到奖励的金币可以在个人中心我的金币中兑换人民币</p>
            <p>3.最终解释权归秒赚大钱所有</p>
        </div>
    </div>
<script src="${util.zepto}"></script>
<script>
    $(function(){
        $('a').click(function(){
            var href = $(this).data('href');
            if(href){
                if(typeof MiJSBridge=="object") {
                    MiJSBridge.call("open", {url: href});
                } else {
                    window.location.href= href;
                }
            }
        });
    });
</script>
<#include "/common/footer.ftl" />