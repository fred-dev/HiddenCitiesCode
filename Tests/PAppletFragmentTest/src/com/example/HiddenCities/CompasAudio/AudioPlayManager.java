package com.example.HiddenCities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayManager
{

	public Thread			mThread;

	public File				mFile;
	public volatile String	mPath;
	public volatile boolean	mIsPlaying		= false;
	public volatile boolean	mHasSource		= false;
	public volatile boolean	mIsLooping		= false;
	public volatile float	mVol			= 1;
	public float			mMaxVol			= 0;
	public AudioTrack		mTrack;
	public int				mMusicLength	= 0;
	public int				mMinBuffSize	= 0;
	public volatile int		mBufferSize		= 0;		// each kb is 1024 byte,
														// right?
	public volatile byte[]	mByteData		= null;
	public FileInputStream	mInputStream	= null;
	public volatile int		mBytesRead;

	// Path of the file, buffer size in kb (the less the buffer size, more
	// responsive the audio stream and more stutters
	public AudioPlayManager(String aPath, int aBufferSize)
	{
		mPath = aPath;
		mBufferSize = aBufferSize * 1024;
		makeFile();
		makeThread();

	}

	public void makeThread()
	{
		mThread = new Thread(new Runnable() {

			@Override
			public void run()
			{
				System.out.println("thread ran");
				try {
					while (mHasSource) {
						int ret = 0;
						while (mBytesRead < mMusicLength && mIsPlaying) {
							ret = mInputStream.read(mByteData, 0, mBufferSize);
							if (ret != -1) {
								// Write the byte array to the track
								mTrack.setStereoVolume(mVol * mMaxVol, mVol
										* mMaxVol);
								mTrack.write(mByteData, 0, ret);
								mBytesRead += ret;
							} else if (mIsLooping) {
								mInputStream.reset();
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});
		mThread.start();
	}

	public void makeFile()
	{
		// release();
		mFile = new File(mPath);
		mByteData = new byte[(int) mBufferSize];
		mBytesRead = 0;

		mMinBuffSize = AudioTrack.getMinBufferSize(44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
		mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				mMinBuffSize, AudioTrack.MODE_STREAM);
		mMaxVol = mTrack.getMaxVolume();
		try {
			mInputStream = new FileInputStream(mFile);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mMusicLength = (int) mFile.length();

		mTrack.play();

		setIsPlaying(true);
		mHasSource = true;
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

	public String getPath()
	{
		return mPath;
	}

	public void setBufferSize(int aBufferSize)
	{
		pause();
		mBufferSize = aBufferSize;
		mByteData = new byte[(int) mBufferSize];
		play();
	}

	public void pause()
	{
		setIsPlaying(false);
		mTrack.pause();
	}

	public void stop()
	{
		setIsPlaying(false);
		mTrack.stop();
		mBytesRead = 0;
	}

	public void play()
	{
		mTrack.play();
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
		if (mTrack != null) {
			mTrack.stop();
			mTrack.release();
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
	}

	public boolean getIsLooping()
	{
		return mIsLooping;
	}

	public void setVolume(float aVol)
	{
		mVol = aVol;
	}

	public float getVolume()
	{
		return mVol;
	}

}
