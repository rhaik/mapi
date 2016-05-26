package com.cyhd;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.cyhd.service.util.CollectionUtil;

public class TestCollectionUtil {

	public static void main(String[] args) {
		test2();
	}

	private static void test2() {
		Calendar c = Calendar.getInstance();
		System.out.println(c.getTime().toLocaleString());
		c.add(Calendar.MINUTE, -4);
		System.out.println(c.getTime().toLocaleString());
	}

	private static void test() {
		List<String> list = Arrays.asList(new String[]{"a","b","c","d"});
		String join = CollectionUtil.join(list, "");
		System.out.println(join);
	}
}
