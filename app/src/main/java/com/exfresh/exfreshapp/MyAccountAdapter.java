package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahendra on 3/31/2015.
 */
public class MyAccountAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;



    public MyAccountAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public MyAccountAdapter (){  };

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }



    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_my_account_row, null);


        TextView MyAccTxtVwItem = (TextView)vi.findViewById(R.id.my_acc_txt_itm); // item
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image2); // thumb image

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

        MyAccTxtVwItem.setText(listdata.get(My_AccountActivity.KEY_ITEM));
        imageLoader.DisplayImage(listdata.get(My_AccountActivity.KEY_IMAGE_URL), thumb_image);

        return vi;

    }
 }
