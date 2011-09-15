package com.recognize.book.util;

import java.util.ArrayList;

import com.recognize.book.database.Database.Book;

import android.database.Cursor;
import android.util.Log;


/**
 * BookInfo is a class that storage the information of a book
 * @author MumMum
 *
 */
public class BookInfo {
	public String id; // The id of book
	public String title; // The title of book
	public String author; //The author of book
	public String rating; // The score of book
	public String info; // The base information of book, like comment...
	public String price; // Price of book
	public String image; // The path of file. This file is image file
	
	/**
	 * Constructor
	 * @param id
	 * @param title
	 * @param author
	 * @param rating
	 * @param info
	 * @param price
	 * @param image
	 */
	public BookInfo(String id, String title, String author, 
					String rating, String info, String price, String image){
		this.id = id;
		this.title = title;
		this.author = author;
		this.rating = rating;
		this.info = info;
		this.price = price;
		this.image = image;
	}
	
	/**
	 * Constructor
	 * 
	 * @param listInfo
	 */
	public BookInfo(ArrayList<String> listInfo){
		this.id = listInfo.get(0);
		this.title = listInfo.get(1);
		this.author = listInfo.get(2);
		this.rating = listInfo.get(3);
		this.info = listInfo.get(4);
		this.price = listInfo.get(5);
		this.image = listInfo.get(6);
	}
	
	/**
	 * Constructor
	 * 
	 * @param bookCursor
	 */
	public BookInfo(Cursor bookCursor){
		this.id = bookCursor.getString(bookCursor.getColumnIndex(Book.BOOK_ID));
		this.title = bookCursor.getString(bookCursor.getColumnIndex(Book.TITLE));
		this.author = bookCursor.getString(bookCursor.getColumnIndex(Book.AUTHOR));
		this.rating = bookCursor.getString(bookCursor.getColumnIndex(Book.RATING));
		this.info = bookCursor.getString(bookCursor.getColumnIndex(Book.INFO));
		this.price = bookCursor.getString(bookCursor.getColumnIndex(Book.PRICE));
		this.image = bookCursor.getString(bookCursor.getColumnIndex(Book.IMAGE_PATH));
	}
	
	/**
	 * Constructor
	 */
	public BookInfo(){
	}
	
	/**
	 * Convert the information of book to a ArrayList<String>
	 * 
	 * @return
	 */
	public ArrayList<String> bookToArrayString(){
		ArrayList<String> result = new ArrayList<String>();
		result.add(0, id);
		result.add(1, title);
		result.add(2, author);
		result.add(3, rating);
		result.add(4, info);
		result.add(5, price);
		result.add(6, image);
		return result;
	}
}
