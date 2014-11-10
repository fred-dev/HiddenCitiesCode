package processing.test.cameragettingstarted;

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


public class CameraGettingStarted extends PApplet {

/**
 * <p>Ketai Sensor Library for Android: http://KetaiProject.org</p>
 *
 * <p>Ketai Camera Features:
 * <ul>
 * <li>Interface for built-in camera</li>
 * <li></li>
 * </ul>
 * <p>Updated: 2012-10-21 Daniel Sauter/j.duran</p>
 */

	KetaiCamera mCam;
	int			mCamId=0;
	
	APButton	mButton;
	
	MultiMarker	mNyAr;
	
public void setup() {
	
  orientation(LANDSCAPE);
  imageMode(CENTER);
  size(displayWidth, displayHeight, P3D);
  smooth();
  colorMode(HSB, 360, 100, 100, 100);
  
  mCam = new KetaiCamera(this, 1280, 720, 30);
  mCam.setCameraID(mCamId);
  mCam.start();
  
  mButton = new APButton(width/2-40, 3*height/4-20, 80, 40, "Take Photo");
  mButton.init(this);
  
  mNyAr=new MultiMarker(this,width,height,"camera_para.dat",NyAR4PsgConfig.CONFIG_PSG);
  mNyAr.addARMarker("patt.hiro",80);//id=0
  mNyAr.addARMarker("patt.kanji",80);//id=1
  
}

public void draw() {
	background(0);
	tint(0, 0,100);
	image(mCam, width/2, height/2);
  	ellipse(mouseX, mouseY, 20, 20);
  	
  	mNyAr.detect(mCam);
  	//mNyAr.drawBackground(mCam);
}

public void onCameraPreviewEvent()
{
  mCam.read();
}

// start/stop camera preview by tapping the screen
public void mousePressed()
{

}

public void mouseReleased()
{
	mCam.stop();
	mCamId++;
	mCamId = mCamId % 2;
	mCam.setCameraID(mCamId);
	mCam.start();
}

public void keyPressed() {
  
  
}


}
