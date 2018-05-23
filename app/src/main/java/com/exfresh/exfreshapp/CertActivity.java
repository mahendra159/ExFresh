package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


/**
 * Created by Mahendra on 6/12/2015.
 */
public class CertActivity extends FragmentActivity implements CertAdapter.customButtonListener {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ProgressDialog pDialog;

    public ImageLoaderBig imageLoader;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();


    String Product_Id;
    String Id_Shop;
    String CERT_URL;
    String CERT_PRODUCT_F_IMAGE_URL;
    String CERT_PRODUCT_S_IMAGE_URL;
    String CERT_PIC_URL;
    String URL_PHP;
    String URL_FARM_DETAIL;

    GoogleMap googleMap;


    // Creating JSON Parser object
    JSONParser_cart jParser = new JSONParser_cart();

    // products JSONArray
    JSONArray farm_details = null;
    JSONArray conta_details = null;
    ArrayList<HashMap<String, String>> FarmDetailList;
    ArrayList<HashMap<String, String>> ContaDetailList;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SUCCESS_CONTA = "success_contaminants";

    private static final String TAG_FARM_DETAILS = "farm_dtls";
    private static final String TAG_CONTA_DETAILS = "conta_dtls";

    static final String TAG_FARM_NAME = "farm_name";
    static final String TAG_FARM_ADD = "product_source_add";
    static final String TAG_FARM_LAT = "product_loc_lat";
    static final String TAG_FARM_LONG = "product_loc_long";
    static final String TAG_FARM_DESC = "product_cert_desc";
    static final String TAG_PRODUCT_F_PIC = "farm_pic1";
    static final String TAG_PRODUCT_S_PIC = "farm_pic2";

    static final String TAG_PRODUCT_F_PIC_BIG = "farm_pic1_big";
    static final String TAG_PRODUCT_S_PIC_BIG = "farm_pic2_big";

    static final String TAG_CONTA_NAME = "name";
    static final String TAG_CONTA_TYPE = "type";
    static final String TAG_CONTA_WL = "water_level";
    static final String TAG_CONTA_DWL = "detected_water_level";
    static final String TAG_CONTA_SL = "soil_level";
    static final String TAG_CONTA_DSL = "detected_soil_level";
    static final String TAG_CONTA_VL = "vegetable_level";
    static final String TAG_CONTA_DVL = "detected_vegetable_level";

    static final String TAG_CONTA_CL8 = "cl8";
    static final String TAG_CONTA_CL9 = "cl9";
    static final String TAG_CONTA_CL10 = "cl10";
    static final String TAG_CONTA_CL11 = "cl11";
    static final String TAG_CONTA_CL12 = "cl12";


    static final String TAG_MSG = "msg";

    String lat;
    String lng;
    String f_name;

    ListView list;
    CertAdapter adapter;

    ListView list_conta;
    CertContaAdapter contaadapter;

    String CERT_HELP = "Pref_cert_help_disp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cert);


        cd = new ConnectionDetector(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        Product_Id = extras.getString("product_id");

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        Id_Shop = preferences.getString("Pref_shop_id", null);
        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_FARM_DETAIL = URL_PHP.concat("GetFarmDetail.php");

        CERT_URL = preferences.getString("Pref_cert_url", null);
        CERT_PRODUCT_F_IMAGE_URL = CERT_URL.concat("farm_pic/" + Product_Id + "-1.jpg");
        CERT_PRODUCT_S_IMAGE_URL = CERT_URL.concat("farm_pic/" + Product_Id + "-2.jpg");
        CERT_PIC_URL = CERT_URL.concat("cert_pic/" + Product_Id + ".jpg");

        FarmDetailList = new ArrayList<HashMap<String, String>>();
        ContaDetailList = new ArrayList<HashMap<String, String>>();

        String disp_cert_help = preferences.getString("Pref_cert_help_disp", null);
        if (disp_cert_help==null) {

            if (!((Activity) CertActivity.this).isFinishing()) {
                onCoachMark();

            }
        }


        new GetLatLong().execute();

    }


    public void onCoachMark(){

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.coach_mark);
        dialog.setCanceledOnTouchOutside(true);
        //for dismissing anywhere you touch
        View masterView = dialog.findViewById(R.id.coach_mark_master_view);
        masterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                editor.putString(CERT_HELP, "1");
                editor.commit();
            }
        });
        dialog.show();
    }


    private class GetLatLong extends AsyncTask<String, String, String> {

        String message;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CertActivity.this);
            pDialog.setMessage("Loading ....");
            pDialog.show();

        }
        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_product", Product_Id));
            params.add(new BasicNameValuePair("id_shop", Id_Shop));

            JSONObject json = jParser.makeHttpRequest(URL_FARM_DETAIL, "POST", params);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    farm_details = json.getJSONArray(TAG_FARM_DETAILS);

                    for (int i = 0; i < farm_details.length(); i++) {
                        JSONObject c = farm_details.getJSONObject(i);

                        String Farm_Name = c.getString(TAG_FARM_NAME);

                        String Farm_Add = c.getString(TAG_FARM_ADD);
                        String Farm_Lat = c.getString(TAG_FARM_LAT);
                        String Farm_Long = c.getString(TAG_FARM_LONG);
                        String Farm_Desc = c.getString(TAG_FARM_DESC);
                        String Farm_Pic1 = c.getString(TAG_PRODUCT_F_PIC);
                        String Farm_Pic2 = c.getString(TAG_PRODUCT_S_PIC);

                        String Farm_Pic1_Big = c.getString(TAG_PRODUCT_F_PIC_BIG);
                        String Farm_Pic2_Big = c.getString(TAG_PRODUCT_S_PIC_BIG);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(TAG_FARM_NAME, Farm_Name);
                        map.put(TAG_FARM_ADD, Farm_Add);
                        map.put(TAG_FARM_LAT, Farm_Lat);
                        map.put(TAG_FARM_LONG,Farm_Long);
                        map.put(TAG_FARM_DESC, Farm_Desc);

                        map.put(TAG_PRODUCT_F_PIC, Farm_Pic1);
                        map.put(TAG_PRODUCT_S_PIC, Farm_Pic2);

                        map.put(TAG_PRODUCT_F_PIC_BIG, Farm_Pic1_Big);
                        map.put(TAG_PRODUCT_S_PIC_BIG, Farm_Pic2_Big);

                        FarmDetailList.add(map);
                    }


                } else if (success == 0) {
                    message = json.getString(TAG_MSG);
                }

                // get cert contaminants

                int success_conta = json.getInt(TAG_SUCCESS_CONTA);

                if (success_conta == 1) {
                    conta_details = json.getJSONArray(TAG_CONTA_DETAILS);

                    for (int i = 0; i < conta_details.length(); i++) {
                        JSONObject c = conta_details.getJSONObject(i);

                        String Conta_Name = c.getString(TAG_CONTA_NAME);
                        String Conta_Type = c.getString(TAG_CONTA_TYPE);
                        String Conta_WL = c.getString(TAG_CONTA_WL);
                        String Conta_DWL = c.getString(TAG_CONTA_DWL);
                        String Conta_SL = c.getString(TAG_CONTA_SL);
                        String Conta_DSL = c.getString(TAG_CONTA_DSL);

                        String Conta_VL = c.getString(TAG_CONTA_VL);
                        String Conta_DVL = c.getString(TAG_CONTA_DVL);

                        String CL8 = c.getString(TAG_CONTA_CL8);
                        String CL9 = c.getString(TAG_CONTA_CL9);
                        String CL10 = c.getString(TAG_CONTA_CL10);
                        String CL11 = c.getString(TAG_CONTA_CL11);
                        String CL12 = c.getString(TAG_CONTA_CL12);

                        HashMap<String, String> map_conta = new HashMap<String, String>();

                        map_conta.put(TAG_CONTA_NAME, Conta_Name);
                        map_conta.put(TAG_CONTA_TYPE, Conta_Type);
                        map_conta.put(TAG_CONTA_WL, Conta_WL);
                        map_conta.put(TAG_CONTA_DWL, Conta_DWL);

                        map_conta.put(TAG_CONTA_SL, Conta_SL);
                        map_conta.put(TAG_CONTA_DSL, Conta_DSL);

                        map_conta.put(TAG_CONTA_VL, Conta_VL);
                        map_conta.put(TAG_CONTA_DVL, Conta_DVL);

                        map_conta.put(TAG_CONTA_CL8, CL8);
                        map_conta.put(TAG_CONTA_CL9, CL9);
                        map_conta.put(TAG_CONTA_CL10, CL10);
                        map_conta.put(TAG_CONTA_CL11, CL11);
                        map_conta.put(TAG_CONTA_CL12, CL12);

                        ContaDetailList.add(map_conta);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    if (FarmDetailList.size()==0){
                        final Dialog dialog = new Dialog(CertActivity.this, R.style.ThemeWithCorners);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.no_farm_popup);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);

                        TextView text = (TextView) dialog.findViewById(R.id.tv);
                        text.setText(message);

                        Button button = (Button) dialog.findViewById(R.id.Button01);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                        dialog.show();
                    }else {

                        list = (ListView) findViewById(R.id.farm_dtl_list);

                        adapter = new CertAdapter(CertActivity.this, FarmDetailList);
                        adapter.setCustomButtonListner(CertActivity.this);
                        //adapter.setCustomImageListner(CertActivity.this);
                        list.setAdapter(adapter);

                        list_conta = (ListView) findViewById(R.id.conta_dtl_list);
                        contaadapter = new CertContaAdapter(CertActivity.this, ContaDetailList);
                        list_conta.setAdapter(contaadapter);

                        Utility.setListViewHeightBasedOnChildren(list);
                        Utility.setListViewHeightBasedOnChildren(list_conta);

                    }

                } });
        }
    }

    /*
    @Override
    public void onImgClickListner(int position, String imgurl) {

        imageLoader = new ImageLoaderBig(CertActivity.this);

        final Dialog bigpicDialog = new Dialog(CertActivity.this, R.style.ThemeWithCorners);
        bigpicDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        bigpicDialog.setCancelable(false);
        bigpicDialog.setContentView(R.layout.preview_image);
        final Button btnClose = (Button) bigpicDialog.findViewById(R.id.btnIvClose);
        TouchImageView img = (TouchImageView) bigpicDialog.findViewById(R.id.img);
        imageLoader.DisplayImage(imgurl, img);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                bigpicDialog.dismiss();
            }
        });
        bigpicDialog.show();
    }
    */



    @Override
    public void onButtonClickListner(int position, String latitude,String longitude, String farm_name) {

        lat=latitude;
        lng =longitude;
        f_name = farm_name;



        final Intent GoToDisplay = new Intent(CertActivity.this,cert_map.class);
        GoToDisplay.putExtra("f_name", f_name);
        GoToDisplay.putExtra("lat", lat);
        GoToDisplay.putExtra("lng", lng);
        this.startActivity(GoToDisplay);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void drawMarker(LatLng point){
        // Clears all the existing coordinates
        googleMap.clear();

        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point);

        // Setting title for the InfoWindow
        markerOptions.title("Position");

        // Setting InfoWindow contents
        markerOptions.snippet("Latitude:" + point.latitude + ",Longitude" + point.longitude);

        // Adding marker on the Google Map
        googleMap.addMarker(markerOptions);


        // Moving CameraPosition to the user input coordinates
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 14.0f));
    }
}
