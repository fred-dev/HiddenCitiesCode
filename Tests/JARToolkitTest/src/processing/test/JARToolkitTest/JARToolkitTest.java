package processing.test.JARToolkitTest;

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
import java.nio.*;

import ketai.camera.*;
import android.content.res.AssetManager;
import apwidgets.*;
import net.sourceforge.jartoolkit.core.JARToolKit;

public class JARToolkitTest extends PApplet {
	
	
	
	
	PImage 								mTestImage;
	int									CAPTURE_WIDTH=320;
	int									CAPTURE_HEIGHT=240;
	KetaiCamera 						mCam;
	int 								mCamId = 1;
	ByteBuffer 							mImageBuffer;
	PImage 								mBufferImage;

	APWidgetContainer 					mWidgetContainer;
	APButton 							mButtonTakePhoto, mButtonChangeCamera, mButtonStartCamera;
	
	String mCamParamPath;
	String mMarkerConfigPath;
	JARToolKit							mJARToolkit=null;

	public void setup() {
		
		
		orientation(PORTRAIT);
		imageMode(CENTER);

//		mTestImage = loadImage("1.jpg");
//		mCam = new KetaiCamera(this, 320, 240, 24);
//		println(mCam.getNumberOfCameras());
//		mCam.setCameraID(mCamId);
//		mCam.start();
//		mImageBuffer = ByteBuffer.allocate(mCam.width * mCam.height * 4);
//		mBufferImage = createImage(mCam.width, mCam.height, ARGB);
//		mBufferImage.loadPixels();
//		for (int i = 0; i < mBufferImage.pixels.length; i++) {
//			mBufferImage.pixels[i] = color(random(255), random(255),
//					random(255));
//		}
//		mBufferImage.updatePixels();
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
//		mCamParamPath = "file:///android_asset/camera_para_new";
		println(mCamParamPath);
		mMarkerConfigPath = getAssetPath("marker.dat");
//		mMarkerConfigPath = "file:///android_asset/marker_new";
		println(mMarkerConfigPath);
		try
		{
			mJARToolkit = JARToolKit.create();
		}
		catch(InstantiationException e)
		{
			println("init failed");
			System.err.println(e);
			System.exit(0);
		}
		println("init done");
		if( mJARToolkit.paramLoad(mCamParamPath)==0){
			println("Config load error");
			exit();
		}
		if( mJARToolkit.multiReadConfigFile(mMarkerConfigPath)==0){
			println("Config load error");
			exit();
		}
		
		mJARToolkit.paramChangeSize(CAPTURE_WIDTH, CAPTURE_HEIGHT);
		mJARToolkit.initCparam();
		mJARToolkit.paramDisplay();
	}

	public void draw() {

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

	public void onCameraPreviewEvent() {
		mCam.read();
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

		} else if (widget == mButtonStartCamera) {
			if (mCam.isStarted()) {
				mCam.stop();
			} else {
				mCam.start();
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
}
