package com.example.fred360;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import com.example.fred360.PFAsset;
import com.example.fred360.PFNavigationMode;
import com.example.fred360.PFView;

public final class i extends GLSurfaceView
  implements SensorEventListener, PFView, a
{
  private e a;
  private g b;
  private Activity c;
  private SensorManager d;
  private Sensor e;
  private int f;
  private final float[] g = new float[4];
  private double h;
  private float[] i = new float[16];
  private float[] j = new float[16];
  private float[] k = new float[16];
  private boolean l = false;
  private float[] m = new float[16];
  private PFNavigationMode n;
  private float o;
  private float p;
  private float q;
  private float r;
  private float s;
  private long t = 0L;
  private boolean u = false;

  public i(Activity paramActivity)
  {
    super(paramActivity);
    this.c = paramActivity;
    paramActivity = this;
    this.n = PFNavigationMode.MOTION;
    paramActivity.b = null;
    paramActivity.p = 0.0F;
    paramActivity.q = 0.0F;
    paramActivity.r = 0.0F;
    paramActivity.s = 0.0F;
    paramActivity.setEGLContextClientVersion(2);
    paramActivity.a = new e(paramActivity);
    paramActivity.setRenderer(paramActivity.a);
    paramActivity.f = 0;
    paramActivity.c.getWindowManager().getDefaultDisplay().getDisplayId();
    switch (paramActivity.c.getWindowManager().getDefaultDisplay().getRotation())
    {
    case 1:
      paramActivity.f = 90;
      break;
    case 2:
      paramActivity.f = 180;
      break;
    case 3:
      paramActivity.f = 270;
    }
    float[] arrayOfFloat;
    Matrix.setIdentityM(arrayOfFloat = new float[16], 0);
    Matrix.rotateM(arrayOfFloat, 0, 90.0F, 0.0F, 0.0F, 1.0F);
    Matrix.rotateM(arrayOfFloat, 0, -paramActivity.s, 1.0F, 0.0F, 0.0F);
    Matrix.rotateM(arrayOfFloat, 0, paramActivity.r + 90.0F, 0.0F, 1.0F, 0.0F);
    paramActivity.a.a(arrayOfFloat);
    paramActivity.setRenderMode(0);
    paramActivity.requestFocus();
    paramActivity.setFocusableInTouchMode(true);
    paramActivity.d = ((SensorManager)paramActivity.getContext().getSystemService("sensor"));
    paramActivity.d.getSensorList(-1);
    paramActivity.e = paramActivity.d.getDefaultSensor(11);
    if (paramActivity.e == null)
    {
      paramActivity.n = PFNavigationMode.TOUCH;
      return;
    }
    paramActivity.d.registerListener(paramActivity, paramActivity.e, 0);
  }

  public final Activity b()
  {
    return this.c;
  }

  public final void displayAsset(PFAsset paramPFAsset)
  {
    this.b = ((g)paramPFAsset);
    this.b.a(this);
  }

  public final void onAccuracyChanged(Sensor paramSensor, int paramInt)
  {
    Log.d(paramSensor.getName(), String.valueOf(paramInt));
  }

  public final boolean supportsNavigationMode(PFNavigationMode paramPFNavigationMode)
  {
    if (paramPFNavigationMode == PFNavigationMode.TOUCH)
      return true;
    return (paramPFNavigationMode == PFNavigationMode.MOTION) && (this.e != null);
  }

  public final void requestRender()
  {
    if (this.u)
      return;
    this.u = true;
    long l1;
    if ((l1 = System.currentTimeMillis()) - this.t > 33L)
    {
      super.requestRender();
      this.t = l1;
    }
    this.u = false;
  }

  public final void onSensorChanged(SensorEvent paramSensorEvent)
  {
    if (this.n == PFNavigationMode.TOUCH)
      return;
    if (paramSensorEvent.accuracy == 0)
      return;
    switch (paramSensorEvent.sensor.getType())
    {
    case 11:
      paramSensorEvent = paramSensorEvent.values;
      float[] arrayOfFloat1 = new float[16];
      paramSensorEvent[1] = (-paramSensorEvent[1]);
      paramSensorEvent[2] = (-paramSensorEvent[2]);
      SensorManager.getRotationMatrixFromVector(arrayOfFloat1, paramSensorEvent);
      Matrix.rotateM(arrayOfFloat1, 0, 90.0F, 1.0F, 0.0F, 0.0F);
      this.a.a(arrayOfFloat1);
      requestRender();
      return;
    case 4:
      if (this.h != 0.0D)
      {
        double d1 = (paramSensorEvent.timestamp - this.h) * 9.999999717180685E-10D;
        float f1 = paramSensorEvent.values[0];
        float f2 = -paramSensorEvent.values[1];
        float f5 = -paramSensorEvent.values[2];
        float f6;
        if ((f6 = (float)Math.sqrt(f1 * f1 + f2 * f2 + f5 * f5)) > 0.1F)
        {
          f1 /= f6;
          f2 /= f6;
          f5 /= f6;
        }
        float f4 = (float)Math.sin(f3 = (float)(f6 * d1 / 2.0D));
        float f3 = (float)Math.cos(f3);
        this.g[0] = (f4 * f1);
        this.g[1] = (f4 * f2);
        this.g[2] = (f4 * f5);
        this.g[3] = f3;
      }
      this.h = paramSensorEvent.timestamp;
      float[] arrayOfFloat2;
      SensorManager.getRotationMatrixFromVector(arrayOfFloat2 = new float[16], this.g);
      this.i = ((float[])arrayOfFloat2.clone());
      if (!this.l)
      {
        this.j = ((float[])this.i.clone());
        this.l = true;
      }
      else
      {
        this.k = ((float[])this.j.clone());
        Matrix.multiplyMM(this.j, 0, this.i, 0, this.k, 0);
      }
      this.m = ((float[])this.j.clone());
      Matrix.rotateM(this.m, 0, 90.0F, 1.0F, 0.0F, 0.0F);
      this.a.a(this.m);
    }
  }

  public final void a()
  {
    this.a.a();
    i locali = this;
    super.requestRender();
  }

  public final void c()
  {
    if (this.b != null)
      this.b.c();
  }

  public final View getView()
  {
    return this;
  }

  public final void setNavigationMode(PFNavigationMode paramPFNavigationMode)
  {
    if (this.e == null)
    {
      this.n = PFNavigationMode.TOUCH;
      this.a.a(0, this.c.getResources().getConfiguration().orientation);
      return;
    }
    this.n = paramPFNavigationMode;
    if (this.n == PFNavigationMode.TOUCH)
    {
      this.a.a(0, this.c.getResources().getConfiguration().orientation);
      Matrix.setIdentityM(paramPFNavigationMode = new float[16], 0);
      Matrix.rotateM(paramPFNavigationMode, 0, -this.s, 1.0F, 0.0F, 0.0F);
      Matrix.rotateM(paramPFNavigationMode, 0, this.r + 90.0F, 0.0F, 1.0F, 0.0F);
      this.a.a(paramPFNavigationMode);
      return;
    }
    this.a.a(this.f, this.c.getResources().getConfiguration().orientation);
  }

  public final void setFieldOfView(float paramFloat)
  {
    this.o = paramFloat;
    this.a.a(this.o);
  }

  public final void setViewRotation(float paramFloat)
  {
    this.a.b(paramFloat);
  }

  public final boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.n == PFNavigationMode.MOTION)
      return true;
    float f1 = paramMotionEvent.getX();
    float f2 = paramMotionEvent.getY();
    switch (paramMotionEvent.getAction())
    {
    case 2:
      paramMotionEvent = f1 - this.p;
      float f3 = f2 - this.q;
      this.r += paramMotionEvent * 0.1F;
      this.s += f3 * 0.1F;
      Matrix.setIdentityM(paramMotionEvent = new float[16], 0);
      Matrix.rotateM(paramMotionEvent, 0, -this.s, 1.0F, 0.0F, 0.0F);
      Matrix.rotateM(paramMotionEvent, 0, this.r + 90.0F, 0.0F, 1.0F, 0.0F);
      this.a.a(paramMotionEvent);
      requestRender();
    }
    try
    {
      Thread.sleep(33L);
    }
    catch (InterruptedException localInterruptedException)
    {
      (paramMotionEvent = localInterruptedException).printStackTrace();
    }
    this.p = f1;
    this.q = f2;
    return true;
  }

  public final void a(boolean paramBoolean)
  {
  }
}