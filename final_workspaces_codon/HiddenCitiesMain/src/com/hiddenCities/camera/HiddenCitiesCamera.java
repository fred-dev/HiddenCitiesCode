package com.hiddenCities.camera;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.hiddenCities.R;
import com.hiddenCities.main.HiddenCitiesMain;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

/**
 * Take a picture directly from inside the app using this fragment.
 * 
 * Reference: http://developer.android.com/training/camera/cameradirect.html
 * Reference:
 * http://stackoverflow.com/questions/7942378/android-camera-will-not-
 * work-startpreview-fails Reference:
 * http://stackoverflow.com/questions/10913181/camera-preview-is-not-restarting
 * 
 * Created by Rex St. John (on behalf of AirPair.com) on 3/4/14.
 */
public class HiddenCitiesCamera extends Fragment implements Button.OnClickListener
{

	// Activity result key for camera
	static final int	REQUEST_TAKE_PHOTO	= 11111;

	View				mView;
	HiddenCitiesMain	mActivity;

	/**
	 * OnCreateView fragment override
	 * 
	 * @param inflater
	 * @param container
	 * @param savedInstanceState
	 * @return
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		mActivity = (HiddenCitiesMain) getActivity();
		mView = inflater.inflate(R.layout.camera_layout, container, false);
		dispatchTakePictureIntent();

		return mView;
	}

	/**
	 * Start the camera by dispatching a camera intent.
	 */
	protected void dispatchTakePictureIntent()
	{

		// Check if there is a camera.
		Context context = mActivity;
		PackageManager packageManager = context.getPackageManager();
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA) == false) {
			Toast.makeText(mActivity, "This device does not have a camera.", Toast.LENGTH_SHORT).show();
			return;
		}

		// Camera exists? Then proceed...
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
			// Create the File where the photo should go.
			// If you don't do this, you may get a crash in some devices.
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
				Toast toast = Toast.makeText(mActivity, "There was a problem saving the photo...", Toast.LENGTH_SHORT);
				toast.show();
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				Uri fileUri = Uri.fromFile(photoFile);
				mActivity.setCapturedImageURI(fileUri);
				mActivity.setCurrentPhotoPath(fileUri.getPath());
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mActivity.getCapturedImageURI());
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	/**
	 * The activity returns with the photo.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{

		if (requestCode == REQUEST_TAKE_PHOTO) {
			if (resultCode == Activity.RESULT_OK) {
				addPhotoToGallery();
				mActivity.uploadFile(mActivity.getCurrentPhotoPath());
				mActivity.detachCameraScene();
			} else if (resultCode == Activity.RESULT_CANCELED) {
			} else {
				mActivity.detachCameraScene();
			}
		}

	}

	/**
	 * Creates the image file to which the image must be saved.
	 * 
	 * @return
	 * @throws IOException
	 */
	protected File createImageFile() throws IOException
	{
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(imageFileName, /* prefix */
				".jpg", /* suffix */
				storageDir /* directory */
		);

		// Save a file: path for use with ACTION_VIEW intents
		mActivity.setCurrentPhotoPath("file:" + image.getAbsolutePath());
		return image;
	}

	/**
	 * Add the picture to the photo gallery. Must be called on all camera images
	 * or they will disappear once taken.
	 */
	protected void addPhotoToGallery()
	{
		Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(mActivity.getCurrentPhotoPath());
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		mActivity.sendBroadcast(mediaScanIntent);
	}

	/**
	 * Deal with button clicks.
	 * 
	 * @param v
	 */
	@Override
	public void onClick(View v)
	{
		dispatchTakePictureIntent();
	}

	/**
	 * Scale the photo down and fit it to our image views.
	 * 
	 * "Drastically increases performance" to set images using this technique.
	 * Read more:http://developer.android.com/training/camera/photobasics.html
	 */
	private void setFullImageFromFilePath(String imagePath, ImageView imageView)
	{
		// Get the dimensions of the View
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imagePath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
		imageView.setImageBitmap(bitmap);
	}
}