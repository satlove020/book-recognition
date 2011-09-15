package com.recognize.book.activities;

import java.io.File;


import com.recognize.book.util.Constant;
import com.recognize.book.util.Res;
import com.recognize.book.util.TransferData;
import com.recognize.book.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
 * The AfterLoadActivity show the image after load an image from sdcard
 * This activity to show image and do search function.
 */
public class AfterLoadActivity extends Activity {
    /** Called when the activity is first created. */
	protected String path; 
	protected boolean taken; 
	private ImageView imageView;
	private Handler handler = new Handler();
	private LinearLayout progressBarLayout;
	private String imageFilePath;
	private String stringFromServer = new String();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.after_load);
        imageView = (ImageView) findViewById(R.id.image);
        Bundle extras = getIntent().getExtras(); 
        if (extras !=null) {
        	/* get path of image file from previous Intent */
        	imageFilePath = extras.getString(Constant.PATH_INTENT);
        }
        /* Set image view from the path file*/
        imageView.setImageURI(Uri.parse(imageFilePath));
        
    }
    
	/**
	 * Call this method when click on delete button.
	 * Delete the image in memory card.
	 * @param v
	 */
	public void delete(View v) {
		
		/* Creat the Dialog alert user when delete image. */
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle(Res.getString(this, R.string.delete_title));
		alertDialog.setMessage(Res.getString(this, R.string.delete_question));
		
		/* Set button OK. */
		alertDialog.setButton(Res.getString(this, R.string.delete_title_ok_button), 
				new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				File file = new File(imageFilePath);
				if(file.delete()) {
					/* Send message to screen when delete file complete. */
					Toast.makeText(getApplicationContext(),
							Res.getString(getApplicationContext(), 
										  R.string.delete_message_ok), 
							Toast.LENGTH_LONG).show();
				
					Intent intent = new Intent().setClass(
							getApplicationContext(), LoadActivity.class);
		        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				} else {
					Toast.makeText(getApplicationContext(),
							Res.getString(getApplicationContext(), 
									      R.string.delete_message_ok), 
							Toast.LENGTH_LONG).show();
				}
		   }
		});
		
		/* Set button Cancel. */
		alertDialog.setButton2(Res.getString(this, R.string.delete_title_cancel_button), 
			new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				return;
		   }
		});
		
		/* Set icon and show the dialog when click in delete button*/
		alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
		
	}
	
	/**
	 * Call method when click on Cancel button.
	 * Back to the previous activity.
	 * @param v
	 */
	public void cancel(View v){
		finish();
	}
	
	/**
	 * Call method when click on Search button.
	 * Search image and receive data from server
	 * @param v
	 */
	public void search(View v){
		/*
		 * Create a progress bar layout, it show when user wait for receiving
		 * list result book
		 */
		progressBarLayout = (LinearLayout)findViewById(R.id.progress_bar_layout); 
		progressBarLayout.setVisibility(LinearLayout.VISIBLE);
		new Thread(sendInfoImageThread).start();
	}
	
	/**
	 * This method send image and receive data from server.
	 */
	private Runnable sendInfoImageThread = new Runnable(){ 
		public void run() {
			while (true){
				try {
					stringFromServer = TransferData.sendImageForResult(imageFilePath);
					break;
				} catch (Exception e){
					Log.e("SEARCH", "XXX");
					break;
				}
			} 
			
			/* Hide the progress bar layout. */
            handler.post(new Runnable() 
            {
                @SuppressWarnings("static-access")
				public void run() {
                    progressBarLayout.setVisibility(progressBarLayout.GONE);
                    Intent intent = new Intent().setClass(
                    		getApplicationContext(), ListResultActivity.class);
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