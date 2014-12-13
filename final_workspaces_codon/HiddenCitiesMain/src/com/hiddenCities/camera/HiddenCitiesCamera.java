package com.hiddenCities.camera;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.hiddenCities.R;
import com.hiddenCities.main.HiddenCitiesMain;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;

import java.io.IOException;

/**
 * Take a picture directly from inside the app using this fragment.
 * 
 * Reference: http://developer.android.com/training/camera/cameradirect.html
 * Reference:
 * http://stackoverflow.com/questions/7942378/android-camera-will-not-
 * work-startpreview-fails Reference:
 * http://stackoverflow.com/questions/10913181/camera-preview-is-not-restarting
 * 
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
public class HiddenCitiesCamera extends Fragment implements TextureView.SurfaceTextureListener
{

	/**
	 * More or less straight out of TextureView's doc.
	 * <p>
	 * TODO: add options for different display sizes, frame rates, camera
	 * selection, etc.
	 */
	HiddenCitiesMain			mActivity;
	View						mView;
	private Camera				mCamera;
	private TextureView			mTextureView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mActivity = (HiddenCitiesMain) getActivity();
		mView = inflater.inflate(R.layout.camera_layout, container, false);
		mTextureView = (TextureView) mView.findViewById(R.id.textureView);
		mTextureView.setSurfaceTextureListener(this);

		return mView;
	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
	{
		mCamera = Camera.open();
		if (mCamera == null) {
			// Seeing this on Nexus 7 2012 -- I guess it wants a rear-facing camera, but
			// there isn't one.  TODO: fix
			throw new RuntimeException("Default camera not available");
		}

		try {
			mCamera.setPreviewTexture(surface);
			mCamera.startPreview();
		} catch (IOException ioe) {
			// Something bad happened
		}
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
	{
		// Ignored, Camera does all the work for us
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
	{
		mCamera.stopPreview();
		mCamera.release();
		return true;
	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture surface)
	{
		// Invoked every time there's a new Camera preview frame
		//Log.d(TAG, "updated, ts=" + surface.getTimestamp());
	}
}