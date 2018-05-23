package com.exfresh.exfreshapp;

import android.widget.BaseAdapter;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Mahendra on 4/21/2015.
 */
public class ReturnDetailAdapter extends BaseAdapter {


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    public int status_flag;



    public ReturnDetailAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public ReturnDetailAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_returndetail_row, null);
        //vi = inflater.inflate(R.layout.activity_order_row_header,null);
//ViewGroup header =(ViewGroup)inflater.inflate(R.layout.activity_order_row_header,null);

        TextView Product_Name_txt = (TextView)vi.findViewById(R.id.product_name); // item
        TextView Product_Quantity_txt = (TextView)vi.findViewById(R.id.product_quantity);
        TextView Product_Price_txt = (TextView)vi.findViewById(R.id.product_price);
        TextView Total_Price_txt = (TextView)vi.findViewById(R.id.total_price);
        //TextView Order_Id_txt = (TextView)vi.findViewById(R.id.order_id);
        //TextView Delivery_Date = (TextView)vi.findViewById(R.id.delivery_date);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);



        String Product_Price  = listdata.get(Return_Detail.TAG_PRODUCT_PRICE);
        String Product_Price_formatted = "Rs."+Product_Price.substring(0,Product_Price.length()-4);
        String Total_Price = listdata.get(Return_Detail.TAG_TOTAL_PRICE_TAX_INCLUSIVE);
        String Total_Price_formatted = "Rs."+Total_Price.substring(0,Total_Price.length()-4);
        //String Order_Ref = listdata.get(My_Returns.TAG_ORDER_REFERENCE);

        //String Return_Date_Formatted = convertDate(Return_Date_Add);



        Product_Name_txt.setText(listdata.get(Return_Detail.TAG_PRODUCT_NAME));
        Product_Quantity_txt.setText(listdata.get(Return_Detail.TAG_PRODUCT_QUANTITY));
        Product_Price_txt.setText(Product_Price_formatted);
        Total_Price_txt.setText(Total_Price_formatted);
        //Order_Id_txt.setText(Id_Order);


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
