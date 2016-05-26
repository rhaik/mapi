<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<link rel="stylesheet" type="text/css" href="${util.swipercss}">
<link rel="stylesheet" type="text/css" href="${util.static}css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}css/style.css">
<div class="banner-wrapper">
    <img src="${util.static}images/img/banner.png" />
</div>
<h1 class="tuiguang_title">产品介绍</h1>
<div class="tuiguang_con">
    <div class="tuiguang_con_list"><img src="${util.static}images/img/con1.png" /></div>
    <div class="tuiguang_con_list"><img src="${util.static}images/img/con3.png" /></div>
    <div class="tuiguang_con_list"><img src="${util.static}images/img/con2.png" /></div>
</div>
<#if userFriendIncome??>
    <h1 class="tuiguang_title">网友成绩</h1>
    <div class="score tuiguang_score">
        <#list userFriendIncome as uf>
        <#if uf.user??>
        <div class="score_list clearfix" <#if (uf_index >=10)>style="display:none;"</#if> >
            <div class="score_pic"><img width="100%" src="${uf.user.headImg}"></div>
            <div class="score_text">
                <div class="score_top">
                    <b>${uf.user.uniName!""}</b>
                    <em>${uf.beforMinute}</em>
                </div>
                <div class="score_bot">${uf.remarks}</div>
            </div>
        </div>
        </#if>
        </#list>
    </div>
</#if>
<div class="wechat"><span><img src="${util.static}images/img/wechat_sma_f.png"></span>客服微信：xiaomiaozhushou</div>
<div class="bot_anzhuang_btn">
    <a class="fl install_btn">安装教程</a>
    <a class="fr" id="download-btn">安装秒赚</a>
</div>
<#if isIOS9>
<div class="bg2">
    <div class="install_main">
        <div class="main_tit">
            <h1>安装教程</h1>
        </div>
        <div style="padding:10px 0;" class="main_tit dn">
            <h1>安装完成</h1>
        </div>
        <b class="close">×</b>
        <div class="swiper-container">
            <div class="swiper-wrapper">
                <div class="swiper-slide"><img src="${util.static}images/img/img1.jpg"></div>
                <div class="swiper-slide"><img src="${util.static}images/img/img2.jpg"></div>
                <div class="swiper-slide"><img src="${util.static}images/img/img3.jpg"></div>
                <div class="swiper-slide"><img src="${util.static}images/img/img4.jpg"></div>
                <div class="swiper-slide"><img src="${util.static}images/img/img5.jpg"></div>
                <div class="swiper-slide"><img src="${util.static}images/img/img6.jpg"></div>
            </div>
            <!-- Add Pagination -->
            <div class="swiper-pagination"></div>
        </div>
        <div class="last_btn cert">前往企业证书</div>
    </div>
</div>
<#elseif ios/>
<div class="bg2">
    <div class="install_main">
        <div class="main_tit">
            <h1>安装教程</h1>
        </div>
        <b>×</b>
        <div class="swiper-container1">
            <div class="swiper-wrapper">
                <div class="swiper-slide"><img src="${util.static}images/img/img01.jpg"></div>
            </div>
        </div>
        <div class="last_btn" style="margin-top:0">我知道了</div>
    </div>
</div>
</#if>
<script src="${util.jquery}"></script>
<script src="${util.swiperjs}"></script>
<script>
    $(function(){
         $('body').addClass('bg_f');
         $('#download-btn').click(function(){
            <#if ios>
                <#if isWeixin>
                $('.safari_open').show();
                <#else>
                location.href = "/www/downloads/ysurl";
                showSwiper();
                </#if>
            <#else>
            window.location.href= '${util.ctx}/www/downloads/url';
            </#if>
        });
        $(".safari_open").click(function(){
            $(this).hide();
        });

        $('.install_main b').on('click',function(){
            $('.bg2').hide();
        })
        $('.install_btn').on('click',function(){
            showSwiper();
        })
        $('.bg2').on('click', '.last_btn', function(){
            if($(this).hasClass('cert')){
                location.href = 'prefs:root=General&path=ManagedConfigurationList';
            }else {
                $('.bg2').hide();
            }
        }).on('click', '.close', function(){
            $('.bg2').hide();
        });

        function showSwiper(){
             $('.bg2').show();
             var swiper = new Swiper('.swiper-container', {
                pagination: '.swiper-pagination',
                loop: true,
                paginationClickable: true,
                autoplay: 1500,
                loopAdditionalSlides : 1
            });
        }

        $('#download-btn').trigger('click');
    });
</script>
<#include "/common/footer.ftl">