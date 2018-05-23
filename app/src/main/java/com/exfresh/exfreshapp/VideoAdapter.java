package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Mahendra on 7/14/2015.
 */
public class VideoAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public ImageLoader imageLoader;


    public VideoAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());

    }

    public VideoAdapter (){  };

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
            vi = inflater.inflate(R.layout.fragment_video_row, null);

        TextView video_name = (TextView)vi.findViewById(R.id.video_name); // title
        TextView video_link = (TextView)vi.findViewById(R.id.video_link); // artist name
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.media_image);


        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);


        video_name.setText(listdata.get("name"));
        video_link.setText(listdata.get(VideoFragment.TAG_VIDEO_LINK));
        String Video_Image_Url = listdata.get("thumbnail");
        imageLoader.DisplayImage(Video_Image_Url, thumb_image);

        return vi;

    }




}
