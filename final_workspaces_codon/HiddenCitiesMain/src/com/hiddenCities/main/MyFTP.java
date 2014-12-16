package com.hiddenCities.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyFTP
{
	FTPClient		mFTPClient;
	boolean			mIsConnected	= false;
	String			mIP;
	String			mUserName, mPassword;
	Thread			mThread;
	ExecutorService	mExecutor;

	public MyFTP()
	{
		mFTPClient = new FTPClient();
		mExecutor = Executors.newFixedThreadPool(2);

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

					mFTPClient.setConnectTimeout(40 * 1000);
					mFTPClient.connect(InetAddress.getByName(mIP));
					mIsConnected = mFTPClient.login(mUserName, mPassword);
					if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
						mFTPClient.setFileType(FTP.ASCII_FILE_TYPE);
						mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
						mFTPClient.enterLocalPassiveMode();
						mFTPClient.changeToParentDirectory();
					}
				} catch (SocketException e) {
					e.printStackTrace();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mIsConnected = true;
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
		Runnable workerThread = new Runnable() {
			@Override
			public void run()
			{
				try {
					FileInputStream srcFileStream = new FileInputStream(file);
					boolean didStoreFile = mFTPClient.storeFile(serverFilePath, srcFileStream);
					Log.e("Status", String.valueOf(didStoreFile));
					srcFileStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		mExecutor.execute(workerThread);
		return true;
	}

	public boolean uploadFile(String aFilePath, String aServerFilePath)
	{
		if (!mIsConnected) {
			return false;
		}
		final File file = new File(aFilePath);
		final String serverFilePath = aServerFilePath;
		Runnable workerThread = new Runnable() {
			@Override
			public void run()
			{
				try {
					FileInputStream srcFileStream = new FileInputStream(file);
					boolean didStoreFile = mFTPClient.storeFile(serverFilePath, srcFileStream);
					Log.e("Status", String.valueOf(didStoreFile));
					srcFileStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		mExecutor.execute(workerThread);
		return true;
	}

	public boolean uploadFile(String aFilePath, String aServerFilePath, String aFileName)
	{
		if (!mIsConnected) {
			return false;
		}
		System.out.println("File path is " + aFilePath);
		final File file = new File(aFilePath);
		final String serverFilePath = aServerFilePath;
		System.out.println("Server Path is " + serverFilePath);
		final String fileName = aFileName;
		System.out.println("File name is " + fileName);
		
		Runnable workerThread = new Runnable() {
			@Override
			public void run()
			{
				try {
					mFTPClient.changeToParentDirectory();
					FileInputStream srcFileStream = new FileInputStream(file);
					System.out.println("Working Directory is " + mFTPClient.printWorkingDirectory());
					if (!mFTPClient.changeWorkingDirectory(serverFilePath)) {
						System.out.println("making directory");
						mFTPClient.makeDirectory(serverFilePath);
						System.out.println("made directory");
						mFTPClient.changeWorkingDirectory(serverFilePath);

					}
					System.out.println("Working Directory is " + mFTPClient.printWorkingDirectory());
					boolean didStoreFile = mFTPClient.storeFile(fileName, srcFileStream);
					Log.e("Status", String.valueOf(didStoreFile));
					if (didStoreFile) {
						System.out.println("Uploaded File");
					}
					srcFileStream.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		mExecutor.execute(workerThread);
		return true;
	}

}
