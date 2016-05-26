$(document).bind("pageshow", function() {
    startNum = -1, count = (typeof(count) == 'undefined'? 0 : count), minMessageId = -1, hasMore = true;
    function fixed(elm) {
        if (elm.data("iscroll-plugin")) {
            return;
        }
        elm.css({overflow: 'hidden'});
        var barHeight = 0; // 页头页尾高度
        var loader,
            pullUpEl, pullUpOffset;

        var isRefreshing = false; // 一次滑动事务
        // 设置页头样式
        var $wrapper = $('#wrapper');
        if($wrapper.size()){
            barHeight += parseInt($wrapper.css('margin-top'))
        }

        var $header = elm.find('[data-role="header"]');
        if ($header.length) {
            $header.css({
                "z-index": 1000,
                padding: 0,
                width: "100%"
            });
            barHeight += $header.height();
        }

        // 设置页尾样式
        var $footer = elm.find('[data-role="footer"]');
        if ($footer.length) {
            $footer.css({
                "z-index": 1500,
                padding: 0,
                width: "100%"
            });
            barHeight += $footer.height();
        }

        // 设置内容区域样式、高度
        var $wrapper = elm.find('[data-role="content"]');
        if ($wrapper.length) {
            $wrapper.css({
                "z-index": 1
            });
            $wrapper.height($(window).height() - barHeight);
            $wrapper.bind('touchmove', function(e) {
                e.preventDefault();
            });
        }

        // 设置滚动区域
        var scroller = elm.find('[data-iscroll="scroller"]').get(0);
        if (!scroller) {
            $($wrapper.get(0)).children().wrapAll("<div data-iscroll='scroller'></div>");
        }


        var isInit = 0;
        var pullDownEl, pullDownOffset,
            pullUpEl, pullUpOffset, generatedCount = 0;


        /**
         * 初始化iScroll控件
         */

        pullDownEl = document.getElementById('pullDown');
        pullDownOffset = pullDownEl.offsetHeight;
        pullUpEl = document.getElementById('pullUp');
        pullUpOffset = pullUpEl.offsetHeight;

        myScroll = new iScroll(
            $wrapper.get(0), {
                //scrollbarClass: 'myScrollbar', /* 自定义样式 */
                useTransition: true, //是否使用CSS变换
                topOffset: pullDownOffset,
                hScroll: true,
                vScroll: true,
                hScrollbar: false,
                vScrollbar: true,
                fixedScrollbar: true,
                fadeScrollbar: true,
                hideScrollbar: true,
                bounce: true,
                momentum: true,
                lockDirection: true,
                checkDOMChanges: true,
                onRefresh: function() {
                    if (pullDownEl.className.match('loading')) {
                        pullDownEl.className = '';
                        pullDownEl.querySelector('.pullDownLabel').innerHTML = '下拉刷新...';
                        setTimeout(function(){$('#pullDown').hide();}, 200);
                    } else if (pullUpEl.className.match('loading')) {
                        pullUpEl.className = '';
                        pullUpEl.querySelector('.pullUpLabel') && (pullUpEl.querySelector('.pullUpLabel').innerHTML = '上拉加载更多...');
                    }
                },
                onScrollMove: function() {
                    if (this.y > 15 && !pullDownEl.className.match('flip')) {
                    	$('#pullDown').show();
                        pullDownEl.className = 'flip';
                        pullDownEl.querySelector('.pullDownLabel').innerHTML = '松手开始更新...';
                        this.minScrollY = 0;
                    } else if (this.y < 15 && pullDownEl.className.match('flip')) {
                        pullDownEl.className = '';
                        pullDownEl.querySelector('.pullDownLabel').innerHTML = '下拉刷新...';
                        this.minScrollY = -pullDownOffset;
                    } else if (this.y < (this.maxScrollY - 15) && !pullUpEl.className.match('flip')) {
                    	if(pullUpEl.querySelector('.pullUpLabel') ==null) return ;
                        pullUpEl.className = 'flip';
                        pullUpEl.querySelector('.pullUpLabel').innerHTML = '松手开始更新...';
                        this.maxScrollY = this.maxScrollY;
                    } else if (this.y > (this.maxScrollY + 15) && pullUpEl.className.match('flip')) {
                    	if(pullUpEl.querySelector('.pullUpLabel') ==null) return ;
                        pullUpEl.className = '';
                        pullUpEl.querySelector('.pullUpLabel').innerHTML = '上拉加载更多...';
                        this.maxScrollY = pullUpOffset;
                    }
                },
                onScrollEnd: function() {
                    if (pullDownEl.className.match('flip')) {
                        pullDownEl.className = 'loading';
                        pullDownEl.querySelector('.pullDownLabel').innerHTML = '加载中...';
                        pullDownAction(); // Execute custom function (ajax call?)
                    } else if (pullUpEl.className.match('flip')) {
                        pullUpEl.className = 'loading';
                        pullUpEl.querySelector('.pullUpLabel').innerHTML = '加载中...';
                        pullUpAction(); // Execute custom function (ajax call?)
                    }
                },

            });
        //页面初始化
        isInit = 1;
        isDisplayLoad = false;
        /**
         * 下拉刷新
         * myScroll.refresh();		// 数据加载完成后，调用界面更新方法
         */
        pullDownAction = function() {
        	startNum =-1;
        	lastId = 0;
            setTimeout(loadData, 1000); // <-- Simulate network congestion, remove setTimeout from production!
        }
		
        /**
         * 上拉刷新
         * myScroll.refresh();		// 数据加载完成后，调用界面更新方法
         */
        function pullUpAction() {
            lastId = minMessageId;
            setTimeout(loadData, 1000); // <-- Simulate network congestion, remove setTimeout from production!
        }
        var pullUp =  $('#pullUp').html();
        function loadData() {
            if(startNum !=-1 && count <= 1 && !hasMore) { return false;}
            if ( (startNum >= 0 && startNum >= (count-1) && minMessageId == -1 ) || !hasMore) {
                $('#pullUp').html("已加载完全部信息");
                myScroll.refresh();
                return false;
            } else {
            	$('#pullUp').html(pullUp);
            }
            startNum = startNum + 1;
            console.log("服务器地址：" + serverURL);
            
            $.ajax({
                async: true,
                url: serverURL, // 跨域URL
                type: 'get',
                data: {page: startNum, last_id: lastId},
                timeout: 3000,
                dataType:"json",
                success: function(datas) { //客户端jquery预先定义好的callback函数，成功获取跨域服务器上的json数据后，会动态执行这个callback函数 
                    desplay(datas);
                },
                complete: function(XMLHttpRequest, textStatus) {
                    //alert(textStatus);
                },
                error: function(XMLHttpRequest,xhr,errorThrown) {
                	hideLoading();
                	console.log("错误：" + xhr);
                	console.info(errorThrown);
                    myAlert("请求出错(请检查相关度网络状况.)");
                    myScroll.refresh();
                }
            });
        }

        function desplay(datas) {
            if (datas != null && datas != "") {
            	if(startNum <= 0 && minMessageId < 0) {
					$("#fpmxList").html('');
				}
				var oldId = lastId;
                if(typeof callbackReviceData =="function"){
                 	result =  callbackReviceData(datas); 
                } else {
                    result = "";
                }
                console.log("加载后的当前页：" + startNum + "|| 获取数据" + result);

				if(oldId == 0){
				    $("#fpmxList").prepend(result);
				}else {
                    $("#fpmxList").append(result);
                }
                $("#fpmxList").listview("refresh");
				$('ul').removeClass("ui-listview");
 				$('ul>li').removeClass("ui-li ui-li-static ui-btn-up-c");
 				
 				myScroll.refresh(); 
 				if((startNum >0  || lastId >= 0 ) && datas.data.length < 10) {
 				    hasMore = false;
 					$('#pullUp').html("已加载完全部信息").show();
				}else {
				    $('#pullUp').html(pullUp).show();
				}
            }
            if(isDisplayLoad)
            	hideLoading();
        }

        function showMyAlert(text) {
            $.mobile.loadingMessageTextVisible = true;
            $.mobile.showPageLoadingMsg("a", text, true);

        }

        function myAlert(text) {
            showMyAlert(text);
            setTimeout(hideLoading, 1000);
        }

        showLoading = function() {
            $.mobile.loadingMessageTextVisible = true;
            $.mobile.showPageLoadingMsg("a", "加载中...");
        }

        function hideLoading() {
            $.mobile.hidePageLoadingMsg();
        }


        elm.data("iscroll-plugin", myScroll);

        window.setTimeout(function() {
            myScroll.refresh();
        }, 200);

        reinitScroll = function() {
            $("#fpmxList").empty();
            myScroll.refresh();
            $('#pullUp').html(pullUp).hide();
            pullDownAction();
        };
    }

    $('[data-role="page"][data-iscroll="enable"]').bind("pageshow", function() {
        fixed($(this));
        
    });
    if ($.mobile.activePage.data("iscroll") == "enable") {
        fixed($.mobile.activePage);
    }

});

$(document).bind("mobileinit", function() {
    // $.mobile.ajaxEnabled = false;
    // $.mobile.linkBindingEnabled = false;
    $.mobile.defaultPageTransition = "none";
    $.mobile.activeBtnClass = "ui-btn-hover-a";
    $.mobile.page.prototype.options.domCache = false;
    //禁止hover延迟
    $.mobile.buttonMarkup.hoverDelay = "false";
    $.mobile.loader.prototype.options.text = "正在加载...";
    $.mobile.loader.prototype.options.textVisible = true;
    $.mobile.loader.prototype.options.theme = "a";
    $.mobile.loader.prototype.options.html = "";

    //Thanks: https://github.com/jquery/jquery-mobile/issues/3414 
    $.mobile.loader.prototype.defaultHtml = "<div class='ui-loader' data-overlay-theme='a' class='ui-content' style='opacity: 0.5;'>" +
        "<span class='ui-icon ui-icon-loading'></span>" +
        "<h1></h1>" +
        "<div class='ui-loader-curtain'></div>" +
        "</div>";
});