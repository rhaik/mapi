package com.cyhd.common.util.job;

public interface JobHandler<T> {
	boolean handle(T t);
}
