package processing.test.ArUcoTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;
import processing.test.CameraFTPTest.MyKetaiCamera;

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

import es.ava.aruco.*;

import org.opencv.core.*;
import org.opencv.calib3d.*;
import org.opencv.utils.*;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;

import android.content.res.AssetManager;
import android.os.Environment;

public class Aruco extends PApplet
{

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}

	static int			CAPTURE_WIDTH		= 320, CAPTURE_HEIGHT = 240;
	MyKetaiCamera		mCam;
	int					mCamId				= 0;
	ByteBuffer			mImageBuffer;
	PImage				mBufferImage;

	boolean				mIsDetecting		= true;
	boolean				mIsProcessing		= false;
	Vector<Marker>		mDetectedMarkers;
	Board				mBoardDetected;
	private Mat			mFrame;
	private int			mIdSelected;
	CameraParameters	mCamParam;
	String				mCamParamPath;
	float				mMarkerSizeMeters	= 0.40f;						// 40cm
	BoardConfiguration	mBoardConfig;
	Board				mBoard;
	boolean				mLookForBoard		= false;
	MarkerDetector		mDetector;
	BoardDetector		mBoardDetector;
	Mat					mBoardImageMat;
	PImage				mBoardImage;

	public void setup()
	{
		orientation(PORTRAIT);
		imageMode(CENTER);
		smooth();

		mCam = new MyKetaiCamera(this, CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
		mCam.register(this);
		mCam.setCameraID(mCamId);
		mCam.start();
		while (!mCam.isStarted())
			;
		println("Cam Started");

		//mCamParamPath = getAssetPath("camera.xml");
		setupAruco();
	}

	public void draw()
	{
		//		println(frameRate);
		//		println(mCamParamPath);
		background(0);

		tint(255);

		image(mCam, width / 2, height / 2, width / 2, height / 2);
		println(frameRate);
		//image(toPImage(mFrame), width / 2, height / 2);

	}

	public void onCameraPreviewEvent()
	{
		mCam.read();
		if ((mIsDetecting || mLookForBoard) && !mIsProcessing) {
			//mFrame = toMat(mCam);
			processFrame(mCam);
		}
	}

	public void onDetection(Mat aFrame, Vector<Marker> aDetectedMarkers)
	{
		for (Marker m : aDetectedMarkers) {
			println("Marker Id " + m.getMarkerId() + " Found");
			//				m.draw3dCube(aFrame, mCamParam, new Scalar(255,0,0));
		}

	}

	public void onBoardDetection(Mat aFrame, Board aBoardDetected, float aProb)
	{
		if (aProb > 0.2) {
			//			aBoardDetected
			//					.draw3dAxis(mFrame, mCamParam, new Scalar(255, 0, 0));
		}

	}

	protected void setupAruco()
	{
		mCamParamPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/hiddenCities/xml/calibration.yml";
		println(mCamParamPath);
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
		mBoardConfig = new BoardConfiguration(5, 5, markersId, 100, 20);
		mCamParam = new CameraParameters();
		mCamParam.readFromXML(mCamParamPath);

		mBoard = new Board();
		mBoardImageMat = mBoard.createBoardImage(new Size(5, 5), 40, 20, 200,
				mBoardConfig);
		mBoardImage = toPImage(mBoardImageMat);
		mDetectedMarkers = new Vector<Marker>();
		mBoardDetected = new Board();
		mDetector = new MarkerDetector();
		mBoardDetector = new BoardDetector();
	}

	protected void processFrame(PImage aCam)
	{
		final Mat frame = toMat(aCam);
		final Vector<Marker> detectedMarkers = new Vector<Marker>();
		final Board boardDetected = new Board();
		
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				float prob = 0.0f;
				mIsProcessing = true;
				mDetector.detect(frame, detectedMarkers, mCamParam,
						mMarkerSizeMeters, frame);
				if (mLookForBoard) {

					try {
						float timeStarted = millis();
						prob = mBoardDetector.detect(mDetectedMarkers,
								mBoardConfig, mBoardDetected, mCamParam,
								mMarkerSizeMeters);
						float timeEnded = millis();
						println("Detection Took " + (timeEnded - timeStarted)
								+ " milliseconds");
					} catch (CvException e) {
						e.printStackTrace();
					}

				}
				mIsProcessing = false;
				mDetectedMarkers = (Vector<Marker>) detectedMarkers.clone();
				mBoardDetected = (Board) boardDetected.clone();
				onDetection(frame, mDetectedMarkers);
				if (mLookForBoard) {
					onBoardDetection(frame, mBoardDetected, prob);
				}
			}
			
		});
		thread.start();
		
		
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

	//
	//	public void draw3dAxis(Mat frame, CameraParameters cp, Scalar color,
	//			double height, mBoardDetected , Mat Tvec)
	//	{
	//		//		Mat objectPoints = new Mat(4,3,CvType.CV_32FC1);
	//		MatOfPoint3f objectPoints = new MatOfPoint3f();
	//		Vector<Point3> points = new Vector<Point3>();
	//		points.add(new Point3(0, 0, 0));
	//		points.add(new Point3(height, 0, 0));
	//		points.add(new Point3(0, height, 0));
	//		points.add(new Point3(0, 0, height));
	//		objectPoints.fromList(points);
	//
	//		MatOfPoint2f imagePoints = new MatOfPoint2f();
	//		Calib3d.projectPoints(objectPoints, Rvec, Tvec, cp.getCameraMatrix(),
	//				cp.getDistCoeff(), imagePoints);
	//		List<Point> pts = new Vector<Point>();
	//		Converters.Mat_to_vector_Point(imagePoints, pts);
	//
	//		Core.line(frame, pts.get(0), pts.get(1), color, 2);
	//		Core.line(frame, pts.get(0), pts.get(2), color, 2);
	//		Core.line(frame, pts.get(0), pts.get(3), color, 2);
	//
	//		Core.putText(frame, "X", pts.get(1), Core.FONT_HERSHEY_SIMPLEX, 0.5,
	//				color, 2);
	//		Core.putText(frame, "Y", pts.get(2), Core.FONT_HERSHEY_SIMPLEX, 0.5,
	//				color, 2);
	//		Core.putText(frame, "Z", pts.get(3), Core.FONT_HERSHEY_SIMPLEX, 0.5,
	//				color, 2);
	//	}

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

}
