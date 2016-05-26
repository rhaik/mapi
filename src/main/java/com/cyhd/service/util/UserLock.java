package com.cyhd.service.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserLock {

	private static int length = 1024;
	
	private static Lock[] lock = new ReentrantLock[length];
	static{
		for(int i = 0; i < length; i++){
			lock[i] = new ReentrantLock();
		}
	}
	
	public static Lock getUserLock(int userId){
		return lock[userId % length];
	}
	
}
