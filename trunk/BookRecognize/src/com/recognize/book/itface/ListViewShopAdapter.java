package com.recognize.book.itface;

import java.util.ArrayList;

import com.recognize.book.R;
import com.recognize.book.util.ShopInfo;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewShopAdapter extends BaseAdapter{

	ArrayList<ShopInfo> shopList;
    public Activity context;
    public LayoutInflater inflater;

    public ListViewShopAdapter(Activity context,ArrayList<ShopInfo> shopList) {
        super();

        this.context = context;
        this.shopList = shopList;
        this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return shopList.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return shopList.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public static class ViewHolder
    {
        TextView shopName;
        TextView shopAddress;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder holder;
        if(convertView==null)
        {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_item_row_shop, null);

            holder.shopName = (TextView) convertView.findViewById(R.id.shop_name);
            holder.shopAddress = (TextView) convertView.findViewById(R.id.shop_address);

            convertView.setTag(holder);
        }
        else
            holder=(ViewHolder)convertView.getTag();

        ShopInfo sInfo =  shopList.get(position);

        holder.shopName.setText(sInfo.name);
        holder.shopAddress.setText(sInfo.address);

        return convertView;
    }

}
