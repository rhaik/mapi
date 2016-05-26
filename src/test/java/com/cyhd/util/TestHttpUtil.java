package com.cyhd.util;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.Pair;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * Created by hy on 1/28/16.
 */
public class TestHttpUtil {

    public static void main(String[] args) throws Exception{
        byte[] bytes = Files.readAllBytes(Paths.get("/Users/hy/Downloads/110G31K5-6.jpg"));

        String resp = HttpUtil.upload("http://up.tietuku.com/", new HashMap(){{
            put("Token", "48414099c9cdebbcd9e1f034c1abf8a58937dcac:SV9MLWtRN01UUGRCUm1wekpxYmtYOU1mVkhJPQ==:eyJkZWFkbGluZSI6MTQ1Mzk3NzU3MSwiYWN0aW9uIjoiZ2V0IiwidWlkIjoiNTUzNjU5IiwiYWlkIjoiMTE5NTI4NCIsImZyb20iOiJmaWxlIn0=");
        }}, new HashMap(){{
            put("file", new Pair<String, byte[]>("110G31K5-6.jpg", bytes));
        }});

        System.out.println(resp);
    }
}
