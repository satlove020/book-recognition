package org.k53ca;

import java.sql.*;
import java.util.*;
import java.awt.AlphaComposite;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.*;

/**
 * Utility class which sends formatted data to sql server
 * @author hoangtung
 *
 */
public class DataIO {
	private static Connection con;
	public static boolean WINDOWS = true;
	public static final String BOOK = "book";
	public static final String INFO = "info";
	public static final String AUTHOR = "author";
	public static final String PRICE = "price";
	public static final String TITLE = "title";
	public static final String IMAGE = "image";
	public static final String SHOP = "shop";
	public static final String ADDRESS = "address";
	public static final String COORDINATE = "coordinate";
	public static final String NAME = "name";
	public static final int S_IMG_H = 60;
	public static final int S_IMG_W = 40;
	public static final int B_IMG_H = 200;
	public static final int B_IMG_W = 150;
	
	static char splitChar = '\\';
	private static ArrayList<String> tags;
	
	static {
		try {
			Class.forName(Config.getDriverString()).newInstance();
			con = DriverManager.getConnection(Config.getDBUrl(), Config.getUser(), Config.getPass());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Create a new book from file
	 * @param filename
	 * @return
	 */
	public static Book createBook(String filename) {
		Book book = null;
		//System.out.println(filename);
		try {
			Scanner sc = new Scanner(new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF8")));
			String title, author, info, tags, image;
			float rating;
			int rateCount, bookId, price;
			Vector<KeyPoint> keys;
			Mat dess;
			
			bookId = Integer.valueOf(sc.nextLine());
			title = sc.nextLine();
			author = sc.nextLine();
			info = sc.nextLine();
			tags = sc.nextLine();
			//System.out.println(tags);
			rating = Float.valueOf(sc.nextLine());
			rateCount = Integer.valueOf(sc.nextLine());
			image = sc.nextLine();
			price = Integer.valueOf(sc.nextLine());
			keys = KeyPoint.keysFromScanner(sc);
			dess = Mat.matFromScanner(sc);
			
			book = new Book(bookId, title, author, info, tags, rating, rateCount, image, price, keys, dess);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return book;
	}
	
	/**
	 * Insert a book to database
	 * @param b
	 */
	public static void insertBook(Book b) {
		String sql = "insert into book (title, author, info, tags, rating, rateCount, image, bigImage, price, keypoint, descriptor) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setNString(1, b.title);
			ps.setNString(2, b.author);
			ps.setNString(3, b.info);
			ps.setNString(4, b.tags);
			ps.setString(5, String.valueOf(b.rating));
			ps.setString(6, String.valueOf(b.rateCount));
			ps.setString(7, b.image);
			ps.setString(8, b.bigImg);
			ps.setString(9, b.price+"");
			ps.setBytes(10, Util.objectToByteArray(b.keys));
			ps.setBytes(11, Util.objectToByteArray(b.dess));
			
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Read shop information from file and insert it to database
	 * @param filename
	 */
	public static void readShop(String filename) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document dom = db.parse(filename);
			Element elem = dom.getDocumentElement();
			
			NodeList nl = elem.getElementsByTagName(SHOP);
			int length = nl.getLength();
			for(int i = 0; i < length; ++i) {
				Element n = (Element) nl.item(i);
				NodeList list = n.getElementsByTagName(ADDRESS);
				String address = list.item(0).getFirstChild().getNodeValue();
				list = n.getElementsByTagName(COORDINATE);
				String num[] = list.item(0).getFirstChild().getNodeValue().split(" ");
				float x = Float.valueOf(num[0]), y = Float.valueOf(num[1]);
				list = n.getElementsByTagName(NAME);
				String name = list.item(0).getFirstChild().getNodeValue();
				
				insertShop(name, address, x, y);
				System.out.println(name + " " + address + " " + x + " " + y);
			}
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Insert shop's information to database
	 * @param name
	 * @param address
	 * @param x
	 * @param y
	 */
	public static void insertShop(String name, String address, float x, float y) {
		String sql = "insert into shop (address, title, x, y, phone) values (?, ?, ?, ?, '0979193011')";
		try {
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setNString(1, address);
			ps.setNString(2, name);
			ps.setString(3, x+"");
			ps.setString(4, y+"");
			ps.executeUpdate();
			//System.out.println("Done");
		} 
		catch (Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Read common tags from a file
	 * @param filename
	 */
	public static void readTags(String filename) {
		try {
			Scanner input = new Scanner(new File(filename));
			tags = new ArrayList<String>();
			tags.add(input.next());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Get a rough title of a book through its filename
	 * @param filename
	 * @return
	 */
	public static String getTitle(String filename) {
		String s = filename.substring(0, filename.lastIndexOf(splitChar));
		return s.substring(s.lastIndexOf(splitChar)+1);
	}
	
	/**
	 * Add information to a book 
	 * @param b
	 * @param infoFile
	 */
	public static void addInfo(Book b, String infoFile) {
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		try {
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			
			//parse using builder to get DOM representation of the XML file
			Document dom = db.parse(infoFile);
			
			Element elem = dom.getDocumentElement();
			NodeList nl = elem.getElementsByTagName(TITLE);
			b.title = nl.item(0).getFirstChild().getNodeValue();
			nl = elem.getElementsByTagName(INFO);
			b.info = nl.item(0).getFirstChild().getNodeValue();
			nl = elem.getElementsByTagName(AUTHOR);
			b.author = nl.item(0).getFirstChild().getNodeValue();
			b.tags += b.author.trim().replace(' ', '_');
			nl = elem.getElementsByTagName(PRICE);;
			b.price = Integer.valueOf(nl.item(0).getFirstChild().getNodeValue());
		}catch(ParserConfigurationException pce) {
			System.out.println(infoFile);
			//pce.printStackTrace();
		}catch(SAXException se) {
			System.out.println(infoFile);
			//se.printStackTrace();
		}catch(IOException ioe) {
			//System.out.println(infoFile);
			//ioe.printStackTrace();
		}
	}
	
	/**
	 * Generate tag for a book through its title and the predefined tags
	 * @param title
	 * @return
	 */
	public static String buildTag(String title) {
		Iterator<String> it = tags.iterator();
		int size = tags.size();
		String tag = "";
		for(int i = 0; i < size; ++i) {
			String next = it.next();
			if(title.contains(next)) {
				tag += next + " ";
			}
		}
		
		return tag;
	}
	
	/** 
	 * Resizing a image to a specific size
	 * @param originalImage
	 * @param type
	 * @return
	 */
	private static BufferedImage resizeImageWithHint(BufferedImage originalImage, int w, int h, int type){
		 
		BufferedImage resizedImage = new BufferedImage(w, h, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);
		g.dispose();	
		g.setComposite(AlphaComposite.Src);
	 
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
		RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING,
		RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
	 
		return resizedImage;
    }
	
	/**
	 * Add image to a book
	 * @param b
	 * @param imageFile
	 */
	public static void addImage(Book b, String imageFile) {
		try {
			BufferedImage img = ImageIO.read(new File(imageFile));
			int type = img.getType() == 0? BufferedImage.TYPE_INT_ARGB : img.getType();
			b.image = ConvertValue.bitmapToBase64String(resizeImageWithHint(img, S_IMG_W, S_IMG_H, type));
			b.bigImg = ConvertValue.bitmapToBase64String(resizeImageWithHint(img, B_IMG_W, B_IMG_H, type));
			//System.out.println("Image size " + b.image.length());
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Read information of a book from file
	 * @param filename
	 * @return
	 */
	public static Book readBook(String filename) {
		Book b = new Book();
		
		try {
			Scanner input = new Scanner(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
			b.title = getTitle(input.nextLine());
			b.info = b.title;
			b.tags = buildTag(b.title);
			b.keys = KeyPoint.keysFromScanner(input);
			b.dess = Mat.matFromScanner(input);
			File f = new File(filename);
			String infoFile = f.getParent()+ splitChar + "info.xml";
			String imageFile = filename.substring(0, filename.lastIndexOf('.'));
			//System.out.println(imageFile);
			addInfo(b, infoFile);
			addImage(b, imageFile);
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		
		return b;
	}
	
	public static void main(String args[]) {
		readShop("shop.xml");
		readTags("Tags.txt");
		Scanner input = new Scanner(System.in);
		while(input.hasNext()) {
			String s = input.nextLine();
			insertBook(readBook(s));
			//System.out.println("Done " + s);
		}
	}
	
}
