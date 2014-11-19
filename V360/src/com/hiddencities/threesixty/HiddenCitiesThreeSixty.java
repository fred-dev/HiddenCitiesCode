package com.hiddencities.threesixty;

import java.util.Timer;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.SeekBar;

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
	String[] mPaths;
	AudioPlayManager[] mPlayManagers;
	boolean setter;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		
	
		_frameContainer = (ViewGroup) findViewById(R.id.framecontainer);
		_frameContainer.setBackgroundColor(0xFF000000);
		
		loadVideo("storage/sdcard0/hiddenCities/video/360.mp4");

		mPaths = new String[4];
		mPaths[0] = "storage/sdcard0/hiddenCities/audio/360ch1.wav";
		mPaths[1] = "storage/sdcard0/hiddenCities/audio/360ch2.wav";
		mPaths[2] = "storage/sdcard0/hiddenCities/audio/360ch3.wav";
		mPaths[3] = "storage/sdcard0/hiddenCities/audio/360ch4.wav";

		mPlayManagers = new AudioPlayManager[mPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {
			mPlayManagers[i] = new AudioPlayManager(mPaths[i], 64); // buffersize																// = 64kb
			mPlayManagers[i].setIsLooping(true);
			//mPlayManagers[i].setVolume(0);
			mPlayManagers[i].play();
		}
		
		showControls(false);
		_pfasset.play();
		
	}

	public void showControls(boolean bShow) {

		if (_pfview != null) {
			if (!_pfview.supportsNavigationMode(PFNavigationMode.MOTION))
				Log.d("SimplePlayer", "Not supported nav");
		}
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

		mPlayManagers = new AudioPlayManager[mPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i].getIsPlaying()) {
				mPlayManagers[i].pause();
			}

		}
	}

	public void onProgressChanged(SeekBar seekbar, int progress,
			boolean fromUser) {
		
	}

	public void onStartTrackingTouch(SeekBar seekbar) {
		_updateThumb = false;
	}

	public void onStopTrackingTouch(SeekBar seekbar) {

		_updateThumb = true;
	}

	public void onStartCommand(Intent intent, int flags, int startId) {
		_pfasset.play();
	}

		
//		if (_Rotation > 0 && _Rotation < 90) {
//			_calculatedRotation = _Rotation;
//			mPlayManagers[0].setVolume(_calculatedRotation / 90);
//			mPlayManagers[3].setVolume((90 - _calculatedRotation) / 90);
//			
//		}
//		
//		if (_Rotation > 90 && _Rotation < 180) {
//			_calculatedRotation = _Rotation -90;
//			mPlayManagers[1].setVolume(_calculatedRotation / 90);
//			mPlayManagers[0].setVolume((90 - _calculatedRotation) / 90);
//		}
//		
//		if (_Rotation > 180 && _Rotation < 270) {
//			_calculatedRotation = _Rotation-180;
//			mPlayManagers[2].setVolume(_calculatedRotation / 90);
//			mPlayManagers[1].setVolume((90 - _calculatedRotation) / 90);
//		}
//		
//		if (_Rotation > 270 && _Rotation < 360) {
//			_calculatedRotation = _Rotation -270;
//			mPlayManagers[3].setVolume(_calculatedRotation / 90);
//			mPlayManagers[2].setVolume((90 - _calculatedRotation) / 90);
//		}

}
