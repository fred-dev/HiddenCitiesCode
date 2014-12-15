package com.example.fred360;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import junit.framework.Assert;

public final class e
  implements GLSurfaceView.Renderer
{
  private i a;
  private int b;
  private int c;
  private int d;
  private int e;
  private float[] f = new float[16];
  private float[] g = new float[16];
  private float[] h = new float[16];
  private float[] i = new float[16];
  private float[] j = new float[16];
  private c k;
  private FloatBuffer l;
  private c m;
  private FloatBuffer n;
  private int o = 0;
  private int p;
  private int q;
  private int r;
  private int s;
  private int t;
  private float u;
  private float v;
  private String w;
  private String x;
  private String y;
  private boolean z;
  private int A;
  private static String B = "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";

  public e(i parami)
  {
    this.a = parami;
    Matrix.setIdentityM(this.j, 0);
    Matrix.setIdentityM(this.j, 0);
    this.k = new c(20.0F, 60, 60);
    this.l = this.k.b();
    this.m = new c(256.0F);
    this.n = this.m.b();
    this.u = 75.0F;
    this.v = 0.0F;
    this.A = this.a.hashCode();
  }

  public final void onDrawFrame(GL10 paramGL10)
  {
    this.a.a(true);
    GLES20.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
    GLES20.glClear(16640);
    GLES20.glViewport(0, 0, this.t, this.s);
    paramGL10 = this.t / this.s;
    float f1 = this.u;
    if ((this.o == 90) || (this.o == 180))
    {
      paramGL10 = this.s / this.t;
      f1 *= 1.333333F;
    }
    a(this.h, (float)Math.toRadians(f1), -paramGL10, 1.0F, 20.0F);
    GLES20.glUseProgram(this.b);
    this.a.c();
    this.g = ((float[])this.f.clone());
    Matrix.multiplyMM(this.i, 0, this.g, 0, this.j, 0);
    Matrix.multiplyMM(this.i, 0, this.h, 0, this.i, 0);
    Matrix.setIdentityM(paramGL10 = new float[16], 0);
    Matrix.rotateM(paramGL10, 0, this.o + this.v, 0.0F, 0.0F, 1.0F);
    Matrix.multiplyMM(this.i, 0, paramGL10, 0, this.i, 0);
    Matrix.rotateM(this.i, 0, -90.0F, 0.0F, 1.0F, 0.0F);
    GLES20.glUniformMatrix4fv(this.c, 1, false, this.i, 0);
    e locale = this;
    GLES20.glCullFace(1029);
    locale.l.position(0);
    GLES20.glVertexAttribPointer(locale.e, 3, 5126, false, 20, locale.l);
    locale.l.position(3);
    GLES20.glEnableVertexAttribArray(locale.e);
    GLES20.glVertexAttribPointer(locale.d, 2, 5126, false, 20, locale.l);
    GLES20.glEnableVertexAttribArray(locale.d);
    GLES20.glDrawArrays(4, 0, locale.k.a());
    if (this.A != this.a.hashCode())
      return;
    GLES20.glUseProgram(this.r);
    paramGL10 = (float[])this.h.clone();
    Matrix.orthoM(this.h, 0, -140.0F, this.t - 140, -120.0F, this.s - 120, -1000.0F, 1000.0F);
    this.g = ((float[])this.j.clone());
    Matrix.setLookAtM(this.g, 0, 0.0F, 0.0F, -50.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F);
    Matrix.rotateM(this.g, 0, 180.0F, 0.0F, 0.0F, 1.0F);
    Matrix.multiplyMM(this.i, 0, this.g, 0, this.j, 0);
    Matrix.multiplyMM(this.i, 0, this.h, 0, this.i, 0);
    GLES20.glUniformMatrix4fv(this.c, 1, false, this.i, 0);
    locale = this;
    GLES20.glCullFace(1028);
    locale.n.position(0);
    GLES20.glVertexAttribPointer(locale.e, 3, 5126, false, 20, locale.n);
    locale.n.position(3);
    GLES20.glEnableVertexAttribArray(locale.e);
    GLES20.glVertexAttribPointer(locale.d, 2, 5126, false, 20, locale.n);
    GLES20.glEnableVertexAttribArray(locale.d);
    GLES20.glUniform1i(locale.q, 0);
    GLES20.glActiveTexture(33984);
    GLES20.glBindTexture(3553, locale.p);
    GLES20.glEnable(3042);
    GLES20.glBlendFunc(1, 771);
    GLES20.glDrawArrays(4, 0, locale.m.a());
    this.h = ((float[])paramGL10.clone());
    GLES20.glUseProgram(this.b);
    this.a.a(false);
  }

  public final void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2)
  {
    this.t = paramInt1;
    this.s = paramInt2;
    GLES20.glViewport(0, 0, paramInt1, paramInt2);
    paramGL10 = this.t / this.s;
    if ((this.o == 90) || (this.o == 180))
      paramGL10 = this.s / this.t;
    a(this.h, (float)Math.toRadians(this.u), -paramGL10, 1.0F, 20.0F);
  }

  private static void a(float[] paramArrayOfFloat, float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    paramFloat1 = (float)Math.tan(0.5D * (3.141592653589793D - paramFloat1));
    paramArrayOfFloat[0] = (paramFloat1 / paramFloat2);
    paramArrayOfFloat[1] = 0.0F;
    paramArrayOfFloat[2] = 0.0F;
    paramArrayOfFloat[3] = 0.0F;
    paramArrayOfFloat[4] = 0.0F;
    paramArrayOfFloat[5] = paramFloat1;
    paramArrayOfFloat[6] = 0.0F;
    paramArrayOfFloat[7] = 0.0F;
    paramArrayOfFloat[8] = 0.0F;
    paramArrayOfFloat[9] = 0.0F;
    paramArrayOfFloat[10] = -1.052632F;
    paramArrayOfFloat[11] = -1.0F;
    paramArrayOfFloat[12] = 0.0F;
    paramArrayOfFloat[13] = 0.0F;
    paramArrayOfFloat[14] = -1.052632F;
    paramArrayOfFloat[15] = 0.0F;
  }

  private static String a(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest;
      (localMessageDigest = MessageDigest.getInstance("MD5")).reset();
      localMessageDigest.update(paramString.getBytes());
      int i1 = (paramString = localMessageDigest.digest()).length;
      StringBuilder localStringBuilder = new StringBuilder(i1 << 1);
      for (int i2 = 0; i2 < i1; i2++)
      {
        localStringBuilder.append(Character.forDigit((paramString[i2] & 0xF0) >> 4, 16));
        localStringBuilder.append(Character.forDigit(paramString[i2] & 0xF, 16));
      }
      return localStringBuilder.toString();
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      (paramString = localNoSuchAlgorithmException).printStackTrace();
    }
    return null;
  }

  private int b()
  {
    try
    {
      Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "6528692DAEBD68932111D926EAB4627E9DA299878A473BC5B1858638F0316D7B1435B2DC89A418C51B5EAF522B4B38FC69E58531CE374F172062FC1F7FEB2781C042F2537ED2F190DF4CAE7C93E52A2E"));
      Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "3279A48EBBDD380CA9B528EA73677313F563B72E8DB89C162B0B98E35A7E45208CCBA887822F235B172DCB24F1F499C0"));
      Object localObject1 = Settings.Secure.getString(this.a.b().getContentResolver(), "android_id");
      localObject1 = localObject1 + "-" + Build.SERIAL;
      Object localObject2 = (localObject2 = this.a.b().getPackageManager().getPackageInfo(this.a.b().getPackageName(), 0)).versionName;
      this.z = false;
      Object localObject3 = this.a.b().getClass().getDeclaredMethods();
      Object localObject5;
      String str;
      for (int i1 = 0; i1 < localObject3.length; i1++)
      {
        new String();
        localObject5 = null;
        if ((localObject5 = (str = localObject3[i1]).getName()).equals(b.a(B, "E195908BD01351219EFC5B37D21DB08F")))
          this.z = true;
      }
      if (this.z)
        return -1;
      if ((i1 = this.a.getRootView().getContext().getPackageManager().checkPermission("android.permission.INTERNET", this.a.getRootView().getContext().getPackageName())) != 0)
      {
        Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "CBE3EDD8C15C9EE2BECFF1FA786DD91B93F692B6C8209D13B9E95B10564DF7E9275F4555D64104F1675444E79AF7D74B"));
        Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "8CC2388889835306E5631D09B010CB3CAA394CFDDE79157320F1CAADE979B64F7E6192C3C4F6ED47C00CED3AB96B73A91F7A28018905B7A815B14781C96AD79D823F42FED21E934E43759A70B1CC70329CE5BE4B4F8AC8032DFC1D5FC26AC571"));
        Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "621FDAAEFF63D7EA64C967AAFB7A21265A632A52C9156161D4EB4C4E8796FAEACF3937DC263E5070D25C282F22A17B52"));
        this.w = "";
        this.x = "";
        this.y = "";
        return -1;
      }
      this.w = b.a(B, "F2883342EB6D9842FB5B930EB3B72DF41CF05AE6D1CCD474C7DCCBC573A30714654D4176C587F751B59E75ED9944BC3AB4B8ABBC2AD1D7106555E4323E7DE4B5C860F4290B7CA714BDF6A98F281B31BC115B9974BC8C498039FC17A52FFCC0BFB8F7E9196F2D8E45C518658B900045315B1D74D1D03AECDEC01F7305336CB2B096C3C05B5A448A7F7F2D6E800A0FE69089206B1761447F92B30DC7CF21C01732DDCD27D07343B17F1BB207D4EE97887B2947E82AD646E5AF877A8289E5E7224C4B2288737371E63780C9DA1106B8BA14");
      this.x = b.a(B, "53D9712EB7DF9673A4D59A6F832E27003CF5ECF3A2AFAFAFD114A1DC2780B302240DC0ED953FDAE3903A7DA448EC8C1E47C7E161D2B6EFAEE39F9707886E74B502FDC1C2FC6AAD62B200E75CF8A6CD34B87133D4B7069915F2D7DFA89ACEA16A6E25F425DDEDBCC1CC9A6C407BD7EDEF4D8D0B2B9FCD862137F9B512E339EFEAFD9E96D4D87AE41BBA68F14F718D021AED968BAC3DE21664080AFBC661F0F5318985B44844F1B40F0C72F2426656C858C8B9676373A2C91B64E36B3C459594C5");
      this.y = b.a(B, "5CFC268957405CBA79502F4430A071043A6D81A043DA74C7E4168DB86F4BC685D2549DBA0014916D10BA63CED5E4BF6FF94199C1D869613A6F48071493399B6D9FCCD42BF0745D2F558A948E196BD8BBF701C792B92281A0F9A1C8BDBDD2847B8128BF57DCFE8C3B16EA42CA7DBDA2D4CB553BBF56B6CBDC3DC17438385E5ECEF356ABC909279807772C0346B36DDDDB");
      try
      {
        localObject5 = new b();
        new String();
        str = "nid=" + (String)localObject1 + "&bid=" + this.a.getRootView().getContext().getPackageName() + "&vid=" + (String)localObject2 + "&pln=Android&plv=" + Build.VERSION.SDK_INT + "&man=" + Build.MANUFACTURER + "&mdl=" + Build.MODEL + "&pdt=1.9.0.20130513";
        localObject2 = (localObject2 = b.a(B, "C29C6692DE13511542BB4796D8D668AEC042F2537ED2F190DF4CAE7C93E52A2E")).getBytes(b.a(B, "7FFD6EF83B567C623FFFF022F1475A0E"));
        localObject3 = new byte[16];
        System.arraycopy(localObject2, 0, localObject3, 0, Math.min(localObject2.length, 16));
        localObject2 = str.getBytes(b.a(B, "7FFD6EF83B567C623FFFF022F1475A0E"));
        localObject3 = new SecretKeySpec((byte[])localObject3, b.a(B, "6F14F8EDFD92880F30096E2F80C36F3A"));
        Object localObject4;
        (localObject4 = Cipher.getInstance(b.a(B, "EED2DDAAA75CBD83DCD991E399BD34BAAAAC2712D9501091898E4A6EFE0A22B9"))).init(1, (Key)localObject3);
        localObject2 = b.a(localObject2 = ((Cipher)localObject4).doFinal((byte[])localObject2));
        str = "d=" + (String)localObject2;
        if ((localObject2 = ((b)localObject5).b(b.a(B, "971DD4A9A32DB2B83EF4DB5392C1F6C49EC8B9BFFC8BAEB3086A67A564996EAFCE96123025C150E39478A5170B3F82F3"), str)).equals(new String("0")))
        {
          Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "407A996C9459BE5301FC55FE11FA40F3B0E266F02B75F350242438AA407E152A"));
          Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "621FDAAEFF63D7EA64C967AAFB7A21265A632A52C9156161D4EB4C4E8796FAEACF3937DC263E5070D25C282F22A17B52"));
        }
        else
        {
          localObject4 = b.a((String)localObject2);
          localObject2 = (localObject2 = a(localObject2 = this.a.getRootView().getContext().getPackageName() + (String)localObject1)).getBytes(b.a(B, "B8994E223227C79CEB7749DEDF52441E"));
          localObject3 = new byte[16];
          System.arraycopy(localObject2, 0, localObject3, 0, Math.min(localObject2.length, 16));
          localObject1 = localObject4;
          localObject3 = new SecretKeySpec((byte[])localObject3, b.a(B, "6F14F8EDFD92880F30096E2F80C36F3A"));
          (localObject4 = Cipher.getInstance(b.a(B, "EED2DDAAA75CBD83DCD991E399BD34BAAAAC2712D9501091898E4A6EFE0A22B9"))).init(2, (Key)localObject3);
          if ((localObject2 = ((Cipher)localObject4).doFinal((byte[])localObject1))[0] != 64)
          {
            Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "407A996C9459BE5301FC55FE11FA40F3B0E266F02B75F350242438AA407E152A"));
            Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "621FDAAEFF63D7EA64C967AAFB7A21265A632A52C9156161D4EB4C4E8796FAEACF3937DC263E5070D25C282F22A17B52"));
          }
          else
          {
            this.A = localObject2.hashCode();
            localObject1 = new String((byte[])localObject2, b.a(B, "B8994E223227C79CEB7749DEDF52441E"));
            Log.i(b.a(B, "47F41BED20ADA247325CD27903D87A52"), b.a(B, "2FD697BE15BFC883E2EB71146693672F") + ((String)localObject1).substring(17));
          }
        }
      }
      catch (Exception localException1)
      {
        this.A = 0;
      }
    }
    catch (Exception localException2)
    {
      this.A = 0;
      return -1;
    }
    return 0;
  }

  public final void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig)
  {
    Assert.assertTrue(GLES20.glGetError() == 0);
    GLES20.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);
    Assert.assertTrue(GLES20.glGetError() == 0);
    GLES20.glDisable(2929);
    Assert.assertTrue(GLES20.glGetError() == 0);
    GLES20.glFrontFace(2305);
    Assert.assertTrue(GLES20.glGetError() == 0);
    GLES20.glEnable(2884);
    Assert.assertTrue(GLES20.glGetError() == 0);
    GLES20.glCullFace(1029);
    Assert.assertTrue(GLES20.glGetError() == 0);
    b();
    Assert.assertTrue(GLES20.glGetError() == 0);
    if (this.w == null)
    {
      this.w = "";
      this.x = "";
      this.y = "";
    }
    this.b = a(this.w, this.x);
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.e = GLES20.glGetAttribLocation(this.b, "a_position");
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.c = GLES20.glGetUniformLocation(this.b, "u_ModelViewMatrix");
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.d = GLES20.glGetAttribLocation(this.b, "a_texCoords");
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.r = a(this.w, this.y);
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.q = GLES20.glGetAttribLocation(this.r, "u_logoId");
    Assert.assertTrue(GLES20.glGetError() == 0);
    paramGL10 = paramGL10;
    paramEGLConfig = localObject = BitmapFactory.decodeByteArray(paramEGLConfig = Base64.decode(paramEGLConfig = B, 0), 0, paramEGLConfig.length);
    Assert.assertTrue(GLES20.glGetError() == 0);
    Object localObject = new int[1];
    paramGL10.glGenTextures(1, (int[])localObject, 0);
    Assert.assertTrue(GLES20.glGetError() == 0);
    paramGL10.glBindTexture(3553, localObject[0]);
    Assert.assertTrue(GLES20.glGetError() == 0);
    paramGL10.glTexParameterf(3553, 10241, 9729.0F);
    Assert.assertTrue(GLES20.glGetError() == 0);
    paramGL10.glTexParameterf(3553, 10240, 9729.0F);
    Assert.assertTrue(GLES20.glGetError() == 0);
    paramGL10.glTexParameterf(3553, 10242, 10497.0F);
    Assert.assertTrue(GLES20.glGetError() == 0);
    paramGL10.glTexParameterf(3553, 10243, 10497.0F);
    Assert.assertTrue(GLES20.glGetError() == 0);
    GLUtils.texImage2D(3553, 0, paramEGLConfig, 0);
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.p = localObject[0];
  }

  private static int a(String paramString, int paramInt)
  {
    int[] arrayOfInt = new int[1];
    GLES20.glShaderSource(paramInt = GLES20.glCreateShader(paramInt), paramString);
    GLES20.glCompileShader(paramInt);
    GLES20.glGetShaderiv(paramInt, 35713, arrayOfInt, 0);
    if (arrayOfInt[0] == 0)
    {
      Log.d("Panframe", "Load Shader Failed\n" + GLES20.glGetShaderInfoLog(paramInt));
      return 0;
    }
    return paramInt;
  }

  private int a(String paramString1, String paramString2)
  {
    int[] arrayOfInt = new int[1];
    if ((paramString1 = a(paramString1, 35633)) == 0)
    {
      Log.d("Panframe", "Load Vertex Shader Program Failed");
      return 0;
    }
    if ((paramString2 = a(paramString2, 35632)) == 0)
    {
      Log.d("Panframe", "Load Fragment Shader Program Failed");
      return 0;
    }
    int i1;
    GLES20.glAttachShader(i1 = GLES20.glCreateProgram(), paramString1);
    GLES20.glAttachShader(i1, paramString2);
    GLES20.glLinkProgram(i1);
    GLES20.glGetProgramiv(i1, 35714, arrayOfInt, 0);
    if (arrayOfInt[0] <= 0)
      return 0;
    GLES20.glDeleteShader(paramString1);
    GLES20.glDeleteShader(paramString2);
    return i1;
  }

  public final void a()
  {
  }

  public final void a(float[] paramArrayOfFloat)
  {
    this.f = ((float[])paramArrayOfFloat.clone());
  }

  public final void a(int paramInt1, int paramInt2)
  {
    this.o = paramInt1;
  }

  public final void a(float paramFloat)
  {
    this.u = paramFloat;
  }

  public final void b(float paramFloat)
  {
    this.v = paramFloat;
  }
}