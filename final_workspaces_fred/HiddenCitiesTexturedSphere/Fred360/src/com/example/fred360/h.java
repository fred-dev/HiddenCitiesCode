package com.example.fred360;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.ContentResolver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

final class h extends AsyncTask
{
  private Activity a;
  private g b;
  private String c;
  private DownloadManager.Request d;
  private DownloadManager e;
  private String f = null;

  private h(g paramg)
  {
  }

  private static String b(String paramString)
  {
    try
    {
      MessageDigest localMessageDigest;
      paramString = (localMessageDigest = MessageDigest.getInstance("MD5")).digest(paramString.getBytes());
      for (paramString = (paramString = new BigInteger(1, paramString)).toString(16); paramString.length() < 32; paramString = "0" + paramString);
      return paramString;
    }
    catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
    {
      Log.e("MD5", localNoSuchAlgorithmException.getLocalizedMessage());
    }
    return null;
  }

  private Long a(String[] paramArrayOfString)
  {
    int i = paramArrayOfString[0].lastIndexOf('/');
    this.c = paramArrayOfString[0].substring(i + 1);
    Object localObject1 = this.a.getPackageManager();
    Object localObject3 = this.a.getPackageName();
    try
    {
      localObject3 = (localObject1 = ((PackageManager)localObject1).getPackageInfo((String)localObject3, 0)).applicationInfo.dataDir;
    }
    catch (PackageManager.NameNotFoundException localNameNotFoundException1)
    {
      Log.w("yourtag", "Error Package name not found ", localNameNotFoundException1);
    }
    Object localObject2 = localObject3 + "/" + b(paramArrayOfString[0]) + ".mp4";
    if ((localObject3 = new File((String)localObject2)).exists())
    {
      this.f = ("file://" + (String)localObject2);
      return Long.valueOf(0L);
    }
    this.d = new DownloadManager.Request(Uri.parse(paramArrayOfString[0]));
    this.d.setDescription("Download Panframe 360¡ movie to device");
    this.d.setTitle(this.a.getTitle() + " movie download");
    if (Environment.getExternalStorageState() == "mounted")
      this.d.setDestinationInExternalFilesDir(this.a, Environment.DIRECTORY_MOVIES, this.c + "-temp");
    this.e = ((DownloadManager)this.a.getSystemService("download"));
    long l1 = this.e.enqueue(this.d);
    long l2 = 0L;
    long l3;
    long l4;
    do
    {
      if (this.b.b())
      {
        this.e.remove(new long[] { l1 });
        cancel(true);
        break;
      }
      (localObject2 = new DownloadManager.Query()).setFilterById(new long[] { l1 });
      (localObject2 = this.e.query((DownloadManager.Query)localObject2)).moveToFirst();
      l3 = ((Cursor)localObject2).getLong(((Cursor)localObject2).getColumnIndex("bytes_so_far"));
      l4 = ((Cursor)localObject2).getLong(((Cursor)localObject2).getColumnIndex("total_size"));
      this.f = ((Cursor)localObject2).getString(((Cursor)localObject2).getColumnIndex("local_uri"));
      l2 = l4;
      publishProgress(new Integer[] { Integer.valueOf((int)((float)l3 / (float)l4 * 100.0D)) });
      ((Cursor)localObject2).close();
    }
    while (l3 != l4);
    if (Environment.getExternalStorageState() != "mounted")
    {
      localObject2 = this.a.getPackageManager();
      localObject3 = this.a.getPackageName();
      try
      {
        localObject3 = (localObject2 = ((PackageManager)localObject2).getPackageInfo((String)localObject3, 0)).applicationInfo.dataDir;
      }
      catch (PackageManager.NameNotFoundException localNameNotFoundException2)
      {
        Log.w("yourtag", "Error Package name not found ", localNameNotFoundException2);
      }
      String str = localObject3 + "/" + b(paramArrayOfString[0]) + ".mp4";
      paramArrayOfString = 1;
      localObject3 = null;
      Object localObject4 = this.a.getContentResolver();
      try
      {
        localObject3 = ((ContentResolver)localObject4).openInputStream(Uri.parse(this.f));
      }
      catch (FileNotFoundException localFileNotFoundException1)
      {
        paramArrayOfString = 0;
      }
      if (paramArrayOfString != 0)
      {
        paramArrayOfString = null;
        try
        {
          paramArrayOfString = new FileOutputStream(str);
        }
        catch (FileNotFoundException localFileNotFoundException2)
        {
          (localObject4 = localFileNotFoundException2).printStackTrace();
        }
        try
        {
          a((InputStream)localObject3, paramArrayOfString);
        }
        catch (IOException localIOException1)
        {
          (localObject4 = localIOException1).printStackTrace();
        }
        try
        {
          ((InputStream)localObject3).close();
          ((FileOutputStream)paramArrayOfString).getFD().sync();
          paramArrayOfString.flush();
          paramArrayOfString.close();
        }
        catch (IOException localIOException2)
        {
          (localObject4 = localIOException2).printStackTrace();
        }
      }
      this.f = ("file://" + str);
    }
    this.e.remove(new long[] { l1 });
    return Long.valueOf(l2);
  }

  private static void a(InputStream paramInputStream, OutputStream paramOutputStream)
  {
    byte[] arrayOfByte = new byte[1024];
    int i;
    while ((i = paramInputStream.read(arrayOfByte)) != -1)
      paramOutputStream.write(arrayOfByte, 0, i);
  }

  public final void a(Activity paramActivity)
  {
    this.a = paramActivity;
  }

  public final Uri a(String paramString)
  {
    if (Environment.getExternalStorageState() != "mounted")
    {
      if (this.f == null)
        return Uri.EMPTY;
      return Uri.parse(this.f);
    }
    int i = paramString.lastIndexOf('/');
    paramString = paramString.substring(i + 1);
    this.c = paramString;
    paramString = new File(this.a.getExternalFilesDir(Environment.DIRECTORY_MOVIES), paramString);
    return Uri.parse("file://" + paramString.getAbsolutePath());
  }

  public final void a(g paramg)
  {
    this.b = paramg;
  }

  public final String a()
  {
    return this.c;
  }

@Override
protected Object doInBackground(Object... params) {
	// TODO Auto-generated method stub
	return null;
}
}