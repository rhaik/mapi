package com.cyhd.common.util.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class JavaSerialize implements ByteSerializer {
	public byte[] serialize(Object obj) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ObjectOutputStream osStream = new ObjectOutputStream(os);
			osStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return os.toByteArray();
	}

	public Object deserialize(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data);

		Object obj = null;
		try {
			ObjectInputStream oi = new ObjectInputStream(is);
			obj = oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return obj;
	}
}
