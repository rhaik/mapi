package com.cyhd.common.util.serializer;

public abstract interface ByteSerializer
{
  public abstract byte[] serialize(Object paramObject);

  public abstract Object deserialize(byte[] paramArrayOfByte);
}

