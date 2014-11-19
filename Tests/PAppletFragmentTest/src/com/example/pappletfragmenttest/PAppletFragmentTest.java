package com.example.pappletfragmenttest;

import java.util.ArrayList;
import java.util.List;

import processing.core.*;

//import android.app.Activity;
//import android.app.ActionBar;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.example.pappletfragmenttest.MouseCircle;
import com.example.pappletfragmenttest.MouseLines;
import com.example.pappletfragmenttest.R;

import processing.test.AudioTest2.*;
import processing.test.CameraFTPTest.*;
import com.panframe.android.samples.SimplePlayer.*;

public class PAppletFragmentTest extends Activity
{

	Fragment[]			mScenes;
	ArrayList<String>	mManagerWatcher;
	FragmentManager		mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_papplet_fragment_test);
		if (savedInstanceState == null) {
			mFragmentManager = getFragmentManager();
			mManagerWatcher = new ArrayList<String>();
			mScenes = new Fragment[5];
			mScenes[0] = (Fragment) new MouseCircle();
			mScenes[1] = (Fragment) new MouseLines();
			mScenes[2] = (Fragment) new CompassVideo();
			mScenes[3] = (Fragment) new CameraFTP();
			mScenes[4] = (Fragment) new CompassAudio();
			//			attachMouseCircles();

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.papplet_fragment_test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.Scene0) {
			attachMouseLines();
			System.out.println("Switch == 0");
			return true;
		} else if (id == R.id.Scene1) {
			attachMouseCircles();
			System.out.println("Switch == 1");
			return true;
		} else if (id == R.id.CompassVideo) {
			attachCompassVideo();
			System.out.println("Switch == 2");
			return true;
		} else if (id == R.id.CameraFTP) {
			attachCameraFTP();
			System.out.println("Switch == 3");
			return true;
		} else if (id == R.id.CompassAudio) {
			attachCompassAudio();
			System.out.println("Switch == 4");
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	public void attachMouseCircles()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.add(R.id.container, new MouseCircle(),
				"MouseCircles");
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_ENTER_MASK);
		mManagerWatcher.add("MouseCircles");
		//		fragmentTransaction.replace(R.id.container, mScenes[0]);
		fragmentTransaction.commit();

	}

	public void attachMouseLines()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.add(R.id.container, new MouseLines(), "MouseLines");
		mManagerWatcher.add("MouseLines");
		//		fragmentTransaction.replace(R.id.container, mScenes[1]);

		fragmentTransaction.commit();
	}

	public void attachCompassVideo()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();

		fragmentTransaction.add(R.id.container, new CompassVideo(),
				"CompassVideo");
		mManagerWatcher.add("CompassVideo");
		//		fragmentTransaction.replace(R.id.container, mScenes[2]);
		fragmentTransaction.commit();

	}

	public void attachCameraFTP()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.add(R.id.container, new CameraFTP(), "CameraFTP");
		mManagerWatcher.add("CameraFTP");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void attachCompassAudio()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new CompassAudio(),
				"CompassAudio");
		mManagerWatcher.add("CompassAudio");
		//		fragmentTransaction.replace(R.id.container, mScenes[4]);

		fragmentTransaction.commit();

	}

	public void emptyFragmentManager()
	{
		if (!mManagerWatcher.isEmpty()) {
			FragmentTransaction fragmentTransaction = mFragmentManager
					.beginTransaction();
			for (int i = 0; i < mManagerWatcher.size(); i++) {
				fragmentTransaction
						.setCustomAnimations(android.R.animator.fade_in,
								android.R.animator.fade_out);
				fragmentTransaction.remove(mFragmentManager
						.findFragmentByTag((String) mManagerWatcher.get(i)));
			}
			fragmentTransaction.commit();
			mManagerWatcher.clear();
		}
	}

}
