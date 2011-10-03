package com.recognize.book.activities;

import java.util.ArrayList;

import com.recognize.book.itface.ListViewBookAdapter;
import com.recognize.book.util.BookInfo;
import com.recognize.book.util.Constant;
import com.recognize.book.util.ParseXML;
import com.recognize.book.*;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


/**
 * This activity show the list of book in ListView
 * Each Row is a book with the name of book, the author and the 
 * small image of this book
 */
public class ListResultActivity extends Activity implements OnItemClickListener {
	
	private String xmlString = new String();
	private String imageFrom;
    private ListView listBookView;
    private ListViewBookAdapter adapter;
    private ArrayList<BookInfo> bookInfo = new ArrayList<BookInfo>();
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_result);
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null){
        	xmlString = extras.getString(Constant.XML_STRING);
        	imageFrom = extras.getString(Constant.IMAGE_FROM);
        }
        
        prepareArrayLits();
        if(bookInfo.size() == 0){
        	Toast.makeText(this, Constant.MES_NO_IMAGE, Toast.LENGTH_LONG);
        	finish();
        }
        listBookView = (ListView) findViewById(R.id.list_book_view);
      
        adapter = new ListViewBookAdapter(this, bookInfo);
        listBookView.setAdapter(adapter);
        listBookView.setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
 
    	Intent intent = new Intent(getApplicationContext(), ItemResultActivity.class);
    	intent.putStringArrayListExtra(Constant.ARRAY_STRING_BOOK_INFO, 
    			bookInfo.get(position).bookToArrayString());
    	intent.putExtra(Constant.IMAGE_FROM, imageFrom);
		startActivity(intent);
    }

    /* Method used to prepare the ArrayList,
     * Same way, you can also do looping and adding object into the ArrayList.
     */
    public void prepareArrayLits()
    {
    	bookInfo = new ArrayList<BookInfo>();
    	
    	try {
    		if(imageFrom != null){
    			if(imageFrom.equalsIgnoreCase(Constant.IN_DATABASE)){
    				bookInfo =  ParseXML.getListBookFromDB("<" + Constant.ROOT_PARSE+ ">" + 
							   xmlString + "</" +
							   Constant.ROOT_PARSE +">");
    				return;
    			}
    		}
			bookInfo =  ParseXML.getListBook("<" + Constant.ROOT_PARSE+ ">" + 
												   xmlString + "</" +
												   Constant.ROOT_PARSE +">");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent intent;
        switch (item.getItemId()) {
            case R.id.home:     
            	intent = new Intent().setClass(this, HomeActivity.class);
				startActivity(intent);
                break;
            case R.id.quit: 
            	intent = new Intent().setClass(this, HomeActivity.class);
            	intent.putExtra(Constant.TO_QUIT, Constant.QUIT);
            	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
            	break;
        }
        return true;
    }
    
    
    
}