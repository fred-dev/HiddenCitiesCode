package com.example.HC_ConductorAudioScene;
import com.example.HC_ConductorAudioScene.XMLParser;
import com.example.HC_ConductorAudioScene.XmlValuesModel;
import com.example.PlayFragmentConductor.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class HC_ConductorAudioScene extends Activity implements OnPreparedListener {


	   private MediaPlayer mediaPlayer;
	   String mFileLocation;
	   public static int oneTimeOnly = 0;
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.play_fragment_conductor);
	      
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
	   protected void onResume() {
	 
	       super.onResume();
	 
	   }

	   @Override
	   protected void onPause() {
	       super.onPause();
	   }
}
