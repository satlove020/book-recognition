package com.recognize.book.activities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.recognize.book.util.Constant;
import com.recognize.book.util.TransferData;
import com.recognize.book.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * View image from intent CAMERA_CAPTURE
 * Send image to search
 * get result list
 * 
 */
public class SearchActivity extends Activity {
    /** Called when the activity is first created. */
	protected String path;
	protected boolean taken;
	private final int TAKE_IMAGE = 0;
	

	private ImageView imageView;
	private Bitmap image = null;
	 
	private Handler handler = new Handler();
	private LinearLayout progressBarLayout;
	private String imageFilePath;
	private String XMLString = new String();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        imageView = (ImageView)findViewById(R.id.image);
	    
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivityForResult(intent, TAKE_IMAGE);
    }
    
    
    /**
	 * Get result from child activity, 
	 * result is a picture which taken from camera or get from a file 
	 */
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_IMAGE) {
			if (resultCode == RESULT_OK) {
	            
	            try {
	            	image = (Bitmap)data.getExtras().get("data");
	            	
	            	
	            	String path = tempSave();
	        	    Bitmap result = BitmapFactory.decodeFile(path);
	        	    imageView.setImageBitmap(result);
	        	    
	        	    
	            	/*
	            	 * Test Base64
	            	 *
	            	String testString = ConvertValue.bitmapToBase64String(image);
	            	Toast.makeText(this, testString, Toast.LENGTH_SHORT).show();
	            	Bitmap testImage = ConvertValue.base64StringToBitmap(testString);
	            	imageView.setImageBitmap(testImage);
	            	String path = tempSave();
	            	*/
	            } catch (Exception e) {
	                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT).show();
	                Log.e("===== Camera ========", e.toString()); 
	            }
	            
			}
		}
	}
	
	/*
	 * Temporary save to a folder
	 */
	private String tempSave(){
		//save file into SD card
		File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BookRecoginze");
		
		if (!imageFolder.exists()) {
			//if the folder doesn't exist, create it
			imageFolder.mkdir();
		}
        File file = new File(imageFolder, "book_temp" + ".jpg");
        FileOutputStream fos;

        imageFilePath = file.getPath();
        
	    try {
	       fos = new FileOutputStream(file);
	       image.compress(CompressFormat.JPEG, 50, fos);
	       return file.getAbsolutePath();
	       
	    } catch (FileNotFoundException e) {
	    }
	    
	    return null;
	}
	
	/**
	 * Save image to sdcard
	 * @param v
	 */
	public void save(View v){
		if(image == null){
			Toast.makeText(this, R.string.save_error, Toast.LENGTH_SHORT).show();
			return;
		}
	
		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		
		//save file into SD card
		File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BookRecognize");
		if (!imageFolder.exists()) {
			//if the folder doesn't exist, create it
			imageFolder.mkdir();
		}
        File file = new File(imageFolder, "book_" + simpleDateFormat.format(today) + ".jpg");
        FileOutputStream fos;
        
	    try {
	       fos = new FileOutputStream(file);
	       image.compress(CompressFormat.JPEG, 60, fos);
	       String path = file.getAbsolutePath();
	       Toast.makeText(this, "Your file saved in: " + path, Toast.LENGTH_SHORT).show();
	       finish();
	    } catch (FileNotFoundException e) {
	    }
	    
	    return;
	}
	
	/*
	 * back to previous activity
	 */
	public void cancel(View v){
		finish();
	}
	
	/**
	 * send an image to get list book
	 * @param v
	 */
	public void search(View v){
		progressBarLayout = (LinearLayout)findViewById(R.id.progress_bar_layout); 
		progressBarLayout.setVisibility(LinearLayout.VISIBLE);
		new Thread(sendInfoImageThread).start();
	}
	
	/**
	 * This thread send an image and get the result
	 */
	private Runnable sendInfoImageThread = new Runnable(){ 
		public void run() { 
	    	// TODO Auto-generated method stub 
			while (true){
				try {
					XMLString = TransferData.sendImageForResult(imageFilePath);
					break;
				} catch (Exception e){
					Log.e("SEARCH", "XXX");
					break;
				}
			} 
			
			//---hides the progress bar---
            handler.post(new Runnable() 
            {
                @SuppressWarnings("static-access")
				public void run() {
                    progressBarLayout.setVisibility(progressBarLayout.GONE);
                    Intent intent = new Intent().setClass(getApplicationContext(), ListResultActivity.class);
                    intent.putExtra(Constant.XML_STRING, XMLString);
        			startActivity(intent);
                }
            });
		}  
	}; 
	
	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:     
            	Intent intent = new Intent().setClass(this, HomeActivity.class);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
                break;
            case R.id.load:     
            	Toast.makeText(this, "You pressed Load!", Toast.LENGTH_LONG).show();
                break;
            case R.id.info: 
            	Toast.makeText(this, "You pressed Info!", Toast.LENGTH_LONG).show();
                break;
            case R.id.help: 
            	Toast.makeText(this, "You pressed Help!", Toast.LENGTH_LONG).show();
            	break;
            case R.id.quit: 
            	finish();       
            	break;
        }
        return true;
    }

}