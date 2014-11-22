package processing.test.CameraFTPTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import org.java_websocket.client.WebSocketClient;

//import ketai.camera.KetaiCamera;

import com.example.pappletfragmenttest.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.MapFragment;
//import com.google.android.gms.maps.SupportMapFragment;

import android.graphics.Color;
import android.location.Location;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import apwidgets.*;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.hardware.Camera;
import processing.test.CameraFTPTest.MyFTP;
import processing.test.CameraFTPTest.MyKetaiCamera;

public class CameraFTPWithMap extends PApplet implements ConnectionCallbacks,
		OnConnectionFailedListener, LocationListener,
		OnMyLocationButtonClickListener
{
	int										CAPTURE_WIDTH	= 320,
			CAPTURE_HEIGHT = 240;
	MyKetaiCamera							mCam;
	PShape									mRect;
	int										mCamId			= 0;
	int										mNumOfCam;
	String									mSavePath;
	boolean									mIsTakingPhoto	= false;
	boolean									mShouldRotate	= false;

	APWidgetContainer						mWidgetContainer;
	APButton								mButton;
	APToggleButton							mToggleButton;

	MyFTP									mFTP;
	String									mIP, mUserName, mPassword;

	private static final LatLng				INSTALLATION01	= new LatLng(
																	52.393100,
																	4.911760);
	private static final LatLng				INSTALLATION02	= new LatLng(
																	52.393047,
																	4.911658);
	private static final LatLng				INSTALLATION03	= new LatLng(
																	52.392792,
																	4.912575);
	private static final LatLng				INSTALLATION04	= new LatLng(
																	52.392605,
																	4.913015);
	private static final LatLng				INSTALLATION05	= new LatLng(
																	52.392723,
																	4.913133);

	private Marker							mInstallation01;
	private Marker							mInstallation02;
	private Marker							mInstallation03;
	private Marker							mInstallation04;
	private Marker							mInstallation05;

	private Polyline						mMutablePolyline;
	private MapView							mMapView;
	private GoogleMap						mMap;
	private GoogleApiClient					mGoogleApiClient;

	public View								mPAppletView;
	public ViewGroup						mContainer;
	Bundle									mSavedInstanceState;

	private static final LocationRequest	REQUEST			= LocationRequest
																	.create()
																	.setInterval(
																			5000) // 5 seconds
																	.setFastestInterval(
																			16) // 16ms = 60fps
																	.setPriority(
																			LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mContainer = container;
		mSavedInstanceState = savedInstanceState;

		View contentView;
		//		if (displayWidth == displayWidth && displayHeight == displayHeight) {
		//			// If using the full screen, don't embed inside other layouts
		//			contentView = surfaceView;
		//		} else {
		// If not using full screen, setup awkward view-inside-a-view so that
		// the sketch can be centered on screen. (If anyone has a more efficient
		// way to do this, please file an issue on Google Code, otherwise you
		// can keep your "talentless hack" comments to yourself. Ahem.)
		//		RelativeLayout overallLayout = new RelativeLayout(getActivity());
		//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
		//				RelativeLayout.LayoutParams.WRAP_CONTENT,
		//				RelativeLayout.LayoutParams.WRAP_CONTENT);
		//		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		//
		//		LinearLayout layout = new LinearLayout(getActivity());
		//		layout.addView(surfaceView, sketchWidth(), sketchHeight() / 2);
		//
		//		overallLayout.addView(layout, lp);

		//		}
		//	mMapView = (MapView) contentView.findViewById(R.id.mapView);

		//		overallLayout.addView(mMapView);

		//		contentView = overallLayout;
		//    mContainer.addView(mMapView);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void setup()
	{

		orientation(PORTRAIT);
		imageMode(CENTER);
		smooth();
		//		colorMode(HSB, 360, 100, 100, 100);
		frameRate(30);

		mCam = new MyKetaiCamera(this, CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
		mCam.register(this);
		mCam.setCameraID(mCamId);
		mCam.start();
		while (!mCam.isStarted())
			;
		println("Cam Started");
		mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/hiddenCities/photo/";
		mCam.setPhotoSize(mCam.camera.getParameters()
				.getSupportedPictureSizes().get(0).width, mCam.camera
				.getParameters().getSupportedPictureSizes().get(0).height);
		mCam.autoSettings();
		//		getCameraProperties();

		mWidgetContainer = new APWidgetContainer(this);
		mButton = new APButton(width / 3 - 240, 3 * height / 4 - 90, 440, 180,
				"Take Photo");
		mToggleButton = new APToggleButton(width / 3 - 240,
				3 * height / 4 + 90, 440, 180,
				"Rotate photo while saving (Heavy)");
		mToggleButton.setChecked(mShouldRotate);
		mWidgetContainer.addWidget(mButton);
		mWidgetContainer.addWidget(mToggleButton);

		mRect = createShape(RECT, -mCam.width / 2, -mCam.height / 2,
				mCam.width, mCam.height);
		mRect.setStroke(color(255, 0));
		mRect.setFill(color(255));
		//mRect.tint(255, 0, 0);
		mIP = "184.107.78.188";
		mUserName = "hesam@arashakbari.com";
		mPassword = "chosoo12345";
		mFTP = new MyFTP();
		mFTP.connnectWithFTP(mIP, mUserName, mPassword);
		setupMap();

	}

	public void draw()
	{
		background(0);
		tint(255);
		//		image(mImage, width/2, height/2);
		if (mCam.isStarted()) {
			//image(mCam, width / 2, height / 2);
			mRect.setTexture(mCam);
			pushMatrix();
			translate((float) (width * 3.0 / 4.0), (float) (height * 3.0 / 4.0));
			translate(20, 60);
			if (mCamId == 1) {
				rotate(-HALF_PI);
			} else {
				rotate(HALF_PI);
			}
			scale(2f, 2f);
			//			shape(mRect);
			shape(mRect);
			popMatrix();

		}
		//println(frameRate);
		//		println(mNumOfCam);
	}

	public void getCameraProperties()
	{

		Camera.Parameters params = mCam.camera.getParameters();
		List<Camera.Size> supportedPictureSizes = params
				.getSupportedPictureSizes();
		List<Camera.Size> supportedPreviewSizes = params
				.getSupportedPreviewSizes();
		List<int[]> supportedPreviewFpsRanges = params
				.getSupportedPreviewFpsRange();
		List<Camera.Size> supportedVideoSizes = params.getSupportedVideoSizes();
		for (int i = 0; i < supportedPreviewSizes.size(); i++) {
			Camera.Size previewSize = supportedPreviewSizes.get(i);
			println("Preview Size: " + "Width-" + previewSize.width
					+ " Height-" + previewSize.height);
			//			int[] previewFpsRanges = supportedPreviewFpsRanges.get(i);
			//			for (int j = 0; j < previewFpsRanges.length; j++) {
			//				println("Preview Size: " + "Width-" + previewSize.width
			//						+ " Height-" + previewSize.height);
			//				println("FPS = " + previewFpsRanges[j]);
			//			}
		}

		for (int i = 0; i < supportedPictureSizes.size(); i++) {
			Camera.Size pictureSize = supportedPictureSizes.get(i);
			println("Picture Size: " + "Width-" + pictureSize.width
					+ " Height-" + pictureSize.height);

		}

		for (int i = 0; i < supportedVideoSizes.size(); i++) {
			Camera.Size videoSize = supportedVideoSizes.get(i);
			println("Video Size: " + "Width-" + videoSize.width + " Height-"
					+ videoSize.height);

		}
	}

	public void switchCamera()
	{
		mCam.stop();
		mCamId = (mCamId + 1) % 2;
		mCam.setCameraID(mCamId);
		mCam.start();
		while (!mCam.isStarted())
			;
	}

	public void onCameraPreviewEvent()
	{
		mCam.read();
		//		mCam.loadPixels();
		//		mCamTexture.set(mCam.pixels);
	}

	public void onSavePhotoEvent(String aPath)
	{
		println("Took photo @ " + aPath);
		mIsTakingPhoto = false;
		if (mFTP.isConnected()) {
			mFTP.uploadFile(aPath, getDeviceInfo(),
					aPath.substring(aPath.lastIndexOf('/')));
		}
	}

	public void onClickWidget(APWidget widget)
	{
		if (widget == mButton)
			takePhoto();
		else if (widget == mToggleButton) {
			mShouldRotate = mToggleButton.isChecked();
			mToggleButton.setChecked(mShouldRotate);
		}
	}

	public void takePhoto()
	{
		println("Trying to take photo");
		if (!mIsTakingPhoto) {
			mIsTakingPhoto = true;
			if (mShouldRotate) {
				mCam.saveRotatedPhoto(mSavePath + frameCount + ".jpg");
			} else {
				mCam.savePhoto(mSavePath + frameCount + ".jpg");
			}
		}
	}

	public void mousePressed()
	{
		switchCamera();
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

	public String sketchRenderer()
	{
		return P3D;
	}

	@Override
	public void resume()
	{
		super.resume();
		if (mMapView != null) {
			mMapView.onResume();
		}

	}

	@Override
	public void pause()
	{
		super.pause();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.disconnect();
			mMapView.setVisibility(View.GONE);
		}
		mMapView.onPause();
	}

	@Override
	public void onStop()
	{
		super.onStop();
		mWidgetContainer.release();
		onDestroy();
		mMapView.onDestroy();
		//		ViewGroup container = (ViewGroup) this.getActivity().getWindow().getDecorView();
		//		println(container.getChildCount());

	}

	public String getDeviceInfo()
	{
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		String serial = Build.SERIAL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model + serial);
		} else {
			return capitalize(manufacturer) + model + serial;
		}
	}

	public String capitalize(String s)
	{
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);

		}
	}

	public void setupMap()
	{
		final PApplet pApplet = this;
		this.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				GoogleMapOptions options = new GoogleMapOptions();
				options.camera(new CameraPosition(new LatLng(52.393100,
						4.911760), 1, 0, 0));
				options.zoomControlsEnabled(true);
				options.zoomGesturesEnabled(true);
				options.tiltGesturesEnabled(true);
				options.compassEnabled(true);
				mMapView = new MapView(pApplet.getActivity(), options);
				mMapView.setLayoutParams(new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.FILL_PARENT,
						ViewGroup.LayoutParams.FILL_PARENT));
				mMapView.setLayoutParams(new ViewGroup.LayoutParams(
						sketchWidth(), sketchHeight() / 2));

				////	container.addView(mMapView);
				////	
				mMapView.onCreate(mSavedInstanceState);
				mMapView.onResume();
				////    
				setUpMapIfNeeded();
				setUpGoogleApiClientIfNeeded();
				mGoogleApiClient.connect();

				pApplet.getActivity().getWindow()
						.addContentView(mMapView, mMapView.getLayoutParams());
			}
		});
	}

	private void addMarkersToMap()
	{
		// Uses a colored icon.
		mInstallation01 = mMap.addMarker(new MarkerOptions().position(
				INSTALLATION01).title("Installation 1"));

		// Uses a custom icon with the info window popping out of the center of the icon.
		mInstallation02 = mMap.addMarker(new MarkerOptions().position(
				INSTALLATION02).title("Installation 2"));

		// Creates a draggable marker. Long press to drag.
		mInstallation03 = mMap.addMarker(new MarkerOptions().position(
				INSTALLATION03).title("Installation 3"));

		// A few more markers for good measure.
		mInstallation04 = mMap.addMarker(new MarkerOptions().position(
				INSTALLATION04).title("Installation 4"));

		mInstallation05 = mMap.addMarker(new MarkerOptions().position(
				INSTALLATION05).title("Instlallation 5"));

	}

	private void setUpMapIfNeeded()
	{
		// Do a null check to confirm that we have not already instantiated the map.

		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.

			mMap = mMapView.getMap();
			println("here");

			MapsInitializer.initialize(getActivity());

			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				println("setting up map");

				mMap.setMyLocationEnabled(true);
				mMap.setOnMyLocationButtonClickListener(this);
				addMarkersToMap();

				PolylineOptions options = new PolylineOptions()
						.add(new LatLng(INSTALLATION01.latitude,
								INSTALLATION01.longitude))
						.add(new LatLng(INSTALLATION02.latitude,
								INSTALLATION02.longitude))
						.add(new LatLng(INSTALLATION03.latitude,
								INSTALLATION03.longitude))
						.add(new LatLng(INSTALLATION04.latitude,
								INSTALLATION04.longitude))
						.add(new LatLng(INSTALLATION05.latitude,
								INSTALLATION05.longitude)).color(Color.BLUE)
						.width(15);

				mMutablePolyline = mMap.addPolyline(options);
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
						INSTALLATION03, 17));

			}
		}
	}

	private void setUpGoogleApiClientIfNeeded()
	{
		if (mGoogleApiClient == null) {
			println("setting us google api");
			mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
					.addApi(LocationServices.API).addConnectionCallbacks(this)
					.addOnConnectionFailedListener(this).build();
		}
	}

	/**
	 * Button to get current Location. This demonstrates how to get the current
	 * Location as required without needing to register a LocationListener.
	 */
	public void showMyLocation(View view)
	{
		if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
			String msg = "Location = "
					+ LocationServices.FusedLocationApi
							.getLastLocation(mGoogleApiClient);
			Toast.makeText(getActivity().getApplicationContext(), msg,
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Implementation of {@link LocationListener}.
	 */
	@Override
	public void onLocationChanged(Location location)
	{
		// mMessageView.setText("Location = " + location);
	}

	/**
	 * Callback called when connected to GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnected(Bundle connectionHint)
	{
		LocationServices.FusedLocationApi.requestLocationUpdates(
				mGoogleApiClient, REQUEST, this); // LocationListener
	}

	/**
	 * Callback called when disconnected from GCore. Implementation of
	 * {@link ConnectionCallbacks}.
	 */
	@Override
	public void onConnectionSuspended(int cause)
	{
		// Do nothing
	}

	/**
	 * Implementation of {@link OnConnectionFailedListener}.
	 */
	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		// Do nothing
	}

	@Override
	public boolean onMyLocationButtonClick()
	{
		Toast.makeText(this.getActivity(), "MyLocation button clicked",
				Toast.LENGTH_SHORT).show();
		// Return false so that we don't consume the event and the default behavior still occurs
		// (the camera animates to the user's current position).
		return false;
	}
}
