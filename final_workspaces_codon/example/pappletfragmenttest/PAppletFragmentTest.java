package com.example.pappletfragmenttest;

import hiddenCitiesProject.hiddenCities.cameraFTP.*;

import java.util.ArrayList;
import java.util.List;

import processing.core.*;

//import android.app.Activity;
//import android.app.ActionBar;

import HiddenCitiesProject.hiddenCities.compassVideo.CompassVideo;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.panframe.android.samples.SimplePlayer.*;
import processing.test.ArUcoTest.*;
import processing.test.Vuforia.*;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.*;
import org.java_websocket.client.*;
import org.java_websocket.drafts.*;

import java.net.URI;
import java.net.URISyntaxException;

public class PAppletFragmentTest extends Activity implements LocationListener
{

	Fragment[]				mScenes;
	ArrayList<String>		mManagerWatcher;
	FragmentManager			mFragmentManager;

	private WebSocketClient	mWSClient;
	private static String	mServerPath		= "ws://mprint-hiddencities.rhcloud.com:8000/";
	private String			mUserId;

	private LocationManager	mLocationManager;
	private boolean			mIsConnected	= false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_papplet_fragment_test);
		if (savedInstanceState == null) {
			mFragmentManager = getFragmentManager();
			mManagerWatcher = new ArrayList<String>();
			//			mScenes = new Fragment[5];
			//			mScenes[0] = (Fragment) new MouseCircle();
			//			mScenes[1] = (Fragment) new MouseLines();
			//			mScenes[2] = (Fragment) new CompassVideo();
			//			mScenes[3] = (Fragment) new CameraFTP();
			//			mScenes[4] = (Fragment) new CompassAudio();
			//			

			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mLocationManager.requestLocationUpdates(
					LocationManager.GPS_PROVIDER, 5000, 1, this);
			//mUserId should be obtained from xml file
			mUserId = "arash";
			setupWebSocket();
			mWSClient.connect();
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
			item.setChecked(true);
			return true;
		} else if (id == R.id.Scene1) {
			attachMouseCircles();
			System.out.println("Switch == 1");
			item.setChecked(true);
			return true;
		} else if (id == R.id.CompassVideo) {
			attachCompassVideo();
			System.out.println("Switch == 2");
			item.setChecked(true);
			return true;
		} else if (id == R.id.CameraFTP) {
			attachCameraFTP();
			System.out.println("Switch == 3");
			item.setChecked(true);
			return true;
		} else if (id == R.id.CompassAudio) {
			attachCompassAudio();
			System.out.println("Switch == 4");
			item.setChecked(true);
			return true;
		} else if (id == R.id.CameraFTPWithMap) {
			attachCameraFTPWithMap();
			System.out.println("Switch == 5");
			item.setChecked(true);
			return true;
		} else if (id == R.id.ArucoScene) {
			attachAruco();
			System.out.println("Switch == 6");
			item.setChecked(true);
			return true;
		} else if (id == R.id.Error) {
			triggerError();
			System.out.println("Switch == 666");
			item.setChecked(true);
			return true;
		} else if (id == R.id.VuforiaScene) {
			attachVuforia();
			System.out.println("Switch == 7");
			item.setChecked(true);
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

	public void attachCameraFTPWithMap()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.add(R.id.container, new CameraFTPWithMap(),
				"CameraFTPWithMap");
		mManagerWatcher.add("CameraFTPWithMap");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void attachAruco()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction
				.add(R.id.container, new ArucoScene3(), "ArucoScene");
		mManagerWatcher.add("ArucoScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void attachVuforia()
	{
		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager
				.beginTransaction();
		fragmentTransaction
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		fragmentTransaction.add(R.id.container, new VuforiaScene(),
				"VuforiaScene");
		mManagerWatcher.add("VuforiaScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void triggerError()
	{

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

	public void setupWebSocket()
	{
		WebSocketImpl.DEBUG = true;
		//		Draft draft = new Draft_17();

		try {
			mWSClient = new WebSocketClient(new URI(mServerPath + mUserId)) {

				@Override
				public void onClose(int aCode, String aReason, boolean aRemote)
				{
					System.out.print("You have been disconnected from"
							+ getURI() + "; Code:" + aCode + " " + aReason
							+ "\n");
					mIsConnected = false;
					mWSClient.connect();

				}

				@Override
				public void onError(Exception aError)
				{
					System.out.print("Exception occured ...\n" + aError + "\n");
					mIsConnected = false;
					mWSClient.close();
					mWSClient.connect();

				}

				@Override
				public void onMessage(String aMessage)
				{
					System.out.println(aMessage);
					if (aMessage.equals("Attach Mouse Lines")
							|| aMessage.equals("Tester")) {
						attachMouseLines();
						System.out.println("Switch == 0");
					} else if (aMessage.equals("Attach Mouse Circles")) {
						attachMouseCircles();
						System.out.println("Switch == 1");
					} else if (aMessage.equals("Attach Compass Video")) {
						attachCompassVideo();
						System.out.println("Switch == 2");
					} else if (aMessage.equals("Attach CameraFTP")) {
						attachCameraFTP();
						System.out.println("Switch == 3");
					} else if (aMessage.equals("Attach Compass Audio")) {
						attachCompassAudio();
						System.out.println("Switch == 4");
					} else if (aMessage.equals("Attach CameraFTP With Map")) {
						attachCameraFTPWithMap();
						System.out.println("Switch == 5");
					} else if (aMessage.equals("Attach Aruco Scene")) {
						attachAruco();
						System.out.println("Switch == 6");
					} else if (aMessage.equals("Attach Vuforia Scene")) {
						attachAruco();
						System.out.println("Switch == 7");
					} else if (aMessage.equals("Trigger Error")) {
						triggerError();
						System.out.println("Switch == 666");
					}

				}

				@Override
				public void onOpen(ServerHandshake aHandshake)
				{
					System.out.print("You are connected to the server:"
							+ getURI() + "\n");
					mIsConnected = true;
				}

			};

		} catch (URISyntaxException ex) {
			System.out.println("Is not a valid WebSocker URI");
		}
	}

	@Override
	public void onLocationChanged(Location aLoc)
	{
		//		mLoc.setLatitude(aLoc.getLatitude());
		//		mLoc.setLongitude(aLoc.getLongitude());
		if (mIsConnected) {
			mWSClient.send("map/" + aLoc.getLatitude() + "/"
					+ aLoc.getLongitude());
			System.out.println("map/" + aLoc.getLatitude() + "/"
					+ aLoc.getLongitude());
		}
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub

	}

}
