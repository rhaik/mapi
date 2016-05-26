<#import "/lib/util.ftl" as util>
<#if !(showHeader??)>
    <#assign showHeader=(fromSafari?? && !fromWeixin) />
</#if>
<!DOCTYPE html>
<html lang="zh">
    <head>
        <meta charset="utf-8" />
        <meta name="format-detection" content="telephone=no" />
        <meta name="viewport" content="width=device-width, user-scalable=no,initial-scale=1,minimal-ui" />
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-status-bar-style" content="black" />
        <title><#if fromSafari>秒赚大钱<#else>${title!'秒赚大钱'}</#if></title>
        <link href="${util.frozencss}" rel="stylesheet">
        <#if showHeader>
            <style>
                .header{width:100%; height:44px; line-height:44px; text-align:center; background:#19191f; position:fixed; top:0; left:0; z-index:9999;}
                .header span{font-size:18px; color:#fff; font-weight:bold;}
                .header .back{width:14px; height:24px; display:block; position:absolute; top:0; left:0; padding:10px 15px;}
                .header .right_btn{width:24px; height:24px; display:block; position:absolute; top:0; right:5px; padding:10px;}
                .header img {vertical-align:top;}
                #wrapper {margin-top:44px;}
                <#if showHeader>.ui-poptips {top:44px;}</#if>
            </style>
        </#if>
        <script src="${util.zepto}"></script>
    </head>
 <body>
 <#if showHeader>
    <header class="header">
        <a class="back" href="javascript:history.back();" <#if hideBack>style="display:none;"</#if> ><img width="100%" src="${util.static}images/img/left_icon.png" /></a>
        <span>${title!'秒赚大钱'}</span>
        <a class="right_btn" href="javascript:location.reload();" <#if hideRefresh>style="display:none;"</#if> ><img width="100%" src="${util.static}images/img/refresh.png" /></a>
    </header>
    <div id="wrapper">
</#if>