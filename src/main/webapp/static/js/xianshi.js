$(function(){
    var currentApp = {};
    var $task = null;
    var checkTaskInterval = null;
    var rate = parseFloat($('#task-list').data('rate')) || 1;
    var $doingTask = $('button.haveing').closest('li');
    var $$tips, $$loading;

    function showTips(message) {
        $$tips && $$tips.tips('hide');
        $$tips = Zepto.tips({
            content:message,
            stayTime:2000,
            type:"info"
        });
    }

    function showLoading(msg){
        $$loading = Zepto.loading({content:msg || '加载中'});
    }

    function hideLoading(){
        $$loading && $$loading.loading('hide');
    }


	function bindEvent(){
	    $('#task-list').on('click', 'li', function(e){
            var $this = $(this);
            if($this.hasClass('no-click')) return ;

            if($this.hasClass('ui-li-finish')){
                var $info = $this.find('.ui-list-info');
                if(parseInt($info.data('restnum')) > 0){
                    showDialog('你已试玩过该App，立即邀请好友来完成任务，你将获得高额分成！', '立即邀请', function(){ location.href = '/ios/share.html';}, '取消');
                }else {
                    var msg = $.trim($(this).find('.down-message').html());
                    msg && showTips(msg);
                }
                return;
            }
            if($this.hasClass('future-task')){
                showTips('任务未开始');
                return;
            }

            if($this.hasClass('friend-task')){
                location.href = '/ios/share.html';
                return;
            }

            if(currentApp && currentApp.isDoing && !$this.find('button.haveing').size()){
                showTips('请先完成当前的限时任务');
                return;
            }
            $task = $this;
            getAndShowTaskDetail($task.data('id'));
        });


        $('#detail-panel').on('click', '.launch-app', function(){ //打开应用
            showTips('正在打开应用...');
            if(!currentApp.agreement || currentApp.agreement.indexOf('NES_') >= 0 || currentApp.need_open){
                MiWSBridge.call('openApp', {info:currentApp.info});
            }else {
                openApp(currentApp.agreement);
            }
        }).on('click', '.copy-keyword', function(){ //点击复制
            MiWSBridge.call('pasteApp', {info:currentApp.keyword}, function(result){});
            if(CookieUtil.cookie('hide_cp_hint')){
                showTips('复制成功');
                setTimeout(function(){
                    openApp('https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=');
                }, 500);
            }else {
                $('#copy-dialog').dialog('show');
            }
        }).on('click', '.install-app', function(){ //直接下载
            location.href = currentApp.download_url;
        }).on('click', '.check-status', function(){ //直接下载
            checkTaskStatus(true);
        }).on('copy', '#keyword', function(){ //复制事件
            openApp('https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=');
        });

        $('#accept-dialog').on('click', '.ui-btn', function(){ //接任务
            doAcceptTask();
        })

        $('#copy-dialog').on('click', '.ui-btn-danger', function(){
            $('#copy-dialog').dialog('hide');
            openApp('https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=');
        }).on('click', '.ui-btn-cancel', function(){
            CookieUtil.cookie('hide_cp_hint', 1, 300 * 86400);
            $('#copy-dialog').dialog('hide');
            openApp('https://itunes.apple.com/WebObjects/MZStore.woa/wa/search?media=software&country=CN&mt=8&term=');
        });


        $('#welcome-dialog').on('click', 'button', function(){
            $('#welcome-dialog').dialog('hide');
            CookieUtil.cookie('hide_task_welcome', 1, 30 * 86400);
        });

	}

	function initPage(){
	    if(location.href.indexOf('#td') > 0){
            var pos = location.href.indexOf('#td');
            var tid = location.href.substring(pos + 3);
            getAndShowTaskDetail(tid, true);
        }else { //第一次显示任务列表页
            showTaskList();
            if(!CookieUtil.cookie('hide_task_welcome')){
                $('#welcome-dialog').dialog('show');
            }
            window.onpopstate = function(event){
                if(currentApp.showDetail){ //back from detail page
                    location.reload();
                }
            }
        }
	}

    function getAndShowTaskDetail(tid, firstShow){
        showLoading();
        $.ajax({
            url: '/ios/api/task/info/' + tid,
            type: 'GET',
            dataType: 'json',
            success : function(data){
                hideLoading();
                if(data.code == 0){
                    if(data.status == -2){//check and accept
                        currentApp = data;
                        checkAndAccept();
                    }else if(data.status > 0){
                        currentApp = data;
                        showTaskDetail();
                    }else{
                        showTips(data.desc);
                        firstShow && showTaskList();
                    }
                }else {
                    showDialog(data.message);
                    firstShow && showTaskList();
                }
            },
            error : function(error){
                hideLoading();
                showTips('网络错误，请稍后重试');
                firstShow && showTaskList();
            }
        });
    }

    function showTaskDetail(){
        $('header span').text(currentApp.keyword);
        $('header .right_btn').hide();
        $('body').removeClass("bg_h").addClass("bg_f");
        $('#task-list').hide();
        $('#tishi-box').hide();
        $('#detail-panel').html(getDetailHtml()).show();
        $('header .back').attr('href', 'javascript:;').off('click').on('click', function(){ //放弃任务
             if(currentApp.status == 1 ){
                 showDialog((currentApp.installed? '系统检测到你已下载应用，如果现在放弃，可能导致以后无法再次完成本任务。' : '') + '确定放弃任务吗？', '确定', function(){
                    doAbortTask();
                 }, true);
             }else {
                history.back();
             }

        });

        currentApp.showDetail = true;
        if(currentApp.status > 0){
            countDownTimer();
            setTimeout(checkTaskTimer, MiWSBridge.isOpen? 10 : 1500);
        }

        //修改浏览器历史
        if(location.href.indexOf('#td') < 0){
            history.pushState(currentApp, '任务详情', '#td' + currentApp.tid);
        }

        window.onpopstate = function(event){
            if(currentApp.showDetail){ //back from detail page
                location.reload();
            }
        }
    }

    function getDetailHtml(){
        var step = '';
        var opt = '';
        if(currentApp.require_type == 3){
            step = '<p>1、点击“下载安装”按钮，从苹果AppStore免费下载安装该应用</p><p><i>2、' + currentApp.description + '</i></p>';
            opt = '<div class="btn install-app"><a href="javascript:;">下载安装</a></div>';
        }else {
            var showCopyBtn = false;
            var miVersion = MiWSBridge.version || CookieUtil.cookie('mi_ws_version');
            if(miVersion){
                var version = parseFloat(miVersion.substring(0, 3));
                showCopyBtn = version >= 2.3;
            }
            step = '<p>1、' + (showCopyBtn? '点击“复制关键词”按钮' : '长按虚线框' ) + '，复制并自动跳转AppStore</p><p>2、在AppStore搜索页，粘贴并搜索</p>' +
                   '<p>3、找到图标对应的应用(在第<i>' + currentApp.rank + '</i>个左右)并下载安装</p>' +
                   '<p><i>4、' + currentApp.description + '</i></p>';
            opt = '<div class="icon manual-copy" style="' + (showCopyBtn ? "display:none;" : "") + '"><em>长按复制（必须）<br/>自动跳转到App Store</em><i><img src="/static/images/img/bot.png" /></i>' +
                  '		<div id="copy-area"><div id="keyword"><span id="word">' + currentApp.keyword + '</span></div></div>' +
                  '</div>';
            opt += '<div class="btn copy-keyword" style="' + (showCopyBtn ? "" : "display:none;") + '"><a href="javascript:;" >复制关键词：' + currentApp.keyword + '</a></div>';
        }
        return '<div class="head_top clearfix">' +
                   '<div class="head_top_pic fl"><img src="' + currentApp.icon + '" /></div>' +
                   '<div class="head_text fl"><h1>'+ currentApp.keyword + '</h1>' +
                   '<p><em><img width="100%" src="/static/images/task_11.png" /></em>' + currentApp.download_size +' M&nbsp;&nbsp;&nbsp;&nbsp;<em><img width="100%" src="/static/images/task_12.png" /></em><span id="remain-time">' + currentApp.remain_time + ' 分钟</span></p>' +
                   '</div>' +
                   '<div class="reward_title"><em>+</em><b>' + (currentApp.amount * rate / 100) + '</b><i>元</i></div>' +
                   '</div>' +
                   '<div class="step"><h1>任务步骤</h1>' + step + '</div>' + opt +
                   '<div class="btn launch-app" style="display:none;"><a href="javascript:;">立即打开试玩</a></div>' +
                   '<div class="btn check-status"><a href="javascript:;">检查完成状态</a></div>';
    }

    function showTaskList(){
        currentApp = {};
        $('header span').text('限时任务');
        $('header .right_btn').show();
        $('#task-list').show();
        $('#tishi-box').show();
        $('#detail-panel').hide();
        $('body').removeClass("bg_f").addClass("bg_h");
        $(".ui-dialog").dialog("hide");
        $('header .back').attr('href', 'javascript:history.back();').off('click');

        //如果有进行中的任务，则定时检测其完成状态
        if($doingTask.size()){
            currentApp = {};
            currentApp.keyword = $doingTask.find('h4').text();
            currentApp.pid = $doingTask.data('pid');
            currentApp.tid = $doingTask.data('id');
            var $btn = $doingTask.find('button.haveing');
            currentApp.info = $btn.data('info');
            currentApp.installed = $btn.data('status');
            currentApp.isDoing = true;

            setTimeout(checkTaskTimer, MiWSBridge.isOpen? 10 : 1500);
        }
    }


    function doAcceptTask(){
        if(currentApp.status != -2) return;
        showLoading('正在拼命抢任务');
        MiWSBridge.call('task', {task:currentApp.tid, info:currentApp.info, pid: currentApp.pid}, function(r){
            hideLoading();
            if(r.code != 0) {
                showDialog(r.message);
            } else {
                $(".ui-dialog").dialog("hide");
                currentApp.status = 1;

                showTaskDetail();

                if(currentApp.need_open){//可能进程名重合，需要手动打开试玩
                    showDialog('接收任务成功！请下载安装应用后回到本页面打开试玩，否则无法获得奖励。');
                }else {
                    showTips('接收任务成功，请在'+currentApp.remain_time+'分钟内完成');
                }
            }
        });
    }


    function doAbortTask(){
        showLoading();
        $.ajax({
            url: '/ios/api/task/abort/' + currentApp.tid,
            type: 'POST',
            dataType: 'json',
            success : function(data){
                hideLoading();
                if(data.code == 0){
                    showTips('放弃任务成功');
                    setTimeout(function(){history.back();}, 300);
                }else {
                    showDialog(data.message, '确定', function(){ history.back(); });
                }
            },
            error : function(error){
                hideLoading();
                showTips('网络错误，请稍后重试');
            }
        });
    }

    function countDownTimer(){
        var min = parseInt(currentApp.remain_time) || 0;
        $('#remain-time').html( min + ' 分钟');
        if(min > 0){
            setTimeout(function(){
                currentApp.remain_time = min - 1;
                countDownTimer();
            }, 60 * 1000);
        }
    }

    function checkTaskTimer(){
        checkTaskStatus();
        if(currentApp.finished){
            return;
        }else if(currentApp.installed){
            setTimeout(checkTaskTimer, 20 * 1000);
        }else {
            setTimeout(checkTaskTimer, 5000);
        }
    }


    function checkTaskStatus(forceCheck){
        if(currentApp.finished) {
            clearInterval(checkTaskInterval);
            if(!forceCheck) return;
        }
        //先检查是否已经安装，安装过才请求后台
        if(currentApp.installed){
            $('#detail-panel .install-app').hide();
            $('#detail-panel .manual-copy').hide();
            $('#detail-panel .copy-keyword').hide();
            $('#detail-panel .launch-app').show();

            forceCheck && showLoading();
            $.getJSON('/ios/api/task/check/' + currentApp.tid, function(data){
                forceCheck && hideLoading();
                if(data.status == 0){
                    currentApp.finished = 1;
                    showDialog('恭喜，任务“' + currentApp.keyword + '”已完成', '确定', function(){
                        currentApp.showDetail? history.back() : location.reload();
                    });
                }else if(forceCheck){
                    showDialog(data.desc);
                }
            });
        }else {
            MiWSBridge.call('checkApp', {info: currentApp.info, pid: currentApp.pid}, function(ret){
                if(ret.installed) {
                    $doingTask.find('.down-message').text('等待试用完成');
                    currentApp.installed = true;
                    checkTaskStatus();
                }else if(forceCheck){
                    showDialog('任务进行中');
                }
            });
        }
    }

    function checkAndAccept() {
        //检查应用是否已安装，如果已安装，则直接提示用户已经安装，不弹出接任务的提示框
        MiWSBridge.call('checkApp', {info: currentApp.info, pid: currentApp.pid}, function(ret){
            if(ret.installed) {
                showDialog('您已安装过该App');
                if($task){
                    var rightInfo = '<button class="ui-btn ui-btn-finish">+ '+ (currentApp.amount * rate / 100) + '元</button><span class="down-message">已安装</span>';
                    $task.addClass('ui-li-finish').find('.ui-list-right').html(rightInfo);
                    $task.find('.markup').replaceWith('<div class="ui-list-right">' + rightInfo + '</div>');
                }
            }else{
                //showAcceptDialog();
                //showTaskDetail();

                //直接接任务
                doAcceptTask();
            }
        });
    }

    function showAcceptDialog(){
        $('#accept-dialog .reward-amount').text(currentApp.amount * rate / 100);
        if(currentApp.paid){
            $('#accept-dialog h2').text('【需付费下载】').show();
        }else if(currentApp.require_type == 1){
            $('#accept-dialog h2').text('【请按第三步要求完成任务】').show();
        }
        $('#accept-dialog').dialog('show');
    }

    initPage();
    bindEvent();
});
