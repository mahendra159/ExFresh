package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahendra on 3/29/2015.
 */
public class ShowTotalPriceAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public ShowTotalPriceAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ShowTotalPriceAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_cart_t_price_row, null);

        TextView Total_Price = (TextView)vi.findViewById(R.id.total_price_txtview); // total price
        TextView Total_Items = (TextView)vi.findViewById(R.id.total_items_txtview); // total price
        TextView Cart_Empty_Msg = (TextView)vi.findViewById(R.id.cart_empty_display);
        TextView cart_ety_tot_prce = (TextView)vi.findViewById(R.id.tot_price);
        TextView cart_ety_tot_itms = (TextView)vi.findViewById(R.id.tot_items);

        HashMap<String, String> tot_p = new HashMap<String, String>();
        tot_p = data.get(position);

        final String tot_pr_final = tot_p.get(My_CartActivity.KEY_TOTAL_PRICE);
        final String tot_itm_final = tot_p.get(My_CartActivity.KEY_TOTAL_ITEMS);

      //  title.setText(song.get(My_CartActivity.KEY_NAME));

if (tot_pr_final==null)
{   Cart_Empty_Msg.setText("Cart is Empty");
    //Total_Price.setText("Zero");
    cart_ety_tot_prce.setVisibility(View.INVISIBLE);
    cart_ety_tot_itms.setVisibility(View.INVISIBLE);
   // Total_Items.setText("Zero");

}
 else {
    cart_ety_tot_prce.setVisibility(View.VISIBLE);
    cart_ety_tot_itms.setVisibility(View.VISIBLE);
    Total_Price.setText("Rs." + tot_pr_final);
    Total_Items.setText(tot_itm_final);
}

        return vi;

    }

}
