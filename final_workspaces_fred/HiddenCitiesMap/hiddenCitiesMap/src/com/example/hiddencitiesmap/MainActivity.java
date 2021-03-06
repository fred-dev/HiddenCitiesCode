package com.example.hiddencitiesmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.java_websocket.WebSocketImpl;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends FragmentActivity  

	implements
	OnMyLocationButtonClickListener,
    ConnectionCallbacks,
    OnConnectionFailedListener,
    LocationListener,
    View.OnTouchListener,
    MediaPlayer.OnPreparedListener{
	
	List<XmlValuesModel> markerData = null;
	List<XmlValuesModel> waypointData = null;
	List<XmlValuesModel> networkData = null;
	List<XmlValuesModel> portholeData = null;
	List<XmlValuesModel> colourData = null;
	List<XmlValuesModel> gpsAudioPlayData = null;

	LatLng[] waypointLatLongList = null;
	LatLng[] markerLatLongList= null;
	LatLng[] gpsAudioPlayLatLongList = null;
	String[] gpsAudioPlayerFileNames = null;
	boolean[] gpsAudioHasPlayed = null;
	boolean[] markerVisitedList = null;
	
	Marker[] markerList= null;
	String[] markerSceneIdList = null;
	Vibrator mVibrator = null;
	
private GoogleMap mMap;

private GoogleApiClient mGoogleApiClient;

private static final int[] BUTTON_IDS = { R.id.CameraButton, R.id.HelpButton};
private List<Button> buttonList;
Button mButton;

static final ToneGenerator _toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

static int[] TONE_IDS = { ToneGenerator.TONE_DTMF_1,ToneGenerator.TONE_DTMF_2,};
 
private static final LocationRequest REQUEST = LocationRequest.create()
        .setInterval(5000)         // 5 seconds
        .setFastestInterval(16)    // 16ms = 60fps
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

private BroadcastReceiver myReceiver;

PendingIntent pendingIntent;
AlarmManager alarmManager;
BroadcastReceiver mReceiver;

private MediaPlayer mediaPlayer;
File mediaRoot;
String audioRoot;

private WebSocketClient	mWSClient;
private static String	mWsServerPath;
private String			mWSUserId;
private boolean			mIsWsConnected	= false;

private static final int CAM_REQUREST = 1313;
public static final int MEDIA_TYPE_IMAGE = 1;

MyFTP				mFTP;
String				mFtpIP, mFtpUserName, mFtpPassword,mFtpRemotePath;
static String 				mLocalPathForFTP;
static String				mRemoteFileNameFTP;
private IntentFilter filter;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    //this makes us fullscreen
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	if (getActionBar().isShowing()) getActionBar().hide();
	
	//Here we can override the system font and load whatever we like- should be loaded from Assest, must change to SDcard to get access to RTL fonts
	//FontUtility.overrideFont(getApplicationContext(), "SERIF", "assets/fonts/Roboto-Regular.ttf");
	
	//Turn on Vibrator
	mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	
	//setup buttons
    buttonList = new ArrayList<Button>();
	for (int id : BUTTON_IDS) {
		Button mButton = (Button) findViewById(id);
		mButton.setOnTouchListener(this); // maybe
		buttonList.add(mButton);
	}
	
	//This is to know if the headphones are in or out
	myReceiver = new MusicIntentReceiver();
	RegisterAlarmBroadcast();
	filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
	registerReceiver(myReceiver, filter);
	
	//We set the media root folder for audio here
    mediaRoot = Environment.getExternalStorageDirectory();
	audioRoot=mediaRoot +"/hiddenCities/audio/";
	
    parseSettings();
    
	setupWebSocket();
	mWSClient.connect();
	mFTP = new MyFTP();
    mFTP.connnectWithFTP(mFtpIP, mFtpUserName, mFtpPassword);

    //get the maps going
    setUpMapIfNeeded();
    setUpGoogleApiClientIfNeeded();
    mGoogleApiClient.connect();

}
@Override
protected void onResume() {
    super.onResume();
	
    setUpMapIfNeeded();
    setUpGoogleApiClientIfNeeded();
    mGoogleApiClient.connect();
    registerReceiver(myReceiver, filter);
    setupWebSocket();
	mWSClient.connect();
	mFTP = new MyFTP();
    mFTP.connnectWithFTP(mFtpIP, mFtpUserName, mFtpPassword);

}
//handler for received Intents for the "my-event" event 

@Override
public void onPause() {
    super.onPause();
//    mWSClient.close();
//    mWSClient=null;
    mIsWsConnected=false;
    mFTP=null;
    if (mGoogleApiClient != null) {
        mGoogleApiClient.disconnect();
    }
    unregisterReceiver(myReceiver);
    
    
}


@Override
protected void onDestroy() 
{
    unregisterReceiver(mReceiver);
    super.onDestroy();
  }

public void loadSceneOnGps(String sceneToLoad){
	
	//loadscene
}
public boolean doesWaypointHaveAudioTrigger(){
	
	
	return true;
	
}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
	

			if (requestCode == CAM_REQUREST ) {
				
//				if (resultCode == RESULT_OK) {
//					Toast.makeText(this, "Image saved to:\n" + mLocalPathForFTP,
//							Toast.LENGTH_LONG).show();
//					uploadToFtp(mLocalPathForFTP,mRemoteFileNameFTP);
//				} else if (resultCode == RESULT_CANCELED) {
//					// User cancelled the image capture
//				} else {
//					// Image capture failed, advise user
//				}
			
		}
	}
	
//The next methods launch the native camera and set the file location of where we will store the photo if it is taken. We can calso close the camera
public void takePhoto(){
	Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	Uri photoUri;
	photoUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image	
	cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
    startActivityForResult(cameraIntent, CAM_REQUREST);
    
}
	
private static Uri getOutputMediaFileUri(int type){
    return Uri.fromFile(getOutputMediaFile(type));
}

/** Create a File for saving an image or video */
@SuppressLint("SimpleDateFormat")
private static File getOutputMediaFile(int type){
  File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "hiddenCities/userPhotos");
 
  if (! mediaStorageDir.exists()){
      if (! mediaStorageDir.mkdirs()){
          Log.d("MyCameraApp", "failed to create directory");
          return null;
      }
  }

  // Create a media file name
  String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
  File mediaFile;
  if (type == MEDIA_TYPE_IMAGE){
      mediaFile = new File(mediaStorageDir.getPath() + File.separator +
      "IMG_"+ timeStamp + ".jpg");
      mRemoteFileNameFTP = "IMG_"+ timeStamp + ".jpg";
      mLocalPathForFTP = mediaFile.getAbsolutePath();
      Log.d("Media File", mediaFile.getAbsolutePath());
  
  } else {
      return null;
  }

  return mediaFile;
}
public void cancelPhoto(){
	PackageManager p = getPackageManager();
	String myPackage = getApplicationContext().getPackageName();
	Intent intent = p.getLaunchIntentForPackage(myPackage);
	startActivity(intent);
}


public void uploadToFtp(String aFilePath, String aServerFilePath) {
	if (mFTP.isConnected()) {
		mFTP.uploadFile(aFilePath, aServerFilePath);
	} else
		mFTP.connnectWithFTP(mFtpIP, mFtpUserName, mFtpPassword);
		mFTP.uploadFile(aFilePath, mFtpRemotePath + aServerFilePath);
}

	void parseSettings() {
		try {
			String fileName = "hiddenCities/hiddenCitiesSettings.xml";
			String path = Environment.getExternalStorageDirectory() + "/"+ fileName;
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

			markerData = parser.markerList;
			waypointData = parser.waypointList;
			networkData = parser.networkList;
			gpsAudioPlayData = parser.gpsAudioPlayerList;
			

			if (markerData != null) {
				markerList = new Marker[markerData.size()];
				markerVisitedList = new boolean[markerData.size()];
				markerLatLongList = new LatLng[markerData.size()];
				for (int i = 0; i < markerData.size(); i++) {
					XmlValuesModel xmlRowData = markerData.get(i);
					if (xmlRowData != null) {
						markerLatLongList[i] = new LatLng(xmlRowData.getMarkerLat(),xmlRowData.getMarkerLong());
						markerVisitedList[i] = false;
					} else
						Log.e("Markers", "Markers value null");
				}
			}
			if (waypointData != null) {

				waypointLatLongList = new LatLng[waypointData.size()];

				for (int k = 0; k < waypointData.size(); k++) {
					XmlValuesModel xmlRowData = waypointData.get(k);
					if (xmlRowData != null) {
						waypointLatLongList[k] = new LatLng(xmlRowData.getWaypointLat(),xmlRowData.getWaypointLong());
					} else
						Log.e("Waypoints", "Waypoint value null");
				}
			}
	
			if (networkData != null) {
				for (int m = 0; m < networkData.size(); m++) {
					XmlValuesModel xmlRowData = networkData.get(m);
					if (xmlRowData != null) {
						mFtpIP=xmlRowData.getFtpAddress();
						mFtpUserName=xmlRowData.getFtpUser();
						mFtpPassword=xmlRowData.getFtpPassword();
						mFtpRemotePath=xmlRowData.getFtpRemotePath();
						mWsServerPath=xmlRowData.getWebSocketAdress();	
						mWSUserId=xmlRowData.getWebSocketUser();
					} else
						Log.e("Network Info", "Network Info value null");
				}
			}
			if(gpsAudioPlayData !=null){
				gpsAudioPlayerFileNames = new String[gpsAudioPlayData.size()];
				gpsAudioPlayLatLongList = new LatLng[gpsAudioPlayData.size()];
				gpsAudioHasPlayed = new boolean[gpsAudioPlayData.size()];
				for (int n = 0; n < gpsAudioPlayData.size(); n++) {
					XmlValuesModel xmlRowData = gpsAudioPlayData.get(n);
					if (xmlRowData != null) {
						gpsAudioPlayLatLongList[n] = new LatLng(xmlRowData.getGpsAudioPlayerLat() ,xmlRowData.getGpsAudioPlayerLong());
						gpsAudioPlayerFileNames[n] = xmlRowData.getGpsAudioPlayerAudioFile();
						gpsAudioHasPlayed[n]= false;
					}
				}
				
			}
			

		} catch (Exception e) {
			Log.e("XML parse", "Exception parse xml :" + e);
		}
	}

private void addNewMarker(LatLng point, String title){
	MarkerOptions markerOptions = new MarkerOptions();
    markerOptions.position(point);
    markerOptions.title(title);
    mMap.addMarker(markerOptions);
}

private void setUpMapIfNeeded() {
    if (mMap == null) {
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            if(markerLatLongList!=null){
            	for(int l =0; l<markerLatLongList.length; l++){
            		addNewMarker(markerLatLongList[l],Integer.toString(l));
            	}
            }
            
           if(waypointLatLongList!=null){
        	   PolylineOptions options = new PolylineOptions();
        	   for(int j = 0; j<waypointLatLongList.length; j++){
        		   options.add(waypointLatLongList[j]).color(Color.BLUE).width(15);
        	   }
        	   mMap.addPolyline(options);
        	   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(waypointLatLongList[0], 17));
           }
        }
    }
}

private void setUpGoogleApiClientIfNeeded() {
    if (mGoogleApiClient == null) {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }
}

public void showMyLocation(View view) {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
        String msg = "Location = " + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
public void locationAlarm(int locationId){
	mVibrator.vibrate(1000);
	Toast.makeText(getApplicationContext(), "We are close to waypoint" + Integer.toString(locationId), Toast.LENGTH_SHORT).show();
}
@Override
	public void onLocationChanged(Location location) {
		if (markerLatLongList != null) {
			for (int l = 0; l < markerLatLongList.length; l++) {
				double distance = 0;
				Location locationMarker = new Location("A");
				locationMarker.setLatitude(markerLatLongList[l].latitude);
				locationMarker.setLongitude(markerLatLongList[l].longitude);
				distance = locationMarker.distanceTo(location);
				if (distance < 20) {
					locationAlarm(l);
					preparePlayer("01 Way Out in the World.mp3");
					markerVisitedList[l] = true;
				}
			}
	
		}
		
		if (gpsAudioPlayLatLongList != null) {
			for (int l = 0; l < gpsAudioPlayLatLongList.length; l++) {
				double distance = 0;
				Location locationMarker = new Location("A");
				locationMarker.setLatitude(gpsAudioPlayLatLongList[l].latitude);
				locationMarker.setLongitude(gpsAudioPlayLatLongList[l].longitude);
				distance = locationMarker.distanceTo(location);
				if (distance < 20) {
					if(gpsAudioHasPlayed[l]==false){
						preparePlayer(gpsAudioPlayerFileNames[l]);
						gpsAudioHasPlayed[l] = true;
					}	
				}
			}
	
		}
	
		if (mIsWsConnected) {
			mWSClient.send("map/" + location.getLatitude() + "/"
					+ location.getLongitude());
		}

	}


void doVibrate(){
	mVibrator.vibrate(1000);
}


@Override
public void onConnected(Bundle connectionHint) {
    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,REQUEST,this);  // LocationListener
}

@Override
public void onConnectionSuspended(int cause) {
}

@Override
public void onConnectionFailed(ConnectionResult result) {
}

//@Override
//public boolean onMyLocationButtonClick() {
//    Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
//   
//    return false;
//    
//}

// this is the receiver to know if the headphones are in or out
private class MusicIntentReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
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

//this sets an alarm, basically just a time stamp, the setexact method is as close as we can get to getting a precise time.
void setAlarmWithDelay(long delay){

	alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +delay, pendingIntent);
}

private void RegisterAlarmBroadcast()
{       
    mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Toast.makeText(context, "Alarm time has been reached", Toast.LENGTH_LONG).show();
            preparePlayer("ConductorScene.wav");
        }
    };

    registerReceiver(mReceiver, new IntentFilter("sample") );
    pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("sample"),0 );
    alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
}

@SuppressWarnings("unused")
private void UnregisterAlarmBroadcast()
{
    alarmManager.cancel(pendingIntent); 
    getBaseContext().unregisterReceiver(mReceiver);
}


//buttony things
@Override
public boolean onTouch(View v, MotionEvent event) {
	 int tempStore = 0;
	if (event.getAction() == MotionEvent.ACTION_DOWN) {
		for (int j = 0; j < 2; j++) {
			if (v.getId() == BUTTON_IDS[j]) {
				tempStore = j;
				takePhoto();
				 
			}
		}
		
		_toneGenerator.startTone(TONE_IDS[tempStore]);
		//doVibrate();
		//setAlarmWithDelay(2000);
	}
	if (event.getAction() == MotionEvent.ACTION_UP) {
		_toneGenerator.stopTone();

	}
	return false;
}

//we use async loading for our media files
private void preparePlayer(String fileName) {
     mediaPlayer = new MediaPlayer();
     mediaPlayer.setOnPreparedListener(this);
     try {
          mediaPlayer.setDataSource(audioRoot + fileName);
          
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
     mediaPlayer.prepareAsync();
	   
	}

//once the media file is loaded we will play it
@Override
public void onPrepared(MediaPlayer mp) {
	 mp.start();
	
}
public void setupWebSocket()
{
	WebSocketImpl.DEBUG = true;
	//		Draft draft = new Draft_17();

	try {
		mWSClient = new WebSocketClient(new URI(mWsServerPath + mWSUserId)) {

			@Override
			public void onClose(int aCode, String aReason, boolean aRemote)
			{
				System.out.print("You have been disconnected from"
						+ getURI() + "; Code:" + aCode + " " + aReason
						+ "\n");
				mIsWsConnected = false;
				mWSClient.connect();

			}

			@Override
			public void onError(Exception aError)
			{
				System.out.print("Exception occured ...\n" + aError + "\n");
				mIsWsConnected = false;
				mWSClient.close();
				mWSClient.connect();

			}

			@Override
			public void onMessage(String aMessage)
			{
				System.out.println(aMessage);
				if (aMessage.equals("Attach Mouse Lines")
						|| aMessage.equals("Tester")) {
					
					System.out.println("Switch == 0");
				} else if (aMessage.equals("Attach Mouse Circles")) {
					
					System.out.println("Switch == 1");
				} else if (aMessage.equals("Attach Compass Video")) {
				
					System.out.println("Switch == 2");
				} else if (aMessage.equals("Attach CameraFTP")) {
				
					System.out.println("Switch == 3");
				} else if (aMessage.equals("Attach Compass Audio")) {
				
					System.out.println("Switch == 4");
				} else if (aMessage.equals("Attach CameraFTP With Map")) {
				
					System.out.println("Switch == 5");
				} else if (aMessage.equals("Attach Aruco Scene")) {
				
					System.out.println("Switch == 6");
				} else if (aMessage.equals("Attach Vuforia Scene")) {
				
					System.out.println("Switch == 7");
				} else if (aMessage.equals("Trigger Error")) {
					cancelPhoto();
					preparePlayer("ConductorScene.wav");
					System.out.println("Switch == 666");
				}else if (aMessage.equals("Cancel Photo")) {
					
					System.out.println("Switch == 666");
				}
				

			}

			@Override
			public void onOpen(ServerHandshake aHandshake)
			{
				System.out.print("You are connected to the server:"
						+ getURI() + "\n");
				mIsWsConnected = true;
			}

		};

	} catch (URISyntaxException ex) {	
		System.out.println("Is not a valid WebSocker URI");
	}
}
public void testPath(String fileName){
	System.out.println(audioRoot +fileName);
}
public void onProviderDisabled(String arg0)
{
	// TODO Auto-generated method stub

}

public void onProviderEnabled(String arg0)
{
	// TODO Auto-generated method stub

}

public void onStatusChanged(String arg0, int arg1, Bundle arg2)
{
	// TODO Auto-generated method stub

}
@Override
public boolean onMyLocationButtonClick() {
	// TODO Auto-generated method stub
	return false;
}
 
}
