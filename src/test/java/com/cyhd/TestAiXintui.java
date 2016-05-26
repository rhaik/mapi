package com.cyhd;

import com.cyhd.common.util.JsonUtils;

import net.sf.json.JSONObject;

public class TestAiXintui {

	public static void main(String[] args) {
		test();
	}

	private static void test() {
		String jsonData = "{\"msgStyle\" :{\"android\": {\"click_action\":1 ,\"sound\":true,\"title\":\"This is Title\",\"unremovable\":false,\"vibrate\":true},\"basic\":{\"content\":\"This is Content\",\"is_notif\":1}},\"option\":{\"period\":86400},\"sendTo\":{\"appkey\":{\"android\":\"1245227470\"},\"token\":{\"android\":[\"4249317714557889999\",\"5190956903548595010\"]},\"userScope\":5}}";
		JSONObject json = JSONObject.fromObject(jsonData);
		org.json.JSONObject jsonObject = new org.json.JSONObject(jsonData);
		System.out.println(jsonData);
		System.out.println(JsonUtils.toCommonString(jsonData));
	}
}
