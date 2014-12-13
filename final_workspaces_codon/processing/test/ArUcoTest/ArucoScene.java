package processing.test.ArUcoTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import processing.test.ArUcoTest.Aruco;
import processing.test.ArUcoTest.Aruco.ArucoListener;
import es.ava.aruco.Board;
import es.ava.aruco.BoardConfiguration;
import es.ava.aruco.Marker;

import hiddenCitiesProject.hiddenCities.cameraFTP.MyKetaiCamera;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Vector;
import java.util.List;

import java.nio.*;

import ketai.camera.*;
import apwidgets.*;

import org.opencv.core.*;
import org.opencv.calib3d.*;
import org.opencv.utils.*;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import com.example.pappletfragmenttest.R;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ArucoScene extends PApplet implements ArucoListener
{
	//	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this.getActivity()) {
	//        @Override
	//        public void onManagerConnected(int status) {
	//            switch (status) {
	//                case LoaderCallbackInterface.SUCCESS:
	//                {
	//                    Log.i("OpenCV::", "OpenCV loaded successfully");
	//                } break;
	//                default:
	//                {
	//                    super.onManagerConnected(status);
	//                } break;
	//            }
	//        }
	//    };

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}
	static {
		//		System.loadLibrary("opencv_java"); //load opencv_java lib
		//		System.loadLibrary("native_camera_r4.3.0");

	}

	static int			CAPTURE_WIDTH	= 640, CAPTURE_HEIGHT = 480;
	MyKetaiCamera		mCam;
	int					mCamId			= 0;
	ByteBuffer			mImageBuffer;
	PImage				mBufferImage;
	PImage				mCamImage		= null;

	VideoPlayManager	mVideoPlayManager;
	Texture				mVideoTexture;
	String				mVideoPath;
	PShape				mRect;

	VideoBlobManager	mVideoBlobManager;
	PImage				mVideoPImage;

	Aruco				mAruco;
	PImage[]			mMarkerImages;
	Vector<double[]>	mMatrices;
	Vector<List<Point>>	mCubePoints;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View contentView;
		RelativeLayout overallLayout = new RelativeLayout(getActivity());
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);

		LinearLayout layout = new LinearLayout(getActivity());
		layout.addView(surfaceView, sketchWidth(), sketchHeight());
		overallLayout.addView(layout, lp);
		contentView = overallLayout;
		//		mMainLayout = overallLayout;
		return contentView;
	}

	@Override
	public void onResume()
	{
		super.onResume();
		//OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_6, this.getActivity(), mLoaderCallback);
	}

	public void setup()
	{
		orientation(PORTRAIT);
		imageMode(CENTER);
		shapeMode(CENTER);
		smooth();

		//		mCam = new MyKetaiCamera(this, CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
		//		mCam.register(this);
		//		mCam.setCameraID(mCamId);
		//				mCam.start();
		//				while (!mCam.isStarted())
		//					;
		//				println("Cam Started");

		//mCamParamPath = getAssetPath("camera.xml");
		mAruco = new Aruco(this, Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/hiddenCities/xml/calibration.xml");

		int[] ids = { 200, 201, 202, 203, 204, 205, 206, 207, 208, 209, 210,
				211, 212, 213, 214, 215, 216, 217, 218, 219, 220, 221, 222,
				223, 224 };
		int[][] markersId = new int[5][5];
		int index = 0;
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				markersId[i][j] = ids[index];
				index++;
			}
		mAruco.setup();
		mAruco.openJavaCamera();
		while (!mAruco.mIsJavaCameraOpened)
			;
		mAruco.setupJavaCamera(CAPTURE_WIDTH, CAPTURE_HEIGHT);
		mAruco.setBoardConfiguration(new MyBoardConfiguration(5, 5, markersId,
				100, 20));
		mAruco.start();

		mVideoPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/hiddenCities/video/VideoTest.mp4";
		//		mVideoPlayManager = new VideoPlayManager(this, mVideoPath, 1920, 1080);
		//		mVideoTexture = mVideoPlayManager.getTexture();
		mRect = createShape(RECT, 0, 0, CAPTURE_WIDTH, CAPTURE_HEIGHT);
		mRect.setStroke(color(255, 0));
		mRect.setFill(color(255));

		mMarkerImages = new PImage[10];

		//		mVideoBlobManager = new VideoBlobManager(this, mVideoPath);
		//		mVideoBlobManager.setIsGettingBitmap(true);
		for (int i = 0; i < 20; i++) {
			mMatrices.add(new double[16]);
		}
		mCubePoints = new Vector<List<Point>>();
		for (int i = 0; i < 20; i++) {
			mCubePoints.add(new Vector<Point>());
		}
	}

	public void draw()
	{
		//		println(frameRate);
		//		println(mCamParamPath);
		background(0);

		tint(255);
		if (mCamImage != null) {
			println("showing mCamImage");
			pushMatrix();
			translate(width / 2, height / 2);
			rotate(radians(90));
			image(mCamImage, 0, 0);
			popMatrix();
		}

		for (int i = 0; i < mMarkerImages.length; i++) {
			if (mMarkerImages[i] != null) {
				pushMatrix();
				translate(100 + 200 * i, 300);
				image(mMarkerImages[i], 0, 0);
				popMatrix();
			}
		}
		

		ellipse(mouseX, mouseY, 30, 30);

		//		if (mCam.isStarted()) {
		//			image(mCam, width / 2, height / 2);
		//		}
		//		if (mBlob.mSavedSurfaceTexture != null) {
		//			println("viewing textureview");
		//			image(toPImage(mBlob.getTextureView().getBitmap()), width / 2,
		//					height / 2);
		//		}
		//		mVideoTexture.bind();
		//		shape(mRect, 0, 0, width, height);
		//		mVideoTexture.unbind();
		//println(frameRate);
		//image(toPImage(mFrame), width / 2, height / 2);
		//		Matrix matrix = new Matrix();
		//		matrix.setRotate(frameCount);
		//		mVideoBlobManager.applyMatrix(matrix);
		//		mVideoBlobManager.update();
		//		mVideoPImage = toPImage(mVideoBlobManager.getBitmap());

		//println(frameRate);
	}

	//	public void

	//	public void onCameraPreviewEvent()
	//	{
	//		mCam.read();
	//		mAruco.setMat(toMat(mCam));
	//	}

	//	public void onBoardDetection(Mat aFrame, Board aBoardDetected, float aProb)
	//	{
	//		if (aProb > 0.2) {
	//			//			aBoardDetected
	//			//					.draw3dAxis(mFrame, mCamParam, new Scalar(255, 0, 0));
	//		}
	//
	//	}
	@Override
	public void onNewFrameAvailable(Mat aFrame)
	{
		println("onNewFrameAvailable");
		println("New Frame's Size is " + aFrame.width() + " " + aFrame.height());
		//mAruco.read(mCamImage);
		mCamImage = toPImage(aFrame);
		mRect.setTexture(mCamImage);

	}

	@Override
	public void onDetection(Mat aFrame, Vector<MyMarker> aDetectedMarkers)
	{
		println("onDetection");
		int i = 0;
		for (MyMarker m : aDetectedMarkers) {
			println("Marker Id " + m.getMarkerId() + " Found with a size of "
					+ m.getSize());
			mMarkerImages[i] = toPImage(Marker.createMarkerImage(
					m.getMarkerId(), (int) (m.getSize() * 1000.0)));
			i++;
		}
	}

	@Override
	public void onBoardDetection(Mat aFrame, MyBoard aBoardDetected,
			float aProbability)
	{
		if (aProbability > 0.2) {

		}

	}

	public void mousePressed()
	{

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

	Mat toMat(PImage image)
	{
		int w = image.width;
		int h = image.height;

		Mat mat = new Mat(h, w, CvType.CV_8UC4);
		byte[] data8 = new byte[w * h * 4];
		int[] data32 = new int[w * h];
		arrayCopy(image.pixels, data32);

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

		PImage image = createImage(w, h, ARGB);
		byte[] data8 = new byte[w * h * 4];
		int[] data32 = new int[w * h];
		mat.get(0, 0, data8);
		ByteBuffer.wrap(data8).asIntBuffer().get(data32);
		arrayCopy(data32, image.pixels);

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

}
