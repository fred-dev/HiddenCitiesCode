package com.example.ErrorFragmentFour;
import com.example.ErrorFragmentFour.XMLParser;
import com.example.ErrorFragmentFour.XmlValuesModel;
import com.example.ErrorFragmentOne.R;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class ErrorFragmentFour extends Activity implements OnTouchListener {


	private TextView error4DetailText1 = null;
	private TextView error4DetailText2 = null;
	private TextView error4DetailText3 = null;

	
	XmlValuesModel infoStringData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.error_fragment_four);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (getActionBar().isShowing())
			getActionBar().hide();

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);


		error4DetailText1 = (TextView) findViewById(R.id.error4DetailText1);
		error4DetailText2 = (TextView) findViewById(R.id.error4DetailText2);
		error4DetailText3 = (TextView) findViewById(R.id.error4DetailText3);
		
		ImageView IV = (ImageView) findViewById(R.id.hcspinwheelAnimationView);
		final AnimationDrawable spinWheelAnimation = (AnimationDrawable) IV.getBackground();
		IV.post(new Runnable() {
		    public void run() {
		        if ( spinWheelAnimation != null ) spinWheelAnimation.start();
		      }
		});
		WebView wb = (WebView) findViewById(R.id.error4animatedbkgd);
		 
		wb.loadDataWithBaseURL(
		        "fake://lala",
		        "<body leftmargin=/�0/� topmargin=/�0/� rightmargin=/�0/� bottommargin=/�0/�><div style=\"text-align: left;\"><IMG id=\"myanim\" SRC=\"file:///android_asset/hcinterfaceerror3animAll.gif\" style=\"height: 100%\"style=\"width: 100%\"leftmargin=/�0/� topmargin=/�0/� rightmargin=/�0/� bottommargin=/�0/� /></div></body>", 
		        "text/html",  
		        "UTF-8", 
		        "fake://lala");
		wb.getSettings().setUseWideViewPort(false);
		wb.setVerticalScrollBarEnabled(false);
		wb.setHorizontalScrollBarEnabled(false);
		wb.setScrollContainer(false);
		
		
//		ImageView IVbkgd = (ImageView) findViewById(R.id.hcspinwheelAnimationView);
//		final AnimationDrawable error3BkgdAnimation = (AnimationDrawable) IVbkgd.getBackground();
//		IVbkgd.post(new Runnable() {
//		    public void run() {
//		        if ( error3BkgdAnimation != null ) error3BkgdAnimation.start();
//		      }
//		});


	}
	void parseSettings() {
		try {
			String fileName = "hiddenCities/hiddenCitiesSettings.xml";
			String path = Environment.getExternalStorageDirectory() + "/"+ fileName;
			File file = new File(path);
			if (file.exists()) {
				Log.d("File", "Yes file is there");
			}

			FileInputStream fileInputStream = new FileInputStream(file);
			XMLParser parser = new XMLParser();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser sp = factory.newSAXParser();
			XMLReader reader = sp.getXMLReader();
			reader.setContentHandler(parser);
			sp.parse(fileInputStream, parser);

		
			infoStringData =(XmlValuesModel) parser.messageValues;

			if (infoStringData != null) {
				XmlValuesModel xmlRowData = infoStringData;
				if (xmlRowData != null) {
					//error2DetailText1.setText(Html.fromHtml(xmlRowData.getError2DetailText1()));
					//error2DetailText2.setText(Html.fromHtml(xmlRowData.getError2DetailText2()));
					//error2DetailText3.setText(Html.fromHtml(xmlRowData.getError2DetailText3()));
					
				}else
					Log.e("infoStrings", "infoStrings value null");
				}
		} catch (Exception e) {
			Log.e("XML parse", "Exception parse xml :" + e);
		}
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (getActionBar().isShowing())
			getActionBar().hide();

		View decorView = getWindow().getDecorView();
		decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
				| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
				| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
				| View.SYSTEM_UI_FLAG_FULLSCREEN
				| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
		
		

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		// when the button is pressed we can go to the map view
		Toast.makeText(getApplicationContext(), "So now we go to the map",
				Toast.LENGTH_SHORT).show();
		parseSettings();
		return false;
	}

}
