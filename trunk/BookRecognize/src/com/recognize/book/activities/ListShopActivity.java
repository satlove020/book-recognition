package com.recognize.book.activities;

import java.util.ArrayList;

import com.recognize.book.itface.ActionItem;
import com.recognize.book.itface.ListViewShopAdapter;
import com.recognize.book.itface.QuickAction;
import com.recognize.book.util.Constant;
import com.recognize.book.util.ParseXML;
import com.recognize.book.util.ShopInfo;
import com.recognize.book.*;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Each book have some shop that selling it. 
 * ListShopActivity show a list shop and information of the shop by ListView
 *
 */
public class ListShopActivity extends Activity implements OnItemClickListener {

	private String TAG = "ListShopActivity";
	private String xmlString = new String();
    private ListView listShopView;
    private ListViewShopAdapter adapter;
    private ArrayList<ShopInfo> shopInfo = new ArrayList<ShopInfo>();
    private int positionShopInList;
    /**
	 * Right arrow icon on each listview row
	 */
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_result);
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null){
        	xmlString = extras.getString(Constant.XML_STRING);
        }
        
        prepareArrayLits();
        listShopView = (ListView) findViewById(R.id.list_book_view);
      
        adapter = new ListViewShopAdapter(this, shopInfo);
        listShopView.setAdapter(adapter);
        listShopView.setOnItemClickListener(this);
    }
    
    /**
     * The function will be call when click in the item of list Shop
     */
    public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
    	positionShopInList = position;
		QuickAction m = create();
		m.show(v);
    }
    
    /**
     * creat a QuickAction
     * 
     * @return
     */
    private QuickAction create() {
    	//Add action item
        ActionItem showMapAction = new ActionItem();
		
        showMapAction.setTitle(Constant.SHOW_MAP);
        showMapAction.setIcon(getResources().getDrawable(R.drawable.shop_map));

		//Accept action item
		ActionItem callAction = new ActionItem();
		
		callAction.setTitle(Constant.CALL_PHONE);
		callAction.setIcon(getResources().getDrawable(R.drawable.shop_call));
		
		
		final QuickAction mQuickAction 	= new QuickAction(this);
		
		mQuickAction.addActionItem(showMapAction);
		mQuickAction.addActionItem(callAction);
		
		//setup the action item click listener
		mQuickAction.setOnActionItemClickListener(new QuickAction.OnActionItemClickListener() {
			public void onItemClick(int pos) {
				
				if (pos == 0) { //Add item selected
					showMap();
				} else if (pos == 1) { //Accept item selected
					callPhone();
				}
			}
		});
		
		//setup on dismiss listener, set the icon back to normal
		mQuickAction.setOnDismissListener(new PopupWindow.OnDismissListener() {
			public void onDismiss() {
			}
		});
		
		return mQuickAction;
	}

	/**
	 * Preparing a list of shop to show.
	 */
    public void prepareArrayLits()
    {
    	shopInfo = new ArrayList<ShopInfo>();
    	
    	try {
			shopInfo =  ParseXML.getListShop("<" + Constant.ROOT_PARSE+ ">" + 
												   xmlString + "</" +
												   Constant.ROOT_PARSE +">");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    
    /**
     * show a location of the shop thi the map
     */
    private void showMap(){
    	/* The latitude of the shop in the map*/
    	String latitude = shopInfo.get(positionShopInList).latitude;
    	/* The longitude of the shop in the map*/
    	String longitude = shopInfo.get(positionShopInList).longitude;
    	/* The name of shop showing in the map*/
    	String name = shopInfo.get(positionShopInList).name;
    	try{
        	Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
        			Uri.parse("geo:0,0?q=" + latitude + "," + longitude + 
        					"(" + name + ")"));
        	startActivity(intent);
        } catch (Exception e) {
        	
        }
    }
    
    /**
     * Call to a phonenumber
     */
    private void callPhone(){
    	/* The phonenumber to contact with shop*/
    	String phoneNumber = shopInfo.get(positionShopInList).numberPhone;
    	try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        } catch (Exception e) {
             Log.e(TAG, e.toString());
             Toast.makeText(this, Constant.ERROR_CALL, Toast.LENGTH_LONG);
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
        switch (item.getItemId()) {
            case R.id.home:     
            	Intent intent = new Intent().setClass(this, HomeActivity.class);
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