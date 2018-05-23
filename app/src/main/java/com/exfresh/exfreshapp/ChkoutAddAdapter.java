package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ScrollView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Mahendra on 4/3/2015.
 */
public class ChkoutAddAdapter extends BaseAdapter {


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;


    public ChkoutAddAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    public ChkoutAddAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_ckout_address_row, null);

        TextView Id_Address = (TextView)vi.findViewById(R.id.id_address);
        TextView Alias = (TextView)vi.findViewById(R.id.alias); // item
        TextView F_Name = (TextView)vi.findViewById(R.id.f_name);
        TextView Add1 = (TextView)vi.findViewById(R.id.add1);
        TextView City = (TextView)vi.findViewById(R.id.city);
        TextView Mobile = (TextView)vi.findViewById(R.id.mobile);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

        Id_Address.setText(listdata.get(CheckOutAddressActivity.TAG_ID_ADDRESS));
        Alias.setText(listdata.get(CheckOutAddressActivity.TAG_ALIAS));
        Alias.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        String fname = listdata.get(CheckOutAddressActivity.TAG_FNAME);
        String lname = listdata.get(CheckOutAddressActivity.TAG_LNAME);
        String complete_name = fname.concat(" ").concat(lname);
        F_Name.setText(complete_name);

        String addr1 = listdata.get(CheckOutAddressActivity.TAG_ADD1);
        String addr2 = listdata.get(CheckOutAddressActivity.TAG_ADD2);
        String fulladd = addr1.concat(addr2);

        Add1.setText(fulladd);

        City.setText(listdata.get(CheckOutAddressActivity.TAG_CITY));
        Mobile.setText(listdata.get(CheckOutAddressActivity.TAG_PHONE_MOBILE));

        return vi;
    }
}
