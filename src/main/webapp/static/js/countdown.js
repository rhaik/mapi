(function($){
    $.fn.countDown = function(dt, cb){
        var date = dt, callback = cb;
        if (typeof dt == 'function') {
            date = '';
            callback = dt;
        }
        this.each(function(){
            var $this = $(this);
            var o = {
                hm: $this.find(".hm"),
                sec: $this.find(".sec"),
                mini: $this.find(".mini"),
                hour: $this.find(".hour"),
                day: $this.find(".day"),
                month:$this.find(".month"),
                year: $this.find(".year")
            };
            var timeID;
            var falg = true;
            var f = {
                haomiao: function(n){
                    if(n < 10)return "00" + n.toString();
                    if(n < 100)return "0" + n.toString();
                    return n.toString();
                },
                zero: function(n){
                    var _n = parseInt(n, 10);//解析字符串,返回整数
                    if(_n > 0){
                        if(_n <= 9){
                            _n = "0" + _n
                        }
                        return String(_n);
                    }else{
                        return "00";
                    }
                },
                dv: function(){
                    var _d = $this.data("end") || date;
                    var now = new Date(),
                        endDate = new Date(_d);
                    //现在将来秒差值
                    var dur = (endDate - now.getTime()) / 1000 , mss = endDate - now.getTime() ,pms = {
                        hm: 0,
                        sec: 0,
                        mini: 0,
                        hour: 0,
                        day: 0,
                        month: 0,
                        year: 0
                    };
                    if(mss > 0){
                        pms.hm = mss % 1000;
                        pms.sec = dur % 60;
                        pms.mini = Math.floor((dur / 60)) > 0? (Math.floor((dur / 60)) % 60) : 0;
                        pms.hour = Math.floor((dur / 3600)) > 0? (Math.floor((dur / 3600)) % 24) : 0;
                        pms.day = Math.floor((dur / 86400)) > 0? (Math.floor((dur / 86400)) % 30) : 0;
                        //月份，以实际平均每月秒数计算
                        pms.month = Math.floor((dur / 2629744)) > 0? (Math.floor((dur / 2629744)) % 12) : 0;
                        //年份，按按回归年365天5时48分46秒算
                        pms.year = Math.floor((dur / 31556926)) > 0? ((dur / 31556926)) : 0;
                    }else{
                        pms.year=pms.month=pms.day=pms.hour=pms.mini=pms.sec=pms.hm = 0;

                        clearTimeout(timeID);
                        if(falg && typeof callback=="function") {
                            falg = false;
                            callback();
                        }
                        return false;
                    }
                    return pms;
                },
                display: function(){
                    var pms = f.dv();
                    if(pms){
                        if (o.year.size()){
                             o.year.html(f.zero(pms.year));
                        }else {
                            pms.month += 12 * pms.year;
                        }

                        if(o.month.size()){
                            o.month.html(f.zero(pms.month));
                        }else {
                            pms.day += 30 * pms.month;
                        }

                        if(o.day.size()){
                            o.day.html(f.zero(pms.day));
                        }else {
                            pms.hour += 24 * pms.day;
                        }

                        if(o.hour.size()){
                            o.hour.html(f.zero(pms.hour));
                        }else {
                            pms.mini += 60 * pms.hour;
                        }

                        if (o.mini.size()){
                            o.mini.html(f.zero(pms.mini));
                        }else {
                            pms.sec += 60 * pms.mini;
                        }

                        if(o.sec.size()){
                            o.sec.html(f.zero(pms.sec));
                        }else {
                            pms.hm += 1000 * pms.sec;
                        }

                        o.hm.html(f.haomiao(pms.hm));
                        timeID = setTimeout(f.display, 1);
                    }
                }
            };
            f.display();
        });
    }
})(window.$ || window.jQuery || window.Zepto);