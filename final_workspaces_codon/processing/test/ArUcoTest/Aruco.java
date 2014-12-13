package processing.test.ArUcoTest;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.sql.Date;
import java.util.List;
import java.util.Vector;
import java.lang.System;

import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.JavaCameraView;
import org.opencv.core.CvException;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import com.example.pappletfragmenttest.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.view.SurfaceView;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;

import es.ava.aruco.*;
import es.ava.aruco.exceptions.ExtParamException;
import processing.test.ArUcoTest.MyBoard;
import processing.test.ArUcoTest.MyBoardConfiguration;
import processing.test.ArUcoTest.MyCameraParameters;
import processing.test.ArUcoTest.MyJavaCameraView;

public class Aruco implements Runnable, CvCameraViewListener2
{
	PApplet					mPApplet;
	ArucoListener			mListener;
	boolean					mIsDetecting			= true;
	boolean					mIsProcessing			= false;
	boolean					mIsNewFrameAvailable	= false;
	Vector<MyMarker>		mDetectedMarkers;
	MyBoard					mBoardDetected;
	Mat						mFrame					= null;
	int						mIdSelected;
	MyCameraParameters		mCamParam;
	String					mCamParamPath;
	float					mMarkerSizeMeters		= 0.40f;	// 40cm
	MyBoardConfiguration	mBoardConfig			= null;
	MyBoard					mBoard;
	boolean					mLookForBoard			= false;
	MyMarkerDetector		mDetector;
	MyBoardDetector			mBoardDetector;
	VideoCapture			mCVCamera;
	MyJavaCameraView		mJavaCameraView;
	int						mCameraId				= 0;
	boolean					mIsJavaCameraStarted	= false;
	boolean					mIsJavaCameraOpened		= false;
	Aruco					mSelf;
	Vector<double[]>		mMatrices;
	Vector<Vector<Point>>	mCubePoints;

	Thread					mThread;

	public Aruco()
	{

	}

	public Aruco(PApplet aPApplet, String aCamParamPath)
	{
		mPApplet = aPApplet;
		mCamParamPath = aCamParamPath;
		mListener = (ArucoListener) aPApplet;
		mSelf = this;
	}

	public void setup()
	{
		PApplet.println("Aruco is setting up");
		PApplet.println(mCamParamPath);
		mCamParam = new MyCameraParameters();
		mCamParam.readFromXML(mCamParamPath);
		PApplet.println("Camera Calibration xml validity: "
				+ mCamParam.isValid());
		mBoard = new MyBoard();
		mDetectedMarkers = new Vector<MyMarker>();
		mBoardDetected = new MyBoard();
		mDetector = new MyMarkerDetector();
		mBoardDetector = new MyBoardDetector();

		mMatrices = new Vector<double[]>();
		for (int i = 0; i < 20; i++) {
			mMatrices.add(new double[16]);
		}
		mCubePoints = new Vector<Vector<Point>>();
		for (int i = 0; i < 20; i++) {
			mCubePoints.add(new Vector<Point>());
		}

		PApplet.println("Aruco is set up");
	}

	public void start()
	{
		PApplet.println("Aruco is starting");
		mThread = new Thread(this);
		mThread.start();
	}

	@Override
	public void run()
	{
		String TAG = "running";
		Log.i(TAG, "Starting processing thread");
		while (mIsDetecting) {

			//			Bitmap bmp = null;

			synchronized (this) {
				//				if (mCVCamera == null)
				//					break;
				//
				//				if (!mCVCamera.grab()) {
				//					Log.e(TAG, "mCamera.grab() failed");
				//					break;
				//				}
				if (mIsNewFrameAvailable) {
					if (!mIsProcessing) {
						mIsProcessing = true;
						PApplet.println("I'm Processing");
						processFrame(mFrame);
						mIsNewFrameAvailable = false;
						mIsProcessing = false;
					}
				}

				//processFrame(mFrame);
			}

		}

		Log.i(TAG, "Finishing processing thread");

	}

	public void setMat(PImage aPImage)
	{

		if (!mIsProcessing) {
			mFrame = toMat(aPImage);
			mIsNewFrameAvailable = true;
		}

	}

	public void setMat(PImage aPImage, int aResizeFactor)
	{

		//if (!mIsProcessing) {
			mFrame = toMat(aPImage);
			Imgproc.resize(mFrame, mFrame, new Size(mFrame.width()
					/ aResizeFactor, mFrame.height() / aResizeFactor));
			PApplet.println("CVMat Size after resize is " + mFrame.width()
					+ " X " + mFrame.height());
			mIsNewFrameAvailable = true;
		//}

	}

	public void setLookForBoard(boolean aLookForBoard)
	{
		mLookForBoard = aLookForBoard;
	}

	public Mat getCameraMat()
	{
		mIsNewFrameAvailable = false;
		return mFrame;
	}

	public Bitmap processFrameReturnBitmap(VideoCapture aCapture)
	{
		aCapture.retrieve(mFrame, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
		mIsNewFrameAvailable = true;
		mListener.onNewFrameAvailable(mFrame);

		mDetector.detect(mFrame, mDetectedMarkers, mCamParam,
				mMarkerSizeMeters, mFrame);

		mListener.onDetection(mFrame, mDetectedMarkers);

		if (mLookForBoard == true) {
			float prob = 0f;
			try {
				prob = mBoardDetector.detect(mDetectedMarkers, mBoardConfig,
						mBoardDetected, mCamParam, mMarkerSizeMeters);

			} catch (CvException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mListener.onBoardDetection(mFrame, mBoardDetected, prob);
		}
		Bitmap bmp = Bitmap.createBitmap(mFrame.cols(), mFrame.rows(),
				Bitmap.Config.ARGB_8888);

		try {
			org.opencv.android.Utils.matToBitmap(mFrame, bmp);
			return bmp;
		} catch (IllegalArgumentException e) {
			bmp.recycle();
			return null;
		}
	}

	public void read(PImage aPImage)
	{
		synchronized (this) {
			aPImage = toPImage(mFrame);
		}
	}

	public void processFrame(VideoCapture aCapture)
	{
		aCapture.retrieve(mFrame, Highgui.CV_CAP_ANDROID_COLOR_FRAME_RGBA);
		mIsNewFrameAvailable = true;
		mListener.onNewFrameAvailable(mFrame);

		mDetector.detect(mFrame, mDetectedMarkers, mCamParam,
				mMarkerSizeMeters, mFrame);

		mListener.onDetection(mFrame, mDetectedMarkers);

		if (mLookForBoard == true) {
			float prob = 0f;
			try {
				prob = mBoardDetector.detect(mDetectedMarkers, mBoardConfig,
						mBoardDetected, mCamParam, mMarkerSizeMeters);

			} catch (CvException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mListener.onBoardDetection(mFrame, mBoardDetected, prob);
		}

	}

	public void processFrame(Mat aFrame)
	{
		//		PApplet.println("Processing Frame");
		mDetector.detect(mFrame, mDetectedMarkers, mCamParam,
				mMarkerSizeMeters, mFrame);
		if (mDetectedMarkers.size() > 0) {
			for (int i = 0; i < mDetectedMarkers.size(); i++) {
				MyMarker m = mDetectedMarkers.get(i);
				m.get3dCube(mFrame, mCamParam);
				//				m.getMatrix();

			}
			mListener.onDetection(mFrame, mDetectedMarkers);
			//			for (int i1 = 0; i1 < mCubePoints.size(); i1++) {
			//				if (mCubePoints.get(i1) != null) {
			//					PApplet.println("showing cubePoint No."+i1);
			//					Vector<Point> cubePoints = (Vector<Point>)mCubePoints.get(i1);
			//					PApplet.println("has"+ cubePoints.size());
			//				}
			//			}
		}

		if (mLookForBoard == true) {
			float prob = 0f;
			try {
				prob = mBoardDetector.detect(mDetectedMarkers, mBoardConfig,
						mBoardDetected, mCamParam, mMarkerSizeMeters);

			} catch (CvException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mListener.onBoardDetection(mFrame, mBoardDetected, prob);
		}

	}

	public void setBoardConfiguration(MyBoardConfiguration aBoardConfig)
	{
		mBoardConfig = aBoardConfig;
	}

	public MyBoardConfiguration getBoardConfiguration()
	{
		return mBoardConfig;
	}

	public boolean openJavaCamera()
	{
		final RelativeLayout mainLayout = (RelativeLayout) mPApplet.getView();
		final LinearLayout pappletLayout = (LinearLayout) mainLayout
				.getChildAt(0);
		Log.i("openJavaCamera", "openCamera");
		synchronized (this) {
			mPApplet.getActivity().runOnUiThread(new Runnable() {
				public void run()
				{

					mJavaCameraView = new MyJavaCameraView(mPApplet
							.getActivity(), MyJavaCameraView.CAMERA_ID_ANY);
					//					mJavaCameraView = (MyJavaCameraView) mPApplet.getActivity().findViewById(R.id.myJavaCameraView);
					LayoutParams javaCameraViewLayoutParams = new RelativeLayout.LayoutParams(
							LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
					mJavaCameraView.setLayoutParams(javaCameraViewLayoutParams);
					mIsJavaCameraOpened = true;
					mJavaCameraView.setCvCameraViewListener(mSelf);
					//mJavaCameraView.setVisibility(View.INVISIBLE);
					mJavaCameraView.setAlpha(1.0f);
					mJavaCameraView.setVisibility(SurfaceView.VISIBLE);

					mainLayout.addView(mJavaCameraView,
							javaCameraViewLayoutParams);
				}
			});
		}
		return true;
	}

	public void releaseJavaCamera()
	{
		Log.i("releaseCamera", "releaseCamera");
		synchronized (this) {
			mPApplet.getActivity().runOnUiThread(new Runnable() {
				public void run()
				{
					if (mJavaCameraView != null) {
						mJavaCameraView.disableView();
						mJavaCameraView.disconnect();
					}
				}
			});

		}
	}

	public void setupJavaCamera(final int width, final int height)
	{
		Log.i("setupCamera", "setupCamera(" + width + ", " + height + ")");
		synchronized (this) {
			mPApplet.getActivity().runOnUiThread(new Runnable() {
				public void run()
				{
					if (mJavaCameraView != null && mIsJavaCameraOpened) {
						mJavaCameraView.disconnect();
						mJavaCameraView.setResolution(new Size(width, height));
						mJavaCameraView.setPreviewSize(width, height);
						mJavaCameraView.connect();
						mJavaCameraView.enableView();
					}
				}
			});
		}

	}

	public boolean openNativeCamera()
	{
		Log.i("openNativeCamera", "openCamera");
		synchronized (this) {
			releaseNativeCamera();
			mCVCamera = new VideoCapture(Highgui.CV_CAP_ANDROID);
			if (!mCVCamera.isOpened()) {
				mCVCamera.release();
				mCVCamera = null;
				Log.e("oepnCamera", "Failed to open native camera");
				return false;
			}
		}
		return true;
	}

	public void releaseNativeCamera()
	{
		Log.i("releaseCamera", "releaseCamera");
		synchronized (this) {
			if (mCVCamera != null) {
				mCVCamera.release();
				mCVCamera = null;
			}
		}
	}

	public void setupNativeCamera(int width, int height)
	{
		Log.i("setupCamera", "setupCamera(" + width + ", " + height + ")");
		synchronized (this) {
			if (mCVCamera != null && mCVCamera.isOpened()) {
				List<Size> sizes = mCVCamera.getSupportedPreviewSizes();
				int mFrameWidth = width;
				int mFrameHeight = height;

				// selecting optimal camera preview size
				{
					double minDiff = Double.MAX_VALUE;
					for (Size size : sizes) {
						if (Math.abs(size.height - height) < minDiff) {
							mFrameWidth = (int) size.width;
							mFrameHeight = (int) size.height;
							minDiff = Math.abs(size.height - height);
						}
					}
				}

				mCVCamera.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, mFrameWidth);
				mCVCamera.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, mFrameHeight);
			}
		}

	}

	Mat toMat(PImage image)
	{
		int w = image.width;
		int h = image.height;

		Mat mat = new Mat(h, w, CvType.CV_8UC4);
		byte[] data8 = new byte[w * h * 4];
		int[] data32 = new int[w * h];
		PApplet.arrayCopy(image.pixels, data32);

		ByteBuffer bBuf = ByteBuffer.allocate(w * h * 4);
		IntBuffer iBuf = bBuf.asIntBuffer();
		iBuf.put(data32);
		bBuf.get(data8);
		mat.put(0, 0, data8);

		return mat;
	}

	// Convert Mat (CvType=CV_8UC4) to PImage (ARGB)
	PImage toPImage(Mat mat)
	{
		int w = mat.width();
		int h = mat.height();

		PImage image = mPApplet.createImage(w, h, PApplet.ARGB);
		byte[] data8 = new byte[w * h * 4];
		int[] data32 = new int[w * h];
		mat.get(0, 0, data8);
		ByteBuffer.wrap(data8).asIntBuffer().get(data32);
		PApplet.arrayCopy(data32, image.pixels);

		return image;
	}

	PImage toPImage(Bitmap aBitmap)
	{
		if (aBitmap == null) {
			return null;
		}
		PImage img = new PImage(aBitmap.getWidth(), aBitmap.getHeight(),
				PConstants.ARGB);
		aBitmap.getPixels(img.pixels, 0, img.width, 0, 0, img.width, img.height);
		img.updatePixels();
		return img;
	}

	public interface ArucoListener
	{

		public abstract void onDetection(Mat aFrame,
				Vector<MyMarker> aDetectedMarkers);

		//
		public abstract void onBoardDetection(Mat aFrame,
				MyBoard aBoardDetected, float aProbability);

		public abstract void onNewFrameAvailable(Mat aFrame);
	}

	@Override
	public void onCameraViewStarted(int width, int height)
	{
		mIsJavaCameraStarted = true;
		PApplet.println("Aruco's Java Camera has started");

	}

	@Override
	public void onCameraViewStopped()
	{
		mIsJavaCameraStarted = false;
		PApplet.println("Aruco's Java Camera has stopped");

	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame)
	{
		//synchronized (this) {
		//			if (!mIsProcessing) {
		mFrame = inputFrame.rgba();
		mIsNewFrameAvailable = true;
		//mListener.onNewFrameAvailable(mFrame);
		//}
		//}

		return null;
	}

}
