package com.example.hiddencitiiesnativecamera;



import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import org.apache.commons.net.SocketClient;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

public class HCNativeCamera extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	ImageView imgFavorite;
	SocketClient ftpClient;
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      setContentView(R.layout.activity_hcnative_camera);
	      imgFavorite = (ImageView)findViewById(R.id.imageView1);
	      imgFavorite.setOnClickListener(new OnClickListener() {
	         @Override
	         public void onClick(View v) {
	            open();
	         }
	      });
	   }
	   
	   void uploadToFTP(String server, String user, String password, String filename){
		
		        FTPClient client = new FTPClient();
		        FileInputStream fis = null;
		 
		        try {
		            client.connect(server);
		            client.login(user, password);
		            fis = new FileInputStream(filename);
		            client.storeFile(filename, fis);
		            client.logout();
		        } catch (IOException e) {
		            e.printStackTrace();
		        } finally {
		            try {
		                if (fis != null) {
		                    fis.close();
		                }
		                client.disconnect();
		            } catch (IOException e) {
		                e.printStackTrace();
		            }
		        }
	   }
	   public void open(){
	      Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	     // fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
	     // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
	      startActivityForResult(intent, 0);
	   }

	   @Override
	   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	      // TODO Auto-generated method stub
	      super.onActivityResult(requestCode, resultCode, data);
	      //if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
	          if (resultCode == RESULT_OK) {
	              // Image captured and saved to fileUri specified in the Intent
	        	  fileUri = data.getData();
	        	  Log.e("File",data.getData().getPath());
	        	  uploadToFTP("ftp://webmaster@fredrodrigues.net@ftp.greenhost.nl", "webmaster@fredrodrigues.net", "ifUjWys3", fileUri.getPath());
	          } else if (resultCode == RESULT_CANCELED) {
	              // User cancelled the image capture
	          } else {
	              // Image capture failed, advise user
	          }
	     // }
	      Bitmap bp = (Bitmap) data.getExtras().get("data");
	      imgFavorite.setImageBitmap(bp);
	   }
	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	      // Inflate the menu; this adds items to the action bar if it is present.
	      getMenuInflater().inflate(R.menu.main, menu);
	      return true;
	   }
	}