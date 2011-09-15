package com.recognize.book.util;

import android.content.Context;

public class Res {
	Context context;
	
	public static String getString(Context context, int resInt){
		return context.getResources().getString(resInt);
	}
}
