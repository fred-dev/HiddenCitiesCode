package processing.test.ArUcoTest;

import processing.core.PApplet;
import processing.core.PImage;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.os.Environment;
import android.view.TextureView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class VideoBlobManager
{
	PApplet					mPApplet;
	String					mVideoPath;
	VideoBlob				mBlob;
	TextureView				mTextureView;
	RelativeLayout			mMainLayout;
	Matrix					mTextureViewTransformMatrix;
	boolean					mIsGettingSurfaceTexture	= false;
	boolean					mIsGettingBitmap			= false;
	Bitmap					mBitmap = null;
	SurfaceTexture			mSurfaceTexture = null;
	PImage					mPImage = null;
	BitmapGetter			mBitmapGetter;
	SurfaceTextureGetter	mSurfaceTextureGetter;

	public VideoBlobManager(PApplet aPApplet, String aPath)
	{
		mPApplet = aPApplet;
		mVideoPath = aPath;
		mSurfaceTextureGetter = new SurfaceTextureGetter();
		mBitmapGetter = new BitmapGetter();
		setupTextureView();
	}

	public void setupTextureView()
	{
		final RelativeLayout mainLayout = (RelativeLayout) mPApplet.getView();
		final LinearLayout pappletLayout = (LinearLayout) mainLayout
				.getChildAt(0);

		mPApplet.getActivity().runOnUiThread(new Runnable() {
			public void run()
			{
				LayoutParams textureViewlayoutParams = new RelativeLayout.LayoutParams(
						LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				//		this.getActivity().getWindow().addContentView(tv, lp);
				mTextureView = new TextureView(mPApplet.getActivity());
				mTextureView.setOpaque(true);
				mTextureView.setLayoutParams(textureViewlayoutParams);
				mBlob = new VideoBlob(mTextureView, mVideoPath, 0);
				//				papplet.getActivity().getWindow().addContentView(mTextureView, textureViewlayoutParams);
				mainLayout.addView(mTextureView, textureViewlayoutParams);
				mTextureViewTransformMatrix = mTextureView.getMatrix();
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
		Bitmap	bitmap	= null;
		boolean isRunning=false;
		BitmapGetter()
		{

		}

		@Override
		public void run()
		{
			isRunning=true;
			bitmap = mTextureView.getBitmap();
			isRunning=false;
		}

		public Bitmap getBitmap()
		{
			return bitmap;
		}

	}

	class SurfaceTextureGetter implements Runnable
	{
		SurfaceTexture	surfaceTexture	= null;
		boolean isRunning=false;
		SurfaceTextureGetter()
		{

		}

		@Override
		public void run()
		{
			isRunning=true;
			surfaceTexture = mTextureView.getSurfaceTexture();
			isRunning=false;
		}

		public SurfaceTexture getSurfaceTexture()
		{
			return surfaceTexture;
		}

	}

}
