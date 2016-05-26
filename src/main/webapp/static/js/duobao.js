(function($){
    function myAlert(msg, callback) {
        if(typeof MiJSBridge=="object") {
             MiJSBridge.call('alert', {title: msg}, function(){ typeof callback == 'function' && callback(); });
        } else {
            alert(msg);
            if (typeof callback == 'function'){
                setTimeout(function(){callback();}, 200);
            }
        }
    }

    function myConfirm(msg, callback){
        if(typeof MiJSBridge=="object") {
            MiJSBridge.call('confirm', {message: msg}, function(result){ result.index && typeof callback == 'function' && callback(); });
        }else {
            confirm(msg) && typeof callback == 'function' && callback();
        }
    }

    function toUrl(url) {
        if(typeof MiJSBridge=="object") {
            MiJSBridge.call("open", {url: url});
        } else {
            window.location.href= url;
        }
    }
    function myClose() {
        if(typeof MiJSBridge=="object") {
            MiJSBridge.call('close');
        } else {
            history.back();
        }
    }

    function myPost(url, params, success){
        if(typeof _MiJS != 'undefined' && _MiJS.os.android){
            MiJSBridge.call('ajax', {type: 'POST', url: url, data: (typeof(params) == 'string'? params : $.param(params))  }, success);
        }else{
            $.ajax({
                   type: "POST",
                   url: url,
                   data: params,
                   dataType:'json',
                   success:success
            });
        }
    }

    $.cookie = function(name, value, expire){
        if(!name){
            var cookies = {};
            var allCookies = document.cookie.split(';');
            for(var i = 0, len = allCookies.length; i < len; ++ i){
                var item = allCookies[i].trim().split('=');
                if (item.length == 2){
                    cookies[item[0]] = decodeURIComponent(item[1]);
                }
            }
            return cookies;
        }else if(!value){ //read
            var cookie = document.cookie;
            var cname = name + '=';
            var pos = cookie.indexOf(cname);
            if(pos >= 0){
                var end = cookie.indexOf(';', pos);
                end < 0 && (end = cookie.length);
                value = document.cookie.substring(pos + cname.length, end);
            }
            return value? decodeURIComponent(value) : undefined;
        }else { //set
            expire = parseInt(expire) || 86400; //1day in second
            var d = new Date();
            d.setTime(d.getTime() + (expire *1000));
            var expireStr = "expires="+d.toUTCString();
            document.cookie = name + "=" + encodeURIComponent(value) + "; " + expireStr + '; path=/';
        }
    };

    //pull to load more
    $.scrollWindow = function(params, callback){
        var page = 1, loading = false;
        if(params.page == 0){
            page = 0;
            scrollLoading();
        }else {
            page = parseInt(params.page) || 1;
        }

        $(window).off('scroll').on('scroll', function() {
            if ($(window).scrollTop() + $(window).height() > $(document).height() - 60 && !loading) {
                loading = true;
                scrollLoading();
            }
        });

        function scrollLoading() {
            var time = setTimeout(function() {
                //show loading
                $('#loading-panel p').text('正在加载中...');
                $('#loading-panel span').show();
                $('#loading-panel').show();
            }, 500);

            $.ajax({
                type: "GET",
                url: params.url,
                dataType: 'json',
                data: {
                    page : page
                },
                success: function(resp) {
                    clearTimeout(time);
                    //hide loading
                    $('#loading-panel').hide();
                    page += 1;
                    if (callback && callback(page, resp) === false){
                         $('#loading-panel span').hide();
                         $('#loading-panel p').text('已加载完所有数据');
                         $('#loading-panel').show();
                        return false;  //load complete
                    }
                    loading = false;
                },
                error: function(err) {
                    //hide loading
                     $('#loading-panel').hide();
                }
            });
        }
    };

    //container support scroll window to load more
    $.fn.initWindowScroll = function() {
        //only first element
        return $(this).eq(0).each(function(){
            var $container = $(this);
            var url = $container.data('url'), template = $container.data('template');
            if (url && template){
                $('#loading-panel').hide();
                //if data-page=0, will load the first page
                var page = $container.data('page') == 0 ? 0 : ($container.data('page') || 1);
                if($container.children().size() >= 10 || page == 0){
                    $.scrollWindow({url:url, page:page}, function(page, resp){
                        var html = handlebars[template](resp);
                        $container.attr('data-page', page).append(html);

                        if(resp.data.length < 10) {
                            return false;
                        }
                    });
                }else {
                    $(window).off('scroll');
                    $('#loading-panel span').hide();
                    $('#loading-panel p').text('已加载完所有数据');
                    $('#loading-panel').show();
                }
            }
        });
    };

    $.showTips = function(info, sticky){
        $.hideTips();
        $('body').append('<div id="pg_info_0x01" class="point_bg" ' + (sticky? '' : 'onclick="$(this).remove()"') + '><div class="point_box">' + info + '</div></div>');
    };

    $.hideTips = function(){
        $('#pg_info_0x01').remove();
    };

    $.showDialog = function(title, msg, options){
        options = options || {};
        var html = '<div id="pg_dialog_0x01" class="popup_bg"><div class="number_box">' +
                   (title? ('<h1>' + title + '</h1>') : '') +
                   (msg? ('<h3>' + msg + '</h3>') : '') +
                   '<div class="popup_bot"><div class="bot_btn on sureBtn"><a>' + (options.sureText? options.sureText : '确定') +'</a></div>' +
                   '<div class="bot_btn cancelBtn"><a>'+ (options.cancelText? options.cancelText : '取消') + '</a></div>' +
                   '</div></div></div>';

        $('body').append(html);
        $('#pg_dialog_0x01 .sureBtn').click(function(){ options.sureCallback? options.sureCallback() : $.hideDialog(); });
        $('#pg_dialog_0x01 .cancelBtn').click(function(){ options.cancelCallback? options.cancelCallback() : $.hideDialog(); });
    };

    $.hideDialog = function(){
        $('#pg_dialog_0x01').remove();
    };

    window.myAlert = myAlert;
    window.myConfirm = myConfirm;
    window.toUrl = toUrl;
    window.myClose = myClose;
    window.myPost = myPost;

    //ready function
    $(function(){

         //init handlebars templates
         var handlebars = {};
         if(typeof Handlebars != 'undefined'){
            $('script').each(function(){
                var $this = $(this);
                if($this.attr('type') == 'text/x-handlebars-template'){
                    var name = $this.attr('id');
                    handlebars[name] = Handlebars.compile($this.html());
                }
            });
         }
         window.handlebars = handlebars;


         //init click event
        $('body').on('click', 'a', function(event){
            var href = $(this).data('href');
            href && toUrl(href);
        });

        $('.scrollWindow').initWindowScroll();

        window.onpageshow = function(event){
            event.persisted && onPageRefresh();
        };

        if(window.MiJSBridge){
             onMiJSBridgeReady();
        }else{
             document.addEventListener('MiJSBridgeReady', onMiJSBridgeReady);
        }

        function onMiJSBridgeReady(){
            window.onpageshow = null;
            MiJSBridge.on("onRefreshPage", onPageRefresh);
        }

        function onPageRefresh(){
            if(typeof $.onPageShowAgain == 'function'){
                $.onPageShowAgain(event);
            }else if($('body').data('refresh')){
                location.reload();
            }
        }
    });

})(window.$ || window.jQuery || window.Zepto);




