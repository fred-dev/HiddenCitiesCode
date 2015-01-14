package com.hiddenCities.login;

import java.io.File;
import java.io.FileInputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hiddenCities.R;
import com.hiddenCities.main.HiddenCitiesMain;
import com.hiddenCities.main.XMLParser;
import com.hiddenCities.main.XmlValuesModel;

@SuppressLint("NewApi")
public class HiddenCitiesLogin extends Fragment
{

	private EditText	username			= null;
	private EditText	email				= null;
	private TextView	enterDetailsText	= null;
	private TextView	welcomeTextView		= null;

	private Button		login;
	int					counter				= 3;

	HiddenCitiesMain	mActivity;
	View				mView;
	XmlValuesModel loginStringData = null;
	


	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mActivity = (HiddenCitiesMain) getActivity();
		mView = inflater.inflate(R.layout.login_layout, container, false);
		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (mActivity.getActionBar().isShowing())
			mActivity.getActionBar().hide();

		View decorView = getActivity().getWindow().getDecorView();
	      decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	                                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
	                                    | View.SYSTEM_UI_FLAG_FULLSCREEN
	                                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	      
	  	  username = (EditText) mView.findViewById(R.id.nameEntry);
	      email = (EditText)mView.findViewById(R.id.emailEntry);
	      welcomeTextView =(TextView)mView.findViewById(R.id.welcomeText);   
	      enterDetailsText = (TextView)mView.findViewById(R.id.enterDetailsText);  
	      
	      parseSettings();

		return mView;
	}

	void parseSettings() {
		try {
			String fileName = "hiddenCities/hiddenCitiesSettings.xml";
			String path = Environment.getExternalStorageDirectory() + "/"
					+ fileName;
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

			loginStringData = (XmlValuesModel) parser.idValues;

			if (loginStringData != null) {
				XmlValuesModel xmlRowData = loginStringData;
				if (xmlRowData != null) {

					username.setHint(Html.fromHtml(xmlRowData.getUsernameHintString()));
					email.setHint(Html.fromHtml(xmlRowData.getEmailHintString()));
					welcomeTextView.setText(Html.fromHtml(xmlRowData.getWelcomeString()));
					Log.d("thisis what it reads", xmlRowData.getWelcomeString());
					enterDetailsText.setText(Html.fromHtml(xmlRowData.getEnterDetailsString()));

				} else
					Log.e("infoStrings", "infoStrings value null");
			}
		} catch (Exception e) {
			Log.e("XML parse", "Exception parse xml :" + e);
		}
	}

	public void login(View view)
	{
		String mEmail;
		mEmail = email.getText().toString();

		if (username.getText().toString().equals("")) {
			Toast.makeText(mActivity.getApplicationContext(), "oops, you forgot your name", Toast.LENGTH_SHORT).show();
			return;
		}

		else if (email.getText().toString().equals("")) {
			Toast.makeText(mActivity.getApplicationContext(), "oops, you forgot your email", Toast.LENGTH_SHORT).show();
			return;
		} else if (isEmailValid(mEmail) == false) {
			Toast.makeText(mActivity.getApplicationContext(), "Sorry, that email is invalid", Toast.LENGTH_SHORT)
					.show();
			return;
		}

		mActivity.saveToXml(username.getText().toString(), "username", 0);
		mActivity.saveToXml(email.getText().toString(), "useremail", 0);
		mActivity.login();

	}

	@Override
	public void onResume()
	{

		super.onResume();

	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	public static boolean isEmailValid(String email)
	{
		boolean isValid = false;

		String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		if (matcher.matches()) {
			isValid = true;

		}
		return isValid;
	}

}
