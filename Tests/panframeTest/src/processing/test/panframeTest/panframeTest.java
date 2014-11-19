package processing.test.panframeTest;

import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList;
import java.util.Timer;
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

import com.panframe.android.lib.*;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.*;
import android.os.Bundle;


public class panframeTest extends PApplet implements PFAssetObserver {
	
	String				mPath;

	PFView				mPFView;
	PFAsset 			mPFAsset;
    PFNavigationMode 	mCurrentNavigationMode = PFNavigationMode.TOUCH;
    
    public void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);
    mPath= Environment.getExternalStorageDirectory().getAbsolutePath()+ "/hiddenCities/video/360.mp4";
    println(mPath);
    loadVideo(mPath);
    mPFAsset.play();
    }

public void setup()
{
  orientation(LANDSCAPE);
  imageMode(CENTER);
  
  background(255,0,0);
  
}

public void loadVideo(String aPath){
	mPFView = PFObjectFactory.view(this);               
    mPFAsset = PFObjectFactory.assetFromUri(this, Uri.parse(aPath), this);
    mPFView.displayAsset(mPFAsset);
    mPFView.setNavigationMode(mCurrentNavigationMode);
    ViewGroup view = (ViewGroup)getWindow().getDecorView();
    int childCount = view.getChildCount();
    println("Main View has "+ childCount + "children (excluding panframe view");
    view.removeAllViews();
    childCount = view.getChildCount();
    println("Main View has "+ childCount + "after removeAllView()");
    view.addView(mPFView.getView(), childCount);
    childCount = view.getChildCount();
    println("Main View has "+ childCount + "children (including panframe view");
//    view.bringChildToFront(view.getChildAt(0));
//    view.requestLayout();
//    view.invalidate();
}

public void draw()
{
  ellipse(mouseX, mouseY, 20,20);
  
  
  
  
}

public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
	switch (status)
	{
		case LOADED:
			Log.d("SimplePlayer", "Loaded");
			break;
		case DOWNLOADING:
			Log.d("SimplePlayer", "Downloading 360 movie: "+ mPFAsset.getDownloadProgress()+" percent complete");
			break;
		case DOWNLOADED:
			Log.d("SimplePlayer", "Downloaded to "+asset.getUrl());
			break;
		case DOWNLOADCANCELLED:
			Log.d("SimplePlayer", "Download cancelled");
			break;
		case PLAYING:
			Log.d("SimplePlayer", "Playing");
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			break;
		case PAUSED:
			Log.d("SimplePlayer", "Paused");

			break;
		case STOPPED:
			Log.d("SimplePlayer", "Stopped");
			
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case COMPLETE:
			Log.d("SimplePlayer", "Complete");

	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case ERROR:
			Log.d("SimplePlayer", "Error");
			break;
	}
}



public void mousePressed()
{
	if (mPFAsset.getStatus() == PFAssetStatus.PLAYING)
		mPFAsset.pause();
	else
		mPFAsset.play();
}

public void keyPressed()
{
  
}

  public int sketchWidth() { return displayWidth; }
  public int sketchHeight() { return displayHeight; }
}
