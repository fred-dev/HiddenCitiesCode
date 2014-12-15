package com.example.fred360;


public abstract interface PFAssetObserver
{
  public abstract void onStatusMessage(PFAsset paramPFAsset, PFAssetStatus paramPFAssetStatus);
}
