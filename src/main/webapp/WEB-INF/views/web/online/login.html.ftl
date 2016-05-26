<#import "/lib/util.ftl" as util />
<#include "/common/header.ftl" />
<link rel="stylesheet" type="text/css" href="${util.static}/css/common.css">
<link rel="stylesheet" type="text/css" href="${util.static}/css/style.css">
<div class="sign">
    <div class="sign_top">
        <em><img src="${util.static}/images/img/logo.png" /></em>
        <span>秒赚大钱</span>
        <a href="javascript:;" id="login-btn">登录</a>
        <i>新用户首次登录可获得现金红包</i>
        <p>${clientInfo.appVer}</p>
    </div>
</div>
<script src="${util.jquery}"></script>
<script src="${util.zepto}"></script>
<script src="${util.basejs}"></script>
<script type="text/javascript">
    $(function(){
        $("#login-btn").click(function(){
            MiJSBridge.call('userLogin', {}, function(result){
                if(result.state == 0){
                    location.reload();
                }else {
                    MiJSBridge.call('alert', {message: '登录失败，请稍后重试'});
                }
            });
        });
    });
</script>
<#include "/common/footer.ftl" />