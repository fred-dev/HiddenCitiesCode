package com.example.fred360;


public abstract interface PFAsset
{
  public abstract void play();

  public abstract void stop();

  public abstract void pause();

  public abstract float getPlaybackTime();

  public abstract void setPLaybackTime(float paramFloat);

  public abstract float getDuration();

  public abstract String getUrl();

  public abstract PFAssetStatus getStatus();

  public abstract int getDownloadProgress();
}