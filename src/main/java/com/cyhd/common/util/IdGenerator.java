package com.cyhd.common.util;

public class IdGenerator {

	/**
	 * Timestamp[5 Bytes] can be used for 34 years
	 * 2000-01-01 00:00:00.000
	 */
	private final static long base_timestamp = 946656000000l;
	
	/**
	 * two byte number to indicate which server the message generated
	 */
	private final int serverId;
	
	private long lastTimestamp;
	private byte currIncrement;
	
	public IdGenerator(int serverId) {
		this.serverId = serverId;
	}
	
	/**
	 * Id = ServerId[2 Byte] + Timestamp[5 Bytes] + Increment[1 Byte]
	 * @return makes a sense: next id always greater than last.
	 */
	public synchronized long getNextId() {
		
		long t = System.currentTimeMillis() - base_timestamp;
		
		if(t < lastTimestamp) {
			t = lastTimestamp;
		}
		
		if(t == lastTimestamp) {
			if((currIncrement & 0xff) == 0xff)
				t++;
			else 
				currIncrement++;
		}
		
		if(t > lastTimestamp) {
			lastTimestamp = t;
			currIncrement = 0;
		}
		
		return(
				 (0x000000000000ffffl & serverId) << 48 
				|(0x000000ffffffffffl & t) << 8 
				|(0x00000000000000ffl & currIncrement)
			);
	}
}
