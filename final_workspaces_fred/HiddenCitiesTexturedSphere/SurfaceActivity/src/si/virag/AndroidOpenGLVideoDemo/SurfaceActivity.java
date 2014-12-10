package si.virag.AndroidOpenGLVideoDemo;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.Surface;
import android.view.TextureView;
import si.virag.AndroidOpenGLVideoDemo.gl.VideoTextureRenderer;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

public class SurfaceActivity extends Activity implements TextureView.SurfaceTextureListener, OnPreparedListener
{
    private static final String LOG_TAG = "SurfaceTest";

    private TextureView surface;
    private MediaPlayer player;
    private VideoTextureRenderer renderer;

    private int surfaceWidth;
    private int surfaceHeight;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        surface = (TextureView) findViewById(R.id.surface);
        surface.setSurfaceTextureListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (surface.isAvailable())
            startPlaying();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (player != null)
            player.release();
        if (renderer != null)
            renderer.onPause();
    }

    private void startPlaying()
    {
        renderer = new VideoTextureRenderer(this, surface.getSurfaceTexture(), surfaceWidth, surfaceHeight);
        player = new MediaPlayer();

        try
        {
        	FileDescriptor afd = null;

           // AssetFileDescriptor afd = getAssets().openFd("big_buck_bunny.mp4");
            File root = Environment.getExternalStorageDirectory();
            String mFileLocation = root + "/hiddenCities/video/360.mp4";
            FileInputStream fis = new FileInputStream(mFileLocation);
            afd = fis.getFD(); 
           // player.setDataSource(afd);
            player.setDataSource(mFileLocation);
            player.setSurface(new Surface(renderer.getVideoTexture()));
            player.setLooping(true);
            player.setOnPreparedListener(this);
            player.prepare();
            

            

        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not open input video!");
        }
    }
    public void onPrepared(MediaPlayer mp){
    	
    	renderer.setVideoSize(mp.getVideoWidth(), mp.getVideoHeight());
	      mp.start();
	    }
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
        startPlaying();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface)
    {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface)
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
