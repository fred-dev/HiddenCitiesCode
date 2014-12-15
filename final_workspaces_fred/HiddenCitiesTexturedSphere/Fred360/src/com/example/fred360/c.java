package com.example.fred360;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class c extends d
{
  public c(float paramFloat)
  {
    paramFloat = new float[] { 128.0F, 128.0F, 128.0F, -128.0F, 128.0F, 128.0F, -128.0F, -128.0F, 128.0F, 128.0F, -128.0F, 128.0F, 128.0F, 128.0F, 128.0F, 128.0F, -128.0F, 128.0F, 128.0F, -128.0F, -128.0F, 128.0F, 128.0F, -128.0F, 128.0F, 128.0F, 128.0F, 128.0F, 128.0F, -128.0F, -128.0F, 128.0F, -128.0F, -128.0F, 128.0F, 128.0F, -128.0F, 128.0F, 128.0F, -128.0F, 128.0F, -128.0F, -128.0F, -128.0F, -128.0F, -128.0F, -128.0F, 128.0F, -128.0F, -128.0F, -128.0F, 128.0F, -128.0F, -128.0F, 128.0F, -128.0F, 128.0F, -128.0F, -128.0F, 128.0F, 128.0F, -128.0F, -128.0F, -128.0F, -128.0F, -128.0F, -128.0F, 128.0F, -128.0F, 128.0F, 128.0F, -128.0F };
    float[] arrayOfFloat1 = { 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 1.0F, 1.0F, 0.0F, 1.0F };
    float[] arrayOfFloat2 = { 1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F, -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, 1.0F, -1.0F, 1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, -1.0F, 1.0F, -1.0F, -1.0F, 1.0F, 1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, -1.0F, 1.0F, -1.0F, 1.0F, 1.0F, -1.0F };
    short[] arrayOfShort = { 0, 1, 2, 0, 2, 3, 4, 5, 6, 4, 6, 7, 8, 9, 10, 8, 10, 11, 12, 13, 14, 12, 14, 15, 16, 17, 18, 16, 18, 19, 20, 21, 22, 20, 22, 23 };
    float[] arrayOfFloat3 = { 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 1.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F, 0.0F, 0.0F, -1.0F };
    this.a = ByteBuffer.allocateDirect(paramFloat.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.a.put(paramFloat).position(0);
    this.b = ByteBuffer.allocateDirect(arrayOfFloat1.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.b.put(arrayOfFloat1).position(0);
    this.c = ByteBuffer.allocateDirect(arrayOfFloat2.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.c.put(arrayOfFloat2).position(0);
    this.d = ByteBuffer.allocateDirect(arrayOfShort.length << 1).order(ByteOrder.nativeOrder()).asShortBuffer();
    this.d.put(arrayOfShort).position(0);
    this.e = ByteBuffer.allocateDirect(arrayOfFloat3.length << 2).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.e.put(arrayOfFloat3);
    this.f = arrayOfShort.length;
  }

  public c(float paramFloat, int paramInt1, int paramInt2)
  {
    this.f = 21600;
    this.a = ByteBuffer.allocateDirect(129600).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.e = ByteBuffer.allocateDirect(129600).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.b = ByteBuffer.allocateDirect(86400).order(ByteOrder.nativeOrder()).asFloatBuffer();
    this.d = ByteBuffer.allocateDirect(this.f << 1).order(ByteOrder.nativeOrder()).asShortBuffer();
    paramInt2 = new f();
    f localf1 = new f();
    f localf2 = new f();
    int k = 0;
    int m = 0;
    int n = 0;
    for (paramFloat = 0; paramFloat <= 60; paramFloat++)
    {
      float f1;
      float f2 = (f1 = paramFloat / 60.0F) * 3.141593F;
      paramInt2.a(0.0F, 1.0F, 0.0F);
      paramInt2.b(f2);
      for (paramInt1 = 0; paramInt1 <= 60; paramInt1++)
      {
        float f3;
        float f4 = (f3 = paramInt1 / 60.0F) * 6.283186F;
        localf1.a(paramInt2);
        localf1.a(f4);
        localf2.a(localf1);
        localf2.a(Float.valueOf(20.0F));
        this.a.put(k++, localf2.a);
        this.a.put(k++, localf2.b);
        this.a.put(k++, localf2.c);
        this.e.put(m++, localf1.a);
        this.e.put(m++, localf1.b);
        this.e.put(m++, localf1.c);
        this.b.put(n++, f3);
        this.b.put(n++, f1);
      }
    }
    int i1 = 0;
    for (paramFloat = 0; paramFloat < 60; paramFloat++)
    {
      int i2 = paramFloat * 61;
      for (paramInt1 = 0; paramInt1 < 60; paramInt1++)
      {
        int i3 = i2 + paramInt1;
        paramInt2 = i2 + paramInt1 + 1;
        int i = i2 + (paramInt1 + 1 + 61);
        int j = i2 + (paramInt1 + 61);
        this.d.put(i1++, (short)i3);
        this.d.put(i1++, (short)i);
        this.d.put(i1++, (short)paramInt2);
        this.d.put(i1++, (short)i3);
        this.d.put(i1++, (short)j);
        this.d.put(i1++, (short)i);
      }
    }
  }
}