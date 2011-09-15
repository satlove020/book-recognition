package com.recognize.book.util;

import java.io.ByteArrayOutputStream;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * This class to convert type of values
 *
 */
public class ConvertValue {
	/**
	 * Convert base64 String to a bitmap image
	 * @param base64String the base64 String
	 * @return a bitmap image
	 */
	public static Bitmap base64StringToBitmap(String base64String){
		try{
			byte[] decodedString = Base64.decode(base64String);
			return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		} catch (Exception e) {
			Log.e("BookRecognize","ConvertValue.base64StrongToBitmap() ERROR" + e.toString());
		}
		return null;
	}
	
	/**
	 * Convert bitmap image to base64 String.
	 * @param image
	 * @return
	 */
	public static String bitmapToBase64String(Bitmap image){
		try {
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    		byte[] byteArray = stream.toByteArray();
    		return Base64.encodeToString(byteArray, false);
        } catch (Exception e) {
        	Log.e("BookRecognize","ConvertValue.bitmapToBase64String() ERROR" + e.toString());
        }
        
        return "";
	}
}
