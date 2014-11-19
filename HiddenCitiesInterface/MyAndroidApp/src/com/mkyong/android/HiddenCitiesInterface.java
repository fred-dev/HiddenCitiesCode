package com.mkyong.android;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;

import android.media.ToneGenerator;
import android.os.Bundle;
import android.widget.ImageButton;

import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;

@SuppressLint("ClickableViewAccessibility")
public class HiddenCitiesInterface extends Activity implements
		View.OnTouchListener {
	
	static final ToneGenerator _toneGenerator = new ToneGenerator(
			AudioManager.STREAM_MUSIC, 100);
	private List<ImageButton> imageButtonList;
	
	private static final int[] BUTTON_IDS = { R.id.ImageButton01,
			R.id.ImageButton02, R.id.ImageButton03, R.id.ImageButton04,
			R.id.ImageButton05, R.id.ImageButton06, R.id.ImageButton07,
			R.id.ImageButton08, R.id.ImageButton09, R.id.ImageButton10,
			R.id.ImageButton11, R.id.ImageButton12,

	};
	
	 static  int[] TONE_IDS = { 
			ToneGenerator.TONE_DTMF_1,
			ToneGenerator.TONE_DTMF_2, 
			ToneGenerator.TONE_DTMF_3,
			ToneGenerator.TONE_DTMF_4,
			ToneGenerator.TONE_DTMF_5,
			ToneGenerator.TONE_DTMF_6,
			ToneGenerator.TONE_DTMF_7,
			ToneGenerator.TONE_DTMF_8,
			ToneGenerator.TONE_DTMF_9,
			ToneGenerator.TONE_DTMF_P, 
			ToneGenerator.TONE_DTMF_0,
			ToneGenerator.TONE_DTMF_S,

	};
	 
	 int tempStore;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);
		
	
		imageButtonList = new ArrayList<ImageButton>();
	    for(int id : BUTTON_IDS) {
	    	ImageButton imageButton = (ImageButton)findViewById(id);
	        imageButton.setOnTouchListener(this); // maybe
	        imageButtonList.add(imageButton);
	    }
		
		

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		System.out.println(v.getId());

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for(int j=0;j<12;j++){
				if(v.getId()==BUTTON_IDS[j]){
					tempStore=j;
				}
			}
			_toneGenerator.startTone(TONE_IDS[tempStore]);
			System.out.println(BUTTON_IDS[tempStore]);
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			_toneGenerator.stopTone();
		}
		
		return false;
	}

}