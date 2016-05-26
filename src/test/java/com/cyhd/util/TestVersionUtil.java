package com.cyhd.util;

import com.cyhd.service.util.VersionUtil;
import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by hy on 9/18/15.
 */
public class TestVersionUtil extends TestCase {

    public void testIOSVersion(){
        Assert.assertEquals(9.0, VersionUtil.getIOSVersion("iPhone OS9.0"), 0.0001);

        Assert.assertEquals(0, VersionUtil.getIOSVersion("iPhone OS"), 0.0001);

        Assert.assertEquals(8.2, VersionUtil.getIOSVersion("iPhone OS8.2"), 0.001);
    }
}
