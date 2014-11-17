package com.mkyong.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;

public class HiddenCitiesInterface extends Activity implements View.OnClickListener {

	ImageButton imageButton01,imageButton02,imageButton03,imageButton04,imageButton05,imageButton06,imageButton07,imageButton08,imageButton09,imageButton10,imageButton11,imageButton12 ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main);

		imageButton01 = (ImageButton) findViewById(R.id.ImageButton01);
		imageButton02 = (ImageButton) findViewById(R.id.ImageButton02);
		imageButton03 = (ImageButton) findViewById(R.id.ImageButton03);
		imageButton04 = (ImageButton) findViewById(R.id.ImageButton04);
		imageButton05 = (ImageButton) findViewById(R.id.ImageButton05);
		imageButton06 = (ImageButton) findViewById(R.id.ImageButton06);
		imageButton07 = (ImageButton) findViewById(R.id.ImageButton07);
		imageButton08 = (ImageButton) findViewById(R.id.ImageButton08);
		imageButton09 = (ImageButton) findViewById(R.id.ImageButton09);
		imageButton10 = (ImageButton) findViewById(R.id.ImageButton10);
		imageButton11 = (ImageButton) findViewById(R.id.ImageButton11);
		imageButton12 = (ImageButton) findViewById(R.id.ImageButton12);

		imageButton01.setOnClickListener(this);
		imageButton02.setOnClickListener(this);
		imageButton03.setOnClickListener(this);
		imageButton04.setOnClickListener(this);
		imageButton05.setOnClickListener(this);
		imageButton06.setOnClickListener(this);
		imageButton07.setOnClickListener(this);
		imageButton08.setOnClickListener(this);
		imageButton09.setOnClickListener(this);
		imageButton10.setOnClickListener(this);
		imageButton11.setOnClickListener(this);
		imageButton12.setOnClickListener(this);
		
		
		
	       

	}



	@Override
	public void onClick(View v) {
		System.out.println(v.getId());
		 switch(v.getId()) {
         case R.id.ImageButton01:
        	 
         // do stuff;
         break;
         case R.id.ImageButton02:
         // do stuff;
         break;
		
	}
	}
}