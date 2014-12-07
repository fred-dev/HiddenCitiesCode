package com.hiddencities.threesixty;

import java.io.File;
import java.util.Timer;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.panframe.android.lib.PFAsset;
import com.panframe.android.lib.PFAssetObserver;
import com.panframe.android.lib.PFAssetStatus;
import com.panframe.android.lib.PFNavigationMode;
import com.panframe.android.lib.PFObjectFactory;
import com.panframe.android.lib.PFView;
import com.panframe.android.samples.SimplePlayer.R;



public class HiddenCitiesThreeSixty extends Activity implements PFAssetObserver {

	PFView _pfview;
	PFAsset _pfasset;
	PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;
	float _Rotation;
	float _calculatedRotation;
	float _range;
	boolean _updateThumb = true;;
	Timer _scrubberMonitorTimer;
	PFAssetObserver _observer;
	ViewGroup _frameContainer;

	boolean setter;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
	
		_frameContainer = (ViewGroup) findViewById(R.id.framecontainer);
		_frameContainer.setBackgroundColor(0xFF000000);
		File root = Environment.getExternalStorageDirectory();


		loadVideo(root + "/hiddenCities/video/360.mp4");



		_pfasset.play();
		
	}

	public void loadVideo(String filename) {

		_pfview = PFObjectFactory.view(this);
		_pfasset = PFObjectFactory.assetFromUri(this, Uri.parse(filename), this);
		_pfview.displayAsset(_pfasset);
		_pfview.setNavigationMode(_currentNavigationMode);
		_frameContainer.addView(_pfview.getView(), 0);

	}


	public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
		switch (status) {
		case LOADED:
			Log.d("SimplePlayer", "Loaded");
			break;
		case DOWNLOADING:
			Log.d("SimplePlayer","Downloading 360 movie: " + _pfasset.getDownloadProgress()+ " percent complete");
			break;
		case DOWNLOADED:
			Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
			break;
		case DOWNLOADCANCELLED:
			Log.d("SimplePlayer", "Download cancelled");
			break;
		case PLAYING:
			Log.d("SimplePlayer", "Playing");
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			Log.d("SimplePlayer", Float.toString(_pfview.getView().getRotationY()));
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

	public boolean onCreateOptionsMenu(Menu menu) {

		return true;
	}

	public void onPause() {
		super.onPause();
		if (_pfasset != null) {
			if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
				_pfasset.pause();
		}


	}





}
