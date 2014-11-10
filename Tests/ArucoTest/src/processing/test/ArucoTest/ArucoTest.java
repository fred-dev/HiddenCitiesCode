package processing.test.ArucoTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

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


public class ArucoTest extends PApplet {

	static {
		if (!OpenCVLoader.initDebug()) {
			// Handle initialization error
		}
	}
	
	KetaiCamera mCam;
	int mCamId = 1;
	ByteBuffer mImageBuffer;
	PImage mBufferImage;

	APWidgetContainer mWidgetContainer;
	APButton mButtonTakePhoto, mButtonChangeCamera, mButtonStartCamera;
	
	boolean			mIsDetecting=true;
	Vector<Marker> mDetectedMarkers;
	Board mBoardDetected;
	Mat mCamMat;
	CameraParameters mCamParam;
	String mCamParamPath;
	float mMarkerSizeMeters = 0.40f; // 40cm
	BoardConfiguration mBoardConfig;
	Board mBoard;
	MarkerDetector mDetector;
	BoardDetector mBoardDetector;
	Mat mBoardImageMat;
	PImage mBoardImage;

	public void setup() {
		orientation(PORTRAIT);
		imageMode(CENTER);
		smooth();
		
		mCam = new KetaiCamera(this, 320, 240, 24);
		println(mCam.getNumberOfCameras());
		mCam.setCameraID(mCamId);
		mCam.start();
		
		mWidgetContainer = new APWidgetContainer(this);
		mButtonTakePhoto = new APButton(width / 2 - 80, 3 * height / 4 - 80,
				160, 80, "Take Photo");
		mButtonChangeCamera = new APButton(width / 2 - 80, 3 * height / 4 + 0,
				160, 80, "Change Camera");
		mButtonStartCamera = new APButton(width / 2 - 80, 3 * height / 4 + 80,
				160, 80, "Start/Stop Camera");
		mWidgetContainer.addWidget(mButtonTakePhoto);
		mWidgetContainer.addWidget(mButtonChangeCamera);
		mWidgetContainer.addWidget(mButtonStartCamera);
		
		mCamParamPath = getAssetPath("camera.xml");
		mCamParamPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/calibration/camera.xml";
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

	public void draw() {
		println(frameRate);
		println(mCamParamPath);
		background(0);
		mCamMat = toMat(mCam);
		if (mIsDetecting) {
			detectBoard();
		}
		tint(255);
		image(mCam, width / 2, height / 2);
		ellipse(mouseX, mouseY, 20, 20);

	}
	
	public void detectBoard()
	{
		mDetector.detect(mCamMat, mDetectedMarkers, mCamParam, mMarkerSizeMeters,mCamMat);
		float prob=0.0f;
		try{
		float timeStarted = millis();
		prob = mBoardDetector.detect(mDetectedMarkers, mBoardConfig,
				mBoardDetected, mCamParam, mMarkerSizeMeters);
		float timeEnded = millis();
		println("Detection Took " + (timeEnded - timeStarted)
				+ " milliseconds");
		}
		catch (CvException e) {
			e.printStackTrace();
		}
		onBoardDetection(mCamMat, mBoardDetected, prob);
		
	}
	
	public void onCameraPreviewEvent() {
		mCam.read();
	}
	
	
	public void onBoardDetection(Mat mFrame, Board aBoardDetected, float aProb)
	{
		if(aProb>0.2){
			aBoardDetected.draw3dAxis(mCamMat, mCamParam, new Scalar(255,0,0));
		}
		
	}


	public void mousePressed() {

	}

	public void keyPressed() {

	}

	public int sketchWidth() {
		return displayWidth;
	}

	public int sketchHeight() {
		return displayHeight;
	}
	
	public void draw3dAxis(Mat frame, CameraParameters cp, Scalar color, double height, Mat Rvec, Mat Tvec){
//		Mat objectPoints = new Mat(4,3,CvType.CV_32FC1);
		MatOfPoint3f objectPoints = new MatOfPoint3f();
		Vector<Point3> points = new Vector<Point3>();
		points.add(new Point3(0,     0,     0));
		points.add(new Point3(height,0,     0));
		points.add(new Point3(0,     height,0));
		points.add(new Point3(0,     0,     height)); 
		objectPoints.fromList(points);
		
		MatOfPoint2f imagePoints = new MatOfPoint2f();
		Calib3d.projectPoints( objectPoints, Rvec, Tvec,
				cp.getCameraMatrix(), cp.getDistCoeff(), imagePoints);
		List<Point> pts = new Vector<Point>();
		Converters.Mat_to_vector_Point(imagePoints, pts);
		
		Core.line(frame ,pts.get(0),pts.get(1), color, 2);
		Core.line(frame ,pts.get(0),pts.get(2), color, 2);
		Core.line(frame ,pts.get(0),pts.get(3), color, 2);

		Core.putText(frame, "X", pts.get(1), Core.FONT_HERSHEY_SIMPLEX, 0.5,  color,2);
		Core.putText(frame, "Y", pts.get(2), Core.FONT_HERSHEY_SIMPLEX, 0.5,  color,2);
		Core.putText(frame, "Z", pts.get(3), Core.FONT_HERSHEY_SIMPLEX, 0.5,  color,2);
	}

	Mat toMat(PImage image) {
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
	PImage toPImage(Mat mat) {
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

	public String getAssetPath(String aFile) {
		File f = new File(getCacheDir() + "/" + aFile);
		if (!f.exists())
			try {
				AssetManager assetManager = getAssets();
				InputStream inputStream = null;

				inputStream = assetManager.open(aFile);

				int size = inputStream.available();
				byte[] buffer = new byte[size];
				inputStream.read(buffer);
				inputStream.close();

				FileOutputStream fos = new FileOutputStream(f);
				fos.write(buffer);
				fos.close();

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		return f.getAbsolutePath();
	}
	
	public void onClickWidget(APWidget widget) {
		if (widget == mButtonTakePhoto) {
			takePhoto();
		} else if (widget == mButtonChangeCamera) {
			mCamId++;
			mCamId %= 2;
			mCam.stop();
			mCam.setCameraID(mCamId);
			println(mCamId);
			mCam.start();
			while(!mCam.isStarted());

		} else if (widget == mButtonStartCamera) {
			if (mCam.isStarted()) {
				mCam.stop();
			} else {
				mCam.start();
				while(!mCam.isStarted());
			}
		}
	}

	public void takePhoto() {

	}

	// public String sketchRenderer() {
	// return P3D;
	// }

	// private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this)
	// {
	// @Override
	// public void onManagerConnected(int status) {
	// switch (status) {
	// case LoaderCallbackInterface.SUCCESS:
	// {
	// println("OpenCV loaded successfully");
	// //mOpenCvCameraView.enableView();
	// } break;
	// default:
	// {
	// super.onManagerConnected(status);
	// } break;
	// }
	// }
	// };
	//
	// @Override
	// public void onResume()
	// {
	// super.onResume();
	// OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this,
	// mLoaderCallback);
	// }

}
