package com.example.hiddencitiesmap;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

public class MyFTP
{
	FTPClient	mFTPClient;
	FTPFile[]	mFTPFiles;
	boolean		mIsConnected	= false;
	String		mIP;
	String		mUserName, mPassword;
	Thread		mThread;

	public MyFTP()
	{
		mFTPClient = new FTPClient();
	}

	public boolean getIsConnected()
	{
		return mIsConnected;
	}

	public void setIP(String aIP)
	{
		mIP = aIP;
	}

	public void setUserName(String aUserName)
	{
		mUserName = aUserName;
	}

	public void setPassword(String aPassword)
	{
		mPassword = aPassword;
	}

	public String getIP()
	{
		return mIP;
	}
	
	public boolean isConnected()
	{
		return mIsConnected;
	}

	public String getUserName()
	{
		return mUserName;
	}

	public String getPassword()
	{
		return mPassword;
	}

	public void connnectWithFTP(String aIP, String aUserName, String aPassword)
	{
		mIP = aIP;
		mUserName = aUserName;
		mPassword = aPassword;
		connectWithFTP();
	}

	public void connnectWithFTP(String aUserName, String aPassword)
	{
		mUserName = aUserName;
		mPassword = aPassword;
		connectWithFTP();
	}

	public void connectWithFTP()
	{
		mThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				mIsConnected = false;
				try {

					mFTPClient.setConnectTimeout(10 * 1000);
					mFTPClient.connect(InetAddress.getByName(mIP));
					mIsConnected = mFTPClient.login(mUserName, mPassword);
					if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
						mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
						mFTPClient.enterLocalPassiveMode();
						mFTPFiles = mFTPClient.listFiles();
					}
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mIsConnected = true;
				Log.d("FTP", "Connected");
			}
		});
		mThread.start();
	}

	public boolean uploadFile(File aFile, String aServerFilePath)
	{
		if (!mIsConnected) {
			return false;
		}
		final File file = aFile;
		final String serverFilePath = aServerFilePath;
		mThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try {
					FileInputStream srcFileStream = new FileInputStream(file);
					boolean didStoreFile = mFTPClient.storeFile(
							serverFilePath, srcFileStream);
					Log.e("FTP File Store Status", String.valueOf(didStoreFile));
					srcFileStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mThread.start();
		return true;
	}
	
	public boolean uploadFile(String aFilePath, String aServerFilePath)
	{
		if (!mIsConnected) {
			return false;
		}
		final File file = new File(aFilePath);
		final String serverFilePath = aServerFilePath;
		mThread = new Thread(new Runnable() {
			@Override
			public void run()
			{
				try {
					FileInputStream srcFileStream = new FileInputStream(file);
					boolean didStoreFile = mFTPClient.storeFile(
							serverFilePath, srcFileStream);
					Log.e("FTP File Store Status", String.valueOf(didStoreFile));
					srcFileStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		mThread.start();
		return true;
	}

}
