package org.k53ca;

import java.sql.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.*;

import javax.imageio.ImageIO;
//import java.awt.*;


/**
 * Bridge between the server written in Java and the matching engine written in C++.
 * This class assigns job to the matching engine and passes the result back to the server
 * @author hoangtung
 *
 */
@SuppressWarnings("unchecked")
public class Matcher {
	private static final float WRONG_THRESHOLD = 0.01f;
	private static Process p;
	private static Scanner in;
	private static Vector<Integer> bookId;

	/**
	 * Fetch data from database
	 */
	static synchronized void fetch() {
		bookId = new Vector<Integer>();
		Vector<Book> books = new Vector<Book>();
		try {
			Class.forName(Config.getDriverString()).newInstance();
			Connection con = DriverManager.getConnection(Config.getDBUrl(), Config.getUser(), Config.getPass());
			PreparedStatement ps = con.prepareStatement("select bookid, keypoint, descriptor from book");
			ResultSet rs = ps.executeQuery();
			
			while(rs.next()) {
				books.add(new Book(rs.getInt(1), null, null, null, null, 0, 0, null, 0, (Vector<KeyPoint>) Util.objectFromByteArray(rs.getBytes(2)), (Mat) Util.objectFromByteArray(rs.getBytes(3))));
				bookId.add(rs.getInt(1));
			}
			writeData(books);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write fetched keypoints and descriptors to file
	 * @param b
	 */
	private synchronized static void writeData(Vector<Book> b) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter("Data.txt"));
			int dataSize = 1;
			Iterator<Book> it = b.iterator();
			int size = b.size();
			for(int i = 0; i < size; ++i) {
				Book temp = it.next();
				dataSize += temp.dess.rows*temp.dess.cols+3 + temp.keys.size()*7+1;
			}
			bw.write(dataSize + " ");
			bw.write(size + " ");
			it = b.iterator();
			for(int i = 0; i < size; ++i) {
				Book temp = it.next();
				int kSize = temp.keys.size();
				bw.write(kSize + " ");
				for(int j = 0; j < kSize; ++j) {
					writeKey(bw, temp.keys.get(j));
				}
				writeDes(bw, temp.dess);
			}
			bw.close();
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
	}
	
	/**
	 * Write a KeyPoint to file
	 * @param bw
	 * @param k
	 * @throws IOException
	 */
	private synchronized static void writeKey(BufferedWriter bw, KeyPoint k) throws IOException {
		bw.write(k.angle+" ");
		bw.write(k.classId + " ");
		bw.write(k.octave + " ");
		bw.write(k.x + " ");
		bw.write(k.y + " ");
		bw.write(k.response + " ");
		bw.write(k.size + " ");
	}
	
	/**
	 * Write a Mat to file
	 * @param bw
	 * @param k
	 * @throws IOException
	 */
	private synchronized static void writeDes(BufferedWriter bw, Mat k) throws IOException {
		bw.write(k.rows + " ");
		bw.write(k.cols + " ");
		bw.write(k.type + " ");
		int size = k.rows * k.cols;
		for(int i = 0; i < size ;++i) {
			bw.write(k.data[i] + " ");
		}
	}
	
	/**
	 * Get the matching subprocess
	 * @return the matching process
	 */
	public synchronized static Process getProcess() {
		return p;
	}
	
	/**
	 * Compare an image to database images to find out the most similar ones. 
	 * @param image
	 * @return ids of the most similar images
	 */
	public synchronized static Vector<Integer> match(String image) {
		BufferedImage img = ConvertValue.base64StringToBitmap(image);
		Vector<Integer> ids = new Vector<Integer>();
		String filename = System.currentTimeMillis()+".jpg";
		
		try {
			// write the image to a file which will be the input for the matching engine
			ImageIO.write(img, "JPG", new File(filename));
			//System.out.println("Done write");
			//long start = System.currentTimeMillis();

			// start the matching program
			p = null;
			try {
				Runtime.getRuntime().addShutdownHook(new MyShutdownHook());
				p = Runtime.getRuntime().exec("BookRecognition " + filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
			in = new Scanner(new BufferedInputStream(p.getInputStream()));

			// read the result from the program
			int size = in.nextInt();
			for(int i = 0; i < size; ++i) {
				float matches = in.nextFloat();
				int idx = in.nextInt();
				if(matches > WRONG_THRESHOLD) {
					break;
				}
				//System.out.println(matches + " " + idx);
				ids.add(bookId.get(idx));
			}
			
			//System.out.println("Done match " + (System.currentTimeMillis()-start));
		}
		catch(Exception exc) {
			exc.printStackTrace();
		}
		finally {
			try {
				if(p != null)
					p.destroy();
				Runtime.getRuntime().exec("rm " + filename);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ids;
	}
}

class MyShutdownHook extends Thread {
	public void run() {
		Matcher.getProcess().destroy();
	}
}
