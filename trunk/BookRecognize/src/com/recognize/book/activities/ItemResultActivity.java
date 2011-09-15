package com.recognize.book.activities;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.recognize.book.database.DBManager;
import com.recognize.book.itface.RatingDialog;
import com.recognize.book.util.BookInfo;
import com.recognize.book.util.Constant;
import com.recognize.book.util.ConvertValue;
import com.recognize.book.util.TransferData;
import com.recognize.book.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


/**
 * Show a book and it's information.
 * Have some function with this book for example rate for this book, find a similar
 * book...
 */
public class ItemResultActivity extends Activity {
	public static LinearLayout progressBarLayout;
	private static String TAG = "ItemResultActivity";
	private String stringFromServer = new String();
	private String imageFrom;
	private Handler handler = new Handler();
	private Button storageImage;
	private BookInfo bookInfo;
	private ImageView imageView;
	private Bitmap imageBitmap;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.item_result); 
		
		/*
		 * The arrayStringInfo content information of a book
		 */
		ArrayList<String> arrayStringInfo = new ArrayList<String>();
		Bundle extras = getIntent().getExtras(); 
		
		/*
		 * get extras from previous activity
		 */
        if(extras !=null){
        	arrayStringInfo = extras.getStringArrayList(Constant.ARRAY_STRING_BOOK_INFO);
        	imageFrom = extras.getString(Constant.IMAGE_FROM);
        	if(imageFrom == null){
        		imageFrom = new String();
        	}
        }
        
        /*
         * If load list view from the SDCARD, the button storage will be created 
         * to delete the image 
         */
        if(imageFrom.equalsIgnoreCase(Constant.IN_SDCARD)){
        	storageImage = (Button) findViewById(R.id.storage);
        	storageImage.setText("Delete");
        }
        bookInfo = new BookInfo(arrayStringInfo);
	    
        /* Set View */
        TextView title = (TextView) findViewById(R.id.title);
        TextView author = (TextView) findViewById(R.id.author);
        TextView price = (TextView) findViewById(R.id.price);
        TextView info = (TextView) findViewById(R.id.info);
        RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        
        
        title.setText("Title: " + bookInfo.title);
        author.setText("Author: " + bookInfo.author);
        price.setText("Price: " + bookInfo.price);
        info.setText("Info: " + bookInfo.info);
        int ratingScore = 10*(int)Float.parseFloat(bookInfo.rating);
        Log.e(TAG, bookInfo.rating);
        ratingBar.setProgress(ratingScore);
        
        
        try {
        	/*Decode image from base64String */
			String base64String = TransferData.getBase64Image(bookInfo.id);
			imageBitmap = ConvertValue.base64StringToBitmap(base64String);
			imageView = (ImageView) findViewById(R.id.image);
			imageView.setImageBitmap(imageBitmap);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
        
	}		
	
    /**
     * Rating a book, using RatingDialog
     * @param v
     */
    public void rate(View v){
    	RatingDialog ratingDialog = new RatingDialog(this, new OnReadyListener());
    	ratingDialog.setTitle(Constant.VIEW_TITLE_RATING_DIALOG);
    	ratingDialog.show();
    }
    
    
    private class OnReadyListener implements RatingDialog.ReadyListener {
        public void ready(String ratingScore) {
            sendScore(ratingScore);
        }
    }
    
    /**
     * Send score after rating. So this method call after rate method
     * @param ratingScore
     */
    public void sendScore(String ratingScore) {
    	String rateResult = new String();
    	try {
			rateResult = TransferData.rate(ratingScore ,bookInfo.id);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
    	
    	if (rateResult.compareTo("OK") == 0) {
    		Toast.makeText(this, Constant.MES_RATE_OK, Toast.LENGTH_LONG).show();
    	}else{
    		Toast.makeText(this, Constant.MES_RATE_ERROR, Toast.LENGTH_LONG).show();
    	}
    }
    
    /**
     * Get information of shop for this book
     * @param v
     */
    public void buy(View v) {
    	progressBarLayout = (LinearLayout)findViewById(R.id.progress_bar_layout); 
		progressBarLayout.setVisibility(LinearLayout.VISIBLE);
		new Thread(buyThread).start();
    	Toast.makeText(this, "You pressed Buy!", Toast.LENGTH_LONG).show();
    }
    
    /**
     * Get the xml String from server and go to new Activity to show the shop
     */
    private Runnable buyThread = new Runnable() { 
		public void run() { 
	    	// TODO Auto-generated method stub 
			while (true){
				try {
					stringFromServer = TransferData.getXMLListShop(bookInfo.id);
					Log.e("XMLSHOP", stringFromServer);
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
                    Intent intent = new Intent().setClass(getApplicationContext(), ListShopActivity.class);
                    intent.putExtra(Constant.XML_STRING, stringFromServer);
        			startActivity(intent);
                }
            });
		}  
	}; 
    
    
    public void storage(View v) {
    	if(imageFrom.equalsIgnoreCase(Constant.IN_SDCARD)) {
    		delete();
    	} else {
    		save();
    	}
    }
    
    /**
     * Delete a book from database and go to ListResultActivity
     */
    public void delete() {
    	String bookId = bookInfo.id;
    	DBManager.deleteBookById(this, bookId);
    	String xmlString = DBManager.getAllBookInXMLForm(this);
    	Intent intent = new Intent().setClass(
        		getApplicationContext(), ListResultActivity.class);
    	Log.e("HOME", xmlString);
        intent.putExtra(Constant.XML_STRING, xmlString);
        intent.putExtra(Constant.IMAGE_FROM, Constant.IN_SDCARD);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
    }
    
    /*
     * save book to database
     */
    public void save() {
    	try{
    		bookInfo.image = saveToSdCard(imageBitmap);
    		DBManager.addBook(this, bookInfo);
    	} catch (Exception e){
    		Toast.makeText(this, Constant.ERROR, Toast.LENGTH_LONG);
    		Log.e(TAG, e.toString());
    	}
    	finish();
    }
    
    /*
     * save a Bitmap image to Sdcard
     */
    private static String saveToSdCard(Bitmap image) {
    	if(image == null){
			return null;
		}
	
		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		
		//save file into SD card
		File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BookRecognizeSave");
		if (!imageFolder.exists()) {
			//if the folder doesn't exist, create it
			imageFolder.mkdir();
		}
        File file = new File(imageFolder, "book_" + simpleDateFormat.format(today) + ".jpg");
        FileOutputStream fos;
        
    	try {
 	       fos = new FileOutputStream(file);
 	       image.compress(CompressFormat.JPEG, 100, fos);
 	       String path = file.getAbsolutePath();
 	       return path;
 	    } catch (FileNotFoundException e) {
 	    	Log.e(TAG, e.toString());
 	    }
 	    
 	    return null;
	}

    /**
     * find a similar book and show dialog processing
     * @param v
     */
	public void findSimilar(View v) {
		progressBarLayout = (LinearLayout)findViewById(R.id.progress_bar_layout); 
		progressBarLayout.setVisibility(LinearLayout.VISIBLE);
		new Thread(sendInfoImageThread).start();
	}
	
	/**
	 * send book id to get similar book
	 */
	private Runnable sendInfoImageThread = new Runnable() { 
		public void run() { 
	    	// TODO Auto-generated method stub 
			while (true){
				try {
					stringFromServer = TransferData.findSimilar(bookInfo.id);
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
                    intent.putExtra(Constant.XML_STRING, stringFromServer);
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
