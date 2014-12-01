package processing.test.ArUcoTest;

import java.io.File;
import java.io.IOException;

import android.util.Log;
import android.view.Surface;

public class PlayMovieThread extends Thread {
    private static final String	TAG	= "PlayMovieThread ::";
	private final File mFile;
    private final Surface mSurface;
    private final SpeedControlCallback mCallback;
    private MoviePlayer mMoviePlayer;

    /**
     * Creates thread and starts execution.
     * <p>
     * The object takes ownership of the Surface, and will access it from the new thread.
     * When playback completes, the Surface will be released.
     */
    public PlayMovieThread(File file, Surface surface, SpeedControlCallback callback) {
        mFile = file;
        mSurface = surface;
        mCallback = callback;
        System.out.println("Making the PlayMovieThread");
        start();
    }

    /**
     * Asks MoviePlayer to halt playback.  Returns without waiting for playback to halt.
     * <p>
     * Call from UI thread.
     */
    public void requestStop() {
        mMoviePlayer.requestStop();
    }

    @Override
    public void run() {
        try {
            mMoviePlayer = new MoviePlayer(mFile, mSurface, mCallback);
            mMoviePlayer.setLoopMode(true);
            mMoviePlayer.play();
        } catch (IOException ioe) {
            Log.e(TAG, "movie playback failed", ioe);
        } finally {
            mSurface.release();
            Log.d(TAG, "PlayMovieThread stopping");
        }
    }
}