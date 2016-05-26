<#import "/lib/util.ftl" as util />
<html>
<head>
    <meta charset="utf-8" />
    <meta name="format-detection" content="telephone=no" />
    <meta name="viewport" content="width=device-width, user-scalable=no,initial-scale=1,minimal-ui" />
    <meta name="apple-mobile-web-app-capable" content="yes" />
    <meta name="apple-mobile-web-app-status-bar-style" content="black" />
    <title>${articleTask.name!''}</title>
</head>
<body style="margin:0;padding:0;">
<div id='wx_pic' style='margin:0 auto;display:none;'>
    <img src="${article.img!''}" />
</div>
<div style="overflow: auto;-webkit-overflow-scrolling:touch;">
<iframe src="${article.url!''}" frameBorder="0" width="100%" style="height: 100%; min-width: 100%; width: 10px; *width: 100%;" scrolling="no"></iframe>
</div>
</body>
</html>