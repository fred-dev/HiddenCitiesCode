package processing.test.ArUcoTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import processing.core.PApplet;
import processing.opengl.PShader;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.Surface;

public class VideoPlayManager implements Runnable,
		MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener,
		MediaPlayer.OnBufferingUpdateListener,
		SurfaceTexture.OnFrameAvailableListener
{

	public Thread			mThread;
	public PApplet			mPApplet;
	public File				mFile;
	public volatile String	mPath;
	public volatile Uri		mUri;
	public boolean			mIsPrepared				= false;
	public boolean			mIsNewFrameAvailable	= false;
	public volatile boolean	mIsPlaying				= false;
	public volatile boolean	mHasSource				= false;
	public volatile boolean	mIsLooping				= false;
	public volatile float	mVol					= 1;
	public float			mMaxVol					= 0;
	public MediaPlayer		mPlayer;
	public int				mLength					= 0;
	// right?
	public volatile byte[]	mByteData				= null;
	public FileInputStream	mInputStream			= null;
	public volatile int		mBytesRead;

	public SurfaceTexture	mTexture;
	public PShader			mShader;
	public int				mTextureId;
	public Surface			mSurface;

	// Path of the file, buffer size in kb (the less the buffer size, more
	// responsive the audio stream and more stutters
	public VideoPlayManager(PApplet aPApplet, Uri aUri)
	{
		mPApplet = aPApplet;
		mPath = aUri.getPath();
		mUri = aUri;
		mPlayer = new MediaPlayer();
		mTexture = new SurfaceTexture(mTextureId);
		mTexture.setOnFrameAvailableListener(this);
		mSurface = new Surface(mTexture);
		
		try {
			
			mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mPlayer.setDataSource(mPApplet.getActivity(), mUri);
			mPlayer.setSurface(mSurface);
			mPlayer.setLooping(mIsLooping);
			mPlayer.setOnBufferingUpdateListener(this);
			mPlayer.setOnCompletionListener(this);
			mPlayer.setOnPreparedListener(this);
			mPlayer.prepareAsync();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void onPrepared(MediaPlayer aPlayer)
	{
		if (mPlayer == aPlayer) {
			mIsPrepared = true;
		}

	}

	@Override
	public void onBufferingUpdate(MediaPlayer aPlayer, int aPercentBuffered)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onCompletion(MediaPlayer aPlayer)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onFrameAvailable(SurfaceTexture aTexture)
	{
		synchronized (this) {
			mIsNewFrameAvailable = true;
		}

	}
	

	@Override
	public void run()
	{
		// TODO Auto-generated method stub
		
	}

	public void updateTexture()
	{
		synchronized (this) {
			if (mIsNewFrameAvailable) {
				mTexture.updateTexImage();
				//                mTexture.getTransformMatrix(videoTextureTransform);
				mSurface = new Surface(mTexture);
				mIsNewFrameAvailable = false;
			}
		}
	}

	public void makeThread()
	{
		mThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				//				System.out.println("thread ran");
				//				try {
				//					while (mHasSource) {
				//						int ret = 0;
				//						while (mBytesRead < mMusicLength && mIsPlaying) {
				//							ret = mInputStream.read(mByteData, 0, mBufferSize);
				//							if (ret != -1) {
				//								// Write the byte array to the track
				//								mTrack.setStereoVolume(mVol * mMaxVol, mVol
				//										* mMaxVol);
				//								mTrack.write(mByteData, 0, ret);
				//								mBytesRead += ret;
				//							} else if (mIsLooping) {
				//								mInputStream.reset();
				//							}
				//						}
				//					}
				//				} catch (Exception e) {
				//					e.printStackTrace();
				//				}
				//			}

			}
		});
		mThread.start();
	}

	public void makeFile()
	{
		//		// release();
		//		mFile = new File(mPath);
		//		mByteData = new byte[(int) mBufferSize];
		//		mBytesRead = 0;
		//
		//		mMinBuffSize = AudioTrack.getMinBufferSize(44100,
		//				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		//		mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
		//				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
		//				mMinBuffSize, AudioTrack.MODE_STREAM);
		//		mMaxVol = mTrack.getMaxVolume();
		//		try {
		//			mInputStream = new FileInputStream(mFile);
		//
		//		} catch (FileNotFoundException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		mMusicLength = (int) mFile.length();
		//
		//		mPlayer.play();
		//
		//		setIsPlaying(true);
		//		mHasSource = true;
	}

	public void setPath(String aPath)
	{
		stop();
		mHasSource = false;
		release();
		mPath = aPath;
		makeFile();
		makeThread();
	}

	public Uri getUri()
	{
		return mUri;
	}

	public String getPath()
	{
		return mPath;
	}

	public void pause()
	{
		if (!mIsPrepared)
			return;
		setIsPlaying(false);
		mPlayer.pause();
	}

	public void stop()
	{
		if (!mIsPrepared)
			return;
		setIsPlaying(false);
		mPlayer.stop();
		mBytesRead = 0;
	}

	public void play()
	{
		if (!mIsPrepared)
			return;
		mPlayer.start();
		setIsPlaying(true);

	}

	public void release()
	{
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		mInputStream = null;
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.release();
			mPlayer = null;
		}
	}

	public void setIsPlaying(boolean aIsPlaying)
	{
		mIsPlaying = aIsPlaying;
		System.out.println(aIsPlaying);
	}

	public boolean getIsPlaying()
	{
		return mIsPlaying;
	}

	public void setIsLooping(boolean aIsLooping)
	{
		mIsLooping = aIsLooping;
		mPlayer.setLooping(mIsLooping);
	}

	public boolean getIsLooping()
	{
		return mIsLooping;
	}

	public void setVolume(float aVol)
	{
		mVol = aVol;
		mPlayer.setVolume(mVol, mVol);
	}

	public float getVolume()
	{
		return mVol;
	}

	public Surface getSurface()
	{
		return mSurface;
	}

	public SurfaceTexture getTexture()
	{
		return mTexture;
	}

	////////SurfaceTexture things
	private static final int		EGL_OPENGL_ES2_BIT			= 4;
	private static final int		EGL_CONTEXT_CLIENT_VERSION	= 0x3098;
	private static final String		LOG_TAG						= "SurfaceTest.GL";
	private EGL10					egl;
	private EGLDisplay				eglDisplay;
	private EGLContext				eglContext;
	private EGLSurface				eglSurface;

	private long					lastFpsOutput				= 0;
	private int						frames;
	private Context					ctx;
	private FloatBuffer textureBuffer;
    private float textureCoords[] = { 0.0f, 1.0f, 0.0f, 1.0f,
                                      0.0f, 0.0f, 0.0f, 1.0f,
                                      1.0f, 0.0f, 0.0f, 1.0f,
                                      1.0f, 1.0f, 0.0f, 1.0f };
	private int[] textures = new int[1];

	private void pingFps()
	{
		if (lastFpsOutput == 0)
			lastFpsOutput = System.currentTimeMillis();

		frames++;

		if (System.currentTimeMillis() - lastFpsOutput > 1000) {
			Log.d(LOG_TAG, "FPS: " + frames);
			lastFpsOutput = System.currentTimeMillis();
			frames = 0;
		}
	}

	private void initGL()
	{
		egl = (EGL10) EGLContext.getEGL();
		eglDisplay = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

		int[] version = new int[2];
		egl.eglInitialize(eglDisplay, version);

		EGLConfig eglConfig = chooseEglConfig();
		eglContext = createContext(egl, eglDisplay, eglConfig);

		eglSurface = egl.eglCreateWindowSurface(eglDisplay, eglConfig,
				mTexture, null);

		if (eglSurface == null || eglSurface == EGL10.EGL_NO_SURFACE) {
			throw new RuntimeException("GL Error: "
					+ GLUtils.getEGLErrorString(egl.eglGetError()));
		}

		if (!egl.eglMakeCurrent(eglDisplay, eglSurface, eglSurface, eglContext)) {
			throw new RuntimeException("GL Make current error: "
					+ GLUtils.getEGLErrorString(egl.eglGetError()));
		}
	}

	private void deinitGL()
	{
		egl.eglMakeCurrent(eglDisplay, EGL10.EGL_NO_SURFACE,
				EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT);
		egl.eglDestroySurface(eglDisplay, eglSurface);
		egl.eglDestroyContext(eglDisplay, eglContext);
		egl.eglTerminate(eglDisplay);
		Log.d(LOG_TAG, "OpenGL deinit OK.");
	}

	private EGLContext createContext(EGL10 egl, EGLDisplay eglDisplay,
			EGLConfig eglConfig)
	{
		int[] attribList = { EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
		return egl.eglCreateContext(eglDisplay, eglConfig,
				EGL10.EGL_NO_CONTEXT, attribList);
	}

	private EGLConfig chooseEglConfig()
	{
		int[] configsCount = new int[1];
		EGLConfig[] configs = new EGLConfig[1];
		int[] configSpec = getConfig();

		if (!egl.eglChooseConfig(eglDisplay, configSpec, configs, 1,
				configsCount)) {
			throw new IllegalArgumentException("Failed to choose config: "
					+ GLUtils.getEGLErrorString(egl.eglGetError()));
		} else if (configsCount[0] > 0) {
			return configs[0];
		}

		return null;
	}

	private int[] getConfig()
	{
		return new int[] { EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
				EGL10.EGL_RED_SIZE, 8, EGL10.EGL_GREEN_SIZE, 8,
				EGL10.EGL_BLUE_SIZE, 8, EGL10.EGL_ALPHA_SIZE, 8,
				EGL10.EGL_DEPTH_SIZE, 0, EGL10.EGL_STENCIL_SIZE, 0,
				EGL10.EGL_NONE };
	}
	
	private void setupTexture(Context context)
    {
        ByteBuffer texturebb = ByteBuffer.allocateDirect(textureCoords.length * 4);
        texturebb.order(ByteOrder.nativeOrder());
        
        ctx = context;
        
        textureBuffer = texturebb.asFloatBuffer();
        textureBuffer.put(textureCoords);
        textureBuffer.position(0);

        // Generate the actual texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glGenTextures(1, textures, 0);
        checkGlError("Texture generate");

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        checkGlError("Texture bind");

        mTexture = new SurfaceTexture(textures[0]);
    }
	
	protected void initGLComponents()
    {
//        setupVertexBuffer();
        setupTexture(ctx);
//        loadShaders();
    }

    protected void deinitGLComponents()
    {
        GLES20.glDeleteTextures(1, textures, 0);
//        GLES20.glDeleteProgram(shaderProgram);
        mTexture.release();
        mTexture.setOnFrameAvailableListener(null);
    }

    public void checkGlError(String op)
    {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e("SurfaceTest", op + ": glError " + GLUtils.getEGLErrorString(error));
        }
    }


}
