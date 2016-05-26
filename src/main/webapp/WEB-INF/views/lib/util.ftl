<#function jsonQuote str>
	<#if (str?exists) >
		<#local js=str?replace('\\','\\\\')/>
		<#local js=js?replace('"','\\"')/>
        <#local js=js?replace('\n','\\n')/>
        <#local js=js?replace("\r","\\r")/>
        <#local js=js?replace("\b","\\b")/>
        <#local js=js?replace("\f","\\f")/>
        <#local js=js?replace("\t","\\t")/>
        <#return js>
    <#else>
		<#return "">
	</#if>
</#function>

<#function fen2yuan str>
    <#return (str/100)?float >
</#function>

<#function fen2yuanS str>
	<#return (str/100)?string("#.##") >
</#function>

<#assign ctx = request.contextPath>
<#assign domain="${ctx}"/>
<#assign static="${ctx}/static/"/>
<#function avatar str>
	<#if (str?? && str?length > 1) >
		<#return str>
	<#else>
		<#return '${static}/images/rank_avatar.png'>
	</#if>
</#function>

<#assign jquery = "//lib.sinaapp.com/js/jquery/2.0/jquery.min.js" />
<#assign jquerymobile = "//lib.sinaapp.com/js/jquery-mobile/1.3.0/jquery.mobile-1.3.0.min.js" />
<#assign zepto = "//lib.sinaapp.com/js/zepto/1.0/zepto.min.js" />
<#assign handlebars = "//cdn.bootcss.com/handlebars.js/4.0.5/handlebars.min.js" />
<#assign swiperjs = "//cdn.bootcss.com/Swiper/3.0.8/js/swiper.min.js" />
<#assign iscroll = "${static}js/iscroll.js" />


<#assign basejs = "${static}js/base.js" />
<#assign exchangejs = "${static}js/exchange.js" />


<#assign frozencss = "${static}frozenui/1.2.1/css/frozen.css" />
<#assign swipercss = "//cdn.bootcss.com/Swiper/3.0.8/css/swiper.min.css" />