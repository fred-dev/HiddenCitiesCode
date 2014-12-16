package com.hiddenCities.compassVideo;

import java.io.File;
import java.util.*;

import com.hiddenCities.R;
import com.hiddenCities.compassVideo.AudioPlayManager;
import com.hiddenCities.main.HiddenCitiesMain;

import com.panframe.android.lib.*;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.*;
import android.os.Environment;

public class HiddenCitiesCompassVideo extends Fragment implements PFAssetObserver{
	

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
	
	HiddenCitiesMain	mActivity;
	View				mView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mActivity = (HiddenCitiesMain) getActivity();
		mView = inflater.inflate(R.layout.compass_video_layout, container, false);
//		mActivity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	
		_frameContainer = (ViewGroup) mView.findViewById(R.id.framecontainer);
		_frameContainer.setBackgroundColor(0xFF000000);
		File root = Environment.getExternalStorageDirectory();


		loadVideo(root + "/hiddenCities/video/360.mp4");



		_pfasset.play();

		return mView;

	}

	public void loadVideo(String filename) {

		_pfview = PFObjectFactory.view(mActivity);
		_pfasset = PFObjectFactory.assetFromUri(mActivity, Uri.parse(filename), this);
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
			mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			Log.d("SimplePlayer", Float.toString(_pfview.getView().getRotationY()));
			break;
		case PAUSED:
			Log.d("SimplePlayer", "Paused");
			break;
		case STOPPED:
			Log.d("SimplePlayer", "Stopped");
			mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case COMPLETE:
			Log.d("SimplePlayer", "Complete");

			mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case ERROR:
			Log.d("SimplePlayer", "Error");
			break;
		}
	}

	public void onPause() {
		super.onPause();
		if (_pfasset != null) {
			if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
				_pfasset.pause();
		}
		
	}

}
