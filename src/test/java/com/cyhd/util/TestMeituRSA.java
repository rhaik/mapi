package com.cyhd.util;

import com.cyhd.service.util.RSAUtil;
import com.cyhd.service.util.RequestSignUtil;
import junit.framework.Assert;
import junit.framework.TestCase;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hy on 9/8/15.
 */
public class TestMeituRSA extends TestCase{


    public void testTestEncryt(){
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI0iL6WVMz+VIZfgYLKOUPFZwOiVCZ+Oxc6PjgWz2k4REAFNqylL/+7SnJYt0MF2s8XL7t+W7/qKhFH3zAIL0szwdXvEDB04/AVU6y2X6qx2IZ2J4C4t6n8b5N3g8W5/JNtmm6zHh6kw/YM5RR9vR3SBZV5r4ggbhRugaTJYA34BAgMBAAECgYAvkzKUkrLv4Amu9Mgj6K2IbkUFIhhYXPx5IRMzAOm6Hy5SAiiMhz4C96QpS9BvJuB68L/ZRzwmLMNmDi4LEolAYrgikCFON364/kZZF1BnLnktagMd/3mT23kaXeC52pnXFx5dDrVu8Bok3AyFlf1UBVo1T4Lb2tjxdnmIP6eUAQJBAN12u3URfDHYY+bd0zKiQeD04RZKsiq/nA38koPSB0EIEjR7FRJtX/4cQXwQfAZ+zMoQ3zkw7CsMaE19eMYoRTECQQCjJINOhFvcq5MAwz6E4JssF2prJEsskH9W/iaqQyS8YLfBqVPtzuYshGdCLCMUNGY8kK1AaPJvU52XfIrJhtHRAkBdshEHK3me4Q0LLMhgwLMciJ3+P2X3ng9Y/4XBTYeSJOcG2xgELtARA0VVRugiG11rFA5M9PzGDb7HIhGJzJnRAkBaMrPOU2ueo9XQ1CHawXvJcuDJf/V4HCPravThqeHDrQ2rqvzWPFASSNn2QgTbBOWJksvXEq8HUgmNWbQ6G6ohAkEAxHU3QIcAkCK74LBT7nr4fawJn6T7bFgc02zXEfX3GiU8d7t6XKUpEyRqSi4jN8gGcXhzyLoFIhvJTybg+0XP9Q==";

        String data = "Hello World";


        byte[] encrypted = RSAUtil.encrypt(privateKey, data.getBytes());
        Assert.assertNotNull(encrypted);
        Assert.assertTrue(encrypted.length > 0);

        //System.out.println(new String(encrypted));
    }

    public void testOnlineEncryt(){
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ1UsZOzxmmSG8mu9huPa7KJdfpJUeTEfqfkTqKIdre1d8XbiaGgu0vDJWfZAzb38luB1QKn2w17GFYsGTB2KJedJc5DmUycB23eIsbq0bwF4lFNHWnHY+kx9ElfEHVBhUuBB02cDv8ugF2Owcj01q0lKXWALuQ4tosSPTVzV4tJAgMBAAECgYEAlSKSTatM+geo1Y5G9isGcK/CqKTVvx/P24AcVg87UbrvtRr8pLxCrndmPsFEPdfc4Yb5jtHkYCv+Drkwi3KF2GgE669ODeKsdmIuP8IgndXFJ0HsHvfo4WX4r3QREMo9sJT+wr3OoGh7oUAUbaZNKv9kJzEtxWtycZaTp04OIoECQQDtyDPDJsh6cJ/ouUjCF+ixIhyTdLFZif7MyuU9ZOkn7a2xY8HH4lD7QBLHA3AC9crsqWaU5isSO3jiNeqewoeRAkEAqWKKoaNF+SuXikJKlcMgXm9TZoxNrqs3fzvnhqyeiRFUsBJZUS7vBmMgduCOSD5+9qPbnf5RlpGREIkwwTucOQJBAN6MFDZAZD6EjoXodHqEmhq/THOOMjcVes79zxRpD/d48qomLcYAwb6GN4zgYMPEfIqH+iS+T+2ekANYZyDz/KECQDckTEs4kvP92/R3hj5g6m2zwivVKwjc1lFGWCYAlg+7I526K3eBVvD2XkA09DzSk5SJXVp+y2K/+sCuWL/fPwkCQA0gFuYeP3VlmTYbL8hOjWVv1Xfh231gwYeACQDnMNeq6BvbfFKc4hY8oDn95tO7NExtyyWugDWgkOvpM3IkRRw=";

        String data = "Hello World";

        byte[] encrypted = RSAUtil.encrypt(privateKey, data.getBytes());
        Assert.assertNotNull(encrypted);
        Assert.assertTrue(encrypted.length > 0);

        //System.out.println(new String(encrypted));
    }

    public void testRequestSign(){
        String privateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI0iL6WVMz+VIZfgYLKOUPFZwOiVCZ+Oxc6PjgWz2k4REAFNqylL/+7SnJYt0MF2s8XL7t+W7/qKhFH3zAIL0szwdXvEDB04/AVU6y2X6qx2IZ2J4C4t6n8b5N3g8W5/JNtmm6zHh6kw/YM5RR9vR3SBZV5r4ggbhRugaTJYA34BAgMBAAECgYAvkzKUkrLv4Amu9Mgj6K2IbkUFIhhYXPx5IRMzAOm6Hy5SAiiMhz4C96QpS9BvJuB68L/ZRzwmLMNmDi4LEolAYrgikCFON364/kZZF1BnLnktagMd/3mT23kaXeC52pnXFx5dDrVu8Bok3AyFlf1UBVo1T4Lb2tjxdnmIP6eUAQJBAN12u3URfDHYY+bd0zKiQeD04RZKsiq/nA38koPSB0EIEjR7FRJtX/4cQXwQfAZ+zMoQ3zkw7CsMaE19eMYoRTECQQCjJINOhFvcq5MAwz6E4JssF2prJEsskH9W/iaqQyS8YLfBqVPtzuYshGdCLCMUNGY8kK1AaPJvU52XfIrJhtHRAkBdshEHK3me4Q0LLMhgwLMciJ3+P2X3ng9Y/4XBTYeSJOcG2xgELtARA0VVRugiG11rFA5M9PzGDb7HIhGJzJnRAkBaMrPOU2ueo9XQ1CHawXvJcuDJf/V4HCPravThqeHDrQ2rqvzWPFASSNn2QgTbBOWJksvXEq8HUgmNWbQ6G6ohAkEAxHU3QIcAkCK74LBT7nr4fawJn6T7bFgc02zXEfX3GiU8d7t6XKUpEyRqSi4jN8gGcXhzyLoFIhvJTybg+0XP9Q==";

        HashMap<String, String> infoMap = new HashMap<String, String>();
        infoMap.put("idfa", "A8930814-6EAE-43BE-B928-02105712671C");
        infoMap.put("client_ip", "127.0.0.1");
        infoMap.put("user_id", "" + 87564311);
        infoMap.put("task_id", "adjfaoieuofjasdlkf");
        infoMap.put("app_id", "809810984");

        String customeInfo = JSONObject.fromObject(infoMap).toString();

        //请求参数
        Map<String, String> params = new HashMap<String,String>();
        params.put("appkey", "adfjaksdfjaods");
        params.put("channel", "meitu");
        params.put("click_id", "");
        params.put("user_id", "");
        params.put("page_id", "");
        params.put("pos_id", "");
        params.put("custom_info", customeInfo);


        String query = RequestSignUtil.getSortedRequestString(params);
        //System.out.println(query);


        String sign = RequestSignUtil.signRequestUsingRSA(query, privateKey);

        //System.out.println(sign);
        Assert.assertNotNull(sign);
        Assert.assertTrue(sign.length() > 0);
    }
}
