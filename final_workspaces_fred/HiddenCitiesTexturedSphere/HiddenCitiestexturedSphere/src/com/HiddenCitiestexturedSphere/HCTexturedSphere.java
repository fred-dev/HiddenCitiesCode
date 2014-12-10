package com.HiddenCitiestexturedSphere;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;


public class HCTexturedSphere extends Activity {

	private GLSurfaceView mGlSurfaceView;

	  /**
	   * Called when the activity is first created.
	   * @param savedInstanceState The instance state.
	   */
	  @Override
	  public void onCreate(final Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	    this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    this.mGlSurfaceView = new GLSurfaceView(this);
	    this.mGlSurfaceView.setRenderer(new GlRenderer(this));
	    this.setContentView(this.mGlSurfaceView);
	  }

	  /**
	   * Remember to resume the glSurface.
	   */
	  @Override
	  protected void onResume() {
	    super.onResume();
	    this.mGlSurfaceView.onResume();
	  }

	  /**
	   * Also pause the glSurface.
	   */
	  @Override
	  protected void onPause() {
	    this.mGlSurfaceView.onPause();
	    super.onPause();
	  }
	}
