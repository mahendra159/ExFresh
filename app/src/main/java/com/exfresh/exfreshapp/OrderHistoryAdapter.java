package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
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
 * Created by Mahendra on 3/31/2015.
 */
public class OrderHistoryAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    public int status_flag;



    public OrderHistoryAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public OrderHistoryAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_orderhistory_row, null);
        //vi = inflater.inflate(R.layout.activity_order_row_header,null);
//ViewGroup header =(ViewGroup)inflater.inflate(R.layout.activity_order_row_header,null);

        TextView Order_Reference = (TextView)vi.findViewById(R.id.order_ref); // item
        TextView Order_Date = (TextView)vi.findViewById(R.id.order_date);
        TextView Order_Grand_Total = (TextView)vi.findViewById(R.id.order_total_price);

        TextView Order_Sub_Total = (TextView)vi.findViewById(R.id.sub_total);
        TextView Order_Shipping = (TextView)vi.findViewById(R.id.shipping);

        TextView Order_Carrier_Id = (TextView)vi.findViewById(R.id.carrier_id);
        TextView Order_Carrier_Name = (TextView)vi.findViewById(R.id.carrier_name);

        TextView Order_Status = (TextView)vi.findViewById(R.id.order_status);
        TextView Order_Id = (TextView)vi.findViewById(R.id.order_id);
        TextView Delivery_Date = (TextView)vi.findViewById(R.id.delivery_date);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

        String pos = String.valueOf(position);
        if (MyDebug.LOG) {
            Log.d("Value of pos", pos);
        }

        String Total_Price = listdata.get(OrderHistory.TAG_ORDER_GRAND_TOTAL);
        //String Total_Price = Total_Price_Long.substring(0,Total_Price_Long.length()-4);
        String Order_Date_UN = listdata.get(OrderHistory.TAG_ORDER_DATE);
        String Order_Date_Formatted = convertDate(Order_Date_UN);
        String Order_Status_Name = GetOrderStatus(listdata.get(OrderHistory.TAG_ORDER_STATUS));








        Order_Id.setText(listdata.get(OrderHistory.TAG_ORDER_ID));
        Order_Sub_Total.setText(listdata.get(OrderHistory.TAG_ORDER_SUB_TOTAL));
        Order_Shipping.setText(listdata.get(OrderHistory.TAG_ORDER_Shipping));

        Order_Carrier_Id.setText(listdata.get(OrderHistory.TAG_SHIPPING_CARRIER_ID));
        Order_Carrier_Name.setText(listdata.get(OrderHistory.TAG_SHIPPING_CARRIER_NAME));

        Delivery_Date.setText(listdata.get(OrderHistory.TAG_DELIVERY_DATE));
        Order_Reference.setText(listdata.get(OrderHistory.TAG_ORDER_REFERENCE));
        Order_Date.setText(Order_Date_Formatted);
        Order_Grand_Total.setText(Total_Price);
        Order_Status.setText(Order_Status_Name);

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
