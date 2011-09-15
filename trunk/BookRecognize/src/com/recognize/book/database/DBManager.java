package com.recognize.book.database;

import android.content.Context;
import android.database.Cursor;

import com.recognize.book.database.Database.Book;
import com.recognize.book.util.BookInfo;
import com.recognize.book.util.Constant;

/**
 * Manage the database
 * Transfer data from database.
 */
public class DBManager {
	/**
	 * Get all book storage in the database
	 * And Convert it in to xml form
	 * @param context
	 * @return
	 */
	public static String getAllBookInXMLForm(Context context){
		Database db = new Database(context);
		Cursor cursor = db.getAllBook(Book.ID);
		
		String xmlBookInfo = "<" + Constant.ROOT_PARSE+ ">";
		if(cursor.moveToFirst() == false || cursor.getCount() == 0){
			return null;
		}
		do {
			BookInfo bookInfo = new BookInfo(cursor);
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_ID + ">"  + 
						  bookInfo.id + "</" + Constant.TAG_BOOK_ID + ">";
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_TITLE + ">" +
						  bookInfo.title + "</" + Constant.TAG_BOOK_TITLE + ">";
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_AUTHOR + ">" +
					   	  bookInfo.author + "</" + Constant.TAG_BOOK_AUTHOR + ">";
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_RATING + ">" +
					  	  bookInfo.rating + "</" + Constant.TAG_BOOK_RATING + ">";
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_INFO + ">" +
					  	  bookInfo.info + "</" + Constant.TAG_BOOK_INFO + ">";
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_PRICE + ">" +
					   	  bookInfo.price + "</" + Constant.TAG_BOOK_PRICE + ">";
			
			xmlBookInfo = xmlBookInfo + "<" + Constant.TAG_BOOK_IMAGE + ">" +
					  	  bookInfo.image + "</" + Constant.TAG_BOOK_IMAGE + ">";
			
			cursor.moveToNext();
		} while (cursor.isAfterLast() == false);
		
		xmlBookInfo += "</" + Constant.ROOT_PARSE+ ">";
		db.close();
		return xmlBookInfo;
	}
	
	/**
	 * Delete a book by book id from database
	 * @param context
	 * @param bookId the id of book
	 */
	public static void deleteBookById(Context context, String bookId){
		Database db = new Database(context);
		db.deleteBookByBookId(bookId);
		db.close();
	}
	
	/**
	 * Add a book to database
	 * @param context
	 * @param bookInfo The BookInfo
	 */
	public static void addBook(Context context, BookInfo bookInfo){
		Database db = new Database(context);
		db.addBook(bookInfo.id, bookInfo.title, bookInfo.author, bookInfo.rating,
				   bookInfo.info, bookInfo.price, bookInfo.image);
		db.close();
	}
}
