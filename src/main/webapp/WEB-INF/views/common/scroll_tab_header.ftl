<#import "/lib/util.ftl" as util>
<!DOCTYPE html>
<html>  
    <head>
        <meta charset="utf-8" />
        <meta name="author" content="rhaik" />
        <meta name="format-detection" content="telephone=no" />
        <meta name="viewport" content="width=device-width, user-scalable=no,initial-scale=1,minimal-ui" />
        <meta name="apple-mobile-web-app-capable" content="yes" />
        <meta name="apple-mobile-web-app-status-bar-style" content="black" />
        <title>${title!'赚大钱'}</title>
		<link href="${util.frozencss}" rel="stylesheet">
		<style>
			@-webkit-keyframes am-rotate2{from{background-position:0 0}to{background-position:-444px 0}}
			.ui-loading{width:auto;height:auto;background:'';display:'';-webkit-background-size:'';background:none;}
			.ui-loading .ui-loader{width: 80px; height: 80px; display: -webkit-box; -webkit-box-orient: vertical; -webkit-box-align: center;text-align: center;  background: rgba(0,0,0,.65); border-radius: 6px; color: #fff; font-size: 16px;} 
			.ui-loader{display:none;z-index:9999999;position:fixed;top:50%;left:50%;border:0;margin-left:-40px;margin-top:-40px;}
			.ui-icon-loading{margin: 18px 0 8px; width: 37px;height: 37px;display: block; background-image: url(http://frozenui.github.io/frozenui/img/loading_sprite_white.png);-webkit-background-size: auto 37px;-webkit-animation: am-rotate2 1s steps(12) infinite;}
			.ui-loader h1{display:none;}
			#pullDown,#pullUp{margin:0 auto;text-align:center;background:transparent;height:45px;line-height:40px;padding:0px 10px;font-weight:bold;font-size:14px;color:#888;}
			#pullUp{height:95px;margin-top:10px;padding:0px 10px 0px 0px;}
			#pullDown .pullDownIcon,#pullUp .pullUpIcon{display:inline-block;margin-right:50px;width:40px;height:40px;background:url(${util.static}images/pull-icon@2x.png) 0 0 no-repeat;-webkit-background-size:40px 80px;background-size:40px 80px;-webkit-transition-property:-webkit-transform;-webkit-transition-duration:250ms;}
			#pullDown .pullDownIcon{-webkit-transform:rotate(0deg) translateZ(0);}
			#pullDown .pullDownLabel{position: absolute;left:48%;}
			#pullUp .pullUpIcon{-webkit-transform:rotate(-180deg) translateZ(0);}
			#pullDown.flip .pullDownIcon{-webkit-transform:rotate(-180deg) translateZ(0);}
			#pullUp.flip .pullUpIcon{-webkit-transform:rotate(0deg) translateZ(0);}
			#pullDown.loading .pullDownIcon,#pullUp.loading .pullUpIcon{background-position:0 100%;-webkit-transform:rotate(0deg) translateZ(0);-webkit-transition-duration:0ms;-webkit-animation-name:loading;-webkit-animation-duration:2s;-webkit-animation-iteration-count:infinite;-webkit-animation-timing-function:linear;}@-webkit-keyframes loading{from{-webkit-transform:rotate(0deg) translateZ(0);}to{-webkit-transform:rotate(360deg) translateZ(0);}}
		</style>
		<script src="${util.jquery}"></script>
		<script type="text/javascript" src="${util.jquerymobile}"></script>
		<script type="text/javascript"  src="${util.iscroll}"></script>
		<script type="text/javascript"  src="${util.static}js/initScroll.js"></script>
    </head>
<body>