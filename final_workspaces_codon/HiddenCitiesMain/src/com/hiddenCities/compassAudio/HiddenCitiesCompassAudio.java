package com.hiddenCities.compassAudio;

import java.io.File;

import com.hiddenCities.R;
import com.hiddenCities.compassAudio.AudioPlayManager;
import com.hiddenCities.main.HiddenCitiesMain;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

public class HiddenCitiesCompassAudio extends Fragment implements SensorEventListener
{
	String[]				mPaths;
	AudioPlayManager[]		mPlayManagers;
	private float			currentDegree	= 0f;
	private float			tempDegree		= 0f;

	// device sensor manager
	private SensorManager	mSensorManager;

	HiddenCitiesMain		mActivity;
	View					mView;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mActivity = (HiddenCitiesMain) getActivity();
		mView = inflater.inflate(R.layout.compass_audio_layout, container, false);

		//		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//		 
		if (mActivity.getActionBar().isShowing())
			mActivity.getActionBar().hide();

		File root = Environment.getExternalStorageDirectory();
		ImageView IV = (ImageView) mView.findViewById(R.id.imageView1);
		Bitmap bMap = BitmapFactory.decodeFile(root + "/hiddenCities/images/compasAudioRotate.png");
		IV.setImageBitmap(bMap);
		mPaths = new String[2];
//		mPaths[0] = root + "/hiddenCities/audio/compassAudioTest1.wav";
//		mPaths[1] = root + "/hiddenCities/audio/compassAudioTest2.wav";
		mPaths[0] = root + "/hiddenCities/audio/compasAudio1.wav";
		mPaths[1] = root + "/hiddenCities/audio/compasAudio2.wav";

		mPlayManagers = new AudioPlayManager[mPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {
			mPlayManagers[i] = new AudioPlayManager(mPaths[i], 32); // buffersize = 64kb
			mPlayManagers[i].setIsLooping(true);
			mPlayManagers[i].start();
			while (!mPlayManagers[i].isSetup())
				;
			mPlayManagers[i].play();

		}

		mSensorManager = (SensorManager) mActivity.getSystemService(Activity.SENSOR_SERVICE);

		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);

		return mView;

	}

	@Override
	public void onResume()
	{

		super.onResume();
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i] != null) {
				mPlayManagers[i].play();
			}
		}

	}

	@Override
	public void onPause()
	{
		super.onPause();
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i] != null) {
				mPlayManagers[i].pause();
			}
		}
	}

	public void onStop()
	{
		super.onStop();
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i] != null) {
				mPlayManagers[i].stop();
				mPlayManagers[i].release();
			}
		}
	}

	public void onSensorChanged(SensorEvent event)
	{

		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);

		currentDegree = degree;

		if (currentDegree > 180) {
			tempDegree = currentDegree - 180.0f;

			mPlayManagers[0].setVolume(tempDegree / 180);
			mPlayManagers[1].setVolume((180 - tempDegree) / 180);

		}
		if (currentDegree < 180) {

			mPlayManagers[0].setVolume((180 - currentDegree) / 180);
			mPlayManagers[1].setVolume(currentDegree / 180);

		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub

	}
}
