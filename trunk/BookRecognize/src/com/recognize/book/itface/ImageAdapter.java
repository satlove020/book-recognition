package com.recognize.book.itface;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * ImageAdapter is a Adapter show infor of Book
 *
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public File[] allFile;
    public File imageFolder;

    public ImageAdapter(Context c) {
        mContext = c;
        imageFolder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BookRecognize");
        allFile = imageFolder.listFiles();
    }

    public int getCount() {
        return allFile.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public String getFilePath(int position){
    	return allFile[position].getPath();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        
        Uri imageUri = Uri.parse(allFile[position].getPath());
        imageView.setImageURI(imageUri);
        return imageView;
    }
    
    
}
