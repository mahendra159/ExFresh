package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Mahendra on 4/1/2015.
 */
public class OrderDetailAdapter extends BaseAdapter{


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public static ArrayList<String> mylist = new ArrayList<String>();
    public static Map<String,String> myMap = new HashMap<String,String>();
    //public static EditText Return_Question;
    public static String ret_ques;

    public static ArrayList<Map<String, String>> pplist;
    // Hashmap for ListView


    //HashMap<String, String> map2 = new HashMap<String, String>();
    //Iterator<Map.Entry<String, String>> iterator = myMap.entrySet().iterator();


    public OrderDetailAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public OrderDetailAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_orderdetail_row, null);
        //vi = inflater.inflate(R.layout.activity_order_row_header,null);
//ViewGroup header =(ViewGroup)inflater.inflate(R.layout.activity_order_row_header,null);

        TextView Product_Name = (TextView)vi.findViewById(R.id.Product_Name); // item
        TextView Product_Quantity = (TextView)vi.findViewById(R.id.Product_Quantity);
        TextView Product_Price = (TextView)vi.findViewById(R.id.Product_Price);

        TextView Total_Price = (TextView)vi.findViewById(R.id.Total_Price);
        CheckBox Return_chkbx = (CheckBox)vi.findViewById(R.id.ret_chkbx);
        final EditText Return_Question = (EditText)vi.findViewById(R.id.ret_question);
        Return_Question.setVisibility(View.GONE);
        //Return_chkbx.setOnCheckedChangeListener(CheckChangeAction);

        if (OrderDetail.status_flag ==1)
        {
            Return_chkbx.setEnabled(true);
        }
        else{
            Return_chkbx.setEnabled(false);
        }



        //      TextView Order_Id = (TextView)vi.findViewById(R.id.order_id);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

        //ret_ques = Return_Question.getText().toString();

        String Product_Price_Long = listdata.get(OrderDetail.TAG_PRODUCT_PRICE);
        String Product_Price_formatted = Product_Price_Long.substring(0, Product_Price_Long.length() - 4);
        String Total_Price_Long = listdata.get(OrderDetail.TAG_TOTAL_PRICE_TAX_INCLUSIVE);
        String Total_Price_formatted = "Rs."+Total_Price_Long.substring(0,Total_Price_Long.length()-4);


        String Order_Id = listdata.get(OrderDetail.TAG_ORDER_ID);
        String Product_Id = listdata.get(OrderDetail.TAG_PRODUCT_ID);
        final String Id_Order_Detail = listdata.get(OrderDetail.TAG_ID_ORDER_DETAIL);
        final String Id_Quantity = listdata.get(OrderDetail.TAG_PRODUCT_QUANTITY);
        // String Order_Date_UN = listdata.get(OrderHistory.TAG_ORDER_DATE);
        //      String Order_Date_Formatted = convertDate(Order_Date_UN);
        //      String Order_Status_Name = GetOrderStatus(listdata.get(OrderHistory.TAG_ORDER_STATUS));




        Spannable spanTotalPrice = Spannable.Factory.getInstance().newSpannable(Total_Price_formatted);
        spanTotalPrice.setSpan(new BackgroundColorSpan(0xffb7d1ff), 0, spanTotalPrice.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



        Product_Name.setText(listdata.get(OrderDetail.TAG_PRODUCT_NAME));
        Product_Name.setWidth(110);
        Product_Quantity.setText(listdata.get(OrderDetail.TAG_PRODUCT_QUANTITY));
        Product_Price.setText("Rs." + Product_Price_formatted);
        Total_Price.setText(spanTotalPrice);


        //     Order_Status.setText(Order_Status_Name);

        //String pr_price_trimd = pr_price.substring(0,pr_price.length()-4);

        Return_chkbx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pplist = new ArrayList<Map<String, String>>();
                if (isChecked) {
                    mylist.add(Id_Order_Detail);
                    myMap.put(Id_Order_Detail, Id_Quantity);
                    Iterator<Map.Entry<String, String>> iterator = myMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                    //    Toast.makeText(MyApplication.getContext(), "key value." + entry.getKey() + " 2nd value " + entry.getValue(), Toast.LENGTH_SHORT).show();


                    }

                    //enable question editbox
                    //category_id.setAlpha(0.0f);
                    //category_id.setVisibility(View.GONE);
                    Return_Question.setVisibility(View.VISIBLE);


                } else {
                    Return_Question.setVisibility(View.GONE);

    //                Toast.makeText(MyApplication.getContext(), "Checkbox unchecked ...", Toast.LENGTH_SHORT).show();
                    /*for (int i = 0; i < mylist.size(); i++) {
                        if (mylist.get(i)==Id_Order_Detail){
                            mylist.remove(i);
                            break;
                        }
                    }*/


                    Iterator<Map.Entry<String, String>> iterator = myMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        if (Id_Order_Detail.equalsIgnoreCase(entry.getKey())) {
                            iterator.remove();
                        }
                    }


                }

                pplist.add(myMap);
            }
        });



        return vi;

    }

    //public String get_price() {return mylist;}

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
