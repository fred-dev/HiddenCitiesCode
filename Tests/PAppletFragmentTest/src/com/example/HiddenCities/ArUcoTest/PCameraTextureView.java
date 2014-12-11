package com.example.HiddenCities;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Surface;
import android.view.TextureView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import processing.core.PApplet;
import processing.core.PImage;
import processing.test.ArUcoTest.VideoBlobManager.BitmapGetter;
import processing.test.ArUcoTest.VideoBlobManager.SurfaceTextureGetter;

public class PCameraTextureView implements TextureView.SurfaceTextureListener
{
	PApplet					mPApplet;
	Camera					mCamera;
	Camera.Parameters		mCameraParams;
	Camera.Size				mPreviewSize;
	int						mCameraId;
	TextureView				mTextureView;
	boolean					mIsTextureViewMade			= false;
	boolean					mIsStarted					= false;
	RelativeLayout			mMainLayout;
	Matrix					mTextureViewTransformMatrix;
	boolean					mIsGettingSurfaceTexture	= false;
	boolean					mIsGettingBitmap			= false;
	Bitmap					mBitmap						= null;
	SurfaceTexture			mSurfaceTexture				= null;
	Surface					mSurface;
	PImage					mPImage						= null;
	BitmapGetter			mBitmapGetter;
	SurfaceTextureGetter	mSurfaceTextureGetter;

	PCameraTextureView		mSelf;

	public PCameraTextureView(PApplet aPApplet, int aCameraId)
	{
		mSelf = this;
		mPApplet = aPApplet;
		mCameraId = aCameraId;

		mSurfaceTextureGetter = new SurfaceTextureGetter();
		mBitmapGetter = new BitmapGetter();
		setupTextureView();

	}

	public void setupTextureView()
	{
		PApplet.println("is setting up Texture view");
		final RelativeLayout mainLayout = (RelativeLayout) mPApplet.getView();

		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				LayoutParams textureViewlayoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				mTextureView = new TextureView(mPApplet.getActivity());
//				mTextureView.setOpaque(true);
				mTextureView.setLayoutParams(textureViewlayoutParams);
				mainLayout.addView(mTextureView, textureViewlayoutParams);
				mTextureViewTransformMatrix = mTextureView.getMatrix();
				mTextureView.setSurfaceTextureListener(mSelf);
				//mTextureView.setSurfaceTexture(mSurfaceTexture);
				PApplet.println("finished making Texture view");
				mIsTextureViewMade = true;
			}

		});

	}

	public void setPreviewResolution(final int aWidth, final int aHeight)
	{
		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				if (mIsStarted) {
					mCamera.stopPreview();
					mCameraParams.setPreviewSize(aWidth, aHeight);
					mPreviewSize = mCameraParams.getPreviewSize();
					mCamera.startPreview();
					mTextureView.setLayoutParams(new LayoutParams(
							mPreviewSize.width, mPreviewSize.height));
					PApplet.println("New Resolution is " + mPreviewSize.width
							+ " X " + mPreviewSize.height);
				}
			}
		});
	}

	public void update()
	{
		if (mIsGettingBitmap && !mBitmapGetter.isRunning) {
			mPApplet.getActivity().runOnUiThread(mBitmapGetter);
		}
		if (mIsGettingSurfaceTexture && !mSurfaceTextureGetter.isRunning) {
			mPApplet.getActivity().runOnUiThread(mSurfaceTextureGetter);
		}
	}

	public void applyMatrix(Matrix aMatrix)
	{
		mTextureViewTransformMatrix = aMatrix;
		applyMatrix();
	}

	public void applyMatrix()
	{
		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				mTextureView.setTransform(mTextureViewTransformMatrix);
			}
		});
	}

	public void setPivotPoint(final float aX, final float aY)
	{
		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				mTextureView.setPivotX(aX);
				mTextureView.setPivotY(aY);
			}
		});
	}

	public void setTranslation(final float aX, final float aY)
	{
		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				mTextureView.setTranslationX(aX);
				mTextureView.setTranslationY(aY);
			}
		});
	}

	public void setRotation(final float aDegreeX, final float aDegreeY)
	{
		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				mTextureView.setRotationX(aDegreeX);
				mTextureView.setRotationY(aDegreeY);
			}
		});
	}

	public void setRotation(final float aDegrees)
	{
		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				mTextureView.setRotation(aDegrees);
			}
		});
	}

	public Matrix getMatrix()
	{
		return mTextureViewTransformMatrix;
	}

	public Bitmap getBitmap()
	{

		return mBitmapGetter.getBitmap();
	}

	public SurfaceTexture getSurfaceTexture()
	{

		return mSurfaceTextureGetter.getSurfaceTexture();
	}

	public boolean isGettingBitmap()
	{
		return mIsGettingBitmap;
	}

	public boolean isGettingSurfacetexture()
	{
		return mIsGettingSurfaceTexture;
	}

	public void setIsGettingBitmap(boolean aIsGettingBitmap)
	{
		mIsGettingBitmap = aIsGettingBitmap;
	}

	public void setIsGettingSurfaceTexture(boolean aIsGettingSurfaceTexture)
	{
		mIsGettingSurfaceTexture = aIsGettingSurfaceTexture;
	}

	class BitmapGetter implements Runnable
	{
		Bitmap	bitmap		= null;
		boolean	isRunning	= false;

		BitmapGetter()
		{

		}

		@Override
		public void run()
		{
			isRunning = true;
			bitmap = mTextureView.getBitmap();
			isRunning = false;
		}

		public Bitmap getBitmap()
		{
			return bitmap;
		}

	}

	class SurfaceTextureGetter implements Runnable
	{
		SurfaceTexture	surfaceTexture	= null;
		boolean			isRunning		= false;

		SurfaceTextureGetter()
		{

		}

		@Override
		public void run()
		{
			isRunning = true;
			surfaceTexture = mTextureView.getSurfaceTexture();
			isRunning = false;
		}

		public SurfaceTexture getSurfaceTexture()
		{
			return surfaceTexture;
		}

	}

	@Override
	public void onSurfaceTextureAvailable(SurfaceTexture aSurfaceTexture,
			int aWidth, int aHeight)
	{
		// TODO Auto-generated method stub
		PApplet.println("onSurfaceTextureAvailable " + aWidth + " X " + aHeight);
		try {
			mCamera = Camera.open(mCameraId);
			mCamera.setPreviewTexture(aSurfaceTexture);
			mCamera.startPreview();
			mCameraParams = mCamera.getParameters();
			mPreviewSize = mCamera.getParameters().getPreviewSize();
			this.setPreviewResolution(1920, 1080);
		} catch (java.io.IOException ex) {
			PApplet.println(ex);
		}
		//		mTextureView.setSurfaceTexture(aSurfaceTexture);
		mTextureView.setLayoutParams(new LayoutParams(mPreviewSize.width,
				mPreviewSize.height));
		mTextureView.setAlpha(1.0f);
		mIsStarted = true;
	}

	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture arg0)
	{
		mCamera.stopPreview();
		mCamera.release();
		return true;
	}

	@Override
	public void onSurfaceTextureSizeChanged(SurfaceTexture arg0, int arg1,
			int arg2)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onSurfaceTextureUpdated(SurfaceTexture arg0)
	{
		// TODO Auto-generated method stub

	}
}
