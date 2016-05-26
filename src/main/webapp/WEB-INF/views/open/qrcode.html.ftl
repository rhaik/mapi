<#import "/lib/util.ftl" as util />
<#include "/common/headerNews.ftl" />
<style>
    body{background:#f7f7f9;}
    .box h1{font-size:1.5em; color:#ff8003; text-align:center; padding:2.6em 0; font-weight:normal;}
    .ewm{width:20em; height:20em; margin:0 auto;}
    .box h2{font-size:1.5em; color:#666; text-align:center; margin-top:0.5em; font-weight:normal;}
</style>
<div class="box">
    <h1>${wxAccount.title!''}</h1>
    <div class="ewm"><img width="100%" src="${wxAccount.qrcode!''}" /></div>
    <h2>长按图片识别二维码</h2>
</div>
<#include "/common/footer.ftl"/>