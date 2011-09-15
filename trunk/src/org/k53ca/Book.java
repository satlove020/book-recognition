package org.k53ca;

import java.util.Vector;

/**
 * Contain infomations about a book including id, rating, title, author, comment, tags, price, image, keypoints and descriptors.
 * @author hoangtung
 *
 */
public class Book implements Comparable<Book> {
	int bookID, rateCount;
	String title, author, info, tags;
	float rating;
	String image;
	String bigImg;
	int price;
	Vector<KeyPoint> keys;
	Mat dess;
	int matchKeys;
	
	/**
	 * Create a empty book
	 */
	public Book() {
		
	}
	
	/**
	 * Copy data from another book to this book
	 * @param b
	 */
	public Book(Book b) {
		bookID = b.bookID;
		title = b.title;
		author = b.author;
		info = b.info;
		tags = b.tags;
		rating = b.rating;
		rateCount = b.rateCount;
		image = b.image;
		matchKeys = b.matchKeys;
	}
	
	/**
	 * Create a new book without keypoints and descriptor
	 * @param id
	 * @param tit
	 * @param au
	 * @param in
	 * @param ta
	 * @param ra
	 * @param rc
	 * @param img
	 * @param p
	 */
	public Book(int id, String tit, String au, String in, String ta, float ra, int rc, String img, int p) {
		bookID = id;
		title = tit;
		author = au;
		info = in;
		tags = ta;
		rating = ra;
		rateCount = rc;
		image = img;
		matchKeys = 0;
		price = p;
	}
	
	/**
	 * Create a new book with keypoints and descriptors
	 * @param id
	 * @param tit
	 * @param au
	 * @param in
	 * @param ta
	 * @param ra
	 * @param rc
	 * @param img
	 * @param p
	 * @param keys
	 * @param dess
	 */
	public Book(int id, String tit, String au, String in, String ta, float ra, int rc, String img, int p, Vector<KeyPoint> keys, Mat dess) {
		bookID = id;
		title = tit;
		author = au;
		info = in;
		tags = ta;
		rating = ra;
		rateCount = rc;
		image = img;
		matchKeys = 0;
		this.keys = keys;
		this.dess = dess;
		price = p;
	}
	
	/**
	 * Create a book with keypoints, descritors and number of matches 
	 * @param id
	 * @param tit
	 * @param au
	 * @param in
	 * @param ta
	 * @param ra
	 * @param rc
	 * @param img
	 * @param p
	 * @param keys
	 * @param dess
	 * @param match
	 */
	public Book(int id, String tit, String au, String in, String ta, float ra, int rc, String img, int p, Vector<KeyPoint> keys, Mat dess, int match) {
		bookID = id;
		title = tit;
		author = au;
		info = in;
		tags = ta;
		rating = ra;
		rateCount = rc;
		image = img;
		matchKeys = 0;
		this.keys = keys;
		this.dess = dess;
		matchKeys = match;
		price = p;
	}

	@Override
	public int compareTo(Book arg0) {
		return matchKeys-arg0.matchKeys;
	}
}