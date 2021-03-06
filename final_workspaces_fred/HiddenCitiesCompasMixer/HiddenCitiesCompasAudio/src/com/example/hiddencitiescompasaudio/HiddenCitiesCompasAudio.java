package com.example.hiddencitiescompasaudio;

import java.io.File;
import com.example.hiddencitiescompasaudio.AudioPlayManager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.ImageView;

public class HiddenCitiesCompasAudio extends Activity implements SensorEventListener {
	String[]			mPaths;
	AudioPlayManager[]	mPlayManagers;
	private float currentDegree = 0f;
	private float tempDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_cities_compas_audio);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 
		 if (getActionBar().isShowing()) getActionBar().hide();

		 File root = Environment.getExternalStorageDirectory();
			ImageView IV = (ImageView) findViewById(R.id.imageView1);
			Bitmap bMap = BitmapFactory.decodeFile(root + "/hiddenCities/images/compasAudioRotate.png");
			IV.setImageBitmap(bMap);
			mPaths = new String[2];
			mPaths[0]= root + "/hiddenCities/audio/compasAudio1.wav";
			mPaths[1]= root + "/hiddenCities/audio/compasAudio2.wav";


			mPlayManagers = new AudioPlayManager[mPaths.length];
			for (int i = 0; i < mPlayManagers.length; i++) {
				mPlayManagers[i] = new AudioPlayManager(mPaths[i], 512); // buffersize = 64kb
				mPlayManagers[i].setIsLooping(true);
				mPlayManagers[i].play();
				

			}

		 mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),SensorManager.SENSOR_DELAY_GAME);
    }


    @Override
    protected void onResume() {
  
        super.onResume();
  
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
   
    public void onSensorChanged(SensorEvent event) {

        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        currentDegree = degree;
        
        
        if (currentDegree>180) {
			tempDegree = currentDegree - 180.0f;
			
			mPlayManagers[0].setVolume(tempDegree/180);
			mPlayManagers[1].setVolume((180-tempDegree)/180);
		      
		    }
		    if (currentDegree<180) {
		      
		    	mPlayManagers[0].setVolume((180-currentDegree)/180);
				mPlayManagers[1].setVolume(currentDegree/180);
		      
		    }
        
    }


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}
}
