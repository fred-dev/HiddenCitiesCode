package com.hiddenCities.helpDialer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.hiddenCities.R;
import com.hiddenCities.main.HiddenCitiesMain;

public class HiddenCitiesHelpDialer extends Fragment implements View.OnTouchListener
{

	static ToneGenerator		_toneGenerator	= new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

	private static final int[]	BUTTON_IDS		= { R.id.Button1, R.id.Button2, R.id.Button3, R.id.Button4,
			R.id.Button5, R.id.Button6, R.id.Button7, R.id.Button8, R.id.Button9, R.id.Buttonstar, R.id.Button0,
			R.id.ButtonHash, R.id.ButtonEndCall,

												};
	private List<Button>		buttonList;

	static int[]				TONE_IDS		= { ToneGenerator.TONE_DTMF_1, ToneGenerator.TONE_DTMF_2,
			ToneGenerator.TONE_DTMF_3, ToneGenerator.TONE_DTMF_4, ToneGenerator.TONE_DTMF_5, ToneGenerator.TONE_DTMF_6,
			ToneGenerator.TONE_DTMF_7, ToneGenerator.TONE_DTMF_8, ToneGenerator.TONE_DTMF_9, ToneGenerator.TONE_DTMF_P,
			ToneGenerator.TONE_DTMF_0, ToneGenerator.TONE_DTMF_S,

												};

	String[]					mediaPaths;
	int							menuLevel;
	int							tempStore;
	Button						mButton;
	AudioPlayManager[]			mPlayManagers;

	HiddenCitiesMain			mActivity;
	View						mView;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mActivity = (HiddenCitiesMain) getActivity();
		mView = inflater.inflate(R.layout.help_dialer_layout, container, false);

		mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		if (mActivity.getActionBar().isShowing())
			mActivity.getActionBar().hide();
		menuLevel = 0;
		buttonList = new ArrayList<Button>();
		for (int id : BUTTON_IDS) {
			Button mButton = (Button) mView.findViewById(id);
			mButton.setOnTouchListener(this); // maybe
			buttonList.add(mButton);
		}
		
		_toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

		mediaPaths = new String[2];
		File root = Environment.getExternalStorageDirectory();
		mediaPaths[0] = root + "/hiddenCities/audio/helpDesk/Main Menu.wav";
		mediaPaths[1] = root + "/hiddenCities/audio/helpDesk/call in a q.wav";

		mPlayManagers = new AudioPlayManager[mediaPaths.length];
		for (int i = 0; i < mPlayManagers.length; i++) {

			mPlayManagers[i] = new AudioPlayManager(mediaPaths[i], 64); // buffersize = 64kb
			mPlayManagers[i].setIsLooping(true);
			mPlayManagers[i].start();
			while (!mPlayManagers[i].isSetup());
						}
		mPlayManagers[0].play();

		ImageView IV = (ImageView) mView.findViewById(R.id.imageView1);
		Bitmap bMap = BitmapFactory.decodeFile(root + "/hiddenCities/images/hiddencitieshelpscreen.png");
		IV.setImageBitmap(bMap);

		mView.setOnTouchListener(this);
		return mView;
	}

	@Override
	public void onResume()
	{

		super.onResume();
		mPlayManagers[0].play();
		mPlayManagers[1].pause();
		
	}

	@Override
	public void onPause()
	{
		super.onPause();
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i] != null) {
				mPlayManagers[i].pause();
			}
		}
		_toneGenerator.stopTone();

	}

	@Override
	public void onStop()
	{
		super.onPause();
		for (int i = 0; i < mPlayManagers.length; i++) {
			if (mPlayManagers[i] != null) {
				mPlayManagers[i].stop();
				mPlayManagers[i].release();
			}
		}
		_toneGenerator.release();

	}

	public boolean onTouch(View v, MotionEvent event)
	{
		mView.performClick();
		System.out.println(v.getId());

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			for (int j = 0; j < 12; j++) {
				if (v.getId() == BUTTON_IDS[j]) {
					tempStore = j;
				}
			}
			_toneGenerator.startTone(TONE_IDS[tempStore]);
			System.out.println(BUTTON_IDS[tempStore]);
			Log.d("button number", Integer.toString(tempStore));
			if (v.getId() == R.id.ButtonEndCall) {
				mActivity.detachHelpDialerScene();

			}
		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
			_toneGenerator.stopTone();
			switch (menuLevel) {
				case 0:

					switch (tempStore) {

						case 0:
							if (mPlayManagers[0].getIsPlaying()) {
								mPlayManagers[0].stop();
							}
							mPlayManagers[1].play();
						break;
						case 1:
							if (mPlayManagers[0].getIsPlaying()) {
								mPlayManagers[0].stop();
							}
							mPlayManagers[1].play();
						break;
						case 2:
							if (mPlayManagers[0].getIsPlaying()) {
								mPlayManagers[0].stop();
							}
							mPlayManagers[1].play();
						break;
					}
				break;
			}
		}
		return false;
	}

}
