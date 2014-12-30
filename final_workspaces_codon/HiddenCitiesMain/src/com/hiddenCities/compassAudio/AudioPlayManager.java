package com.hiddenCities.compassAudio;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayManager
{

	public PlayThread				mThread;

	public File						mFile;
	public volatile String			mPath;
	public volatile boolean			mIsPlaying		= false;
	public volatile boolean			mHasSource		= false;
	public volatile boolean			mIsLooping		= false;
	public volatile boolean			mIsSetup		= false;
	public volatile boolean			mIsStopped		= true;
	public volatile boolean			mIsStreamMarked	= false;
	public volatile float			mVol			= 1;
	public float					mMaxVol			= 0;
	public volatile AudioTrack		mTrack;
	public volatile long			mMusicLength	= 0;
	public int						mMinBuffSize	= 0;
	public volatile int				mBufferSize		= 0;		// each kb is 1024 byte,
																// right?
	public volatile byte[]			mByteData		= null;
	public volatile FileInputStream	mInputStream	= null;
	public volatile int				mBytesRead;
	public volatile long			mBytesWritten	= 0;

	// Path of the file, buffer size in kb (the less the buffer size, more
	// responsive the audio stream and more stutters
	public AudioPlayManager(String aPath, int aBufferSize)
	{
		mPath = aPath;
		mBufferSize = aBufferSize * 1024;

	}

	public void start()
	{
		makeThread();
	}

	public void restart()
	{
		makeThread();
		while(!mIsSetup);
		play();
	}

	public void makeThread()
	{
		mThread = new PlayThread(this);
		mThread.start();
	}

	public void makeFile()
	{
		// release();

		try {
			mByteData = new byte[(int) mBufferSize];
			mFile = new File(mPath);
			mInputStream = new FileInputStream(mFile);
			mMusicLength = (long) mFile.length();
			mHasSource = true;
			mBytesRead = 0;
			mBytesWritten = 0;
			int headerOffset = 0x2C;
			mInputStream.skip(headerOffset);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mMinBuffSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);
		mMaxVol = mTrack.getMaxVolume();

	}

	public void resetFile()
	{
		try {
			mFile = new File(mPath);
			mInputStream = new FileInputStream(mFile);
			mMusicLength = (int) mFile.length();
			mBytesRead = 0;
			mBytesWritten = 0;
			mByteData = new byte[(int) mBufferSize];
			mHasSource = true;
			int headerOffset = 0x2C;
			mInputStream.skip(headerOffset);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setPath(String aPath)
	{
		stop();
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
		mBufferSize = aBufferSize * 1024;
		mByteData = new byte[(int) mBufferSize];
		play();
	}

	public void pause()
	{
		setIsPlaying(false);
		if (mTrack != null) {
			mTrack.pause();
		}
	}

	public void stop()
	{
		setIsPlaying(false);
		if (mTrack != null) {
			mTrack.stop();
		}
		resetFile();
		mIsStopped = true;
	}

	public void play()
	{
		if (mTrack != null) {
			mTrack.play();
		}
		else{
			restart();
		}
		setIsPlaying(true);
		mIsStopped = false;
	}

	public void release()
	{

		if (mTrack != null) {
			mTrack.stop();
			mTrack.release();
		}

		if (mInputStream != null) {
			try {
				mInputStream.close();
				mHasSource = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		mInputStream = null;

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

	public boolean isSetup()
	{
		return mIsSetup;
	}

	public class PlayThread extends Thread
	{
		AudioPlayManager	mPlayManager;

		public PlayThread(AudioPlayManager aPlayManager)
		{
			mPlayManager = aPlayManager;
		}

		@Override
		public void run()
		{
			try {
				mPlayManager.makeFile();
				mPlayManager.mTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
						AudioFormat.ENCODING_PCM_16BIT, mBufferSize, AudioTrack.MODE_STREAM);
				mPlayManager.mIsSetup = true;
				int headerOffset = 0x2C;
				int testEndInterval=0;
//				testEndInterval = mBufferSize*15;
				while (mHasSource) {

					while ((mPlayManager.mBytesWritten < mPlayManager.mMusicLength
							- (headerOffset + testEndInterval) )
							&& mPlayManager.mIsPlaying) {
						//						mPlayManager.mBytesRead = mPlayManager.mInputStream.read(mPlayManager.mByteData, 0,
						//								Math.min(mPlayManager.mBufferSize, ((int) mPlayManager.mMusicLength - headerOffset) - (int) mPlayManager.mBytesWritten));
						int bytesRead = mPlayManager.mInputStream.read(mPlayManager.mByteData, 0,
								mPlayManager.mBufferSize);
						if (mPlayManager.mBytesRead + bytesRead >= mPlayManager.mMusicLength - headerOffset) {
							bytesRead = (int) (mPlayManager.mMusicLength - (headerOffset + mPlayManager.mBytesRead));
						}
						mPlayManager.mTrack.setStereoVolume(mPlayManager.mVol * mPlayManager.mMaxVol, mPlayManager.mVol
								* mPlayManager.mMaxVol);
						mPlayManager.mBytesWritten += mPlayManager.mTrack.write(mPlayManager.mByteData, 0, bytesRead);
//						System.out.println("mMusicLength = " + mPlayManager.mMusicLength);
//						System.out.println("mByteWritten = " + mPlayManager.mBytesWritten);
						mPlayManager.mBytesRead += bytesRead;
//						System.out.println("mByteRead = " + mPlayManager.mBytesRead);

					}
					if (mPlayManager.mIsPlaying && mPlayManager.mIsLooping) {
						mPlayManager.resetFile();
						mPlayManager.play();
					}
				}
				System.out.println("Finishing Audio Thread/ releasing");
				mPlayManager.mIsSetup = false;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private static final String	RIFF_HEADER	= "RIFF";
	private static final String	WAVE_HEADER	= "WAVE";
	private static final String	FMT_HEADER	= "fmt ";
	private static final String	DATA_HEADER	= "data";

	private static final int	HEADER_SIZE	= 44;

	private static final String	CHARSET		= "ASCII";

	/* ... */

	public WavInfo readHeader(InputStream wavStream) throws IOException, DecoderException
	{

		ByteBuffer buffer = ByteBuffer.allocate(HEADER_SIZE);
		buffer.order(ByteOrder.LITTLE_ENDIAN);

		wavStream.read(buffer.array(), buffer.arrayOffset(), buffer.capacity());

		buffer.rewind();
		buffer.position(buffer.position() + 20);
		int format = buffer.getShort();
		int channels = buffer.getShort();
		int rate = buffer.getInt();
		buffer.position(buffer.position() + 6);
		int bits = buffer.getShort();
		int dataSize = 0;
		while (buffer.getInt() != 0x61746164) { // "data" marker
			int size = buffer.getInt();
			wavStream.skip(size);

			buffer.rewind();
			wavStream.read(buffer.array(), buffer.arrayOffset(), 8);
			buffer.rewind();
		}
		dataSize = buffer.getInt();

		return new WavInfo(rate, channels, dataSize);
	}

	public class DecoderException extends Exception
	{

		/**
		 * Declares the Serial Version Uid.
		 * 
		 * @see <a
		 *      href="http://c2.com/cgi/wiki?AlwaysDeclareSerialVersionUid">Always
		 *      Declare Serial Version Uid</a>
		 */
		private static final long	serialVersionUID	= 1L;

		/**
		 * Constructs a new exception with <code>null</code> as its detail
		 * message. The cause is not initialized, and may subsequently be
		 * initialized by a call to {@link #initCause}.
		 * 
		 * @since 1.4
		 */
		public DecoderException()
		{
			super();
		}

		/**
		 * Constructs a new exception with the specified detail message. The
		 * cause is not initialized, and may subsequently be initialized by a
		 * call to {@link #initCause}.
		 * 
		 * @param message
		 *            The detail message which is saved for later retrieval by
		 *            the {@link #getMessage()} method.
		 */
		public DecoderException(String message)
		{
			super(message);
		}

		/**
		 * Constructsa new exception with the specified detail message and
		 * cause.
		 * 
		 * <p>
		 * Note that the detail message associated with <code>cause</code> is
		 * not automatically incorporated into this exception's detail message.
		 * </p>
		 * 
		 * @param message
		 *            The detail message which is saved for later retrieval by
		 *            the {@link #getMessage()} method.
		 * @param cause
		 *            The cause which is saved for later retrieval by the
		 *            {@link #getCause()} method. A <code>null</code> value is
		 *            permitted, and indicates that the cause is nonexistent or
		 *            unknown.
		 * @since 1.4
		 */
		public DecoderException(String message, Throwable cause)
		{
			super(message, cause);
		}

		/**
		 * Constructs a new exception with the specified cause and a detail
		 * message of <code>(cause==null ?
		 * null : cause.toString())</code> (which typically contains the class
		 * and detail message of <code>cause</code>). This constructor is useful
		 * for exceptions that are little more than wrappers for other
		 * throwables.
		 * 
		 * @param cause
		 *            The cause which is saved for later retrieval by the
		 *            {@link #getCause()} method. A <code>null</code> value is
		 *            permitted, and indicates that the cause is nonexistent or
		 *            unknown.
		 * @since 1.4
		 */
		public DecoderException(Throwable cause)
		{
			super(cause);
		}
	}

	public class WavInfo
	{

		public int	rate;
		public int	channels;
		public int	dataSize;

		public WavInfo(int rate, int channels, int dataSize)
		{
			this.rate = rate;
			this.channels = channels;
			this.dataSize = dataSize;
		}
	}

}
