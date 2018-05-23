package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahendra on 6/13/2015.
 */
public class CertAdapter extends BaseAdapter {

    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position,String lat, String lng, String farm_name);
    }


    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    /* for image zoom click */
    customImageListener ImageListner;

    public interface customImageListener {
        public void onImgClickListner(int position,String imgurl);
    }

    public void setCustomImageListner(customImageListener listener) {
        this.ImageListner = listener;
    }

        /* for image zoom click end */

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoaderBig imageLoader;
    public int status_flag;




    public CertAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoaderBig(activity.getApplicationContext());

    }

    public CertAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_cert_row, null);


        TextView Farm_Address = (TextView)vi.findViewById(R.id.farm_add); // item
        ImageButton mapbtn = (ImageButton)vi.findViewById(R.id.mapbutton);
        ImageView Product_F_Image = (ImageView)vi.findViewById(R.id.product_f_image);
        final ImageView Product_S_Image = (ImageView)vi.findViewById(R.id.product_s_image);

        TextView Cert_Description = (TextView)vi.findViewById(R.id.cert_desc);




        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

        String pos = String.valueOf(position);
        if (MyDebug.LOG) {
            Log.d("Value of cert pos", pos);
        }



        String Farm_Add = listdata.get(CertActivity.TAG_FARM_ADD);
        final String CERT_PRODUCT_F_IMAGE_URL = listdata.get(CertActivity.TAG_PRODUCT_F_PIC);
        final String CERT_PRODUCT_S_IMAGE_URL = listdata.get(CertActivity.TAG_PRODUCT_S_PIC);
        String Certificate_Description = listdata.get(CertActivity.TAG_FARM_DESC);

        final String CERT_PRODUCT_F_IMAGE_URL_BIG = listdata.get(CertActivity.TAG_PRODUCT_F_PIC_BIG);
        final String CERT_PRODUCT_S_IMAGE_URL_BIG = listdata.get(CertActivity.TAG_PRODUCT_S_PIC_BIG);

        final String farm_name = listdata.get(CertActivity.TAG_FARM_NAME);
        final String lat = listdata.get(CertActivity.TAG_FARM_LAT);
        final String lng = listdata.get(CertActivity.TAG_FARM_LONG);

        Farm_Address.setText(Farm_Add);
        imageLoader.DisplayImage(CERT_PRODUCT_F_IMAGE_URL, Product_F_Image);
        imageLoader.DisplayImage(CERT_PRODUCT_S_IMAGE_URL, Product_S_Image);
        Cert_Description.setText(Certificate_Description);

        if (CERT_PRODUCT_F_IMAGE_URL.equalsIgnoreCase("null") ||CERT_PRODUCT_F_IMAGE_URL.equalsIgnoreCase("") || CERT_PRODUCT_F_IMAGE_URL==null){
            Product_F_Image.setVisibility(View.GONE);
        }

        else {
            Product_F_Image.setVisibility(View.VISIBLE);
            Product_F_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog bigpicDialog = new Dialog(activity, R.style.ThemeWithCorners);
                    bigpicDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    bigpicDialog.setCancelable(false);
                    bigpicDialog.setContentView(R.layout.preview_image);
                    Button btnClose = (Button) bigpicDialog.findViewById(R.id.btnIvClose);
                    TouchImageView img = (TouchImageView) bigpicDialog.findViewById(R.id.img);
                    imageLoader.DisplayImage(CERT_PRODUCT_F_IMAGE_URL_BIG, img);

                    btnClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {

                            bigpicDialog.dismiss();
                        }
                    });
                    bigpicDialog.show();
                }
            });
        }

        if (CERT_PRODUCT_S_IMAGE_URL.equalsIgnoreCase("null") ||CERT_PRODUCT_S_IMAGE_URL.equalsIgnoreCase("") || CERT_PRODUCT_F_IMAGE_URL==null){
            Product_S_Image.setVisibility(View.GONE);
        }
        else{
            Product_S_Image.setVisibility(View.VISIBLE);
            Product_S_Image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                //
                final Dialog bigpicDialog = new Dialog(activity, R.style.ThemeWithCorners);
                bigpicDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                bigpicDialog.setCancelable(false);
                bigpicDialog.setContentView(R.layout.preview_image);
                Button btnClose = (Button) bigpicDialog.findViewById(R.id.btnIvClose);
                //ImageView Preview = (ImageView)bigpicDialog.findViewById(R.id.preview_image);
                TouchImageView img = (TouchImageView) bigpicDialog.findViewById(R.id.img);
                imageLoader.DisplayImage(CERT_PRODUCT_S_IMAGE_URL_BIG, img);

                //imageLoader.DisplayImage(CERT_PRODUCT_S_IMAGE_URL_BIG, img);

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {

                        bigpicDialog.dismiss();
                    }
                });
                bigpicDialog.show();


                    /*
                    if (ImageListner != null) {
                        ImageListner.onImgClickListner(position, CERT_PRODUCT_S_IMAGE_URL_BIG);
                    }
                    */
                }
            });
        }







        mapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customListner != null) {

                    customListner.onButtonClickListner(position, lat, lng, farm_name);
                }
            }

        });

        return vi;
    }
}
