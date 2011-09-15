package com.recognize.book.util;

import java.util.ArrayList;

/**
 * Storage the information of a shop.
 * 
 *
 */
public class ShopInfo {
	public String name; // The name of the shop
	public String address; // The adress of the shop
	public String latitude; // The latitude of the shop
	public String longitude; // The longitude of the shop
	public String numberPhone; // The number phone of the shop
	
	/**
	 * Constructor
	 * @param name
	 * @param address
	 * @param latitude
	 * @param longitude
	 * @param numberPhone
	 */
	public ShopInfo(String name, String address, String latitude, 
					String longitude, String numberPhone) {
		this.name = name;
		this.address = address;
		this.latitude = latitude;
		this.longitude = longitude;
		this.numberPhone = numberPhone;
	}
	
	/**
	 * Constructor
	 * @param listInfo the list string contant the information of ShopInfo
	 */
	public ShopInfo(ArrayList<String> listInfo) {
		this.name = listInfo.get(1);
		this.address = listInfo.get(2);
		this.latitude = listInfo.get(3);
		this.longitude = listInfo.get(4);
		this.numberPhone = listInfo.get(5);
	}
	
	/**
	 * Constructor
	 */
	public ShopInfo(){
	}
	
	/**
	 * Get a list string contant the information of shop
	 * @return
	 */
	public ArrayList<String> shopToArrayString() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(1, this.name);
		result.add(2, this.address);
		result.add(3, this.latitude);
		result.add(4, this.longitude);
		result.add(5, this.numberPhone);
		return result;
	}
}
