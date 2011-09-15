package com.recognize.book.activities;



import com.recognize.book.itface.ImageAdapter;
import com.recognize.book.util.Constant;
import com.recognize.book.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import android.widget.Toast;

/**
 * Load image from sdcard
 * all image will be view in GridView
 *
 */
public class LoadActivity extends Activity {
	private ImageAdapter imageAdapter;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load);
        
        imageAdapter = new ImageAdapter(this);
        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(imageAdapter);

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
            	Intent intent = new Intent(getApplicationContext(), AfterLoadActivity.class);
            	intent.putExtra(Constant.PATH_INTENT, imageAdapter.getFilePath(position));
				startActivity(intent);
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