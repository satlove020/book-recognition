package com.recognize.book.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;

import com.recognize.book.util.Constant;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * The class to manage database
 */
public class Database {
	
	public final static String TABLE_BOOK = "BookInfo";
	
	private static String TAG = "bookrecognize";
	private DatabaseHelper mDatabaseHelper;
	
	/**
	 * Constructor
	 * @param context Context
	 */
	public Database(Context context) {
		mDatabaseHelper = new DatabaseHelper(context);
		try {
			mDatabaseHelper.createDataBase();
			mDatabaseHelper.openDataBase();
		} catch (Exception e) {
			Log.e(TAG, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Close the database
	 */
	public void close(){
		mDatabaseHelper.close();
	}
	
	/********************************************  LIST BOOK  **********************************************************/
	/*******************************************************************************************************************/
	
	
	
	public Cursor getAllBook(String orderBy){
		String[] columns = {Book.ID, Book.BOOK_ID, Book.TITLE, Book.AUTHOR, Book.RATING, Book.INFO, Book.PRICE, Book.IMAGE_PATH};
		Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_BOOK, columns, null, null, null, null, orderBy);
		return cursor;
	}
	
	/**
	 * Get Board by board Id
	 * @param id int
	 * @return Cursor
	 */
	public Cursor getBookById (int id) {
		String selection = Book.ID + "=" + id;
		Cursor cursor = mDatabaseHelper.getReadableDatabase().query(TABLE_BOOK, null, selection, null, null, null, null);
		cursor.moveToNext();
		return cursor;
	}
	
	public void deleteBookByBookId(String bookId){
		
		/* delete a book */
		String where;
		where = Book.BOOK_ID + "=" + bookId;
		mDatabaseHelper.getReadableDatabase().delete(TABLE_BOOK, where, null);
	}
	
	public void addBook(String bookId, String title, String author, String rating,
						String info, String price, String imagePath){
		String where;
		where = Book.BOOK_ID + "=" + bookId;
		
		// insert
		ContentValues values = new ContentValues();
		values.put(Book.BOOK_ID, bookId);
		values.put(Book.TITLE, title);
		values.put(Book.AUTHOR, author);
		values.put(Book.RATING, rating);
		values.put(Book.INFO, info);
		values.put(Book.PRICE, price);
		values.put(Book.IMAGE_PATH, imagePath);
		
		if(mDatabaseHelper.getWritableDatabase().update(TABLE_BOOK, values, where, null) == 0){
			mDatabaseHelper.getWritableDatabase().insert(TABLE_BOOK, null, values);
		}
	}
	
	
	/********************************************  DB CLASS	 ***********************************************************/
	/*******************************************************************************************************************/
	
	public class Book {
		public static final String ID = "id";
		public static final String BOOK_ID = "BookId";
		public static final String TITLE = "Title";
		public static final String AUTHOR = "Author";
		public static final String RATING = "Rating";
		public static final String INFO = "Info";
		public static final String PRICE = "Price";
		public static final String IMAGE_PATH = "ImagePath";
	}
	
	/**
	 * 
	 * DatabaseHelper Class
	 *
	 */
	public class DatabaseHelper extends SQLiteOpenHelper {		

		private SQLiteDatabase mDatabase; 

		private final Context mHelperContext;

		/**
		 * Constructor
		 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
		 * @param context
		 */
		public DatabaseHelper(Context context) {

			super(context, Constant.DB_NAME, null, 1);
			this.mHelperContext = context;
		}	

		/**
		 * Creates a empty database on the system and rewrites it with your own database.
		 * */
		public void createDataBase() throws IOException{

			boolean dbExist = checkDataBase();

			if (dbExist) {
				//do nothing - database already exist
			} else {

				//By calling this method and empty database will be created into the default system path
				//of your application so we are gonna be able to overwrite that database with our database.
				this.getReadableDatabase();

				try {

					copyDataBase();

				} catch (IOException e) {

					throw new Error("Error copying database");

				}
			}

		}

		/**
		 * Check if the database already exist to avoid re-copying the file each time you open the application.
		 * @return true if it exists, false if it doesn't
		 */
		private boolean checkDataBase(){

			SQLiteDatabase checkDB = null;

			try{
				String myPath = Constant.DB_PATH + Constant.DB_NAME;
				checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

			}catch(SQLiteException e){

				//database does't exist yet.

			}

			if(checkDB != null){

				checkDB.close();

			}

			return checkDB != null ? true : false;
		}

		/**
		 * Copies your database from your local assets-folder to the just created empty database in the
		 * system folder, from where it can be accessed and handled.
		 * This is done by transfering bytestream.
		 * */
		private void copyDataBase() throws IOException{

			//Open your local db as the input stream
			InputStream myInput = mHelperContext.getAssets().open(Constant.DB_NAME);

			// Path to the just created empty db
			String outFileName = Constant.DB_PATH + Constant.DB_NAME;
			File out = new File(outFileName);
			if(!out.exists())out.createNewFile();
			//Open the empty db as the output stream
			OutputStream myOutput = new FileOutputStream(outFileName);

			//transfer bytes from the input file to the output file
			byte[] buffer = new byte[1024];
			int length;
			Log.i(TAG, "Copying database");
			while ((length = myInput.read(buffer)) != -1){
				myOutput.write(buffer, 0, length);
			}
			Log.i(TAG, "Database copied successfully");
			//Close the streams
			myOutput.flush();
			myOutput.close();
			myInput.close();

		}

		public void openDataBase() throws SQLException {

			//Open the database
			String myPath = Constant.DB_PATH + Constant.DB_NAME;
			mDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

		}

		@Override
		public synchronized void close() {

			if(mDatabase != null)
				mDatabase.close();

			super.close();

		}

		@Override
		public void onCreate(SQLiteDatabase db) {

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}

	}


}
