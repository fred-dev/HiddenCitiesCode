package com.example.HiddenCities;

import java.util.Vector;

import processing.core.PApplet;
import processing.test.Vuforia.PVuforia.VuforiaControl;
import processing.test.Vuforia.PVuforia.VuforiaException;
import processing.test.Vuforia.PVuforia.VuforiaSession;
import processing.test.Vuforia.VideoPlayerHelper.MEDIA_STATE;
import processing.test.Vuforia.utils.LoadingDialogHandler;
import processing.test.Vuforia.utils.Texture;
import processing.test.Vuforia.utils.VuforiaGLView;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.example.pappletfragmenttest.R;
import com.qualcomm.vuforia.CameraDevice;
import com.qualcomm.vuforia.DataSet;
import com.qualcomm.vuforia.HINT;
import com.qualcomm.vuforia.ImageTracker;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.STORAGE_TYPE;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.Tracker;
import com.qualcomm.vuforia.TrackerManager;
import com.qualcomm.vuforia.Vuforia;

public class VuforiaScene extends PApplet implements PVuforia.VuforiaControl
{
	public static final String		LOGTAG						= "VuforiaScene";

	VuforiaSession					mVuforiaSession;

	Activity						mActivity;

	// Helpers to detect events such as double tapping:
	public GestureDetector			mGestureDetector			= null;
	public SimpleOnGestureListener	mSimpleListener				= null;

	// Movie for the Targets:
	public static final int			NUM_TARGETS					= 7;
	public static final int			STONES						= 0;
	public static final int			CHIPS						= 1;

	public VideoPlayerHelper		mVideoPlayerHelper[]		= null;
	public int						mSeekPosition[]				= null;
	public boolean					mWasPlaying[]				= null;
	public String					mMoviesPaths[]				= null;
	public String					mThumbnailsPaths[]			= null;
	//targets
	public int						mTargetsIds[]				= null;
	public String					mTargetsNames[]				= null;

	// A boolean to indicate whether we come from full screen:
	public boolean					mReturningFromFullScreen	= false;

	// Our OpenGL view:
	public VuforiaGLView			mGlView;

	// Our renderer:
	public VuforiaSceneRenderer		mRenderer;

	// The textures we will use for rendering:
	public Vector<Texture>			mTextures;
	public Vector<Texture>			mUtilityTextures;

	DataSet							mDataSet		= null;
	String							mDataSetPath = null;

	public RelativeLayout			mUILayout;

	public boolean					mFlash						= false;
	public boolean					mContAutofocus				= false;
	public boolean					mExtendedTracking			= false;
	public boolean					mPlayFullscreenVideo		= false;

	public View						mFlashOptionView;

	//    public SampleAppMenu mSampleAppMenu;

	public LoadingDialogHandler		loadingDialogHandler		= new LoadingDialogHandler(
																		this);

	boolean							mIsDroidDevice				= false;
	boolean							mIsInitialized				= false;

	//MeOwn
	boolean							mIsSetup					= false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		mVuforiaSession = new VuforiaSession(this);

		mActivity = this.getActivity();

		//startLoadingAnimation();
		mVuforiaSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		mTextures = new Vector<Texture>();
		mUtilityTextures = new Vector<Texture>();
		loadTextures();

		// Create the gesture detector that will handle the single and
		// double taps:
		mSimpleListener = new SimpleOnGestureListener();
		mGestureDetector = new GestureDetector(
				mActivity.getApplicationContext(), mSimpleListener);

		mVideoPlayerHelper = new VideoPlayerHelper[NUM_TARGETS];
		mSeekPosition = new int[NUM_TARGETS];
		mWasPlaying = new boolean[NUM_TARGETS];
		mMoviesPaths = new String[NUM_TARGETS];
		mThumbnailsPaths = new String[NUM_TARGETS];
		mTargetsIds = new int[NUM_TARGETS];
		mTargetsNames = new String[NUM_TARGETS];

		for (int i = 0; i < NUM_TARGETS; i++) {
			mVideoPlayerHelper[i] = new VideoPlayerHelper();
			mVideoPlayerHelper[i].init();
			mVideoPlayerHelper[i].setActivity(mActivity);
			mMoviesPaths[i] = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/hiddenCities/Vuforia/video/porthole"
					+ (i + 1) + ".m4v";
			mThumbnailsPaths[i] = Environment.getExternalStorageDirectory()
					.getAbsolutePath()
					+ "/hiddenCities/Vuforia/images/porthole"
					+ (i + 1)
					+ ".png";
			mTargetsNames[i] = "porthole" + (i + 1);
		}
		
		mTargetsNames[0] = "stones";
		mTargetsNames[1] = "chips";
		
		mDataSetPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath()
				+ "/hiddenCities/Vuforia/data/DataSet.xml";

		//TODO load these from an xml

		mGestureDetector.setOnDoubleTapListener(new OnDoubleTapListener() {
			public boolean onDoubleTap(MotionEvent e)
			{
				// We do not react to this event
				return false;
			}

			public boolean onDoubleTapEvent(MotionEvent e)
			{
				// We do not react to this event
				return false;
			}

			// Handle the single tap
			public boolean onSingleTapConfirmed(MotionEvent e)
			{
				boolean isSingleTapHandled = false;
				// Do not react if the StartupScreen is being displayed
				for (int i = 0; i < NUM_TARGETS; i++) {
					// Verify that the tap happened inside the target
					if (mRenderer != null
							&& mRenderer.isTapOnScreenInsideTarget(i, e.getX(),
									e.getY())) {
						// Check if it is playable on texture
						if (mVideoPlayerHelper[i].isPlayableOnTexture()) {
							// We can play only if the movie was paused, ready
							// or stopped
							if ((mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PAUSED)
									|| (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.READY)
									|| (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.STOPPED)
									|| (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.REACHED_END)) {
								// Pause all other media
								pauseAll(i);

								// If it has reached the end then rewind
								if ((mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.REACHED_END))
									mSeekPosition[i] = 0;

								mVideoPlayerHelper[i].play(
										mPlayFullscreenVideo, mSeekPosition[i]);
								mSeekPosition[i] = VideoPlayerHelper.CURRENT_POSITION;
							} else if (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING) {
								// If it is playing then we pause it
								mVideoPlayerHelper[i].pause();
							}
						} else if (mVideoPlayerHelper[i].isPlayableFullscreen()) {
							// If it isn't playable on texture
							// Either because it wasn't requested or because it
							// isn't supported then request playback fullscreen.
							mVideoPlayerHelper[i].play(true,
									VideoPlayerHelper.CURRENT_POSITION);
						}

						isSingleTapHandled = true;

						// Even though multiple videos can be loaded only one
						// can be playing at any point in time. This break
						// prevents that, say, overlapping videos trigger
						// simultaneously playback.
						break;
					}
				}

				return isSingleTapHandled;
			}
		});
		mIsSetup=true;
		//		mMainLayout = overallLayout;
		return mGlView;
	}

	public void setup()
	{
		if(mIsSetup){
			return;
		}
		
	}

	// We want to load specific textures from the APK, which we will later
	// use for rendering.
	public void loadTextures()
	{
		for (int i = 0; i < NUM_TARGETS; i++) {
			mTextures.add(Texture.loadTextureFromPath(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/hiddenCities/Vuforia/images/porthole"
					+ (i + 1)
					+ ".jpg"));
			println("Texture "+Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/hiddenCities/Vuforia/images/porthole"
					+ (i + 1)
					+ ".jpg"+"   LOADED");
		}
		mUtilityTextures.add(Texture.loadTextureFromPath(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/hiddenCities/Vuforia/images/play.png"));
		mUtilityTextures.add(Texture.loadTextureFromPath(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/hiddenCities/Vuforia/images/busy.png"));
		mUtilityTextures.add(Texture.loadTextureFromPath(Environment
				.getExternalStorageDirectory().getAbsolutePath()
				+ "/hiddenCities/Vuforia/images/error.png"));
	}

	// Called when the activity will start interacting with the user.
	public void onResume()
	{
		Log.d(LOGTAG, "onResume");
		super.onResume();
		if(!mIsSetup){
			setup();
		}

		// This is needed for some Droid devices to force portrait
		if (mIsDroidDevice) {
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}

		try {
			mVuforiaSession.resumeAR();
			if (mIsInitialized && mContAutofocus) {
				CameraDevice.getInstance().setFocusMode(
						CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);
			}
		} catch (VuforiaException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Resume the GL view:
		if (mGlView != null) {
			println("mGlView != null");
			mGlView.setVisibility(View.VISIBLE);
			mGlView.onResume();
		}

		// Reload all the movies
		if (mRenderer != null) {
			for (int i = 0; i < NUM_TARGETS; i++) {
				if (!mReturningFromFullScreen) {
					mRenderer.requestLoad(i, mMoviesPaths[i], mSeekPosition[i],
							false);
				} else {
					mRenderer.requestLoad(i, mMoviesPaths[i], mSeekPosition[i],
							mWasPlaying[i]);
				}
			}
		}

		mReturningFromFullScreen = false;
	}

	// Called when returning from the full screen player
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (requestCode == 1) {

			mActivity
					.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

			if (resultCode == Activity.RESULT_OK) {
				// The following values are used to indicate the position in
				// which the video was being played and whether it was being
				// played or not:
				String movieBeingPlayed = data.getStringExtra("movieName");
				mReturningFromFullScreen = true;

				// Find the movie that was being played full screen
				for (int i = 0; i < NUM_TARGETS; i++) {
					if (movieBeingPlayed.compareTo(mMoviesPaths[i]) == 0) {
						mSeekPosition[i] = data.getIntExtra(
								"currentSeekPosition", 0);
						mWasPlaying[i] = data.getBooleanExtra("playing", false);
					}
				}
			}
		}
	}

	public void onConfigurationChanged(Configuration config)
	{
		Log.d(LOGTAG, "onConfigurationChanged");
		super.onConfigurationChanged(config);

		mVuforiaSession.onConfigurationChanged();
	}

	// Called when the system is about to start resuming a previous activity.
	public void onPause()
	{
		Log.d(LOGTAG, "onPause");
		super.onPause();

		if (mGlView != null) {
			mGlView.setVisibility(View.INVISIBLE);
			mGlView.onPause();
		}

		// Store the playback state of the movies and unload them:
		for (int i = 0; i < NUM_TARGETS; i++) {
			// If the activity is paused we need to store the position in which
			// this was currently playing:
			if (mVideoPlayerHelper[i].isPlayableOnTexture()) {
				mSeekPosition[i] = mVideoPlayerHelper[i].getCurrentPosition();
				mWasPlaying[i] = mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING ? true
						: false;
			}

			// We also need to release the resources used by the helper, though
			// we don't need to destroy it:
			if (mVideoPlayerHelper[i] != null)
				mVideoPlayerHelper[i].unload();
		}

		mReturningFromFullScreen = false;

		// Turn off the flash
		if (mFlashOptionView != null && mFlash) {
			// OnCheckedChangeListener is called upon changing the checked state
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
				((Switch) mFlashOptionView).setChecked(false);
			} else {
				((CheckBox) mFlashOptionView).setChecked(false);
			}
		}

		try {
			mVuforiaSession.pauseAR();
		} catch (VuforiaException e) {
			Log.e(LOGTAG, e.getString());
		}
	}

	// The final call you receive before your activity is destroyed.
	public void onDestroy()
	{
		Log.d(LOGTAG, "onDestroy");
		super.onDestroy();

		for (int i = 0; i < NUM_TARGETS; i++) {
			// If the activity is destroyed we need to release all resources:
			if (mVideoPlayerHelper[i] != null)
				mVideoPlayerHelper[i].deinit();
			mVideoPlayerHelper[i] = null;
		}

		try {
			mVuforiaSession.stopAR();
		} catch (VuforiaException e) {
			Log.e(LOGTAG, e.getString());
		}

		// Unload texture:
		mTextures.clear();
		mTextures = null;

		System.gc();
	}

	// Pause all movies except one
	// if the value of 'except' is -1 then
	// do a blanket pause
	public void pauseAll(int except)
	{
		// And pause all the playing videos:
		for (int i = 0; i < NUM_TARGETS; i++) {
			// We can make one exception to the pause all calls:
			if (i != except) {
				// Check if the video is playable on texture
				if (mVideoPlayerHelper[i].isPlayableOnTexture()) {
					// If it is playing then we pause it
					mVideoPlayerHelper[i].pause();
				}
			}
		}
	}

	// Do not exit immediately and instead show the startup screen
	public void onBackPressed()
	{
		pauseAll(-1);
		super.onBackPressed();
	}

	public void startLoadingAnimation()
	{
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		mUILayout = (RelativeLayout) new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		mUILayout.setVisibility(View.VISIBLE);
		mUILayout.setBackgroundColor(Color.BLACK);

		// Gets a reference to the loading dialog
		//		loadingDialogHandler.mLoadingDialogContainer = mUILayout
		//				.findViewById(R.id.loading_indicator);

		// Shows the loading indicator at start
		loadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

		// Adds the inflated layout to the view
		mActivity.addContentView(mUILayout, lp);
	}

	// Initializes AR application components.
	public void initApplicationAR()
	{
		// Create OpenGL ES view:
		int depthSize = 16;
		int stencilSize = 0;
		boolean translucent = Vuforia.requiresAlpha();

		mGlView = new VuforiaGLView(mActivity);
		mGlView.init(translucent, depthSize, stencilSize);
		
		mRenderer = new VuforiaSceneRenderer(this, mVuforiaSession);
		mRenderer.setTextures(mTextures);
		mRenderer.setUtilityTextures(mUtilityTextures);

		// The renderer comes has the OpenGL context, thus, loading to texture
		// must happen when the surface has been created. This means that we
		// can't load the movie from this thread (GUI) but instead we must
		// tell the GL thread to load it once the surface has been created.
		for (int i = 0; i < NUM_TARGETS; i++) {
			mRenderer.setVideoPlayerHelper(i, mVideoPlayerHelper[i]);
			mRenderer.requestLoad(i, mMoviesPaths[i], 0, false);
		}

		mGlView.setRenderer(mRenderer);

		for (int i = 0; i < NUM_TARGETS; i++) {
			float[] temp = { 0f, 0f };
			mRenderer.targetPositiveDimensions[i].setData(temp);
			mRenderer.videoPlaybackTextureID[i] = -1;
		}

	}

	public boolean onTouchEvent(MotionEvent event)
	{
		mGestureDetector.onTouchEvent(event);
		return surfaceTouchEvent(event);
	}

	@Override
	public boolean doInitTrackers()
	{
		// Indicate if the trackers were initialized correctly
		boolean result = true;

		// Initialize the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		Tracker tracker = trackerManager.initTracker(ImageTracker
				.getClassType());
		if (tracker == null) {
			Log.d(LOGTAG, "Failed to initialize ImageTracker.");
			result = false;
		}

		return result;
	}

	@Override
	public boolean doLoadTrackersData()
	{
		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());
		if (imageTracker == null) {
			Log.d(LOGTAG,
					"Failed to load tracking data set because the ImageTracker has not been initialized.");
			return false;
		}

		// Create the data sets:
		mDataSet = imageTracker.createDataSet();
		if (mDataSet == null) {
			Log.d(LOGTAG, "Failed to create a new tracking data.");
			return false;
		}

		// Load the data sets:
		if (!mDataSet.load(mDataSetPath,
				STORAGE_TYPE.STORAGE_ABSOLUTE)) {
			Log.d(LOGTAG, "Failed to load data set.");
			return false;
		}

		// Activate the data set:
		if (!imageTracker.activateDataSet(mDataSet)) {
			Log.d(LOGTAG, "Failed to activate data set.");
			return false;
		}

		Log.d(LOGTAG, "Successfully loaded and activated data set.");
		return true;
	}

	@Override
	public boolean doStartTrackers()
	{
		// Indicate if the trackers were started correctly
		boolean result = true;

		Tracker imageTracker = TrackerManager.getInstance().getTracker(
				ImageTracker.getClassType());
		if (imageTracker != null) {
			imageTracker.start();
			Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 2);
		} else
			result = false;

		return result;
	}

	@Override
	public boolean doStopTrackers()
	{
		// Indicate if the trackers were stopped correctly
		boolean result = true;

		Tracker imageTracker = TrackerManager.getInstance().getTracker(
				ImageTracker.getClassType());
		if (imageTracker != null)
			imageTracker.stop();
		else
			result = false;

		return result;
	}

	@Override
	public boolean doUnloadTrackersData()
	{
		// Indicate if the trackers were unloaded correctly
		boolean result = true;

		// Get the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		ImageTracker imageTracker = (ImageTracker) trackerManager
				.getTracker(ImageTracker.getClassType());
		if (imageTracker == null) {
			Log.d(LOGTAG,
					"Failed to destroy the tracking data set because the ImageTracker has not been initialized.");
			return false;
		}

		if (mDataSet != null) {
			if (imageTracker.getActiveDataSet() == mDataSet
					&& !imageTracker.deactivateDataSet(mDataSet)) {
				Log.d(LOGTAG,
						"Failed to destroy the tracking data set StonesAndChips because the data set could not be deactivated.");
				result = false;
			} else if (!imageTracker.destroyDataSet(mDataSet)) {
				Log.d(LOGTAG,
						"Failed to destroy the tracking data set StonesAndChips.");
				result = false;
			}

			mDataSet = null;
		}

		return result;
	}

	@Override
	public boolean doDeinitTrackers()
	{
		// Indicate if the trackers were deinitialized correctly
		boolean result = true;

		// Deinit the image tracker:
		TrackerManager trackerManager = TrackerManager.getInstance();
		trackerManager.deinitTracker(ImageTracker.getClassType());

		return result;
	}

	@Override
	public void onInitARDone(VuforiaException exception)
	{

		if (exception == null) {
			initApplicationAR();

			mRenderer.mIsActive = true;

			// Now add the GL surface view. It is important
			// that the OpenGL ES surface view gets added
			// BEFORE the camera is started and video
			// background is configured.
			this.getActivity().addContentView(
					mGlView,
					new LayoutParams(LayoutParams.MATCH_PARENT,
							LayoutParams.MATCH_PARENT));

			// Sets the UILayout to be drawn in front of the camera
			//mUILayout.bringToFront();

			// Hides the Loading Dialog
			loadingDialogHandler
					.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

			// Sets the layout background to transparent
			//mUILayout.setBackgroundColor(Color.TRANSPARENT);
			try {
				mVuforiaSession.startAR(CameraDevice.CAMERA.CAMERA_DEFAULT);
//				println("mVuforiaSession.startAR");
			} catch (VuforiaException e) {
				Log.e(LOGTAG, e.getString());
			}

			boolean result = CameraDevice.getInstance().setFocusMode(
					CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

			if (result)
				mContAutofocus = true;
			else
				Log.e(LOGTAG, "Unable to enable continuous autofocus");

//						mSampleAppMenu = new SampleAppMenu(this, this, "Video Playback",
//								mGlView, mUILayout, null);
//						setSampleAppMenuSettings();

			mIsInitialized = true;

		} else {
			Log.e(LOGTAG, exception.getString());
			mActivity.finish();
		}

	}

	@Override
	public void onQCARUpdate(State state)
	{
	}

	final public static int	CMD_BACK				= -1;
	final public static int	CMD_EXTENDED_TRACKING	= 1;
	final public static int	CMD_AUTOFOCUS			= 2;
	final public static int	CMD_FLASH				= 3;
	final public static int	CMD_FULLSCREEN_VIDEO	= 4;
	final public static int	CMD_CAMERA_FRONT		= 5;
	final public static int	CMD_CAMERA_REAR			= 6;

	public boolean commandProcess(int command)
	{

		boolean result = true;

		switch (command) {
			case CMD_BACK:
				mActivity.finish();
			break;

			case CMD_FLASH:
				result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);

				if (result) {
					mFlash = !mFlash;
				} else {
					//					showToast(getString(mFlash ? R.string.menu_flash_error_off
					//							: R.string.menu_flash_error_on));
					//					Log.e(LOGTAG,
					//							getString(mFlash ? R.string.menu_flash_error_off
					//									: R.string.menu_flash_error_on));
				}
			break;

			case CMD_AUTOFOCUS:

				if (mContAutofocus) {
					result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);

					if (result) {
						mContAutofocus = false;
					} else {
						//						showToast(getString(R.string.menu_contAutofocus_error_off));
						//						Log.e(LOGTAG,
						//								getString(R.string.menu_contAutofocus_error_off));
					}
				} else {
					result = CameraDevice.getInstance().setFocusMode(
							CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

					if (result) {
						mContAutofocus = true;
					} else {
						//						showToast(getString(R.string.menu_contAutofocus_error_on));
						//						Log.e(LOGTAG,
						//								getString(R.string.menu_contAutofocus_error_on));
					}
				}

			break;

			case CMD_FULLSCREEN_VIDEO:
				mPlayFullscreenVideo = !mPlayFullscreenVideo;

				for (int i = 0; i < mVideoPlayerHelper.length; i++) {
					if (mVideoPlayerHelper[i].getStatus() == MEDIA_STATE.PLAYING) {
						// If it is playing then we pause it
						mVideoPlayerHelper[i].pause();

						mVideoPlayerHelper[i].play(true, mSeekPosition[i]);
					}
				}
			break;

			case CMD_CAMERA_FRONT:
			case CMD_CAMERA_REAR:

				// Turn off the flash
				if (mFlashOptionView != null && mFlash) {
					// OnCheckedChangeListener is called upon changing the checked state
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
						((Switch) mFlashOptionView).setChecked(false);
					} else {
						((CheckBox) mFlashOptionView).setChecked(false);
					}
				}

				doStopTrackers();
				CameraDevice.getInstance().stop();
				CameraDevice.getInstance().deinit();
				try {
					mVuforiaSession
							.startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA.CAMERA_FRONT
									: CameraDevice.CAMERA.CAMERA_BACK);
				} catch (VuforiaException e) {
					showToast(e.getString());
					Log.e(LOGTAG, e.getString());
					result = false;
				}
				doStartTrackers();
			break;

			case CMD_EXTENDED_TRACKING:
				for (int tIdx = 0; tIdx < mDataSet
						.getNumTrackables(); tIdx++) {
					Trackable trackable = mDataSet
							.getTrackable(tIdx);

					if (!mExtendedTracking) {
						if (!trackable.startExtendedTracking()) {
							Log.e(LOGTAG,
									"Failed to start extended tracking target");
							result = false;
						} else {
							Log.d(LOGTAG,
									"Successfully started extended tracking target");
						}
					} else {
						if (!trackable.stopExtendedTracking()) {
							Log.e(LOGTAG,
									"Failed to stop extended tracking target");
							result = false;
						} else {
							Log.d(LOGTAG,
									"Successfully started extended tracking target");
						}
					}
				}

				if (result)
					mExtendedTracking = !mExtendedTracking;

			break;

		}

		return result;
	}

	public void showToast(String text)
	{
		Toast.makeText(this.getActivity(), text, Toast.LENGTH_SHORT).show();
	}

}
