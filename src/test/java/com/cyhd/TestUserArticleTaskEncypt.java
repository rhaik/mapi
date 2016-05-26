package com.cyhd;

import com.cyhd.service.impl.UserArticleTaskService;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by hy on 10/30/15.
 */
public class TestUserArticleTaskEncypt extends TestCase{

    public void testArticleIdEncypt(){
        long num = 12367865318L;
        String host = "ww.shsojob.com.cn";

        String decyptStr = UserArticleTaskService.encyptUserTaskId(host, num);

        long decypt = UserArticleTaskService.decryptUserTaskId(host, decyptStr);

        Assert.assertEquals(num, decypt);
    }

    public void testViewIdEncypt(){
        long num = 12367865318L;
        String host = "http://www.hongjie68888.cn";
        String decyptStr = UserArticleTaskService.encyptViewTaskId(host, num);

        String dehost = "www.hongjie68888.cn";
        long decypt = UserArticleTaskService.decryptViewTaskId(dehost, decyptStr);

        Assert.assertEquals(num, decypt);
    }

    public static void main(String args[]){
        long utid = 15103000009030l;
        String host = "http://www.customsuitsme.com.cn";
        System.out.println("utid:" + UserArticleTaskService.encyptUserTaskId(host, utid));
        System.out.println("viewid:" + UserArticleTaskService.encyptUserTaskId(host, utid));
    }
}
