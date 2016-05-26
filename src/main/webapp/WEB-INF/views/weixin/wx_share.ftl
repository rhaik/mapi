<#if (sharemap??) && fromWeixin>
<script src="https://res.wx.qq.com/open/js/jweixin-1.0.0.js"></script>
<script type="text/javascript">
	wx.config({
	    debug: false,
	    appId: "${sharemap.appid!''}",
	    timestamp: ${sharemap.timestamp!0},
	    nonceStr: "${sharemap.nonceStr!''}",
	    signature: "${sharemap.signature!''}",
	    jsApiList: [
	       "hideMenuItems",
	       "hideAllNonBaseMenuItem"
	    ]
	});
	wx.ready(function () {
		if(location.href.indexOf('weixin/user/tasks.html') < 0)
       		wx.hideAllNonBaseMenuItem();
		else
			wx.hideMenuItems({ menuList:["menuItem:share:appMessage",
		             		    		    "menuItem:share:timeline",
		            		    		    "menuItem:share:qq",
		            		    		    "menuItem:share:weiboApp",
		            		    		    "menuItem:favorite",
		            		    		    "menuItem:share:facebook",
		            		    		    "menuItem:share:QZone",
		            		    		    "menuItem:copyUrl",
		            		    		    "menuItem:originPage",
		            		    		    "menuItem:openWithQQBrowser",
		            		    		    "menuItem:share:email",
		            		    		    "menuItem:share:brand"] });
	});
</script>
</#if>