package com.example.fred360;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public abstract class d
{
  protected FloatBuffer a = null;
  protected FloatBuffer b = null;
  protected FloatBuffer c = null;
  protected ShortBuffer d = null;
  protected FloatBuffer e = null;
  protected int f = 0;

  public final int a()
  {
    return this.f;
  }

  public final FloatBuffer b()
  {
    FloatBuffer localFloatBuffer = ByteBuffer.allocateDirect((this.f << 2) * 5).order(ByteOrder.nativeOrder()).asFloatBuffer();
    int i = 0;
    for (int j = 0; j < this.f; j++)
    {
      int k = this.d.get(j);
      localFloatBuffer.put(i++, this.a.get(k * 3));
      localFloatBuffer.put(i++, this.a.get(k * 3 + 1));
      localFloatBuffer.put(i++, this.a.get(k * 3 + 2));
      localFloatBuffer.put(i++, this.b.get(k << 1));
      localFloatBuffer.put(i++, this.b.get((k << 1) + 1));
    }
    return localFloatBuffer;
  }
}