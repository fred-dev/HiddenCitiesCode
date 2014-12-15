package com.example.fred360;

import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import com.example.fred360.PFAsset;
import com.example.fred360.PFAssetObserver;
import com.example.fred360.PFAssetStatus;
import java.io.File;
import java.io.FileInputStream;
import junit.framework.Assert;

public final class g
  implements SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener, PFAsset
{
  private Context a;
  private Uri b;
  private String c;
  private int[] d;
  private SurfaceTexture e;
  private Surface f;
  private MediaPlayer g;
  private a h;
  private boolean i;
  private boolean j;
  private int k = 0;
  private PFAssetObserver l = null;
  private PFAssetStatus m;
  private Integer n;
  private boolean o;
  private h p;
  private boolean q = false;

  public g(Context paramContext, Uri paramUri)
  {
    this.a = paramContext;
    this.b = paramUri;
    this.h = null;
    this.i = false;
    this.j = false;
    this.m = PFAssetStatus.ERROR;
  }

  public g(Activity paramActivity, String paramString)
  {
    this.a = paramActivity;
    this.h = null;
    this.i = false;
    this.j = false;
    this.m = PFAssetStatus.DOWNLOADING;
    this.n = Integer.valueOf(0);
    this.o = false;
    this.c = paramString;
    this.p = new h(this, (byte)0);
    this.p.a(paramActivity);
    this.p.a(this);
    this.b = this.p.a(paramString);
    if (this.b != Uri.EMPTY)
    {
      if (!(paramActivity = new File(paramActivity.getExternalFilesDir(Environment.DIRECTORY_MOVIES), this.p.a())).exists())
        this.p.execute(new String[] { paramString });
    }
    else
      this.p.execute(new String[] { paramString });
  }

  public final int getDownloadProgress()
  {
    return this.n.intValue();
  }

  public final void a()
  {
    this.n = Integer.valueOf(100);
    this.b = this.p.a(this.c);
    this.q = true;
    a(PFAssetStatus.DOWNLOADED);
    a(PFAssetStatus.LOADED);
  }

  public final boolean b()
  {
    return this.o;
  }

  public final void a(Integer paramInteger)
  {
    this.n = paramInteger;
    a(PFAssetStatus.DOWNLOADING);
  }

  private void a(PFAssetStatus paramPFAssetStatus)
  {
    this.m = paramPFAssetStatus;
    if (this.l != null)
      this.l.onStatusMessage(this, paramPFAssetStatus);
  }

  public final void a(a parama)
  {
    this.h = parama;
  }

  public final void play()
  {
    if (!this.i)
    {
      this.j = true;
      return;
    }
    this.j = false;
    if (this.k == 2)
    {
      pause();
      a(PFAssetStatus.PLAYING);
      this.h.a();
      return;
    }
    if (this.k != 0)
      return;
    try
    {
      this.g.reset();
      if (this.q)
        this.g.setDataSource(new FileInputStream(new File(this.b.getPath())).getFD());
      else
        this.g.setDataSource(this.a, this.b);
      this.g.prepareAsync();
      return;
    }
    catch (Exception localException)
    {
      Log.e("Panframe", "Error playing asset: " + localException.getMessage(), localException);
      a(PFAssetStatus.ERROR);
    }
  }

  public final void pause()
  {
    try
    {
      switch (this.k)
      {
      case 1:
        this.g.pause();
        a(PFAssetStatus.PAUSED);
        this.k = 2;
        return;
      case 2:
        this.g.start();
        a(PFAssetStatus.PLAYING);
        this.h.a();
        this.k = 1;
      }
      return;
    }
    catch (Exception localException)
    {
      Log.e("Panframe", "Error pauss asset: " + localException.getMessage(), localException);
      a(PFAssetStatus.ERROR);
    }
  }

  public final void stop()
  {
    if (this.m == PFAssetStatus.DOWNLOADING)
    {
      g localg = this;
      this.o = true;
      localg.a(PFAssetStatus.DOWNLOADCANCELLED);
      localg.k = 4;
      return;
    }
    if (this.k == 0)
      return;
    try
    {
      this.k = 0;
      this.g.stop();
      a(PFAssetStatus.STOPPED);
      this.g.release();
      return;
    }
    catch (Exception localException)
    {
      Log.e("Panframe", "Error stopping asset: " + localException.getMessage(), localException);
      a(PFAssetStatus.ERROR);
    }
  }

  public final void onFrameAvailable(SurfaceTexture paramSurfaceTexture)
  {
    this.h.a();
  }

  public final void onPrepared(MediaPlayer paramMediaPlayer)
  {
    this.g.start();
    a(PFAssetStatus.PLAYING);
    this.k = 1;
  }

  public final void c()
  {
    if (!this.i)
    {
      g localg = this;
      try
      {
        localg.d = new int[1];
        GLES20.glGenTextures(1, localg.d, 0);
        Assert.assertTrue(GLES20.glGetError() == 0);
        GLES20.glBindTexture(36197, localg.d[0]);
        Assert.assertTrue(GLES20.glGetError() == 0);
        GLES20.glTexParameterf(36197, 10241, 9729.0F);
        Assert.assertTrue(GLES20.glGetError() == 0);
        GLES20.glTexParameterf(36197, 10240, 9729.0F);
        Assert.assertTrue(GLES20.glGetError() == 0);
        GLES20.glTexParameteri(36197, 10242, 33071);
        Assert.assertTrue(GLES20.glGetError() == 0);
        GLES20.glTexParameteri(36197, 10243, 33071);
        localg.e = new SurfaceTexture(localg.d[0]);
        localg.e.setOnFrameAvailableListener(localg);
        localg.f = new Surface(localg.e);
        if (localg.g != null)
        {
          localg.g.setSurface(localg.f);
        }
        else
        {
          localg.g = new MediaPlayer();
          localg.g.setSurface(localg.f);
          localg.g.setOnCompletionListener(localg);
          localg.g.setOnErrorListener(localg);
          localg.g.setOnPreparedListener(localg);
          localg.g.setOnVideoSizeChangedListener(localg);
          localg.g.setAudioStreamType(3);
        }
      }
      catch (Exception localException)
      {
        Log.e("Panframe", "Error initializing asset: " + localException.getMessage(), localException);
        localg.a(PFAssetStatus.ERROR);
      }
      localg.i = true;
      if ((localg.m != PFAssetStatus.DOWNLOADING) && (localg.m != PFAssetStatus.DOWNLOADCANCELLED))
      {
        localg.a(PFAssetStatus.LOADED);
        if (localg.j)
          localg.play();
      }
    }
    Assert.assertTrue(GLES20.glGetError() == 0);
    this.e.updateTexImage();
    Assert.assertTrue(GLES20.glGetError() == 0);
  }

  public final void a(PFAssetObserver paramPFAssetObserver)
  {
    this.l = paramPFAssetObserver;
  }

  public final void onCompletion(MediaPlayer paramMediaPlayer)
  {
    a(PFAssetStatus.COMPLETE);
    this.k = 0;
  }

  public final float getPlaybackTime()
  {
    if (this.m == PFAssetStatus.ERROR)
      return -1.0F;
    if (this.k == 0)
      return 0.0F;
    if ((this.m == PFAssetStatus.PLAYING) || (this.m == PFAssetStatus.PAUSED))
      return this.g.getCurrentPosition() / 1000.0F;
    return 0.0F;
  }

  public final float getDuration()
  {
    if (this.m == PFAssetStatus.ERROR)
      return -1.0F;
    if ((this.m == PFAssetStatus.PLAYING) || (this.m == PFAssetStatus.PAUSED))
      return this.g.getDuration() / 1000.0F;
    return 0.0F;
  }

  public final String getUrl()
  {
    return this.b.toString();
  }

  public final PFAssetStatus getStatus()
  {
    return this.m;
  }

  public final void onVideoSizeChanged(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
  }

  public final boolean onError(MediaPlayer paramMediaPlayer, int paramInt1, int paramInt2)
  {
    return false;
  }

  public final void setPLaybackTime(float paramFloat)
  {
    if ((this.m == PFAssetStatus.PLAYING) || (this.m == PFAssetStatus.PAUSED))
      this.g.seekTo((int)(paramFloat * 1000.0F));
    if (this.m == PFAssetStatus.COMPLETE)
    {
      this.m = PFAssetStatus.PAUSED;
      this.k = 2;
    }
  }
}