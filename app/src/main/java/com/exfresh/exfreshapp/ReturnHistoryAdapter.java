package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
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
 * Created by Mahendra on 4/20/2015.
 */
public class ReturnHistoryAdapter extends BaseAdapter {


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    public int status_flag;



    public ReturnHistoryAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public ReturnHistoryAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_returns_row, null);
        //vi = inflater.inflate(R.layout.activity_order_row_header,null);
//ViewGroup header =(ViewGroup)inflater.inflate(R.layout.activity_order_row_header,null);

        TextView Id_Order_Return_txt = (TextView)vi.findViewById(R.id.id_order_return); // item
        TextView OrderRef_txt = (TextView)vi.findViewById(R.id.order_reference);
        TextView Return_Status_txt = (TextView)vi.findViewById(R.id.state);
        TextView Date_Add_txt = (TextView)vi.findViewById(R.id.date_add);
        TextView Order_Id_txt = (TextView)vi.findViewById(R.id.order_id);
        //TextView Delivery_Date = (TextView)vi.findViewById(R.id.delivery_date);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);


        String Id_Order_Return = listdata.get(My_Returns.TAG_ID_ORDER_RETURN);
        String Id_Order = listdata.get(My_Returns.TAG_ID_ORDER);
        String Order_State  = listdata.get(My_Returns.TAG_STATE);
        String Return_Date_Add = listdata.get(My_Returns.TAG_DATE_ADD);
        String Order_Ref = listdata.get(My_Returns.TAG_ORDER_REFERENCE);

        String Return_Date_Formatted = convertDate(Return_Date_Add);



        Id_Order_Return_txt.setText(Id_Order_Return);
        OrderRef_txt.setText(Order_Ref);
        Return_Status_txt.setText(Order_State);
        Date_Add_txt.setText(Return_Date_Formatted);
        Order_Id_txt.setText(Id_Order);


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
