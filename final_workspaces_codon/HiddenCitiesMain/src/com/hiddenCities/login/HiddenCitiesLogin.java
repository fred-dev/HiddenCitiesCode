package com.hiddenCities.login;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
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

@SuppressLint("NewApi")
public class HiddenCitiesLogin extends Fragment
{

	private EditText	username			= null;
	private EditText	email				= null;
	private TextView	emailTextView		= null;
	private TextView	userNameTextView	= null;
	private TextView	welcomeTextView		= null;

	private Button		login;
	int					counter				= 3;

	HiddenCitiesMain	mActivity;
	View				mView;

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
		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (mActivity.getActionBar().isShowing())
			mActivity.getActionBar().hide();

		username = (EditText) mView.findViewById(R.id.editText1);
		email = (EditText) mView.findViewById(R.id.editText2);
		login = (Button) mView.findViewById(R.id.button1);

		welcomeTextView = (TextView) mView.findViewById(R.id.welcomeText);
		userNameTextView = (TextView) mView.findViewById(R.id.usernameText);
		emailTextView = (TextView) mView.findViewById(R.id.emailText);
		welcomeTextView.setText("Welcome to Hidden Cities, please enter your name and email address:");
		userNameTextView.setText("Name:");
		emailTextView.setText("Email:");

		return mView;
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
		Toast.makeText(mActivity.getApplicationContext(), "Logging in, I'll just be a moment", Toast.LENGTH_SHORT)
				.show();

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
