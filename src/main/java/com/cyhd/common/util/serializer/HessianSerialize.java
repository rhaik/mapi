package com.cyhd.common.util.serializer;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class HessianSerialize implements ByteSerializer {
	public byte[] serialize(Object obj) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		HessianOutput ho = new HessianOutput(os);
		try {
			ho.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return os.toByteArray();
	}

	public Object deserialize(byte[] data) {
		ByteArrayInputStream is = new ByteArrayInputStream(data);
		HessianInput hi = new HessianInput(is);
		Object obj = null;
		try {
			obj = hi.readObject();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
