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
 * Created by Mahendra on 3/23/2015.
 */
public class CategoryAdapter extends BaseAdapter {


    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;


    String TAG_SUCCESS;

    public CategoryAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

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
            vi = inflater.inflate(R.layout.activity_category_row, null);

        TextView category_name = (TextView)vi.findViewById(R.id.category_name); // title
        TextView category_id = (TextView)vi.findViewById(R.id.category_id); // artist name

        HashMap<String, String> category = new HashMap<String, String>();
        category = data.get(position);

        category_name.setText(category.get(Category.TAG_CATEGORY_NAME));
        category_id.setText(category.get(Category.TAG_CATEGORY_ID));
        //category_id.setAlpha(0.0f);
        //category_id.setVisibility(View.GONE);
        return vi;
    }
}
