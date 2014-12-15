package com.example.fred360;

import java.io.UnsupportedEncodingException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class b
{
  DefaultHttpClient a;
  HttpContext b;
  private String e;
  HttpResponse c = null;
  HttpPost d = null;

  public static String a(String paramString1, String paramString2)
  {
    paramString1 = paramString1.getBytes();
    (localObject = new byte[16])[0] = paramString1[0];
    localObject[1] = paramString1[1];
    localObject[2] = paramString1[2];
    localObject[3] = paramString1[3];
    localObject[4] = paramString1[4];
    localObject[5] = paramString1[5];
    localObject[6] = paramString1[6];
    localObject[7] = paramString1[7];
    localObject[8] = paramString1[8];
    localObject[9] = paramString1[9];
    localObject[10] = paramString1[10];
    localObject[11] = paramString1[11];
    localObject[12] = paramString1[12];
    localObject[13] = paramString1[13];
    localObject[14] = paramString1[14];
    localObject[15] = paramString1[15];
    paramString1 = (String)localObject;
    paramString2 = a(paramString2);
    Object localObject = paramString2;
    paramString1 = paramString1;
    paramString1 = new SecretKeySpec(paramString1, "AES");
    (paramString2 = Cipher.getInstance("AES/ECB/PKCS5Padding")).init(2, paramString1);
    paramString1 = paramString1 = paramString2.doFinal((byte[])localObject);
    return new String(paramString1);
  }

  public static byte[] a(String paramString)
  {
    int i;
    byte[] arrayOfByte = new byte[i = paramString.length() / 2];
    for (int j = 0; j < i; j++)
      arrayOfByte[j] = Integer.valueOf(paramString.substring(2 * j, 2 * j + 2), 16).byteValue();
    return arrayOfByte;
  }

  public static String a(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null)
      return "";
    StringBuffer localStringBuffer1 = new StringBuffer(2 * paramArrayOfByte.length);
    for (int i = 0; i < paramArrayOfByte.length; i++)
    {
      int j = paramArrayOfByte[i];
      StringBuffer localStringBuffer2;
      (localStringBuffer2 = localStringBuffer1).append("0123456789ABCDEF".charAt(j >> 4 & 0xF)).append("0123456789ABCDEF".charAt(j & 0xF));
    }
    return localStringBuffer1.toString();
  }

  public b()
  {
    BasicHttpParams localBasicHttpParams;
    HttpConnectionParams.setConnectionTimeout(localBasicHttpParams = new BasicHttpParams(), 10000);
    HttpConnectionParams.setSoTimeout(localBasicHttpParams, 10000);
    this.a = new DefaultHttpClient(localBasicHttpParams);
    this.b = new BasicHttpContext();
  }

  public String b(String paramString1, String paramString2)
  {
    return a(paramString1, paramString2, null);
  }

  public String a(String paramString1, String paramString2, String paramString3)
  {
    this.e = null;
    this.a.getParams().setParameter("http.protocol.cookie-policy", "rfc2109");
    this.d = new HttpPost(paramString1);
    this.c = null;
    paramString1 = null;
    this.d.setHeader("User-Agent", "SET YOUR USER AGENT STRING HERE");
    this.d.setHeader("Accept", "text/html,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5");
    this.d.setHeader("Content-Type", "application/x-www-form-urlencoded");
    try
    {
      paramString1 = new StringEntity(paramString2, "UTF-8");
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
    }
    this.d.setEntity(paramString1);
    try
    {
      this.c = this.a.execute(this.d, this.b);
      if (this.c != null)
        this.e = EntityUtils.toString(this.c.getEntity());
    }
    catch (Exception localException)
    {
    }
    return this.e;
  }
}