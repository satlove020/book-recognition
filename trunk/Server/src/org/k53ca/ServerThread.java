package org.k53ca;

import java.util.*;
import java.io.*;
import java.net.*;
import java.sql.*;

/**
 * ServerThread is responsible for serving user's request.
 * @author hoangtung
 *
 */
public class ServerThread extends Thread {
	Socket skt;
	BufferedInputStream inFromClient;
	BufferedOutputStream outToClient;
	boolean hasJob;
	String jobType = NO_JOB;
	Connection con;
	
	public static final String NO_JOB = "NO_JOB";
	public static final String MATCH = "MATCH";
	public static final String RATE = "RATE";
	public static final String SIMILAR = "SIMILAR";
	public static final String BUY = "BUY";
	public static final int MAX_SIMILAR_BOOK = 10;
	private static final int MAX_SHOP = 10;
	private static final String IMAGE = "IMAGE";
	
	/**
	 * Create new instance 
	 */
	public ServerThread() {
		try {
			Class.forName(Config.getDriverString()).newInstance();
			con = DriverManager.getConnection(Config.getDBUrl(), Config.getUser(), Config.getPass());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Reconnect to sql server if connection was broken
	 * @return
	 */
	private boolean reconnect() {
		try {
			Class.forName(Config.getDriverString()).newInstance();
			con = DriverManager.getConnection(Config.getDBUrl(), Config.getUser(), Config.getPass());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	/**
	 * Assign job to this thread
	 * @param s
	 * @return
	 */
	public boolean setJob(Socket s) {
		if(hasJob) {
			return false;
		}
		
		skt = s;
		try {
			inFromClient = new BufferedInputStream(skt.getInputStream());
			outToClient = new BufferedOutputStream(skt.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

		hasJob = true;
		return true;
	}
	
	/**
	 * 
	 */
	public void run() {
		while(true) {
			if(hasJob) {
				doJob();
			}
			else {
				try {
					sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Get user's request type
	 * @return User's request type
	 */
	private String getJobType() {
		String s = null;
		int next = 0;
		try {
			switch(inFromClient.read()) {
			case 'M':
				s = MATCH;
				next = 5;
				break;
			case 'R':
				s = RATE;
				next = 4;
				break;
			case 'S':
				s = SIMILAR;
				next = 7;
				break;
			case 'B':
				s = BUY;
				next = 3;
				break;
			case 'I':
				s = IMAGE;
				next = 5;
				break;
			default:
				next = 5;
				s = NO_JOB;
			}
			
			for(int i = 0; i < next; ++i) System.out.print((char)inFromClient.read());
			System.out.print("OK\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return s;
	}
	
	/**
	 * Do the job assigned to this thread. This function determines the job type and calls
	 * the appropriate function to handle the job
	 */
	private void doJob() {
		jobType = getJobType();
		if(jobType.equals(MATCH)) {
			doMatchString();
		}
		else if(jobType.equals(SIMILAR)) {
			doFindSimilar();
		}
		else if(jobType.equals(RATE)) {
			doRate();
		}
		else if(jobType.equals(BUY)) {
			doBuy();
		}
		else if(jobType.equals(IMAGE)) {
			doImage();
		}
		
		jobType = NO_JOB;
		hasJob = false;
		try {
			inFromClient.close();
			outToClient.close();
			skt.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Do the matching job: find the best match books and returns them to user
	 */
	private void doMatchString() {
		BufferedReader br = new BufferedReader(new InputStreamReader(inFromClient));
		try {
			//System.out.println("Reading");
			String image = br.readLine();
			String temp = null;
			while((temp = br.readLine()) != null) {
				image += "\n" + temp;
			}
			//System.out.println("Read ok");
			try {
				Vector<Book> books = getBooks(Matcher.match(image));
				responseBooks(books);
			}
			catch(Exception exc) {
				responseError("Cannot find book");
				exc.printStackTrace();
				reconnect();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void doImage() {
		Scanner input = new Scanner(inFromClient);
		String id = input.next();
		try {
			PreparedStatement ps = con.prepareStatement("select bigimage from book where bookid = " + id);
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				String img = rs.getString(1);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
				bw.write(img);
				bw.close();
			}
		}
		catch(SQLException exc) {
			reconnect();
			responseError("Cannot load image");
			exc.printStackTrace();
		}
		catch(Exception exc) {
			responseError("Cannot load image");
			exc.printStackTrace();
		}
	}

	/**
	 * Responsible for handling buy request. The information of available shops are returned to user
	 */
	private void doBuy() {
		Scanner input = new Scanner(inFromClient);
		String id = input.next();
		try {
			Statement ps = con.createStatement();
			// find shops which sell the book user want
			ResultSet rs = ps.executeQuery("select title, address, x, y, phone from shop, book_shop where shop.shopId=book_shop.shopId and bookId = " + id);
			String ad[] = new String[MAX_SHOP];
			String name[] = new String[MAX_SHOP];
			String phone[] = new String[MAX_SHOP];
			float x[] = new float[MAX_SHOP];
			float y[] = new float[MAX_SHOP];
			int length = 0;
			while(rs.next() && length < MAX_SHOP) {
				ad[length] = rs.getNString(1);
				name[length] = rs.getNString(2);
				x[length] = rs.getFloat(3);
				y[length] = rs.getFloat(4);
				phone[length] = rs.getString(5);
				
				length++;
			}
			
			// if no or too few shops sells the book, find all possible shops
			rs = ps.executeQuery("select address, title, x, y, phone from shop");
			while(rs.next() && length < MAX_SHOP) {
				ad[length] = rs.getNString(1);
				name[length] = rs.getNString(2);
				x[length] = rs.getFloat(3);
				y[length] = rs.getFloat(4);
				phone[length] = rs.getString(5);
				
				length++;
			}
	
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
			bw.write(formatShop(name, ad, phone, x, y, length));
			bw.close();
		}
		catch(SQLException exc) {
			responseError("Cannot find shops");
			exc.printStackTrace();
			reconnect();
		}
		catch(Exception exc) {
			responseError("Cannot find shops");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Format the shops' information
	 * @param name
	 * @param ad
	 * @param x
	 * @param y
	 * @param length
	 * @return
	 */
	private String formatShop(String name[], String ad[], String phone[], float x[], float y[], int length) {
		String s = "";
		s += XmlTag.shopNo_Start+length+XmlTag.shopNo_End;
		for(int i = 0; i < length; ++i) {
			s += XmlTag.shop_Start
					+ XmlTag.name_Start + name[i] + XmlTag.name_End
					+ XmlTag.address_Start + ad[i] + XmlTag.address_End
					+ XmlTag.phone_Start + phone[i] + XmlTag.phone_End
					+ XmlTag.coordinate_Start + x[i] + " " + y[i] + XmlTag.coordinate_End
					+ XmlTag.shop_End;
		}
		
		return s;
	}
	
	/**
	 * Get list of books from database with the specified ids
	 * @param ids
	 * @return
	 * @throws SQLException
	 */
	private Vector<Book> getBooks(Vector<Integer> ids) throws SQLException {
		Vector<Book> result = new Vector<Book>();
		result.setSize(ids.size());
		int id, rc, p;
		String tit, au, in, ta, img;
		float ra;
		
		// create a sql statement for selecting books with the listed ids
		String sql = "select bookid, title, author, info, tags, rating, rateCount, image, price from book where bookid in (";
		int idSize = ids.size();
		Iterator<Integer> it = ids.iterator();
		for(int i = 0; i < idSize; ++i) {
			sql += it.next();
			if(i < idSize-1) {
				sql += ", ";
			}
		}
		sql += ")";
		//System.out.println("SQL " + sql);
		ResultSet rs = con.createStatement().executeQuery(sql);
		// get the result and reorder it 
		while(rs.next()) {
			//System.out.println("Add one book");
			id = rs.getInt(1);
			tit = rs.getNString(2);
			au = rs.getNString(3);
			in = rs.getNString(4);
			ta = rs.getNString(5);
			ra = rs.getFloat(6);
			rc = rs.getInt(7);
			img = rs.getString(8);
			p = rs.getInt(9);
			int idx = ids.indexOf(id);
			//System.out.println("Idx " + idx);
			result.set(idx, new Book(id, tit, au, in, ta, ra, rc, img, p));
		}
		
		return result;
	}
	
	/**
	 * Find simliar books in the database based on their tags
	 */
	private void doFindSimilar() {
		Scanner input = new Scanner(inFromClient);
		Integer bookID = input.hasNext() ? input.nextInt():0;	// read bookId
		try {
			Vector<Book> books = new Vector<Book>();
			
			PreparedStatement ps = con.prepareStatement("select tags from book where bookid = " + bookID);
			ResultSet rs = ps.executeQuery();
			String tags[] = null;
			while(rs.next()) {
				tags = rs.getNString(1).split(" ");
			}
			if(tags == null || tags.length == 0) {
				throw new Exception();
			}
			
			System.out.println("Tags num " + tags.length);
			String sql = "select bookid, title, author, info, tags, rating, rateCount, image, price from book where ";
			for(int i = 0; i < tags.length; ++i) {
				sql += "tags like ? ";
				if(i < tags.length-1) {
					sql += " and ";
				}
			}
			
			ps = con.prepareStatement(sql);
			System.out.println(sql);
			for(int i = 0; i < tags.length; ++i) {
				ps.setNString(i+1, tags[i]);
			}
		
			rs = ps.executeQuery();
			TreeSet<String> titles = new TreeSet<String>();
			int count = 0;
			while(rs.next() && count < MAX_SIMILAR_BOOK) {
				Book b = new Book(rs.getInt(1), rs.getNString(2), rs.getNString(3), rs.getNString(4), rs.getNString(5), rs.getFloat(6), rs.getInt(7), rs.getString(8), rs.getInt(9));
				if(titles.add(b.title)) {
					count++;
					books.add(b);
				}
			}
			
			
			responseBooks(books);
		}
		catch(SQLException sexc) {
			responseError("Cannot find similar books");
			sexc.printStackTrace();
			reconnect();
		}
		catch(Exception exc) {
			responseError("Cannot find similar books");
			exc.printStackTrace();
		}
	}
	
	/**
	 * Standardize a string to avoid sql error
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unused")
	private String formalize(String name) {
		return name.replaceAll("'", "''");
	}
	
	/**
	 * Put book's information in a xml form
	 * @param book
	 * @return
	 */
	private String formatBook(Book book) {
		String s = "";
		
		s += XmlTag.book_Start 
				+ XmlTag.id_Start + book.bookID + XmlTag.id_End 
				+ XmlTag.title_Start + book.title + XmlTag.title_End
				+ XmlTag.author_Start + book.author + XmlTag.author_End 
				+ XmlTag.rating_Start + book.rating + XmlTag.rating_End
				+ XmlTag.info_Start + book.info + XmlTag.info_End
				+ XmlTag.image_Start + book.image + XmlTag.image_End
				+ XmlTag.price_Start + book.price + XmlTag.price_End
				+ XmlTag.book_End;
		
		return s;
	}
	
	/**
	 * Send books' information to user
	 * @param books
	 */
	private void responseBooks(Vector<Book> books) {
		if(books == null || books.size() == 0) {
			responseError("Cannot find book");
			return;
		}
		
		int size = books.size();
		Iterator<Book> it = books.iterator();
		String s = XmlTag.bookNo_Start+size+XmlTag.bookNo_End;
		for(int i = 0; i < size; ++i) {
			s += formatBook(it.next());
		}
		//System.out.println(s);
		
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
		try {
			bw.write(s);
			bw.close();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Send an error message to user
	 * @param message
	 */
	public void responseError(String message) {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
		try {
			bw.write("ERROR " + message);
			bw.close();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Handle rating task. This function allows user to rate a specific book
	 */
	private void doRate() {
		Scanner input = new Scanner(inFromClient);
		// get user's data
		Float score = input.nextFloat(), temp = 0.0f;
		Integer bookID = input.nextInt(), count = 0;
		
		try {
			PreparedStatement ps;
			// find the book which user want to rate
			ps = con.prepareStatement("select rating, rateCount from book where bookid="+bookID);
			ResultSet rs = ps.executeQuery();
			boolean hasRows = false;
			// get the rating and ratecount
			while(rs.next()) {
				temp = rs.getFloat(1);
				count = rs.getInt(2);
				score = (temp*count+score)/(count+1);
				count++;
				hasRows = true;
			}
			if(!hasRows) {
				throw new SQLException();
			}
			
			// update rating and rateCount value for this book
			ps = con.prepareStatement("update book set rating=?, rateCount=? where bookid=?");
			ps.setString(1, score.toString());
			ps.setString(2, count.toString());
			ps.setString(3, bookID.toString());
			ps.executeUpdate();
			
			// reponse to user
			responseRate(bookID);
		} catch (SQLException e) { // if any error occurs, return an error message
			responseError("Cannot rate book " + bookID);
			e.printStackTrace();
			reconnect();
		}
	}
	
	/**
	 * Return a rating message to user
	 * @param id
	 */
	private void responseRate(int id) {
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(outToClient));
		try {
			bw.write("OK");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
