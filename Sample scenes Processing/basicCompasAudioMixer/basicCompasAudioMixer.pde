import de.looksgood.ani.*;
import de.looksgood.ani.easing.*;

/*****************************************************************************************
 
 Android Processing :: Compass example
 
 Display a simple compass on an Android screen
 
 Rolf van Gelder - v 08/03/2011 - http://cage.nl :: http://cagewebdev.com :: info@cage.nl
 
 Don't forget to calibrate your Android compass for more reliable readings!
 
 *****************************************************************************************/

// Sensor classes
import apwidgets.*;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


APMediaPlayer  snd1;
// Sensor objects
SensorManager mSensorManager;
SensorEventListener sensorEventListener;
Sensor accelerometer;
Sensor magnetometer;

// Android fonts
String[] fontList;
PFont androidFont;

// Have we got the right sensors?
boolean sensorAvailable = false;

// azimuth, rotation around the Z axis
// pitch, rotation around the X axis
// roll, rotation around the Y axis
Float azimuth       = 0.0;
Float pitch         = 0.0;
Float roll          = 0.0;


Float sAzimuth       = 0.0;

boolean firstdraw = true;


/********************************************************************************
 
 Setup
 
 ********************************************************************************/
void setup ()
{
  size(480, 640);
  smooth();

  // Sketch stays in portrait mode, even when the phone is rotated
  orientation(PORTRAIT);

  // Select an Android font to use
  fontList = PFont.list();
  androidFont = createFont(fontList[0], 20, true);
  textFont(androidFont);

  snd1 = new APMediaPlayer(this); //create new APMediaPlayer
  snd1.setMediaFile("track1.mp3"); //set the file (files are in data folder)

  snd1.setLooping(false); //restart playback end reached
  snd1.setVolume(0, 0); //Set left and right volumes. Range is from 0.0 to 1.0
}


/********************************************************************************
 
 Draw
 
 ********************************************************************************/
void draw()
{
  background(0);

  // Display sensor readings
  displaySensorReadings();   

  // Show compass
  showCompass();
}

public void mousePressed() { 
  snd1.start();
}
/********************************************************************************
 
 Override the activity class methods
 
 ********************************************************************************/
void onResume()
{ // Called on startup
  super.onResume();

  // Initialize sensor objects
  initSensor();
}

void onPause()
{ // Called on exit
  super.onPause();

  // Unregister sensorEventListener
  exitSensor();
}


/********************************************************************************
 
 Initialize sensors
 
 ********************************************************************************/
void initSensor()
{
  // Initiate instances
  sensorEventListener = new mSensorEventListener();
  mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
  accelerometer  = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  magnetometer   = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

  sensorAvailable = true;

  // Register our listeners
  mSensorManager.registerListener(sensorEventListener, accelerometer, mSensorManager.SENSOR_DELAY_GAME);
  mSensorManager.registerListener(sensorEventListener, magnetometer, mSensorManager.SENSOR_DELAY_GAME);
}


/********************************************************************************
 
 Unregister listener on exit
 
 ********************************************************************************/
void exitSensor()
{
  if (sensorAvailable) mSensorManager.unregisterListener(sensorEventListener);
}


/********************************************************************************
 
 Display sensor readings
 
 ********************************************************************************/
void displaySensorReadings()
{
  if (sensorAvailable)
  {
    text("Azimuth: "+degrees(azimuth)+" ("+azimuth+")", 20, 590);
    text("Pitch: "+pitch, 20, 610);
    text("Roll: "+roll, 20, 630);
  } else
  { // Oops... phone doesn't have the required sensors...
    fill(255, 0, 0);
    text("Compass: NO SENSOR FOUND", 20, 590);
    fill(255);
  }
  
     if (degrees(azimuth) >0) {

      snd1.setVolume(1-map(degrees(azimuth), 0, 180, 0, 1),map(degrees(azimuth), 0, 180, 0, 1));
      
    }
    if (degrees(azimuth)<0) {
      
      snd1.setVolume(1-map(degrees(-azimuth), 0, 180, 0, 1),map(degrees(-azimuth), 0, 180, 0, 1));
      
    }
}


/********************************************************************************
 
 Show compass
 
 ********************************************************************************/
void showCompass()
{
  int cx = width/2;
  int cy = width/2;
  float radius = 0.9 * cx;

  stroke(255);
  noFill();
  ellipse(cx, cy, radius*2, radius*2);

  if (!firstdraw)
  {
    pushMatrix();
    translate(cx, cy);
    rotate(-azimuth);
    line(0, 0, 0, -radius);
    text("NORTH", -25, -radius-5);
    ellipse(0, 0, 10, 10);
    popMatrix();

    // Display value (in degrees)
    fill(255);
    text(str(int(degrees(azimuth))), cx+7, cy+7);
  }

  firstdraw = false;
}


/********************************************************************************
 
 Sensor listener
 
 ********************************************************************************/
class mSensorEventListener implements SensorEventListener
{
  float[] mGravity;
  float[] mGeomagnetic;

  // Orientation values
  float orientation[] = new float[3];  

  // Define all SensorListener methods
  public void onSensorChanged(SensorEvent event)
  {
    if (event.accuracy == SensorManager.SENSOR_STATUS_ACCURACY_LOW) return;

    switch (event.sensor.getType())
    {
    case Sensor.TYPE_MAGNETIC_FIELD:
      mGeomagnetic = event.values.clone();
      break;
    case Sensor.TYPE_ACCELEROMETER:
      mGravity = event.values.clone();
      break;
    }

    if (mGravity != null && mGeomagnetic != null) {
      float I[] = new float[16];
      float R[] = new float[16];
      if (SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic))
      { // Got rotation matrix!
        SensorManager.getOrientation(R, orientation);
        azimuth = orientation[0];
        pitch   = orientation[1];
        roll    = orientation[2];
      }
    }

 
  }

  public void onAccuracyChanged(Sensor sensor, int accuracy)
  {
    // Nada, but leave it in...
  }
}

