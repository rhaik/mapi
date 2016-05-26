package cn.wshz;

import com.cyhd.service.util.VersionUtil;

public class TestVersionUtil {

	public static void main(String[] args) {
		boolean flag = VersionUtil.isRequiredTargetVsersion("1.6", "1.6.0");
		System.out.println(flag);
	}
}
