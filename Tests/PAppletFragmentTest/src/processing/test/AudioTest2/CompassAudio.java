package processing.test.AudioTest2;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.content.Context;
import android.os.Bundle;

import android.os.Environment;

import apwidgets.*;
import ketai.*;

public class CompassAudio extends PApplet implements SensorEventListener
{

	PImage					compasAudioRotate;

	String[]				mPaths;
	AudioPlayManager[]		mPlayManagers	= null;
	private float			currentDegree	= 0f;
	private float			tempDegree		= 0f;

	// device sensor manager
	private SensorManager	mSensorManager;

	public void setup()
	{

		compasAudioRotate = loadImage("storage/sdcard0/hiddenCities/images/compasAudioRotate.png");
		orientation(PORTRAIT);
		imageMode(CENTER);

		mPaths = new String[2];

		mPaths[0] = "storage/sdcard0/hiddenCities/audio/compasAudio1.wav";
		mPaths[1] = "storage/sdcard0/hiddenCities/audio/compasAudio2.wav";

		mPlayManagers = new AudioPlayManager[mPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {
			println("making manager number " + i);
			mPlayManagers[i] = new AudioPlayManager(mPaths[i], 64); // buffersize = 64kb
			mPlayManagers[i].setIsLooping(true);
			mPlayManagers[i].play();

		}

		//changed this line from getSystemService(SENSOR_SERVICE);
		mSensorManager = (SensorManager) getActivity().getSystemService(
				Context.SENSOR_SERVICE);

		mSensorManager.registerListener(this,
				mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);

	}

	public void onSensorChanged(SensorEvent event)
	{

		// get the angle around the z-axis rotated
		float degree = Math.round(event.values[0]);

		currentDegree = degree;

	}

	public void draw()
	{
		// println(frameRate);
		background(0);

		if (currentDegree > 180) {
			tempDegree = currentDegree - 180.0f;

			mPlayManagers[0].setVolume(map((float) tempDegree, 0.0f, 180.0f,
					0.0f, 1.0f));
			mPlayManagers[1].setVolume(map((float) 180 - tempDegree, 0.0f,
					180.0f, 0.0f, 1.0f));

		}
		if (currentDegree < 180) {

			mPlayManagers[0].setVolume(map((float) 180 - currentDegree, 0.0f,
					180.0f, 00.0f, 1.0f));
			mPlayManagers[1].setVolume(map((float) currentDegree, 0.0f, 180.0f,
					0.0f, 1.0f));

		}

		image(compasAudioRotate, 540, 960);
		// println(mPlayManagers[0].getVolume());
		// println(mPlayManagers[1].getVolume());

	}

	public void pause()
	{

		for (int i = 0; i < mPlayManagers.length; i++) {
			mPlayManagers[i].pause();

		}
	}

	public void resume()
	{
//		setup();
		if (mPlayManagers != null) {
			for (int i = 0; i < mPlayManagers.length; i++) {
				if (mPlayManagers[i] != null) {
					mPlayManagers[i].play();
				}
			}
		}
	}

	public void onStop()
	{
		super.onStop();
		for (int i = 0; i < mPlayManagers.length; i++) {
			mPlayManagers[i].stop();
			mPlayManagers[i].release();
			mPlayManagers[i] = null;
		}
		mPlayManagers = null;
		onDestroy();
	}

	public void mousePressed()
	{

	}

	public void keyPressed()
	{

	}

	public int sketchWidth()
	{
		return displayWidth;
	}

	public int sketchHeight()
	{
		return displayHeight;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub

	}
}
