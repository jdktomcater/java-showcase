package com.jdktomcat.showcase.aviator;

import junit.framework.TestCase;

import java.util.HashMap;
import java.util.Map;

public class ExpressionUtilTest extends TestCase {

    private Map<String,String> data = new HashMap<>();

    public void setUp() throws Exception {
        super.setUp();
        data.put("channelMerchantId","");
        data.put("","");
        data.put("","");
        data.put("","");
        data.put("","");
        data.put("","");
        data.put("","");
        data.put("","");
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    public void testExecuteWithCache() {

    }

    public void testExecuteWithoutCache() {
    }
}