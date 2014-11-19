package com.panframe.android.samples.SimplePlayer;

import java.util.*;

import com.example.pappletfragmenttest.R;
import com.panframe.android.lib.*;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.speech.RecognizerIntent;
//import android.support.v4.app.*;
//import android.support.v4.app.LoaderManager.*;
//import android.support.v4.content.*;
import android.util.Log;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.AdapterView.*;




public class CompassVideo extends Fragment implements PFAssetObserver {

	PFView				_pfview;
	PFAsset 			_pfasset;
    PFNavigationMode 	_currentNavigationMode = PFNavigationMode.MOTION;
	
	boolean 			_updateThumb = true;;
    Timer 				_scrubberMonitorTimer;    

    ViewGroup 			_frameContainer;

	
	/**
	 * Creation and initalization of the Activitiy.
	 * Initializes variables, listeners, and starts request of a movie list.
	 *
	 * @param  savedInstanceState  a saved instance of the Bundle
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
//    	setContentView(R.layout.activity_main);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		getActivity().requestWindowFeature(Window.FEATURE_NO_TITLE);
//		getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        _frameContainer = (ViewGroup) getActivity().getWindow().getDecorView();
        _frameContainer.setBackgroundColor(0xFF000000);
		loadVideo("storage/sdcard0/hiddenCities/video/360.mp4");
		showControls(false);	
		_pfasset.play();
//	    Activity    faActivity  = (Activity)    super.getActivity();
	    // Replace LinearLayout by the type of the root element of the layout you're trying to load
//	    LinearLayout        llLayout    = inflater.inflate(R.layout.activity_papplet_fragment_test, container, false);
	    // Of course you will want to faActivity and llLayout in the class and not this method to access them in the rest of
	    // the class, just initialize them here

	    // Content of previous onCreate() here
	    // ...

	    // Don't use this method, it's handled by inflater.inflate() above :
	    // setContentView(R.layout.activity_layout);

	    // The FragmentActivity doesn't contain the layout directly so we must use our instance of     LinearLayout :
	    //llLayout.findViewById(R.id.someGuiElement);
	    // Instead of :
	    // findViewById(R.id.someGuiElement);
//	    return inflater.inflate(R.layout.activity_papplet_fragment_test, container, false);// We must return the loaded Layout
		return null;
	}
   
	/**
	 * Show/Hide the playback controls
	 *
	 * @param  bShow  Show or hide the controls. Pass either true or false.
	 */
    public void showControls(boolean bShow)
    {
    	
		if (_pfview != null)
		{
			if (!_pfview.supportsNavigationMode(PFNavigationMode.MOTION))
//				_touchButton.setVisibility(View.GONE);
            Log.d("SimplePlayer","Not supported nav");
		}
    }
    
    
	/**
	 * Start the video with a local file path
	 *
	 * @param  filename  The file path on device storage
	 */
    public void loadVideo(String filename)
    {
		
        _pfview = PFObjectFactory.view(this.getActivity());               
        _pfasset = PFObjectFactory.assetFromUri(this.getActivity(), Uri.parse(filename), this);
        
        _pfview.displayAsset(_pfasset);
        _pfview.setNavigationMode(_currentNavigationMode);

        _frameContainer.addView(_pfview.getView(), 0);     

    }
	
	/**
	 * Status callback from the PFAsset instance.
	 * Based on the status this function selects the appropriate action.
	 *
	 * @param  asset  The asset who is calling the function
	 * @param  status The current status of the asset.
	 */
	public void onStatusMessage(final PFAsset asset, PFAssetStatus status) {
		switch (status)
		{
			case LOADED:
				Log.d("SimplePlayer", "Loaded");
				break;
			case DOWNLOADING:
				Log.d("SimplePlayer", "Downloading 360 movie: "+ _pfasset.getDownloadProgress()+" percent complete");
				break;
			case DOWNLOADED:
				Log.d("SimplePlayer", "Downloaded to "+asset.getUrl());
				break;
			case DOWNLOADCANCELLED:
				Log.d("SimplePlayer", "Download cancelled");
				break;
			case PLAYING:
				Log.d("SimplePlayer", "Playing");
		        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				
				break;
			case PAUSED:
				Log.d("SimplePlayer", "Paused");
	
				break;
			case STOPPED:
				Log.d("SimplePlayer", "Stopped");
				
				getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				break;
			case COMPLETE:
				Log.d("SimplePlayer", "Complete");

				getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
				break;
			case ERROR:
				Log.d("SimplePlayer", "Error");
				break;
		}
	}
	
	/**
	 * Click listener for the play/pause button
	 *
	 */
	private OnClickListener playListener = new OnClickListener() {
		public void onClick(View v) {
			if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
			{
				_pfasset.pause();
			}
			else
				_pfasset.play();
		}
	};
	
	
    
	/**
	 * Click listener for the stop/back button
	 *
	 */
	private OnClickListener stopListener = new OnClickListener() {
		public void onClick(View v) {
			_pfasset.stop();
		}
	};

	/**
	 * Click listener for the navigation mode (touch/motion (if available))
	 *
	 */
	private OnClickListener touchListener = new OnClickListener() {
		public void onClick(View v) {
//			if (_pfview != null)
//			{

			}
//		}
	};
		
	/**
	 * Setup the options menu
	 *
	 * @param menu The options menu
	 */
    public boolean onCreateOptionsMenu(Menu menu) {
       
        return true;
    }
    
	/**
	 * Called when pausing the app.
	 * This function pauses the playback of the asset when it is playing.
	 *
	 */
    public void onPause() {
        super.onPause(); 
        if (_pfasset != null)
        {
	        if (_pfasset.getStatus() == PFAssetStatus.PLAYING)
	        	_pfasset.pause();
        }
    }
    
    public void onStop()
    {
    	super.onStop();
    	_pfasset.stop();
    	_frameContainer.removeView(_pfview.getView());
    }

	/**
	 * Called when a previously created loader is being reset, and thus making its data unavailable.
	 * 
	 * @param seekbar The SeekBar whose progress has changed
	 * @param progress The current progress level.
	 * @param fromUser True if the progress change was initiated by the user.
	 * 
	 */
	public void onProgressChanged (SeekBar seekbar, int progress, boolean fromUser) {
	}

	/**
	 * Notification that the user has started a touch gesture.
	 * In this function we signal the timer not to update the playback thumb while we are adjusting it.
	 * 
	 * @param seekbar The SeekBar in which the touch gesture began
	 * 
	 */
	public void onStartTrackingTouch(SeekBar seekbar) {
		_updateThumb = false;
	}

	/**
	 * Notification that the user has finished a touch gesture. 
	 * In this function we request the asset to seek until a specific time and signal the timer to resume the update of the playback thumb based on playback.
	 * 
	 * @param seekbar The SeekBar in which the touch gesture began
	 * 
	 */
	
	public void onStopTrackingTouch(SeekBar seekbar) {
		
		_updateThumb = true;
	}    
	
	public void  onStartCommand(Intent intent, int flags, int startId) {
		_pfasset.play();
	}
    
}
