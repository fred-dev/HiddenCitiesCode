package com.example.PlayFragment360Video;
import com.example.PlayFragment360Video.XMLParser;
import com.example.PlayFragment360Video.XmlValuesModel;
import com.example.PlayFragment360Video.R;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class PlayFragment360Video extends Activity implements OnTouchListener {

	private Button play360VideoBtn;

	private TextView tsvideoplayfragtext1 = null;

	
	XmlValuesModel infoStringData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.playfragment_360video);

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

		play360VideoBtn = (Button) findViewById(R.id.tsvideoplaybtn);
		play360VideoBtn.setOnTouchListener(this);

		tsvideoplayfragtext1 = (TextView) findViewById(R.id.tsvideoplayfragtext1);
		
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
					tsvideoplayfragtext1.setText(Html.fromHtml(xmlRowData.getInfoKeyStringInstructionTitleText()));

					
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
