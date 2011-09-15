package com.recognize.book.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.xml.parsers.*;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

/**
 * 
 * @author K53CA
 * This class parsing the xml string and then get the ArrayList of BookInfo class
 */
public class ParseXML {
	public static String TAG = "ParseXML";
	
	/**
	 * This function to get the ArrayList of BookInfo by parsing the xml String
	 * View xml String form in the document
	 * @param xml the xml String
	 * @return the ArrayList of BookInfo. Each element is a BookInfo
	 * @throws ParserConfigurationException
	 */
	public static ArrayList<BookInfo> getListBook(String xml) throws ParserConfigurationException{
		ArrayList<BookInfo> bookInfo = new ArrayList<BookInfo>();
	    try {
	    	/*Creat a document to parsing*/
	    	DocumentBuilder builder;
			DocumentBuilderFactory factory;
			
			
			InputSource is;
			Document dom;
	        factory = DocumentBuilderFactory.newInstance();
	        StringReader sr = new StringReader(xml);
			is = new InputSource(sr);
	        builder = factory.newDocumentBuilder();

	        dom = builder.parse(is);
	        dom.getDocumentElement().normalize();
	        
	        /*Get nodelist from Tag, each node is info of Book*/
	        NodeList nodeListId = dom.getElementsByTagName(Constant.TAG_BOOK_ID);
	        NodeList nodeListTitle = dom.getElementsByTagName(Constant.TAG_BOOK_TITLE);
	        NodeList nodeListAuthor = dom.getElementsByTagName(Constant.TAG_BOOK_AUTHOR);
	        NodeList nodeListRating = dom.getElementsByTagName(Constant.TAG_BOOK_RATING);
	        NodeList nodeListInfo = dom.getElementsByTagName(Constant.TAG_BOOK_INFO);
	        NodeList nodeListPrice = dom.getElementsByTagName(Constant.TAG_BOOK_PRICE);
	        NodeList nodeListImage = dom.getElementsByTagName(Constant.TAG_BOOK_IMAGE);
	        
	        for(int i = 0; i < nodeListId.getLength(); i++) {
	        	String id = ((Node)nodeListId.item(i)).getTextContent();
	        	String title = ((Node)nodeListTitle.item(i)).getTextContent();
	        	String author = ((Node)nodeListAuthor.item(i)).getTextContent();
	        	String rating = ((Node)nodeListRating.item(i)).getTextContent();
	        	String info = ((Node)nodeListInfo.item(i)).getTextContent();
	        	String price = ((Node)nodeListPrice.item(i)).getTextContent();
	        	String image = ((Node)nodeListImage.item(i)).getTextContent();
	        	
	        	Bitmap bpImage = ConvertValue.base64StringToBitmap(image);
	        	String imagePath = save(bpImage, i);
	        	
	        	BookInfo bi = new BookInfo(id, title, author, rating, info, price, imagePath);
	        	Log.e("ID", id);
	        	Log.e("Title", title);
	        	bookInfo.add(bi);
	        }
	        
	        return bookInfo;
	    } catch(Exception e){
	    	Log.e(TAG, e.toString());
	    }
	    
		return null;
	}
	
	
	
	/**
	 * This function to get the ArrayList of BookInfo by parsing the xml String
	 * View xml String form in the document
	 * @param xml the xml String
	 * @return the ArrayList of BookInfo. Each element is a BookInfo
	 * @throws ParserConfigurationException
	 */
	public static ArrayList<BookInfo> getListBookFromDB(String xml) throws ParserConfigurationException{
		ArrayList<BookInfo> bookInfo = new ArrayList<BookInfo>();
	    try {
	    	/*Creat a document to parsing*/
	    	DocumentBuilder builder;
			DocumentBuilderFactory factory;
			
			
			InputSource is;
			Document dom;
	        factory = DocumentBuilderFactory.newInstance();
	        StringReader sr = new StringReader(xml);
			is = new InputSource(sr);
	        builder = factory.newDocumentBuilder();

	        dom = builder.parse(is);
	        dom.getDocumentElement().normalize();
	        
	        /*Get nodelist from Tag, each node is info of Book*/
	        NodeList nodeListId = dom.getElementsByTagName(Constant.TAG_BOOK_ID);
	        NodeList nodeListTitle = dom.getElementsByTagName(Constant.TAG_BOOK_TITLE);
	        NodeList nodeListAuthor = dom.getElementsByTagName(Constant.TAG_BOOK_AUTHOR);
	        NodeList nodeListRating = dom.getElementsByTagName(Constant.TAG_BOOK_RATING);
	        NodeList nodeListInfo = dom.getElementsByTagName(Constant.TAG_BOOK_INFO);
	        NodeList nodeListPrice = dom.getElementsByTagName(Constant.TAG_BOOK_PRICE);
	        NodeList nodeListImage = dom.getElementsByTagName(Constant.TAG_BOOK_IMAGE);
	        
	        for(int i = 0; i < nodeListId.getLength(); i++) {
	        	String id = ((Node)nodeListId.item(i)).getTextContent();
	        	String title = ((Node)nodeListTitle.item(i)).getTextContent();
	        	String author = ((Node)nodeListAuthor.item(i)).getTextContent();
	        	String rating = ((Node)nodeListRating.item(i)).getTextContent();
	        	String info = ((Node)nodeListInfo.item(i)).getTextContent();
	        	String price = ((Node)nodeListPrice.item(i)).getTextContent();
	        	String image = ((Node)nodeListImage.item(i)).getTextContent();
	        	
	        	BookInfo bi = new BookInfo(id, title, author, rating, info, price, image);
	        	Log.e("ID", id);
	        	Log.e("Title", title);
	        	bookInfo.add(bi);
	        }
	        
	        return bookInfo;
	    } catch(Exception e){
	    	Log.e(TAG, e.toString());
	    }
	    
		return null;
	}
	
	/**
	 * This function to get the ArrayList of ShopInfo by parsing the xml String
	 * View xml String form in the document
	 * @param xml the xml String
	 * @return the ArrayList of ShopInfo. Each element is a ShopInfo
	 * @throws ParserConfigurationException
	 */
	public static ArrayList<ShopInfo> getListShop(String xml) throws ParserConfigurationException{
		ArrayList<ShopInfo> shopInfo = new ArrayList<ShopInfo>();
	    try {
	    	DocumentBuilder builder;
			DocumentBuilderFactory factory;
			
			
			InputSource is;
			Document dom;
	        factory = DocumentBuilderFactory.newInstance();
	        StringReader sr = new StringReader(xml);
			is = new InputSource(sr);
	        builder = factory.newDocumentBuilder();

	        dom = builder.parse(is);
	        dom.getDocumentElement().normalize();
	        
	        /*Get nodelist from Tag, each node is info of Shop*/
	        NodeList nodeListName = dom.getElementsByTagName(Constant.TAG_SHOP_NAME);
	        NodeList nodeListAdress = dom.getElementsByTagName(Constant.TAG_SHOP_ADRESS);
	        NodeList nodeListCoordinate = dom.getElementsByTagName(Constant.TAG_SHOP_COORDINATE);
	        NodeList nodeListPhone = dom.getElementsByTagName(Constant.TAG_SHOP_PHONE);
	        
	        for(int i = 0; i < nodeListName.getLength(); i++){
	        	String name = ((Node)nodeListName.item(i)).getTextContent();
	        	String adress = ((Node)nodeListAdress.item(i)).getTextContent();
	        	String coordinate = ((Node)nodeListCoordinate.item(i)).getTextContent();
	        	String[] coor = coordinate.split(" ");
	        	String latitude = coor[0];
	        	String longitude = coor[1];
	        	String phone = ((Node)nodeListPhone.item(i)).getTextContent();
	        	
	        	
	        	ShopInfo si = new ShopInfo(name, adress, latitude, longitude,
	        							   phone);
	        	Log.e("NAME", name);
	        	shopInfo.add(si);
	        }
	        
	        return shopInfo;
	    } catch (ParserConfigurationException e){
	    	Log.e(TAG, e.toString());
	    } catch(Exception e){
	    	Log.e(TAG, e.toString());
	    }
	    
		return null;
	}
	
	/**
	 * Save image in to sdcard and return path of this image
	 * @param image the input Image
	 * @return the file path of image
	 */
	public static String save(Bitmap image, int position){
		if(image == null){
			return null;
		}
	
		Date today = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		
		
		//save file into SD card
		File imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BookRecognizeSmall");
		if (!imageFolder.exists()) {
			//if the folder doesn't exist, create it
			imageFolder.mkdir();
		}
        File file = new File(imageFolder, "book_" + simpleDateFormat.format(today)
        		+ Integer.toString(position) + ".jpg");
        FileOutputStream fos;
        
	    try {
	       fos = new FileOutputStream(file);
	       image.compress(CompressFormat.JPEG, 60, fos);
	       String path = file.getAbsolutePath();
	       return path;
	    } catch (FileNotFoundException e) {
	    	Log.e(TAG, e.toString());
	    }
	    
	    return null;
	}
}
