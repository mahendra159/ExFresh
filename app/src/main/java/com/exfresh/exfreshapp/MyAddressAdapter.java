package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Mahendra on 4/21/2015.
 */
public class MyAddressAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;



    public MyAddressAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public MyAddressAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_myaddress_row, null);
        //vi = inflater.inflate(R.layout.activity_order_row_header,null);
//ViewGroup header =(ViewGroup)inflater.inflate(R.layout.activity_order_row_header,null);

        TextView Id_Address = (TextView)vi.findViewById(R.id.id_address);
        TextView Alias = (TextView)vi.findViewById(R.id.alias); // item
        TextView F_Name = (TextView)vi.findViewById(R.id.f_name);
        TextView L_Name = (TextView)vi.findViewById(R.id.l_name);
        TextView Add1 = (TextView)vi.findViewById(R.id.add1);
        TextView Add2 = (TextView)vi.findViewById(R.id.add2);
        TextView City = (TextView)vi.findViewById(R.id.city);
        TextView PostCode = (TextView)vi.findViewById(R.id.postcode);
        TextView Phone = (TextView)vi.findViewById(R.id.phone);
        TextView Mobile = (TextView)vi.findViewById(R.id.mobile);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

/*
        String Total_Price_Long = listdata.get(OrderHistory.TAG_ORDER_Total_Price);
        String Total_Price = Total_Price_Long.substring(0,Total_Price_Long.length()-4);
        String Order_Date_UN = listdata.get(OrderHistory.TAG_ORDER_DATE);
        String Order_Date_Formatted = convertDate(Order_Date_UN);
        String Order_Status_Name = GetOrderStatus(listdata.get(OrderHistory.TAG_ORDER_STATUS));
*/
        Id_Address.setText(listdata.get(CheckOutAddressActivity.TAG_ID_ADDRESS));
        Alias.setText(listdata.get(CheckOutAddressActivity.TAG_ALIAS));
        Alias.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        F_Name.setText(listdata.get(CheckOutAddressActivity.TAG_FNAME));
        L_Name.setText(listdata.get(CheckOutAddressActivity.TAG_LNAME));
        Add1.setText(listdata.get(CheckOutAddressActivity.TAG_ADD1));
        Add2.setText(listdata.get(CheckOutAddressActivity.TAG_ADD2));
        City.setText(listdata.get(CheckOutAddressActivity.TAG_CITY));
        PostCode.setText(listdata.get(CheckOutAddressActivity.TAG_POSTCODE));
        Phone.setText(listdata.get(CheckOutAddressActivity.TAG_PHONE));
        Mobile.setText(listdata.get(CheckOutAddressActivity.TAG_PHONE_MOBILE));
        //String pr_price_trimd = pr_price.substring(0,pr_price.length()-4);

        return vi;

    }

    private String convertDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = format.parse(date);
            SimpleDateFormat convertedFormat = new SimpleDateFormat("dd-MMM,yy hh:mm aa");
            return convertedFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String GetOrderStatus(String status_id) {

        try {
            if (status_id.equalsIgnoreCase("3")){
                return "Processing in progress";
            }
            else if (status_id.equalsIgnoreCase("12")){
                return "Remote payment accepted";
            }
            else if (status_id.equalsIgnoreCase("5")){
                return "Delivered";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

}
