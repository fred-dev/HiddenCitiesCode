package com.hiddenCities.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.hiddenCities.main.MyFTP;
import com.hiddenCities.main.XmlValuesModel;
import com.hiddenCities.main.XMLParser;
import com.hiddenCities.R;
import com.hiddenCities.augmentedReality.HiddenCitiesAugmentedReality;
import com.hiddenCities.camera.HiddenCitiesCamera;
import com.hiddenCities.compassVideo.HiddenCitiesCompassVideo;
import com.hiddenCities.compassAudio.HiddenCitiesCompassAudio;
import com.hiddenCities.helpDialer.HiddenCitiesHelpDialer;
import com.hiddenCities.login.HiddenCitiesLogin;
import com.hiddenCities.map.HiddenCitiesMap;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.*;
import org.java_websocket.client.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.XMLReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class HiddenCitiesMain extends Activity implements LocationListener, MediaPlayer.OnPreparedListener
{

	Fragment[]				mScenes;
	ArrayList<String>		mManagerWatcher;
	FragmentManager			mFragmentManager;

	public WebSocketClient	mWSClient;
	public String			mWSServerPath;
	public String			mWSUserId;

	public LocationManager	mLocationManager;
	public boolean			mIsConnected		= false;

	List<XmlValuesModel>	mMarkerData			= null;
	List<XmlValuesModel>	mWaypointData		= null;
	List<XmlValuesModel>	mNetworkData		= null;
	List<XmlValuesModel>	mIdData				= null;
	
	public String			mUserName, mUserEmail;

	Vibrator				mVibrator			= null;
	File					mMediaRoot;
	String					mAudioRoot;

	MyFTP					mFTP;
	String					mFTPIP, mFTPUserName, mFTPPassword, mFtpRemotePath;
	static String			mLocalPathForFTP;
	static String			mRemoteFileNameFTP;
	boolean					mHasParsedSettings	= false;

	PendingIntent			mPendingIntent;
	AlarmManager			mAlarmManager;
	BroadcastReceiver		mReceiver;

	public MediaPlayer		mMediaPlayer;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity_layout);
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
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, this);
			mReceiver = new MusicIntentReceiver();
			RegisterAlarmBroadcast();

			mMediaRoot = Environment.getExternalStorageDirectory();
			mAudioRoot = mMediaRoot + "/hiddenCities/audio/";
			attachLoginScene();
		}
	}

	@Override
	public void onResume()
	{
		super.onResume();
		IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		registerReceiver(mReceiver, filter);

	}

	@Override
	public void onPause()
	{
		super.onPause();
//		mWSClient.close();
		unregisterReceiver(mReceiver);
	}

	@Override
	public void onDestroy()
	{
		mWSClient.close();
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.CompassVideo) {
			attachCompassVideoScene();
			System.out.println("Switch == CompassVideo");
			item.setChecked(true);
			return true;
		} else if (id == R.id.CompassAudio) {
			attachCompassAudioScene();
			System.out.println("Switch == CompassAudio");
			item.setChecked(true);
			return true;
		} else if (id == R.id.Camera) {
			attachCameraScene();
			System.out.println("Switch == Camera");
			item.setChecked(true);
			return true;
		} else if (id == R.id.Error) {
			triggerError();
			System.out.println("Switch == 666ErrrRRRrroooooOOOOooRRRR");
			item.setChecked(true);
			return true;
		}
		//		} else if (id == R.id.CompassAudio) {
		//			attachCompassAudio();
		//			System.out.println("Switch == CompassAudio");
		//			item.setChecked(true);
		//			return true;
		//		} else if (id == R.id.Map) {
		//			attachCameraFTPWithMap();
		//			System.out.println("Switch == Map");
		//			item.setChecked(true);
		//			return true;
		//		}  
		//		} else if (id == R.id.VuforiaScene) {
		//			attachVuforia();
		//			System.out.println("Switch == VuforiaScene");
		//			item.setChecked(true);
		//			return true;
		//		}

		return super.onOptionsItemSelected(item);
	}

	public void login()
	{
		System.out.println("mayday mayday, Logging in!!!!");
		parseSettings();
		System.out.println("Parsed Settings!!!!");
		attachMapScene();
		attachAugmentedRealityScene();
		detachLoginScene();
		setupWebSocket();
		System.out.println("Setup Sockets!!!!");
		mWSClient.connect();
		System.out.println("Connected Sockets!!!!");
		mFTP = new MyFTP();
		mFTP.connnectWithFTP(mFTPIP, mFTPUserName, mFTPPassword);
		System.out.println("Made and Connected FTP!!!!");
		
	}

	//For sending the data to login Fragment
	public void login(View view)
	{
		((HiddenCitiesLogin) mFragmentManager.findFragmentByTag("LoginScene")).login(view);
	}

	public void attachCompassAudioScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesCompassAudio(), "CompassAudioScene");
		mManagerWatcher.add("CompassAudioScene");
		//		fragmentTransaction.replace(R.id.container, mScenes[2]);
		fragmentTransaction.commit();

	}

	public void detachCompassAudioScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("CompassAudioScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("CompassAudioScene"))
				iter.remove();
		}
		fragmentTransaction.commit();

	}

	public void attachCompassVideoScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesCompassVideo(), "CompassVideoScene");
		mManagerWatcher.add("CompassVideoScene");
		//		fragmentTransaction.replace(R.id.container, mScenes[2]);
		fragmentTransaction.commit();

	}

	public void detachCompassVideoScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("CompassVideoScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("CompassVideoScene"))
				iter.remove();
		}
		fragmentTransaction.commit();

	}

	public void attachMapScene()
	{
		//		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesMap(mMarkerData, mWaypointData), "MapScene");
		mManagerWatcher.add("MapScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void detachMapScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("MapScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("MapScene"))
				iter.remove();
		}
		fragmentTransaction.commit();
	}

	public void attachLoginScene()
	{
		//		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesLogin(), "LoginScene");
		mManagerWatcher.add("LoginScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void detachLoginScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("LoginScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("LoginScene"))
				iter.remove();
		}
		fragmentTransaction.commit();
	}

	public void attachHelpDialerScene()
	{
		//		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesHelpDialer(), "HelpDialerScene");
		mManagerWatcher.add("HelpDialerScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void detachHelpDialerScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("HelpDialerScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("HelpDialerScene"))
				iter.remove();
		}
		fragmentTransaction.commit();
	}

	public void attachCameraScene()
	{
		//		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesCamera(), "CameraScene");
		mManagerWatcher.add("CameraScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void detachCameraScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("CameraScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("CameraScene"))
				iter.remove();
		}
		fragmentTransaction.commit();
	}
	
	public void attachAugmentedRealityScene()
	{
		//		emptyFragmentManager();

		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		fragmentTransaction.add(R.id.container, new HiddenCitiesAugmentedReality(), "AugmentedRealityScene");
		mManagerWatcher.add("AugmentedRealityScene");

		//		fragmentTransaction.replace(R.id.container, mScenes[3]);

		fragmentTransaction.commit();

	}

	public void detachAugmentedRealityScene()
	{
		FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
		fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
		fragmentTransaction.remove(mFragmentManager.findFragmentByTag("AugmentedRealityScene"));

		Iterator<String> iter = mManagerWatcher.iterator();
		while (iter.hasNext()) {
			if (iter.next().equals("AugmentedRealityScene"))
				iter.remove();
		}
		fragmentTransaction.commit();
	}

	public void triggerError()
	{

	}

	public void emptyFragmentManager()
	{
		if (!mManagerWatcher.isEmpty()) {
			FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
			for (int i = 0; i < mManagerWatcher.size(); i++) {
				if (!(mManagerWatcher.get(i).equals("MapScene"))) {
					fragmentTransaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
					fragmentTransaction.remove(mFragmentManager.findFragmentByTag((String) mManagerWatcher.get(i)));
					mManagerWatcher.remove(i);
				}
			}
			fragmentTransaction.commit();
			//			mManagerWatcher.clear();
		}
	}

	public void saveToXml(String value, String tagName, int Item)
	{

		String destFile = Environment.getExternalStorageDirectory().toString()
				+ "/hiddenCities/hiddenCitiesSettings.xml";
		try {

			File fXmlFile = new File(destFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			Node path = doc.getElementsByTagName(tagName).item(Item);
			path.setTextContent(value);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer trans = transFactory.newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(destFile));
			trans.transform(source, result);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doVibrate()
	{
		mVibrator.vibrate(1000);
	}

	public void parseSettings()
	{
		try {
			String fileName = "hiddenCities/hiddenCitiesSettings.xml";
			String path = Environment.getExternalStorageDirectory() + "/" + fileName;
			File file = new File(path);
			if (file.exists()) {
				Log.d("File", "Yes file is there");
			}

			FileInputStream fileInputStream = new FileInputStream(file);
			XMLParser parser = new XMLParser();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser sp = factory.newSAXParser();
			XMLReader reader = sp.getXMLReader();
			reader.setContentHandler(parser);
			sp.parse(fileInputStream, parser);

			mMarkerData = parser.markerList;
			mWaypointData = parser.waypointList;
			mNetworkData = parser.networkList;
			mIdData = parser.idList;

			Log.d("Progress", "we got here");
			if (mNetworkData != null) {
				for (int m = 0; m < mNetworkData.size(); m++) {
					XmlValuesModel xmlRowData = mNetworkData.get(m);
					if (xmlRowData != null) {
						mFTPIP = xmlRowData.getFtpAddress();
						mFTPUserName = xmlRowData.getFtpUser();
						mFTPPassword = xmlRowData.getFtpPassword();
						mFtpRemotePath = xmlRowData.getFtpRemotePath();
						mWSServerPath = xmlRowData.getWebSocketAdress();
						mWSUserId = xmlRowData.getWebSocketUser();
					} else
						Log.e("Network Info", "Network Info value null");
				}

			}
			
			if (mIdData != null) {
				for (int m = 0; m < mIdData.size(); m++) {
					XmlValuesModel xmlRowData = mIdData.get(m);
					if (xmlRowData != null) {
						mUserName = xmlRowData.getUsername();
						mUserEmail = xmlRowData.getUserEmail();
					} else
						Log.e("Network Info", "Network Info value null");
				}

			}

		} catch (Exception e) {
			Log.e("XML parse", "Exception parse xml :" + e);
		}
		mHasParsedSettings = true;
	}

	public void setupWebSocket()
	{
		WebSocketImpl.DEBUG = true;
		//		Draft draft = new Draft_17();

		try {
			mWSClient = new WebSocketClient(new URI(mWSServerPath + mWSUserId)) {

				@Override
				public void onClose(int aCode, String aReason, boolean aRemote)
				{
					System.out.print("You have been disconnected from" + getURI() + "; Code:" + aCode + " " + aReason
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
					if (aMessage.equals("Attach CompassVideo")) {
						attachCompassVideoScene();
						System.out.println("Switch == CompassVideo");
					} else if (aMessage.equals("Attach CompassAudio")) {
						attachCompassAudioScene();
						System.out.println("Switch == CompassAudio");
					} else if (aMessage.equals("Attach CameraScene")) {
						attachCameraScene();
						System.out.println("Switch == CameraFTP");
						//					} else if (aMessage.equals("Attach CompassAudio")) {
						//						attachCompassAudio();
						//						System.out.println("Switch == CompassAudio");
						//					} else if (aMessage.equals("Attach Map")) {
						//						attachCameraFTPWithMap();
						//						System.out.println("Switch == Map");
						//					} else if (aMessage.equals("Attach VuforiaScene")) {
						//						attachVuforia();
						//						System.out.println("Switch == VuforiaScene");
						//					} else if (aMessage.equals("Trigger Error")) {
						triggerError();
						System.out.println("Switch == 666ErrrrrRRRRoooOOOOoooRRrr");
					}

				}

				@Override
				public void onOpen(ServerHandshake aHandshake)
				{
					System.out.print("You are connected to the server:" + getURI() + "\n");
					mIsConnected = true;
				}

			};

		} catch (URISyntaxException ex) {
			System.out.println("Is not a valid WebSocker URI");
		}
	}

	public void uploadToFtp(String aFilePath, String aServerFilePath)
	{
		if (mFTP.isConnected()) {
			mFTP.uploadFile(aFilePath, aServerFilePath);
		} else {
			mFTP.connnectWithFTP(mFTPIP, mFTPUserName, mFTPPassword);
			mFTP.uploadFile(aFilePath, mFtpRemotePath + aServerFilePath);
		}
	}

	@Override
	public void onLocationChanged(Location aLocation)
	{

		if (mIsConnected) {
			mWSClient.send("map/" + aLocation.getLatitude() + "/" + aLocation.getLongitude());
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

	public class MusicIntentReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
				int state = intent.getIntExtra("state", -1);
				switch (state) {
					case 0:
						Toast.makeText(context, "Headset is unplugged", Toast.LENGTH_LONG).show();
						doVibrate();
					break;
					case 1:
						Toast.makeText(context, "Headset is plugged", Toast.LENGTH_LONG).show();
					break;
					default:
						Toast.makeText(context, "I have no idea what the headset state is", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	void setAlarmWithDelay(long delay)
	{

		mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + delay, mPendingIntent);
	}

	public void RegisterAlarmBroadcast()
	{
		mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent)
			{
				Toast.makeText(context, "Alarm time has been reached", Toast.LENGTH_LONG).show();
				preparePlayer("IntroText.wav");
			}
		};

		registerReceiver(mReceiver, new IntentFilter("sample"));
		mPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent("sample"), 0);
		mAlarmManager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));
	}

	public void UnregisterAlarmBroadcast()
	{
		mAlarmManager.cancel(mPendingIntent);
		getBaseContext().unregisterReceiver(mReceiver);
	}

	public void preparePlayer(String fileName)
	{
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			mMediaPlayer = null;
		}
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.setLooping(false);
		try {
			mMediaPlayer.setDataSource(mAudioRoot + fileName);
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		mMediaPlayer.prepareAsync();

	}

	@Override
	public void onPrepared(MediaPlayer mp)
	{
		mp.start();

	}

}
