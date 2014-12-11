package com.example.HiddenCities;

import java.util.List;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Size;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

public class MyJavaCameraView extends JavaCameraView
{

	//		public byte mBuffer[];
	//	    public Mat[] mFrameChain;
	//	    public int mChainIdx = 0;
	//	    public Thread mThread;
	//	    public boolean mStopThread;
	//
	//	    public Camera mCamera;
	//	    public SurfaceTexture mSurfaceTexture;

	public MyJavaCameraView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public MyJavaCameraView(Context context, int aId)
	{
		super(context, aId);
	}

	public List<String> getEffectList()
	{
		return mCamera.getParameters().getSupportedColorEffects();
	}

	public boolean isEffectSupported()
	{
		return (mCamera.getParameters().getColorEffect() != null);
	}

	public String getEffect()
	{
		return mCamera.getParameters().getColorEffect();
	}

	public void setEffect(String effect)
	{
		Camera.Parameters params = mCamera.getParameters();
		params.setColorEffect(effect);
		mCamera.setParameters(params);
	}

	public List<android.hardware.Camera.Size> getResolutionList()
	{
		return mCamera.getParameters().getSupportedPreviewSizes();
	}

	public void setResolution(Size resolution)
	{
		disconnectCamera();
		mMaxHeight = (int) resolution.height;
		mMaxWidth = (int) resolution.width;
		connectCamera(getWidth(), getHeight());
	}

	public void disconnect()
	{
		disconnectCamera();
	}

	public void connect()
	{
		connectCamera(getWidth(), getHeight());
	}

	public void setPreviewSize(int aWidth, int aHeight)
	{
		mCamera.getParameters().setPreviewSize(aWidth, aHeight);
	}

	public android.hardware.Camera.Size getResolution()
	{
		return mCamera.getParameters().getPreviewSize();
	}

}
