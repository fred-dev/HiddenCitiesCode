package com.example.fred360;

public final class f
{
  public float a;
  public float b;
  public float c;
  private static f d = new f();

  public f()
  {
    this.a = 0.0F;
    this.b = 0.0F;
    this.c = 0.0F;
  }

  private f(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.a = paramFloat1;
    this.b = paramFloat2;
    this.c = paramFloat3;
  }

  public final void a(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.a = paramFloat1;
    this.b = paramFloat2;
    this.c = paramFloat3;
  }

  public final void a(f paramf)
  {
    this.a = paramf.a;
    this.b = paramf.b;
    this.c = paramf.c;
  }

  public final void a(Float paramFloat)
  {
    this.a *= paramFloat.floatValue();
    this.b *= paramFloat.floatValue();
    this.c *= paramFloat.floatValue();
  }

  public final void a(float paramFloat)
  {
    float f = (float)Math.cos(paramFloat);
    paramFloat = (float)Math.sin(paramFloat);
    d.a(this.a, this.b, this.c);
    this.a = (d.a * f + d.c * paramFloat);
    this.c = (d.a * -paramFloat + d.c * f);
  }

  public final void b(float paramFloat)
  {
    float f = (float)Math.cos(paramFloat);
    paramFloat = (float)Math.sin(paramFloat);
    d.a(this.a, this.b, this.c);
    this.a = (d.a * f - d.b * paramFloat);
    this.b = (d.a * paramFloat + d.b * f);
  }

  public final String toString()
  {
    return this.a + "," + this.b + "," + this.c;
  }
}