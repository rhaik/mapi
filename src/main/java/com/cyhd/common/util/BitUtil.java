package com.cyhd.common.util;

public class BitUtil {

	/**
	 * 获得bit位方式的状态值
	 * @param state  复合状态值
	 * @param bitNum  二进制某位数的值，从1 开始
	 * @return
	 */
	public static boolean getBitState(int state, int bitNum) {
		int bitValue = 1 << (bitNum - 1) ;
		return bitValue == (state & bitValue) ;
	}
	
	/**
	 * 设置bit位方式的状态值
	 * @param state  复合状态值
	 * @param bitNum  二进制某位数的值，从1 开始
	 * @return
	 */
	public static int setBitState(int state, int bitNum, boolean flag) {
		int bitValue = 1 << (bitNum - 1) ;
		if(flag) 
			return state | bitValue ;
		else if(getBitState(state, bitNum))
			return state - bitValue ;
		else 
			return state ;
	}
	
	public static int setState(int state, int bit, boolean flag) {
		if(flag){
			return state | bit;
		}else{
			return state & ~bit;
		}
	}
	public static boolean isBitState(int state, int bit){
		return (state & bit) == bit;
	}
	
	public static void main(String[] args){
		int i = 0;
		System.out.println(setState(i, 0x01, true));
		//System.out.println(setState(0x01 << 1, 0x01<<3, false));
		//System.out.println(setState(16 + 4, 0x01<<4, false));
		
		System.out.println(isBitState(7, 4));
	}
}
