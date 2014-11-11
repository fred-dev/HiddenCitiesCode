package processing.test.cameragettingstarted2;

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
import java.io.IOException;

import ketai.camera.*;
import apwidgets.*;
import jp.nyatla.nyar4psg.*;

public class CameraGettingStarted2 extends PApplet {

	/**
	 * <p>
	 * Ketai Sensor Library for Android: http://KetaiProject.org
	 * </p>
	 * 
	 * <p>
	 * Ketai Camera Features:
	 * <ul>
	 * <li>Interface for built-in camera</li>
	 * <li></li>
	 * </ul>
	 * <p>
	 * Updated: 2012-10-21 Daniel Sauter/j.duran
	 * </p>
	 */

	KetaiCamera mCam;
	int mCamId = 0;
	
	APWidgetContainer mWidgetContainer;
	APButton mButton;

	NyARMultiBoard mNyAr;
	PFont mFont;

	public void setup() {

		orientation(PORTRAIT);
		imageMode(CENTER);
		size(displayWidth, displayHeight, P3D);
		smooth();
		colorMode(HSB, 360, 100, 100, 100);

		mCam = new KetaiCamera(this, 1280, 720, 30);
		mCam.setCameraID(mCamId);
		mCam.start();
		//while(!mCam.isStarted());
		
		mWidgetContainer = new APWidgetContainer(this);
		mButton = new APButton(width / 2 - 80, 3 * height / 4 - 40, 160, 80,
				"Take Photo");
		mWidgetContainer.addWidget(mButton);

		
		setupNyArtoolkit(mCam.width, mCam.height);
		mFont = createFont("Georgia.ttf", 32);

	}

	public void draw() {
		background(0);
		tint(0, 0, 100);

		if (mCam.isLoaded() && mCam.isStarted()) {
			image(mCam, width / 2, height / 2);
			// if *any* markers have been detected this will be true
			if (mNyAr.detect(mCam)) {
				// going to be doing 2D drawing (drawMarkerPos) so temporarily
				// disable depth testing
				hint(DISABLE_DEPTH_TEST);

				// for all detected markers, draw corner points
				for (int i = 0; i < mNyAr.markers.length; i++) {
					if (mNyAr.markers[i].detected) {
						drawMarkerPos(mNyAr.markers[i].pos2d);
					}
				}

				// depth test back on, we're going to draw 3D YEAH!!
				hint(ENABLE_DEPTH_TEST);

				// for all detected markers:
				/*
				 * for (int i = 0; i < mNyAr.markers.length; i++) { if
				 * (mNyAr.markers[i].detected) { // set the model-view transform
				 * to that of the marker // this will adapt automatically to P3D
				 * or OPENGL renderers mNyAr.markers[i].beginTransform();
				 * 
				 * translate(0, 0, 20);
				 * 
				 * // if it's the hiro marker, draw a 3D cube if (i == 0) {
				 * stroke(255, 200, 0); box(40); } // else draw a sphere else {
				 * stroke(0, 200, 255); sphere(25); }
				 * 
				 * // after drawing marker-relative 3D geometry, we // HAVE to
				 * end the transform (so now we're back in // world space)
				 * mNyAr.markers[i].endTransform();
				 * 
				 * } }
				 */

			}
		}
		ellipse(mouseX, mouseY, 20, 20);

	}

	public void onCameraPreviewEvent() {
		mCam.read();
	}
	
	public void onClickWidget(APWidget widget)
	{
		if (widget == mButton) takePhoto();
	}
	
	public void takePhoto()
	{
		
	}

	// start/stop camera preview by tapping the screen
	public void mousePressed() {

	}

	public void mouseReleased() {
		mCam.stop();
		mCamId++;
		mCamId = mCamId % 2;
		mCam.setCameraID(mCamId);
		mCam.start();
		while(!mCam.isStarted());
		setupNyArtoolkit(mCam.width, mCam.height);

	}

	public void keyPressed() {

	}
	
	public void setupNyArtoolkit(int aWidth, int aHeight)
	{
		// array of pattern file names, these have to be in the data subdir of
				// this sketch
				String[] patts = { "patt.hiro", "patt.kanji" };
				// array of corresponding widths in mm
				double[] widths = { 80, 80 };
				// initialise the NyARMultiBoard
				// the camera parameter file is also in the data subdir
				mNyAr = new NyARMultiBoard(this, aWidth, aHeight, "camera_para.dat",
						patts, widths);
				// marker detection algorithm parameter
				mNyAr.gsThreshold = 120;// (0<n<255) default=110

				// a marker has to be detected with a confidence greater than
				// this threshold for it to be considered a true detection
				mNyAr.cfThreshold = 0.4;// (0.0<n<1.0) default=0.4
		
	}

	public void drawMarkerPos(int[][] pos2d) {
		textFont(mFont);
		stroke(100, 0, 0);
		fill(100, 0, 0);

		// draw ellipses at outside corners of marker
		for (int i = 0; i < 4; i++) {
			ellipse(pos2d[i][0], pos2d[i][1], 5, 5);
		}

		fill(0, 0, 0);
		for (int i = 0; i < 4; i++) {
			text("(" + pos2d[i][0] + "," + pos2d[i][1] + ")", pos2d[i][0],
					pos2d[i][1]);
		}
	}

}
