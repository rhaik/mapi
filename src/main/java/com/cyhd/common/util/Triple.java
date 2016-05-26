package com.cyhd.common.util;

public class Triple<A, B, C> extends Pair<A, B> {
	
	private C third;
	
	public Triple(A first, B second, C third) {
		super(first, second);
		this.third = third;
	}
	
	public C getThird() {
		return third;
	}
	
	@Override
	public String toString(){
		return new StringBuilder("{first=").append(first).append(", second=").append(second).append(", third=").append(third).append("}").toString();
	}

}
