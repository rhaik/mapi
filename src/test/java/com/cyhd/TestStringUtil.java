package com.cyhd;

import com.cyhd.common.util.StringUtil;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by hy on 1/19/16.
 */
public class TestStringUtil extends TestCase {

    public void testChineseString(){
        Assert.assertTrue(StringUtil.isAllChinese("中国"));
        Assert.assertFalse(StringUtil.isAllChinese("中国abc123"));

        Assert.assertTrue(StringUtil.isContainsChinese("中国"));
        Assert.assertTrue(StringUtil.isContainsChinese("8938中国8928398abc123"));
        Assert.assertFalse(StringUtil.isContainsChinese("abkuowu*(*()&*()889829038"));
    }
}
