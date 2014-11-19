package processing.test.CameraFTPTest;

import processing.core.*;
import processing.data.*;
import processing.event.*;
import processing.opengl.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

//import ketai.camera.KetaiCamera;

import apwidgets.*;

import android.os.Build;
import android.os.Environment;
import android.hardware.Camera;
import processing.test.CameraFTPTest.MyFTP;
import processing.test.CameraFTPTest.MyKetaiCamera;

public class CameraFTPTest extends PApplet
{
	int					CAPTURE_WIDTH	= 320, CAPTURE_HEIGHT = 240;
	MyKetaiCamera		mCam;
	PShape				mRect;
	int					mCamId			= 0;
	int					mNumOfCam;
	String				mSavePath;
	boolean				mIsTakingPhoto	= false;
	boolean				mShouldRotate	= false;

	APWidgetContainer	mWidgetContainer;
	APButton			mButton;
	APToggleButton		mToggleButton;

	MyFTP				mFTP;
	String				mIP, mUserName, mPassword;

	public void setup()
	{

		orientation(PORTRAIT);
		imageMode(CENTER);
		smooth();
		//		colorMode(HSB, 360, 100, 100, 100);
		frameRate(30);

		mCam = new MyKetaiCamera(this, CAPTURE_WIDTH, CAPTURE_HEIGHT, 30);
		mCam.register(this);
		mCam.setCameraID(mCamId);
		mCam.start();
		while (!mCam.isStarted())
			;
		println("Cam Started");
		mSavePath = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/hiddenCities/photo/";
		mCam.setPhotoSize(mCam.camera.getParameters()
				.getSupportedPictureSizes().get(0).width, mCam.camera
				.getParameters().getSupportedPictureSizes().get(0).height);
		mCam.autoSettings();
		//		getCameraProperties();

		mWidgetContainer = new APWidgetContainer(this);
		mButton = new APButton(width / 2 - 240, 3 * height / 4 - 90, 480, 180,
				"Take Photo");
		mToggleButton = new APToggleButton(width / 2 - 240,
				3 * height / 4 + 90, 480, 180,
				"Rotate photo while saving (Heavy)");
		mToggleButton.setChecked(mShouldRotate);
		mWidgetContainer.addWidget(mButton);
		mWidgetContainer.addWidget(mToggleButton);

		mRect = createShape(RECT, -mCam.width / 2, -mCam.height / 2,
				mCam.width, mCam.height);
		mRect.setStroke(color(255, 0));
		mRect.setFill(color(255));
		//mRect.tint(255, 0, 0);
		mFTP = new MyFTP();
		//		mFTP.connnectWithFTP(mIP, mUserName, mPassword);

	}

	public void draw()
	{
		background(0);
		tint(255);
		//		image(mImage, width/2, height/2);
		if (mCam.isStarted()) {
			//image(mCam, width / 2, height / 2);
			mRect.setTexture(mCam);
			pushMatrix();
			translate(width / 2, height / 2);
			if (mCamId == 1) {
				rotate(-HALF_PI);
			} else {
				rotate(HALF_PI);
			}
			scale(width / (float) mCam.height, width / (float) mCam.height);
			shape(mRect);
			popMatrix();

		}
		//println(frameRate);
		//		println(mNumOfCam);
	}

	public void getCameraProperties()
	{

		Camera.Parameters params = mCam.camera.getParameters();
		List<Camera.Size> supportedPictureSizes = params
				.getSupportedPictureSizes();
		List<Camera.Size> supportedPreviewSizes = params
				.getSupportedPreviewSizes();
		List<int[]> supportedPreviewFpsRanges = params
				.getSupportedPreviewFpsRange();
		List<Camera.Size> supportedVideoSizes = params.getSupportedVideoSizes();
		for (int i = 0; i < supportedPreviewSizes.size(); i++) {
			Camera.Size previewSize = supportedPreviewSizes.get(i);
			println("Preview Size: " + "Width-" + previewSize.width
					+ " Height-" + previewSize.height);
			//			int[] previewFpsRanges = supportedPreviewFpsRanges.get(i);
			//			for (int j = 0; j < previewFpsRanges.length; j++) {
			//				println("Preview Size: " + "Width-" + previewSize.width
			//						+ " Height-" + previewSize.height);
			//				println("FPS = " + previewFpsRanges[j]);
			//			}
		}

		for (int i = 0; i < supportedPictureSizes.size(); i++) {
			Camera.Size pictureSize = supportedPictureSizes.get(i);
			println("Picture Size: " + "Width-" + pictureSize.width
					+ " Height-" + pictureSize.height);

		}

		for (int i = 0; i < supportedVideoSizes.size(); i++) {
			Camera.Size videoSize = supportedVideoSizes.get(i);
			println("Video Size: " + "Width-" + videoSize.width + " Height-"
					+ videoSize.height);

		}
	}

	public void switchCamera()
	{
		mCam.stop();
		mCamId = (mCamId + 1) % 2;
		mCam.setCameraID(mCamId);
		mCam.start();
		while (!mCam.isStarted())
			;
	}

	public void onCameraPreviewEvent()
	{
		mCam.read();
		//		mCam.loadPixels();
		//		mCamTexture.set(mCam.pixels);
	}

	public void onSavePhotoEvent(String aPath)
	{
		println("Took photo @ " + aPath);
		mIsTakingPhoto = false;
		if (mFTP.isConnected()) {
			mFTP.uploadFile(aPath,
					getDeviceInfo() + aPath.substring(aPath.lastIndexOf('/')));
		}
	}

	public void onClickWidget(APWidget widget)
	{
		if (widget == mButton)
			takePhoto();
		else if (widget == mToggleButton){
			mShouldRotate = mToggleButton.isChecked();
			mToggleButton.setChecked(mShouldRotate);
		}
	}

	public void takePhoto()
	{
		println("Trying to take photo");
		if (!mIsTakingPhoto) {
			mIsTakingPhoto = true;
			if (mShouldRotate) {
				mCam.saveRotatedPhoto(mSavePath + frameCount + ".jpg");
			} else {
				mCam.savePhoto(mSavePath + frameCount + ".jpg");
			}
		}
	}

	public void mousePressed()
	{
		switchCamera();
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

	public String getDeviceInfo()
	{
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		String serial = Build.SERIAL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model + serial);
		} else {
			return capitalize(manufacturer) + model + serial;
		}
	}

	public String capitalize(String s)
	{
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);

		}
	}
}
