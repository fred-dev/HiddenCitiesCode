package com.example.hiddencitiesmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.media.MediaPlayer;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
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
    ConnectionCallbacks,
    OnConnectionFailedListener,
    LocationListener,
    OnMyLocationButtonClickListener ,
    View.OnTouchListener,
    MediaPlayer.OnPreparedListener{
	
	List<XmlValuesModel> markerData = null;
	List<XmlValuesModel> waypointData = null;

	LatLng[] waypointLatLongList = null;
	LatLng[] markerLatLongList= null;
	Marker[] markerList= null;
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


@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	if (getActionBar().isShowing()) getActionBar().hide();
	mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	
    buttonList = new ArrayList<Button>();
	for (int id : BUTTON_IDS) {
		Button mButton = (Button) findViewById(id);
		mButton.setOnTouchListener(this); // maybe
		buttonList.add(mButton);
	}
	myReceiver = new MusicIntentReceiver();
	RegisterAlarmBroadcast();
	
	mediaRoot = Environment.getExternalStorageDirectory();
	audioRoot=mediaRoot +"/hiddenCities/audio/";

 

}

@Override
protected void onResume() {
    super.onResume();
	parseSettings();
    setUpMapIfNeeded();
    setUpGoogleApiClientIfNeeded();
    mGoogleApiClient.connect();
    IntentFilter filter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
    registerReceiver(myReceiver, filter);
    
}
//handler for received Intents for the "my-event" event 

@Override
public void onPause() {
    super.onPause();
    if (mGoogleApiClient != null) {
        mGoogleApiClient.disconnect();
    }
    unregisterReceiver(myReceiver);
}
void doVibrate(){
	mVibrator.vibrate(1000);
}
void parseSettings() {
	try {
		String fileName = "hiddenCities/hiddenCitiesSettings.xml";
		String path = Environment.getExternalStorageDirectory() + "/" + fileName;
		File file = new File(path);
		if(file.exists()) {
			Log.e("File", "Yes file is there");
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
		
		
		if (markerData != null) {
			markerList = new Marker[markerData.size()];
			markerLatLongList = new LatLng[markerData.size()];
			for (int i = 0; i < markerData.size(); i++) {
				XmlValuesModel xmlRowData = markerData.get(i);
				if (xmlRowData != null) {
					markerLatLongList[i] = new LatLng(xmlRowData.getMarkerLat(),xmlRowData.getMarkerLong());
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
	} catch (Exception e) {
		Log.e("Jobs", "Exception parse xml :" + e);
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

@Override
public void onLocationChanged(Location location) {
	if(markerLatLongList!=null){
    	for(int l =0; l<markerLatLongList.length; l++){
    		double distance = 0;
    		Location locationMarker = new Location("A");
    		locationMarker.setLatitude(markerLatLongList[l].latitude);
    		locationMarker.setLongitude(markerLatLongList[l].longitude);
    		distance = locationMarker.distanceTo(location);
    		if(distance<10){
    			mVibrator.vibrate(1000);
    			Toast.makeText(getApplicationContext(), "We are close to waypoint" + Integer.toString(l), Toast.LENGTH_SHORT).show();
    		}
    	}
    }

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

@Override
public boolean onMyLocationButtonClick() {
    Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
    return false;
}
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
            preparePlayer("IntroText.wav");
        }
    };

    registerReceiver(mReceiver, new IntentFilter("sample") );
    pendingIntent = PendingIntent.getBroadcast( this, 0, new Intent("sample"),0 );
    alarmManager = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
}
private void UnregisterAlarmBroadcast()
{
    alarmManager.cancel(pendingIntent); 
    getBaseContext().unregisterReceiver(mReceiver);
}

@Override
public boolean onTouch(View v, MotionEvent event) {
	 int tempStore = 0;
	if (event.getAction() == MotionEvent.ACTION_DOWN) {
		for (int j = 0; j < 2; j++) {
			if (v.getId() == BUTTON_IDS[j]) {
				tempStore = j;
			}
		}
		_toneGenerator.startTone(TONE_IDS[tempStore]);
		doVibrate();
		setAlarmWithDelay(2000);
	}
	if (event.getAction() == MotionEvent.ACTION_UP) {
		_toneGenerator.stopTone();

	}
	return false;
}

private void preparePlayer(String fileName) {
	if(mediaPlayer.isPlaying()){
		mediaPlayer.stop();
		mediaPlayer=null;
	}
	   mediaPlayer = new MediaPlayer();
	   mediaPlayer.setOnPreparedListener(this);
	   mediaPlayer.setLooping(false);
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
@Override
protected void onDestroy() 
{
    unregisterReceiver(mReceiver);
    super.onDestroy();
  }

@Override
public void onPrepared(MediaPlayer mp) {
	 mp.start();
	
}
 
}
