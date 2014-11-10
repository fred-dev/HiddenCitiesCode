package processing.test.javacppTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import ketai.camera.*;
import apwidgets.*;

import android.content.res.*;
import android.util.Log;
import java.nio.*;
import org.bytedeco.javacpp.*;
import static org.bytedeco.javacpp.ARToolKitPlus.*;

//import static org.bytedeco.javacpp.presets.ARToolKitPlus.*;

public class javacppTest extends PApplet {
//	static{
//		System.loadLibrary("artoolkitplus");
//	}
	PImage mTestImage;

	KetaiCamera mCam;
	int mCamId = 1;
	ByteBuffer mImageBuffer;
	PImage mBufferImage;

	APWidgetContainer mWidgetContainer;
	APButton mButtonTakePhoto, mButtonChangeCamera, mButtonStartCamera;

	TrackerMultiMarker mTracker;
	ARMultiMarkerInfoT mTrackerConfig;
	String mCamParamPath;
	String mMarkerConfigPath;
	Resources mRes;
	File mFolder;
	int mThresh = 100;
	boolean mIsDetectLight = true;
	int mNumDetected = 0;
	int[] mDetectedMarkersId;

	public void setup() {
		orientation(PORTRAIT);
		imageMode(CENTER);

		mTestImage = loadImage("1.jpg");
		mCam = new KetaiCamera(this, 320, 240, 24);
		println(mCam.getNumberOfCameras());
		mCam.setCameraID(mCamId);
		mCam.start();
		mImageBuffer = ByteBuffer.allocate(mCam.width * mCam.height * 4);
		mBufferImage = createImage(mCam.width, mCam.height, ARGB);
		mBufferImage.loadPixels();
		for (int i = 0; i < mBufferImage.pixels.length; i++) {
		mBufferImage.pixels[i] = color(random(255), random(255),
		random(255));
		}
		mBufferImage.updatePixels();
		// while(!mCam.isStarted());

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

		mCamParamPath = getAssetPath("camera_para.dat");
		println(mCamParamPath);
		mMarkerConfigPath = getAssetPath("marker.dat");
		println(mMarkerConfigPath);
		// mRes = getResources();
		// mFolder = getFilesDir();
		// try {
		// IO.transferFileToPrivateFS(mFolder, "camera_para.dat", mRes);
		// }
		//
		// catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// try {
		// IO.transferFileToPrivateFS(mFolder, "marker.dat", mRes);
		// }
		//
		// catch (IOException e) {
		// e.printStackTrace();
		// }
		//
		// mCamParamPath = mFolder.getAbsolutePath() + File.separator
		// + "camera_para.dat";
		// mMarkerConfigPath = mFolder.getAbsolutePath() + File.separator
		// + "marker.dat";
		// println(mCamParamPath);
		// println(mMarkerConfigPath);
		println("START THE FUCKING INIT!!!!");
		mTracker = new TrackerMultiMarker(width, height);
		println("DONE FUCKIGN MAKING THE TRACKER!!!!");
		mTracker.setPixelFormat(PIXEL_FORMAT_LUM);
		println("DONE FUCKIGN TESTING A METHOD!!!!");
		// if (!mTracker.init(mCamParamPath, mMarkerConfigPath, 1.0f, 1000.0f))
		// {
		// System.out.println("ERROR: init() failed");
		// System.exit(-1);
		// }

//		if (!mTracker.loadCameraFile(mCamParamPath, 1.0f, 1000.0f)) {
//			println("camera param load error");
//			exit();
//		} else {
//			println("camera param load successful");
//		}
//
//		mTracker.getCamera().printSettings();
//		if ((mTrackerConfig = mTracker.arMultiReadConfigFile(mMarkerConfigPath)) == null) {
//			println("config load error");
//			exit();
//		}
		println("DONE FUCKIGN LOADING FILES!!!!");
		// else{
		// println("config load successful");
		// }
		//
		// if (!mTracker.loadCameraFile(mCamParamPath, 1.0f, 1000.0f)) {
		// println("camera param load error");
		// exit();
		// }
		// else
		// {
		// println("camera param load successful");
		// }
		//
		// if (!mTracker.init(mCamParamPath, mMarkerConfigPath, 1.0f, 1000.0f))
		// {
		// println("camera param/ config data load error");
		// exit();
		// }
		mTracker.setThreshold(mThresh);
		println(mTracker.getThreshold());
		mTracker.setThreshold(mThresh-40);
		println(mTracker.getThreshold());
		mTracker.setUseDetectLite(mIsDetectLight);

	}

	public void draw() {
		background(0);
		println(frameRate);
		
		if (mCam.isStarted()) {
		
		// /* detect the markers in the video frame */
		toByteBuffer(mCam, mImageBuffer);
		// // toPImage(mImageBuffer, mBufferImage);
		mNumDetected = mTracker.calc(mImageBuffer);
		mTracker.getDetectedMarkers(mDetectedMarkersId);
		println(mNumDetected);
		println(mDetectedMarkersId);
		//
		// toPImage(mImageBuffer, mBufferImage);
		
		image(mCam, width / 2, height / 2, width, height);
		}

	}

	public ByteBuffer toByteBuffer(PImage aImage) {
		aImage.loadPixels();
		ByteBuffer byteBuffer = ByteBuffer.allocate(aImage.width
				* aImage.height * 4);
		IntBuffer intBuffer = byteBuffer.asIntBuffer();
		intBuffer.put(aImage.pixels);

		return byteBuffer;
	}

	public void toByteBuffer(PImage aImage, ByteBuffer aBuffer) {
		IntBuffer intBuffer = aBuffer.asIntBuffer();
		int[] iArray = new int[aImage.width * aImage.height];
		aImage.loadPixels();
		arrayCopy(aImage.pixels, iArray);
		intBuffer.put(iArray);
	}

	public PImage toPImage(ByteBuffer aBuffer, int aWidth, int aHeight) {
		PImage image = createImage(aWidth, aHeight, ARGB);
		// int[] iArray = new int[aWidth * aHeight];
		// byte[] bArray = new byte[aWidth * aHeight * 4];
		// aBuffer.get(bArray);
		// ByteBuffer.wrap(bArray).asIntBuffer().get(iArray);
		IntBuffer iBuffer = aBuffer.asIntBuffer();
		image.loadPixels();
		arrayCopy(iBuffer.array(), image.pixels);
		image.updatePixels();
		return image;

	}

	public void toPImage(ByteBuffer aBuffer, PImage aImage) {
		// int[] iArray = new int[aWidth * aHeight];
		// byte[] bArray = new byte[aWidth * aHeight * 4];
		// aBuffer.get(bArray);
		// ByteBuffer.wrap(bArray).asIntBuffer().get(iArray);
		IntBuffer iBuffer = aBuffer.asIntBuffer();
		int[] iArray = new int[aImage.width * aImage.height];
		iBuffer.get(iArray);
		aImage.loadPixels();
		arrayCopy(iArray, aImage.pixels);
		aImage.updatePixels();
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

	//
	// public void onCameraPreviewEvent() {
	// mCam.read();
	// }

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

//	public String sketechRenderer() {
//		return P2D;
//	}
}
