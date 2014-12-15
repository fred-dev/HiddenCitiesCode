package com.example.fred360;

import android.view.View;

public abstract interface PFView
{
  public abstract View getView();

  public abstract void displayAsset(PFAsset paramPFAsset);

  public abstract boolean supportsNavigationMode(PFNavigationMode paramPFNavigationMode);

  public abstract void setNavigationMode(PFNavigationMode paramPFNavigationMode);

  public abstract void setViewRotation(float paramFloat);

  public abstract void setFieldOfView(float paramFloat);
}