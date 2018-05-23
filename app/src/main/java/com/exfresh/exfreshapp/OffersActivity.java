package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.exfresh.exfreshapp.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.exfresh.exfreshapp.CommonUtilities.EXTRA_MESSAGE;

/**
 * Created by Mahendra on 5/20/2015.
 */
public class OffersActivity extends ActionBarActivity {

    // label to display gcm messages
    TextView lblMessage;
    TextView Offer_Expired;

    private PowerManager.WakeLock wakeLock;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    private ImageView OfferImage;


    String Offer_URL;
    String OFFER_IMAGE_URL;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Progress Dialog
    private ProgressDialog pDialog;



    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());

    //    lblMessage = (TextView) findViewById(R.id.lblMessage);

        OfferImage = (ImageView)findViewById(R.id.offer_image);
        Offer_Expired = (TextView)findViewById(R.id.no_offer);

        //Bundle extras = getIntent().getExtras();

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        //MAIN_URL = preferences.getString("Pref_main_url", null);
        Offer_URL = preferences.getString("Pref_offer_url", null);
        OFFER_IMAGE_URL = Offer_URL.concat("/offer.jpg");

        if (MyDebug.LOG) {
            Log.d("Offer URL - ", OFFER_IMAGE_URL);
        }



        //String message = extras.getString("message");
        //String message=getIntent().getStringExtra("message");
        //lblMessage.append(message + "\n");

        registerReceiver(mHandleMessageReceiver, new IntentFilter(DISPLAY_MESSAGE_ACTION));


        new LoadImage().execute(OFFER_IMAGE_URL);

    }

    /**
     * Receiving push messages
     * */
    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);

          // Waking up mobile if it is sleeping
            //WakeLocker.acquire(getApplicationContext());
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            //PowerManager.WakeLock.acquire(getApplicationContext());
            wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP
                    | PowerManager.ON_AFTER_RELEASE,"Wake Lock");

            wakeLock.acquire(15*1000);

            /**
             * Take appropriate action on this message
             * depending upon your app requirement
             * For now i am just displaying it on the screen
             * */

            // Showing received message
  //          lblMessage.append(newMessage + "\n");
            //lblMessage.setText(newMessage + "\n");
//            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

            // Releasing wake lock
            //WakeLocker.release();
            wakeLock.release();
        }
    };



    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OffersActivity.this);
            pDialog.setMessage("Loading ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                OfferImage.setImageBitmap(image);
                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                //Toast.makeText(OffersActivity.this, "Offer expired or Network Error", Toast.LENGTH_SHORT).show();
                //Offer_Expired.setText("No offer available");
                //Offer_Expired.setVisibility(View.VISIBLE);

                //set up dialog
                final Dialog dialog = new Dialog(OffersActivity.this, R.style.ThemeWithCorners);
                dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_usage);

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                text.setText("No offer available.");
                //set up button
                Button button = (Button) dialog.findViewById(R.id.Button01);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        //Intent i= new Intent(OffersActivity.this,Category.class);
                        //startActivity(i);
                        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                });


                //now that the dialog is set up, it's time to show it
                dialog.show();



            }
        }
    }



    @Override
    public void onBackPressed() {
        Intent i= new Intent(OffersActivity.this,Category.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }






}




