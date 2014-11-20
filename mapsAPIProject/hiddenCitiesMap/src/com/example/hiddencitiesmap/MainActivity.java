package com.example.hiddencitiesmap;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.SupportMapFragment;

import android.graphics.Color;
import android.location.Location;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity 

	implements
    ConnectionCallbacks,
    OnConnectionFailedListener,
    LocationListener,
    OnMyLocationButtonClickListener {
	
		private static final LatLng INSTALLATION01 = new LatLng(52.393100, 4.911760);
	    private static final LatLng INSTALLATION02 = new LatLng(52.393047, 4.911658);
	    private static final LatLng INSTALLATION03 = new LatLng(52.392792, 4.912575);
	    private static final LatLng INSTALLATION04 = new LatLng(52.392605, 4.913015);
	    private static final LatLng INSTALLATION05 = new LatLng(52.392723, 4.913133);
	   
	    
	    private Marker mInstallation01;
	    private Marker mInstallation02;
	    private Marker mInstallation03;
	    private Marker mInstallation04;
	    private Marker mInstallation05;
	    
	    private Polyline mMutablePolyline;

private GoogleMap mMap;

private GoogleApiClient mGoogleApiClient;
//private TextView mMessageView;

// These settings are the same as the settings for the map. They will in fact give you updates
// at the maximal rates currently possible.
private void addMarkersToMap() {
    // Uses a colored icon.
	mInstallation01 = mMap.addMarker(new MarkerOptions()
            .position(INSTALLATION01)
            .title("Installation 1"));

    // Uses a custom icon with the info window popping out of the center of the icon.
	mInstallation02 = mMap.addMarker(new MarkerOptions()
            .position(INSTALLATION02)
            .title("Installation 2"));

    // Creates a draggable marker. Long press to drag.
	mInstallation03 = mMap.addMarker(new MarkerOptions()
            .position(INSTALLATION03)
            .title("Installation 3"));

    // A few more markers for good measure.
	mInstallation04 = mMap.addMarker(new MarkerOptions()
            .position(INSTALLATION04)
            .title("Installation 4"));
    
	mInstallation05 = mMap.addMarker(new MarkerOptions()
            .position(INSTALLATION05)
            .title("Instlallation 5"));

}
private static final LocationRequest REQUEST = LocationRequest.create()
        .setInterval(5000)         // 5 seconds
        .setFastestInterval(16)    // 16ms = 60fps
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
   // mMessageView = (TextView) findViewById(R.id.message_text);
}

@Override
protected void onResume() {
    super.onResume();
    
	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
			WindowManager.LayoutParams.FLAG_FULLSCREEN);
	 
	 if (getActionBar().isShowing()) getActionBar().hide();
	
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

private void setUpMapIfNeeded() {
    // Do a null check to confirm that we have not already instantiated the map.
    if (mMap == null) {
        // Try to obtain the map from the SupportMapFragment.
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();
        // Check if we were successful in obtaining the map.
        if (mMap != null) {
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            addMarkersToMap();
           
            PolylineOptions options = new PolylineOptions()
            .add(new LatLng(INSTALLATION01.latitude,INSTALLATION01.longitude))
            .add(new LatLng(INSTALLATION02.latitude,INSTALLATION02.longitude))
            .add(new LatLng(INSTALLATION03.latitude,INSTALLATION03.longitude))
            .add(new LatLng(INSTALLATION04.latitude,INSTALLATION04.longitude))
            .add(new LatLng(INSTALLATION05.latitude,INSTALLATION05.longitude))
            .color(Color.BLUE)
            .width(15);
    
    mMutablePolyline = mMap.addPolyline(options);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INSTALLATION03, 17));
           
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

/**
 * Button to get current Location. This demonstrates how to get the current Location as required
 * without needing to register a LocationListener.
 */
public void showMyLocation(View view) {
    if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
        String msg = "Location = "
                + LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

/**
 * Implementation of {@link LocationListener}.
 */
@Override
public void onLocationChanged(Location location) {
   // mMessageView.setText("Location = " + location);
}

/**
 * Callback called when connected to GCore. Implementation of {@link ConnectionCallbacks}.
 */
@Override
public void onConnected(Bundle connectionHint) {
    LocationServices.FusedLocationApi.requestLocationUpdates(
            mGoogleApiClient,
            REQUEST,
            this);  // LocationListener
}

/**
 * Callback called when disconnected from GCore. Implementation of {@link ConnectionCallbacks}.
 */
@Override
public void onConnectionSuspended(int cause) {
    // Do nothing
}

/**
 * Implementation of {@link OnConnectionFailedListener}.
 */
@Override
public void onConnectionFailed(ConnectionResult result) {
    // Do nothing
}

@Override
public boolean onMyLocationButtonClick() {
    Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
    // Return false so that we don't consume the event and the default behavior still occurs
    // (the camera animates to the user's current position).
    return false;
}

}
