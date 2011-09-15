package com.recognize.book.util;


import java.net.*;
import java.io.*;

import android.util.Log;

/**
 * Client is a Class to contact the server
 *
 */
public class Client {
	private Socket skt;
	/* Protocol of network */
	private String match = "MATCH";
	private String rate = "RATE";
	private String similar = "SIMILAR";
	private String buy = "BUY";
	private String image ="IMAGE";
	private String TAG = "Client";
	
	/**
	 * Create a Client to connect server by ip and port
	 * @param ip ip of server
	 * @param port ip port to connect
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client(String ip, int port) throws UnknownHostException, IOException{
		try {
			skt = new Socket(ip, port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor 
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public Client() throws UnknownHostException, IOException{
		try {
			skt = new Socket(Constant.IP, Constant.PORT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a base64 String encode for the Image
	 * @param base64String the string after encode
	 * @return
	 * true: if complete send the string.
	 * false; if not complete send the string.
	 * @throws IOException
	 */
	public boolean sendImage(String base64String) throws IOException{
		DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
		try {
			dos.writeBytes(match + " " + base64String);
			dos.writeByte('\n');
			Log.e(TAG, "sendImageInfo END");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		skt.shutdownOutput();
		return true;
	}
	
	/**
	 * This method call if method sendImage() be called
	 * It receive list Book from
	 * server..
	 * The received string is xml form. This string show the infomation of list
	 * book that maybe a book user finding.
	 * @return the String in xml form content the info of list book
	 * @throws IOException
	 * View more info mation in XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
	 */
	public String getListBookResult() throws IOException{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			String result = br.readLine();
			Log.e(TAG, "getListBookResult OK");
			Log.e(TAG, result);
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "F";
	}
	
	/**
	 * sending the score that user rate.
	 * @param id the book id in server
	 * @param Score the score user rate
	 * @return
	 * true if complete send
	 * false if not complete send
	 * @throws IOException
	 */
	public boolean rate(String id, String score) throws IOException{
		DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
		
		try {
			dos.writeBytes(rate + " " + id + " " + score + '\n');
			Log.e(TAG, "Rate OK");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * The method call after call if method rate() be called.
	 * @return
	 * true: if complete send score.
	 * flase if not complete send score.
	 * @throws IOException
	 */
	public String getRateResult() throws IOException{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			String result = br.readLine();
			Log.e(TAG, "getRate OK");
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "F";
	}
	
	
	/**
	 * find similar book
	 * @param id the book id in server
	 * @return the string in xml form content a list info of book
	 * @throws IOException
	 */
	public boolean findSimilar(String id) throws IOException{
		DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
		
		try {
			dos.writeBytes(similar + " ");
			dos.writeBytes(id);
			dos.writeByte('\n');
			Log.e(TAG, "findSimilar OK");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	/**
	 * send book id to receive info of shop to buy book
	 * @param id the book id in server
	 * @return 
	 * true: if complete send.
	 * false: if not complete send.
	 * @throws IOException
	 */
	public boolean buy(int id) throws IOException{
		DataOutputStream dos = new DataOutputStream(skt.getOutputStream());
		
		try {
			dos.writeBytes(buy + " ");
			dos.writeInt(id);
			dos.writeByte('\n');
			Log.e(TAG, "buy OK");
			dos.close();
			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * The method call if the metod buy() called
	 * @return the String in xml form content  the info of list shop
	 * @throws IOException
	 */
	public String getBuyInfo() throws IOException{
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			String result = br.readLine();
			br.close();
			Log.e(TAG, "getBuyResult OK");
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "F";
	}
	
	
	/**
	 * Close connection
	 */
	public void close(){
		try {
			skt.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Send id book to get shop
	 * @param id
	 * @return
	 */
	public boolean sendIdToFindShop(String id) {
		DataOutputStream dos;
		
		try {
			dos = new DataOutputStream(skt.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		try {
			dos.writeBytes(buy + " ");
			dos.writeBytes(id);
			dos.writeByte('\n');
			Log.e(TAG, "Buy OK");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * get List of Shop
	 * @return the String get from server
	 */
	public String getListShopResult() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			String result = br.readLine();
			Log.e(TAG, "getListShopResult OK");
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "F";
	}
	
	/**
	 * send id book to get a big image
	 * 
	 * @param id
	 * @return
	 */
	public boolean sendIdToGetImage(String id){
		DataOutputStream dos;
		
		try {
			dos = new DataOutputStream(skt.getOutputStream());
		} catch (IOException e1) {
			e1.printStackTrace();
			return false;
		}
		
		try {
			dos.writeBytes(image + " ");
			dos.writeBytes(id);
			dos.writeByte('\n');
			Log.e(TAG, "Buy OK");
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * get a String that encoded by image
	 * @return
	 */
	public String getBase64Image(){
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
			String result = br.readLine();
			Log.e(TAG, "getListBase64Image OK");
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "F";
	}
}
