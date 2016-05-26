package com.cyhd;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.impl.doubao.ThirdShishicaiService;
import junit.framework.Assert;
import junit.framework.TestCase;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by hy on 1/6/16.
 */
public class TestShishicaiService extends TestCase{


    public void testGetLatestPeriod(){
        Date date = DateUtil.getTodayStartDate();

        for (int i = 0 ; i < 1440; i += 1){
            Date duobaoTime = DateUtil.getAddDate(date, Calendar.MINUTE, i);

            int period = ThirdShishicaiService.getShishicaiPeriod(duobaoTime) + 1;

            Date openTime = ThirdShishicaiService.getShishicaiTime(period);

            Assert.assertTrue(openTime.after(duobaoTime));

//            System.out.println(DateUtil.format(duobaoTime) + "\t" + period + "\t" + DateUtil.format(openTime));
        }
    }

    public void testGetShishicaiDate(){
        for (int i = 1; i <= 120; ++i ){
            int pd = NumberUtil.safeParseInt(String.format("%s%03d", DateUtil.format(new Date(), "yyMMdd"), i));
            Date openDate = ThirdShishicaiService.getShishicaiTime(pd);
            //System.out.println(pd + ":" + openDate);
        }
    }

    public static void main(String[] args){
        Date date = DateUtil.getTodayStartDate();

        for (int i = 0 ; i < 1440; i += 1){
            Date duobaoTime = DateUtil.getAddDate(date, Calendar.MINUTE, i);

            int period = ThirdShishicaiService.getShishicaiPeriod(duobaoTime) + 1;

            Date openTime = ThirdShishicaiService.getShishicaiTime(period);

            System.out.println(DateUtil.format(duobaoTime) + "\t" + period + "\t" + DateUtil.format(openTime));
        }
    }
}
