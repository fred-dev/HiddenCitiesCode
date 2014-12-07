package com.example.hiddencitiesaudioplayer;


import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.WindowManager;

public class AudioPlayer extends Activity implements MediaPlayer.OnPreparedListener{

   private MediaPlayer mediaPlayer;
   String mFileLocation;
   public static int oneTimeOnly = 0;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_audio_player);
      
      getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		 
		 if (getActionBar().isShowing()) getActionBar().hide();
      
      File root = Environment.getExternalStorageDirectory();
	

      mFileLocation = root + "/hiddenCities/audio/IntroText.wav";
      preparePlayer(mFileLocation);


   }
   
 
   private void preparePlayer(String fileLocation) {
	   mediaPlayer = new MediaPlayer();
	   mediaPlayer.setOnPreparedListener(this);
	   try {
		mediaPlayer.setDataSource(fileLocation);
	} catch (IllegalArgumentException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (SecurityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IllegalStateException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	   mediaPlayer.prepareAsync();
	
	   
	}

   public void onPrepared(MediaPlayer mp){
	      mp.start();
	    }
   
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
   // Inflate the menu; this adds items to the action bar if it is present.
   getMenuInflater().inflate(R.menu.audio_player, menu);
   return true;
   }
   @Override
   protected void onResume() {
 
       super.onResume();
 
   }

   @Override
   protected void onPause() {
       super.onPause();
   }

 }