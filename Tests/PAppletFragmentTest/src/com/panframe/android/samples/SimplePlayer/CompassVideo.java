package com.panframe.android.samples.SimplePlayer;

import java.util.*;

import processing.test.AudioTest2.AudioPlayManager;

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

public class CompassVideo extends Fragment implements PFAssetObserver,
		SensorEventListener {

	PFView _pfview;
	PFAsset _pfasset;
	PFNavigationMode _currentNavigationMode = PFNavigationMode.MOTION;

	boolean _updateThumb = true;;
	Timer _scrubberMonitorTimer;

	ViewGroup _frameContainer;

	String[] mPaths;
	AudioPlayManager[] mPlayManagers = null;
	private float _calculatedRotation = 0f;
	float[] mRotation;
	float[] mGravity;
	float[] mGeomagnetic;
	private SensorManager mSensorManager;

	Sensor accelerometer;
	Sensor magnetometer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		_frameContainer = (ViewGroup) getActivity().getWindow().getDecorView();
		_frameContainer.setBackgroundColor(0xFF000000);
		loadVideo("storage/sdcard0/hiddenCities/video/360.mp4");
		showControls(false);
		_pfasset.play();

		mPaths = new String[4];

		mPaths[0] = "storage/sdcard0/hiddenCities/audio/360ch1.wav";
		mPaths[1] = "storage/sdcard0/hiddenCities/audio/360ch2.wav";
		mPaths[2] = "storage/sdcard0/hiddenCities/audio/360ch3.wav";
		mPaths[3] = "storage/sdcard0/hiddenCities/audio/360ch4.wav";

		mPlayManagers = new AudioPlayManager[mPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {
			mPlayManagers[i] = new AudioPlayManager(mPaths[i], 64); // buffersize
																	// // = 64kb
			mPlayManagers[i].setIsLooping(true);
			mPlayManagers[i].setVolume(0);
			mPlayManagers[i].play();
		}

		mSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_GAME);
		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
				SensorManager.SENSOR_DELAY_GAME);

		return null;

	}

	public void showControls(boolean bShow) {

		if (_pfview != null) {
			if (!_pfview.supportsNavigationMode(PFNavigationMode.MOTION))
				Log.d("SimplePlayer", "Not supported nav");
		}
	}

	public void loadVideo(String filename) {

		_pfview = PFObjectFactory.view(this.getActivity());
		_pfasset = PFObjectFactory.assetFromUri(this.getActivity(),
				Uri.parse(filename), this);

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
			Log.d("SimplePlayer",
					"Downloading 360 movie: " + _pfasset.getDownloadProgress()
							+ " percent complete");
			break;
		case DOWNLOADED:
			Log.d("SimplePlayer", "Downloaded to " + asset.getUrl());
			break;
		case DOWNLOADCANCELLED:
			Log.d("SimplePlayer", "Download cancelled");
			break;
		case PLAYING:
			Log.d("SimplePlayer", "Playing");
			getActivity().getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case PAUSED:
			Log.d("SimplePlayer", "Paused");

			break;
		case STOPPED:
			Log.d("SimplePlayer", "Stopped");

			getActivity().getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			break;
		case COMPLETE:
			Log.d("SimplePlayer", "Complete");

			getActivity().getWindow().clearFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
		mPlayManagers = new AudioPlayManager[mPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i].getIsPlaying()) {
				mPlayManagers[i].pause();
			}

		}
	}

	public void onStartCommand(Intent intent, int flags, int startId) {
		_pfasset.play();
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
			mGravity = event.values;
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
			mGeomagnetic = event.values;
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity,
					mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				float azimut = 180 + (float) Math.toDegrees(orientation[0]);

				Log.d("Azimut", Float.toString(azimut));

				if (azimut > 0 && azimut < 90) {
					_calculatedRotation = azimut;
					mPlayManagers[0].setVolume(_calculatedRotation / 90);
					mPlayManagers[3].setVolume((90 - _calculatedRotation) / 90);

				}

				if (azimut > 90 && azimut < 180) {
					_calculatedRotation = azimut - 90;
					mPlayManagers[1].setVolume(_calculatedRotation / 90);
					mPlayManagers[0].setVolume((90 - _calculatedRotation) / 90);
				}

				if (azimut > 180 && azimut < 270) {
					_calculatedRotation = azimut - 180;
					mPlayManagers[2].setVolume(_calculatedRotation / 90);
					mPlayManagers[1].setVolume((90 - _calculatedRotation) / 90);
				}

				if (azimut > 270 && azimut < 360) {
					_calculatedRotation = azimut - 270;
					mPlayManagers[3].setVolume(_calculatedRotation / 90);
					mPlayManagers[2].setVolume((90 - _calculatedRotation) / 90);
				}
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

}
