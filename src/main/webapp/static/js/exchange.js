//金币、积分兑换页面
$(function(){
    var name = $('#coin-wrap').data('name');
    $('#exchange-btn').click(function(){
        if(score_config.balance <= 0){
            return $.myAlert('您还没有' + name);
        }
        var amount = parseInt($('#amount-input').val());
        if(!amount || amount <= 0){
            return $.myAlert('请输入正确的兑换数量');
        }
        if(amount > score_config.balance){
            return $.myAlert('您最多可兑换' + score_config.balance + name);
        }
        doExchange(amount);
    });

    function doExchange(amount){
        $.myPost(score_config.api, {source: score_config.source, exchange_num: amount}, function(result){
            if(result.code){
                $.myAlert(result.message);
            }else {
                $.myAlert('兑换成功', function(){ location.reload();});
            }
        });
    }
});