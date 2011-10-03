package com.recognize.book.activities;



import java.io.File;
import java.io.IOException;


import com.recognize.book.database.DBManager;
import com.recognize.book.database.Database;
import com.recognize.book.database.Database.Book;
import com.recognize.book.util.Constant;
import com.recognize.book.util.Res;
import com.recognize.book.*;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/**
 * HomeActivity is the class extends the class Activity
 *
 */
public class HomeActivity extends Activity {
	/*
	 * Button to take a new image. After click in the button the SearchActivity
	 * will be call. And the camera will be call to take image and search.
	 */
	private Button takeImageButton; 
	
	/*
	 * Button to load image from sdcard. After click in the button the LoadActivity
	 * will be call. 
	 */
	private Button loadImageButton; 
	
	public void onCreate(Bundle savedInstanceState) {
			Bundle extras = getIntent().getExtras(); 
	        if(extras !=null){
	        	String quit = extras.getString(Constant.TO_QUIT);
	        	if(quit.equalsIgnoreCase(Constant.QUIT)){
	        		finish();
	        	}
	        }
	        
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.home);
	        
	        takeImageButton = (Button)findViewById(R.id.take_image);
	        loadImageButton = (Button)findViewById(R.id.load_image);
	        
	        
	        takeImageButton.setOnClickListener(new OnClickListener() {	
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
					startActivity(intent);
				}
			});
	        
	        loadImageButton.setOnClickListener(new OnClickListener(){
	        	
	        	public void onClick(View v){
	        		File saveFolder = new File(Constant.SAVED_FOLDER);
	        		if(saveFolder.exists() == false){
	        			Toast.makeText(getApplicationContext(), 
	        						   Constant.MES_NO_FOLDER, Toast.LENGTH_LONG).show();
	        			
	        		} else if(saveFolder.listFiles().length == 0){
	        			Toast.makeText(getApplicationContext(), 
	        						   Constant.MES_NO_FILE, Toast.LENGTH_LONG).show();
	        		}else {
	        			Intent intent = new Intent(getApplicationContext(), LoadActivity.class);
	        			startActivity(intent);
	        		}
	        	}
	        });
	        
	        
	}		
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent = new Intent();
        switch (item.getItemId()) {
            case R.id.home:     
            	intent = new Intent().setClass(this, HomeActivity.class);
				startActivity(intent);
                break;
            case R.id.load:
            	load();
            	Toast.makeText(this, "You pressed Load!", Toast.LENGTH_LONG).show();
                break;
            case R.id.info: 
            	Toast.makeText(this, "You pressed Info!", Toast.LENGTH_LONG).show();
                break;
            case R.id.help: 
            	createDatabase();
            	//Toast.makeText(this, "You pressed Help!", Toast.LENGTH_LONG).show();
            	break;
            case R.id.quit: 
            	finish();
            	break;
        }
        return true;
    }
    
    public void createDatabase(){
    	Database db = new Database(this);
    	Cursor cursor = db.getBookById(1);
    	if(cursor.moveToFirst()){
    		String result = cursor.getString(cursor.getColumnIndex(Book.TITLE));
    		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
    	}
    }
    
    /**
     * Get all book from database and then convert list book to xml string. After
     * that send this String to new activity called ListResultActivity. ListResultActivity
     * will show all book.
     */
    public void load(){
    	String xmlString = DBManager.getAllBookInXMLForm(this);
    	Intent intent = new Intent().setClass(
        		getApplicationContext(), ListResultActivity.class);
    	Log.e("HOME", xmlString);
        intent.putExtra(Constant.XML_STRING, xmlString);
        intent.putExtra(Constant.IMAGE_FROM, Constant.IN_DATABASE);
		startActivity(intent);
    }
}
