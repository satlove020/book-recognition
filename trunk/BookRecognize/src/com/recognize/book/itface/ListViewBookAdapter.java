package com.recognize.book.itface;

import java.util.ArrayList;

import com.recognize.book.R;
import com.recognize.book.util.BookInfo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ListViewBookAdapter contain the information of a book that view in listview
 *
 */
public class ListViewBookAdapter extends BaseAdapter{

	ArrayList<BookInfo> bookList;

    public Activity context;
    public LayoutInflater inflater;
    /**
     * Constructor
     * @param context
     * @param bookList
     */
    public ListViewBookAdapter(Activity context,ArrayList<BookInfo> bookList) {
        super();

        this.context = context;
        this.bookList = bookList;

        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return bookList.size();
    }

    public Object getItem(int position) {
        return bookList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
    
    /**
     * manager the information view in listview
     *
     */
    public static class ViewHolder
    {
        ImageView book_image;
        TextView book_title;
        TextView book_author;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            if(position == 0){
            	convertView = inflater.inflate(R.layout.list_item_row_book_first_book, null);
            }else{
            	convertView = inflater.inflate(R.layout.list_item_row_book, null);
            }
            /*
             * set holder
             */
            holder.book_image = (ImageView) convertView.findViewById(R.id.book_image);
            holder.book_title = (TextView) convertView.findViewById(R.id.book_title);
            holder.book_author = (TextView) convertView.findViewById(R.id.book_author);

            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();

        BookInfo bInfo =  bookList.get(position);

        /*
         * set holder
         */
        holder.book_image.setImageURI(Uri.parse(bInfo.image));
        holder.book_title.setText(bInfo.title);
        holder.book_author.setText(bInfo.author);
        
        return convertView;
    }

}
