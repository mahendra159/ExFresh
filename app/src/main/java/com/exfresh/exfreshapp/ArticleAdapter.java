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
 * Created by Mahendra on 7/15/2015.
 */
public class ArticleAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public ImageLoader imageLoader;


    public ArticleAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());

    }

    public ArticleAdapter (){  };

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
            vi = inflater.inflate(R.layout.fragment_articles_row, null);

        TextView article_name = (TextView)vi.findViewById(R.id.article_name); // title
        TextView article_link = (TextView)vi.findViewById(R.id.article_link); // artist name
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.article_image);


        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);


        article_name.setText(listdata.get("name"));
        article_link.setText(listdata.get(VideoFragment.TAG_VIDEO_LINK));
        String Article_Image_Url = listdata.get("thumbnail");
        imageLoader.DisplayImage(Article_Image_Url, thumb_image);

        return vi;

    }

}
