package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahendra on 4/2/2015.
 */
public class CheckoutAdapter extends BaseAdapter {

    private static SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public static String pr_price;

    String TAG_SUCCESS;

    public CheckoutAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public CheckoutAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_checkout_row, null);

        prefs = activity.getApplicationContext().getSharedPreferences("PREF_NAME", 0);
        editor = prefs.edit();

        TextView Title = (TextView)vi.findViewById(R.id.title); // title
        TextView Attribute_Quantity = (TextView)vi.findViewById(R.id.attribute);
        TextView Product_Quantity = (TextView)vi.findViewById(R.id.pr_quantity_value);
        TextView Final_Price = (TextView) vi.findViewById(R.id.final_price);
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image

        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        pr_price = song.get(CheckoutActivity.TAG_FINAL_PRICE);
        String pr_price_trimd = pr_price.substring(0,pr_price.length()-4);
        float temp_pr_price_trimd = Float.parseFloat(pr_price_trimd);
        int pr_price_trimd_int = (int)temp_pr_price_trimd;
        String Product_Image_Url = song.get(My_CartActivity.TAG_IMAGE_URL);


        Title.setText(song.get(CheckoutActivity.TAG_PRODUCT_NAME));
        Product_Quantity.setText(song.get(CheckoutActivity.TAG_QUANTITY));
        Final_Price.setText("Rs."+pr_price_trimd_int);
        Attribute_Quantity.setText(song.get(CheckoutActivity.TAG_ATTRIBUTE_NAME));
        imageLoader.DisplayImage(Product_Image_Url, thumb_image);

        return vi;

    }
}