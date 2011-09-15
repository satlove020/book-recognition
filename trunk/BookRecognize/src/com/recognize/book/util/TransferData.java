package com.recognize.book.util;

import java.io.IOException;
import java.net.UnknownHostException;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class to transfer data from client to server and from server to client 
 * @author MumMum
 *
 */
public class TransferData {
	private static Client client;
	
	/**
	 * send a bitmap image for a list of book
	 * @param filePath the file path of the image
	 * @return the xml string from server contain the list of book
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static String sendImageForResult(String filePath) throws UnknownHostException, IOException{
		client = new Client(Constant.IP, Constant.PORT);
		
		Bitmap image = BitmapFactory.decodeFile(filePath);
		String stringToSend = ConvertValue.bitmapToBase64String(image);
		String stringFromServer = new String();
		
		if(client.sendImage(stringToSend)){
			stringFromServer = client.getListBookResult();
		}
		
		Log.e("TRANSFER", stringFromServer);
		
		client.close();
		return stringFromServer;
	}
	
	/**
	 * send a id book for a list of similar book
	 * @param id the id of book
	 * @return the xml String contain the list of book
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static String findSimilar(String id) throws UnknownHostException, IOException{
		client = new Client(Constant.IP, Constant.PORT);
		
		String stringFromServer = new String();
		
		if(client.findSimilar(id)) {
			stringFromServer = client.getListBookResult();
		}else{
			stringFromServer = "ERROR";
		}
		
		Log.e("TRANSFER", stringFromServer);
		
		client.close();
		return stringFromServer;
	}
	
	/**
	 * send id book and score to rating for a book
	 * @param id the id of book
	 * @param score the score
	 * @return the message from server
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static String rate(String id, String score) throws UnknownHostException, IOException {
		client = new Client(Constant.IP, Constant.PORT);
		
		String stringFromServer = new String();
		
		if(client.rate(id, score)) {
			stringFromServer = client.getRateResult();
		}else{
			stringFromServer = "ERROR";
		}
		
		Log.e("TRANSFER", stringFromServer);
		
		client.close();
		return stringFromServer;
	}
	
	/**
	 * send id of book to get all shops selling this book
	 * @param id the id of book
	 * @return the xml string contain the list of shop and its information
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static String getXMLListShop(String id) throws UnknownHostException, IOException {
		client = new Client(Constant.IP, Constant.PORT);
		String XMLListShop = new String();
		
		if(client.sendIdToFindShop(id)){
			XMLListShop = client.getListShopResult();
		}
		
		Log.e("TRANSFER", XMLListShop);
		
		client.close();
		return XMLListShop;
	}
	
	/**
	 * send id to get a big image and return string encode from image.
	 * this string is base64 string.
	 * @param id
	 * @return a base64 String
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static String getBase64Image(String id) throws UnknownHostException, IOException {
		client = new Client(Constant.IP, Constant.PORT);
		String base64Image = new String();
		
		if(client.sendIdToGetImage(id)){
			base64Image = client.getBase64Image();
		}
		
		Log.e("TRANSFER", base64Image);
		
		client.close();
		return base64Image;
	}
}
