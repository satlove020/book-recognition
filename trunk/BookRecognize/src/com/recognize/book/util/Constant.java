package com.recognize.book.util;

public class Constant {
	/*
	 * Error and Message
	 */
	public static String ERROR = "Error!!!";
	public static String ERROR_CALL = "Can not call.Please test your service.";
	public static String MES_RATE_ERROR = "Error. Please try again.";
	public static String MES_RATE_OK = "Thanks for rating.";
	public static String VIEW_TITLE_RATING_DIALOG = "Rating ...";
	public static String MES_NO_IMAGE = "Can not find image.";
	/*
	 * Path File
	 */
	public static String SAVEDPATH = "/Android/data/com.recoginze.book/photos";
	public static String DB_PATH = "/data/data/com.recognize.book/databases/";
	public static String DB_NAME = "BookRecognize.db";
	public static String SAVED_FOLDER = "/sdcard/BookRecognize";
	
	/*
	 * Extra Intent
	 */
	public static String PATH_INTENT = "FilePath";
	public static String XML_STRING = "XMLString";
	public static String ARRAY_STRING_BOOK_INFO = "ArrayStringBookInfo";
	
	public static String IMAGE_FROM = "image_from";
	public static String IN_SERVER = "in_server";
	public static String IN_SDCARD = "in_sdcard";
	public static String IN_DATABASE = "in_database";
	public static String TO_QUIT = "To_quit";
	public static String QUIT = "Quit";
	
	/*
	 * Server Information
	 */
	public static String IP = "119.15.161.26";
	public static int PORT = 8080;  

	
	/*
	 * Parsing
	 */
	public static String ROOT_PARSE = "ListBook";
	
	public static String TAG_BOOK_ID = "id";
	public static String TAG_BOOK_TITLE = "title";
	public static String TAG_BOOK_AUTHOR = "author";
	public static String TAG_BOOK_RATING = "rating";
	public static String TAG_BOOK_PRICE = "price";
	public static String TAG_BOOK_INFO = "info";
	public static String TAG_BOOK_IMAGE = "image";
	
	public static String TAG_SHOP_NAME = "name";
	public static String TAG_SHOP_ADRESS = "address";
	public static String TAG_SHOP_COORDINATE = "coordinate";
	public static String TAG_SHOP_PHONE = "phone";
	
	
	/*
	 * ListShopActivity
	 */
	public static String SHOW_MAP = "Get Location";
	public static String CALL_PHONE = "Call";
}
