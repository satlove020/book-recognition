package org.k53ca;

import java.awt.image.*;
import java.io.*;

import javax.imageio.ImageIO;

public class ConvertValue {
	public static BufferedImage base64StringToBitmap(String base64String){
		try{
			byte[] decodedString = Base64.decode(base64String);
			
			return ImageIO.read(new ByteArrayInputStream(decodedString));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String bitmapToBase64String(BufferedImage image){
		try {
    		ByteArrayOutputStream stream = new ByteArrayOutputStream();
    		ImageIO.write(image, "JPG", stream);
    		byte[] byteArray = stream.toByteArray();
    		return Base64.encodeToString(byteArray, false);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return "";
	}
}