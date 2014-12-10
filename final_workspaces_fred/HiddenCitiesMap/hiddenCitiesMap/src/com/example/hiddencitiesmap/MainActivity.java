package com.example.hiddencitiesmap;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
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
    View.OnTouchListener{
	
	List<XmlValuesModel> markerData = null;
	List<XmlValuesModel> waypointData = null;

	LatLng[] waypointLatLongList = null;
	LatLng[] markerLatLongList= null;
	Marker[] markerList= null;
	
	
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

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	if (getActionBar().isShowing()) getActionBar().hide();
    buttonList = new ArrayList<Button>();
	for (int id : BUTTON_IDS) {
		Button mButton = (Button) findViewById(id);
		mButton.setOnTouchListener(this); // maybe
		buttonList.add(mButton);
	}
}

@Override
protected void onResume() {
    super.onResume();
	parseSettings();
    setUpMapIfNeeded();
    setUpGoogleApiClientIfNeeded();
    mGoogleApiClient.connect();
}

@Override
public void onPause() {
    super.onPause();
    if (mGoogleApiClient != null) {
        mGoogleApiClient.disconnect();
    }
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
	}
	if (event.getAction() == MotionEvent.ACTION_UP) {
		_toneGenerator.stopTone();

	}
	return false;
}

}
