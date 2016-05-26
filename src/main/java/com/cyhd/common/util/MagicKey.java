package com.cyhd.common.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;


public class MagicKey {

	/**
	 * Timestamp[5 Bytes] can be used for 34 years
	 * 2000-01-01 00:00:00.000
	 */
	private final static long base_timestamp = 946656000000l;
	
	// 3 days
	public static final long DAY_INTERVAL = 24 * 3600 * 1000;
	public static final long DEFAULT_INTERVAL = 1 * DAY_INTERVAL;
	
	// Make the length be a multiple of 4
	protected static final int LENGTH = 16;
	
	private static final int DEFAULT_MAGIC = 21993961;
	
	private long interval;
	private int magic;
	// hidden values
	private int hide;
	private int extra; 	//
	// Key expiration time
	private Date expiration;
	
	public MagicKey(){
		this(DEFAULT_INTERVAL, DEFAULT_MAGIC);
	}
	
	public MagicKey(int magic){
		this(DEFAULT_INTERVAL, magic);
	}
	
	public MagicKey(long interval, int magic){
		this.interval = interval;
		this.magic = magic;
	}
	
	public MagicKey(int uid, int extra, Date time){
		this.hide = uid;
		this.extra = extra;
		this.expiration = time;
	}
	
	public int getHideValue() {
		return hide;
	}
	
	public int getExtra(){
		return extra;
	}
	
	public Date getExpiration() {
		return expiration;
	}

	/**
	 * Whether this key is valid (is not forged and yet expired)? 
	 * 
	 * @return
	 */
	public boolean isValid() {
		return expiration != null && (new Date().before(expiration));
	}

	public String encode(int hv) {
		return encode(hv, 0);
	}
	/**
	 * Generate an automatically login key for specified user. We encode the user Id and 
	 * expiration time in the key string.
	 * 
	 * @param hideValue
	 * @return
	 */
	public String encode(int hideValue, int extraValue) {
		// To make the key more mess, we add an arbitrary integer to perturb the data.
		// At the end of the data, we add the magic number too, but the magic number is not
		// encoded in the final key. It's used only when creating the message digest.
		
		// Make sure this byte array is of length LENGTH + 4 (with an additional magic number)
		byte[] data = null;
		try {
			Date expiration = new Date(System.currentTimeMillis() + interval);
			ByteArrayOutputStream baos = new ByteArrayOutputStream(LENGTH + 4);
			DataOutputStream dos = new DataOutputStream(baos);
			dos.writeInt(expiration.hashCode() * dos.hashCode());	// An arbitrary number to perturb the data
			dos.writeInt(hideValue);
			dos.writeInt(extraValue);
			long time = (expiration.getTime()-base_timestamp)/1000l;
			dos.writeInt((int)time);
			dos.writeInt(0);
			dos.close();
			baos.close();
			data = baos.toByteArray();
		} catch (IOException ioe) {
			// impossible
			data = new byte[LENGTH + 4];
		}
		// Add the magic number to the data. The magic number is used to create digest
		data[LENGTH] = (byte) (magic & 0x000000ff);
		data[LENGTH + 1] = (byte) ((magic & 0x0000ff00) >> 8);
		data[LENGTH + 2] = (byte) ((magic & 0x00ff0000) >> 16);
		data[LENGTH + 3] = (byte) ((magic & 0xff000000) >> 24);
		
		// MD5 hash (16 bytes)
		// Make sure the hash is of length LENGTH, truncate it if it's too long.
		byte[] hash = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			hash = md.digest(data);
			if (hash.length != LENGTH) {
				if (hash.length > LENGTH)
					hash = Arrays.copyOfRange(hash, 0, LENGTH);
				else 
					hash = Arrays.copyOf(hash, LENGTH);
			}
		} catch (NoSuchAlgorithmException nsae) {
			hash = new byte[LENGTH];
		}
		
		// Weave user id, expiration time and the arbitrary integer with the hash.
		// The magic number is not used here.
		byte[] result = new byte[LENGTH * 2];
		for (int i = 0; i < LENGTH; i++) {
			result[2 * i] = (byte) ((hash[i] & 0x03) | ((data[i] & 0x0f) << 2) | ((hash[i] & 0x0C) << 4));
			result[2 * i + 1] = (byte) ((hash[i] & 0xC0) | ((data[i] & 0xf0) >> 2) | ((hash[i] &0x30) >> 4));
		}
		
		// Convert the result byte array to a String
		char[] buf = new char[result.length * 2];
		for (int i = 0; i < result.length; i++) {
			byte b = (byte) ((result[i] & 0xf0) >> 4);
			buf[2 * i] = Character.forDigit(b, 16);
			b = (byte) (result[i] & 0x0f);
			buf[2 * i + 1] = Character.forDigit(b, 16);			
		}
		return String.valueOf(buf);
	}

	public static MagicKey decode(String key){
		return decode(key, DEFAULT_MAGIC);
	}
	
	/**
	 * Create an object of this type from the key string. If the key is valid, then the object
	 * is correctly initialized. Otherwise, this object is not initialized and its <code>isValid()</code>
	 * method returns false.
	 * 
	 * @param key
	 * @see encode()
	 */
	public static MagicKey decode(String key, int magicCode) {
		if (key == null || key.length() != LENGTH * 4)
			return null;
		
		// Convert the string to a byte array
		byte[] array = new byte[LENGTH * 2];
		for (int i = 0; i < array.length; i++) {
			int c = Character.digit(key.charAt(2 * i), 16);
			array[i] |= (c & 0x0f) << 4;
			c = Character.digit(key.charAt(2 * i + 1), 16);
			array[i] |= c & 0x0f;
		}
		
		// Extract data and hash parts from the array
		byte[] data = new byte[LENGTH + 4];		// additional 4 bytes for the magic number
		byte[] hash = new byte[LENGTH];
		for (int i = 0; i < LENGTH; i++) {
			data[i] = (byte) (((array[2 * i] & 0x3C) >> 2) | ((array[2 * i + 1] & 0x3C) << 2));
			hash[i] = (byte) (((array[2 * i] & 0xC0) >> 4) | (array[2 * i] & 0x03) | 
								(array[2 * i + 1] & 0xC0) | ((array[2 * i + 1] & 0x03) << 4));
		}
		// Add the magic number to data, to make the digest
		data[LENGTH] = (byte) (magicCode & 0x000000ff);
		data[LENGTH + 1] = (byte) ((magicCode & 0x0000ff00) >> 8);
		data[LENGTH + 2] = (byte) ((magicCode & 0x00ff0000) >> 16);
		data[LENGTH + 3] = (byte) ((magicCode & 0xff000000) >> 24);
		
		// Make the digest
		byte[] digest = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			digest = md.digest(data);
			if (digest.length != LENGTH) {
				if (digest.length > LENGTH)
					digest = Arrays.copyOfRange(digest, 0, LENGTH);
				else 
					digest = Arrays.copyOf(digest, LENGTH);
			}
		} catch (NoSuchAlgorithmException nsae) {
			digest = new byte[LENGTH];
		}
		// Verify the hash
		for (int i = 0; i < LENGTH; i++)
			if (hash[i] != digest[i])
				return null;
		
		// Verification succeeds, initialize the user id and expiration time
		try {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
			dis.readInt();	// discard the first 4 bytes
			int uid = dis.readInt();
			int extra = dis.readInt();
			int t = dis.readInt();
			Date time = new Date(t*1000l+base_timestamp);
			dis.close();
			MagicKey mk = new MagicKey(uid, extra, time);
			return mk;
		} catch (IOException ioe) {
			return null;
		}
	}
	
	public static final MagicKey TEST = new MagicKey();
	
	public static final class Test {
		public static void main(String args[])throws Exception {

			MagicKey magic = MagicKey.TEST;
			int uid = 91763258;//2409748839L  348759L
//			String id = "d681246adda7cede42c2f7c1bca77199";
//			if (id != null){
//				uid = UidEncodeUtils.decode(id);
//			}
			System.out.println(Character.digit('a',10));
			System.out.println("uid = " + uid);
			String key = magic.encode(uid, 1);
			//System.out.println("key length = " + key.length());
			//Thread.sleep(1);
			System.out.println("key = " + key);
			MagicKey url = MagicKey.decode(key);
			System.out.println(url.getExtra());
//			MagicKey url = MagicKey.decode(key);
//			if (url != null){
//				System.out.println("isValid: " + url.isValid());
//				System.out.println("User id: " + url.getHideValue());
//				System.out.println("Extra: " + url.getExtra());
//				System.out.println("Expiration: " + url.getExpiration());
//			}else {
//				System.out.println("isValid: null");
//			}
		}
	}
	
}
