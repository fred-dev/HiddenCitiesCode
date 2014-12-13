package processing.test.ArUcoTest;

import java.io.File;

import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;


public class VideoBlob implements TextureView.SurfaceTextureListener {
	private final String TAG = "VideoBlob::";
    private final String LTAG;
    private TextureView mTextureView;
    private int mMovieTag;

    public SurfaceTexture mSavedSurfaceTexture;
    private PlayMovieThread mPlayThread;
    private SpeedControlCallback mCallback;
    private File mFile;

    /**
     * Constructs the VideoBlob.
     *
     * @param view The TextureView object we want to draw into.
     * @param movieTag Which movie to play.
     * @param ordinal The blob's ordinal (only used for log messages).
     */
    public VideoBlob(TextureView view, String aPath, int ordinal) {
        LTAG = TAG + ordinal;
        Log.d(LTAG, "VideoBlob: tag=" + aPath + " view=" + view);
//        mMovieTag = movieTag;
        mFile = new File(aPath);

        mCallback = new SpeedControlCallback();
        System.out.println("Making the VideoBlob");

        recreateView(view);
    }

    /**
     * Performs partial construction.  The VideoBlob is already created, but the Activity
     * was recreated, so we need to update our view.
     */
    public void recreateView(TextureView view) {
        Log.d(LTAG, "recreateView: " + view);
        mTextureView = view;
        mTextureView.setSurfaceTextureListener(this);
        if (mSavedSurfaceTexture != null) {
        	System.out.println("while recreating view in Video Blob, mSavedSurfaceTexture is NOT null");
            Log.d(LTAG, "using saved st=" + mSavedSurfaceTexture);
            view.setSurfaceTexture(mSavedSurfaceTexture);
        }
        else{
        	System.out.println("while recreating view in Video Blob, mSavedSurfaceTexture is null");

        }
    }
    
    public TextureView getTextureView()
    {
    	return mTextureView;
    }

    /**
     * Stop playback and shut everything down.
     */
    public void stopPlayback() {
        Log.d(LTAG, "stopPlayback");
        mPlayThread.requestStop();
        // TODO: wait for the playback thread to stop so we don't kill the Surface
        //       before the video stops

        // We don't need this any more, so null it out.  This also serves as a signal
        // to let onSurfaceTextureDestroyed() know that it can tell TextureView to
        // free the SurfaceTexture.
        mSavedSurfaceTexture = null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture st, int width, int height) {
        Log.d(LTAG, "onSurfaceTextureAvailable size=" + width + "x" + height + ", st=" + st);

        // If this is our first time though, we're going to use the SurfaceTexture that
        // the TextureView provided.  If not, we're going to replace the current one with
        // the original.
        System.out.println("extracted");
        if (mSavedSurfaceTexture == null) {
            mSavedSurfaceTexture = st;

            
            mPlayThread = new PlayMovieThread(mFile, new Surface(st), mCallback);
        } else {
            // Can't do it here in Android <= 4.4.  The TextureView doesn't add a
            // listener on the new SurfaceTexture, so it never sees any updates.
            // Needs to happen from activity onCreate() -- see recreateView().
            //Log.d(LTAG, "using saved st=" + mSavedSurfaceTexture);
            //mTextureView.setSurfaceTexture(mSavedSurfaceTexture);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture st, int width, int height) {
        Log.d(LTAG, "onSurfaceTextureSizeChanged size=" + width + "x" + height + ", st=" + st);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture st) {
        Log.d(LTAG, "onSurfaceTextureDestroyed st=" + st);
        // The SurfaceTexture is already detached from the EGL context at this point, so
        // we don't need to do that.
        //
        // The saved SurfaceTexture will be null if we're shutting down, so we want to
        // return "true" in that case (indicating that TextureView can release the ST).
        return (mSavedSurfaceTexture == null);
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture st) {
        //Log.d(TAG, "onSurfaceTextureUpdated st=" + st);
    }
}

/**
 * Thread object that plays a movie from a file to a surface.
 * <p>
 * Currently loops until told to stop.
 */
