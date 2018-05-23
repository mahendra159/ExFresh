package com.exfresh.exfreshapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gcm.GCMRegistrar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static com.exfresh.exfreshapp.CommonUtilities.SENDER_ID;
import static com.exfresh.exfreshapp.CommonUtilities.SERVER_URL;

/**
 * Created by Mahendra on 3/21/2015.
 */
public class SplashScreen extends Activity {

    //String URL_PHP = "http://www.exfresh.co.in/phpscripts/";
    String URL_PHP = "http://exfresh.online/exfresh3/phpscripts/";

    Context context;

    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;
    AsyncTask<Void, Void, Void> locRegisterTask;

    public static String name;
    public static String email;
    public static String mPhoneNumber;
    public static String email_phone;

    private PowerManager.WakeLock wakeLock;

    Button ShopNow;
    private TextView SelectedLOC;
    private TextView Welcome_header;
    private ImageView ChangeLOC;

    List<String> shopNames = new ArrayList<String>();
    List<String> shopIds = new ArrayList<String>();
    List<String> shopUrls = new ArrayList<String>();
    List<String> msg_list = new ArrayList<String>();
    List<String> shopGroupIds = new ArrayList<String>();
    List<String> shopGroupNames = new ArrayList<String>();

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ProgressDialog pDialog;

    ProgressBar pBar;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    JSONParser_cart jParser = new JSONParser_cart();

    JSONArray msg_ids = null;
    JSONArray shop_locs = null;
    JSONArray shop_grps = null;

    String KEY_LOGIN_NAME = "LoginStatus";
    String KEY_WEBSERVICE = "Pref_Webservice_Key";
    String KEY_LOC = "Pref_shop_loc";
    String KEY_SHOP_ID = "Pref_shop_id";
    String KEY_SHOP_URL = "Pref_shop_url";
    String KEY_URL_PHP = "Pref_url_php";
    String MAIN_URL = "Pref_main_url";
    String OFFER_URL = "Pref_offer_url";
    String CERT_URL = "Pref_cert_url";

    String KEY_SHOP_CITY = "Pref_shop_city";
    String KEY_SHOP_GROUP_ID = "Pref_shop_group_id";

    String URL_GET_MSG;
    String URL_GET_KEY_MSG;
    String URL_SHOP_LOC;
    String URL_SHOP_GROUP;





    private static final String TAG_SHOPS = "shoplist";
    static final String TAG_ID_SHOP = "id_shop";
    static final String TAG_ID_SHOP_GROUP = "id_shop_group";
    static final String TAG_SHOP_GROUP_NAME = "name";

    static final String TAG_SHOP_NAME = "name";
    static final String TAG_SHOP_URL="shop_url";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SUCCESS_MSG = "success_msg";
    private static final String TAG_MSGS = "msgs";
    static final String TAG_SINGLE_MSG = "msg";
    private static final String TAG_KEY = "service_key";

    private static final String TAG_SHOP_CITY = "shopcities";

    String LOCATION_SET = "Pref_Location_Set";

    private static String DEFAULT_USER;
    String Shop_GRP_Id;
    Integer backButtonCount=0;
    String Shop_LOC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Initialise sharedPreferences Object
        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        //Intent intent ;
        editor = preferences.edit();

        cd = new ConnectionDetector(getApplicationContext());

        //addListenerOnButton();

        email = getEmail(this);
        name = ("User");

        TelephonyManager tMgr = (TelephonyManager)SplashScreen.this.getSystemService(Context.TELEPHONY_SERVICE);
        mPhoneNumber = tMgr.getLine1Number();
        if (MyDebug.LOG) {
            Log.d("Phone Number", mPhoneNumber);
        }

        email_phone = email.concat(" Phone-").concat(mPhoneNumber);


        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this);

        if ( (preferences.getString("Pref_Location_Set", null)=="2")||(preferences.getString("Pref_Location_Set", null)==null) ) {
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                final String stringLatitude = String.valueOf(gpsTracker.latitude);

                final String stringLongitude = String.valueOf(gpsTracker.longitude);

                final String country = gpsTracker.getCountryName(this);

                final String city = gpsTracker.getLocality(this);

                String postalCode = gpsTracker.getPostalCode(this);

                final String addressLine = gpsTracker.getAddressLine(this);

                final Context context = this;

                locRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        LocationServerUtilities.register(context, stringLatitude, stringLongitude, email,mPhoneNumber, city, country, addressLine);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                locRegisterTask.execute(null, null, null);
            }
            /*else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
            }*/
        }



        final String regId = GCMRegistrar.getRegistrationId(this);

        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            alert.showAlertDialog(SplashScreen.this, "Internet Connection Error",
                    "Please connect to working Internet connection", false);
            return ;
        }

        if (GCMRegistrar.isRegisteredOnServer(this)){
            // Registration is not present, register now with GCM so go in else part and register
            //Toast.makeText(SplashScreen.this, "Notifications are already enabled.", Toast.LENGTH_LONG).show();
        }
        else {
            // Check if GCM configuration is set
            if (SERVER_URL == null || SENDER_ID == null || SERVER_URL.length() == 0
                    || SENDER_ID.length() == 0) {
                // GCM sernder id / server url is missing
                //alert.showAlertDialog(SplashScreen.this, "Configuration Error!","Please set your Server URL and GCM Sender ID", false);
                // stop executing code by return
                if (MyDebug.LOG) {
                    Log.d("Configuration Error", "Please set your Server URL and GCM Sender ID");
                }
                return ;
            }

            // Make sure the device has the proper dependencies.
            GCMRegistrar.checkDevice(this);

            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
            GCMRegistrar.checkManifest(this);

            if (regId.equals("")) {
                if (MyDebug.LOG) {
                    Log.d("Registrain Id:", regId);
                }
                // Registration is not present, register now with GCM
                GCMRegistrar.register(this, SENDER_ID);
            }
        }

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }


        editor.putString(KEY_URL_PHP, URL_PHP);
        editor.commit();

        URL_GET_MSG = URL_PHP.concat("Get_Splash_Msg.php");
        URL_GET_KEY_MSG = URL_PHP.concat("Get_Key_Msg.php");
        URL_SHOP_LOC = URL_PHP.concat("Get_Shop_Loc.php");
        URL_SHOP_GROUP = URL_PHP.concat("Get_Shop_Group.php");

        TextView Welcome_header = (TextView)findViewById((R.id.hello_msg));

        SelectedLOC = (TextView)findViewById(R.id.selected_location);
        ChangeLOC = (ImageView)findViewById(R.id.change_loc);

        Shop_LOC = preferences.getString("Pref_shop_loc", null);
        Shop_GRP_Id = preferences.getString("Pref_shop_group_id", null);

        String disp_f_name = preferences.getString("Pref_Cus_First_Name", "");

        if (Shop_LOC==null || Shop_LOC.equalsIgnoreCase("ExFresh")){

        }
        else {
            SelectedLOC.setText(Shop_LOC);
            ChangeLOC.setVisibility(View.VISIBLE);
        }


        SelectedLOC.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View mView, MotionEvent mMotionEvent) {

                preferences.edit().remove("Pref_shop_loc").commit();
                preferences.edit().remove("Pref_main_url").commit();
                preferences.edit().remove("Pref_shop_id").commit();

                Shop_LOC = null;
                new GetLOC().execute();
                return false;
            }
        });

        int f_name_len = disp_f_name.length();
        if (f_name_len >= 1) {

            Welcome_header.setText("Hello " + disp_f_name);
        }

        if (preferences.getString("Pref_Webservice_Key", null)==null){
        new GetKey().execute();
        }
        /*else{
            new ShowPopup().execute();
        }*/

        ((MyApplication) this.getApplication()).setKey(DEFAULT_USER);

         new GetShopGroup().execute();

        /*
        // check if GPS enabled
        GPSTracker gpsTracker = new GPSTracker(this);

        if ( preferences.getString("Pref_Location_Set", null)==null ) {
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                final String stringLatitude = String.valueOf(gpsTracker.latitude);

                final String stringLongitude = String.valueOf(gpsTracker.longitude);

                final String country = gpsTracker.getCountryName(this);

                final String city = gpsTracker.getLocality(this);

                String postalCode = gpsTracker.getPostalCode(this);

                final String addressLine = gpsTracker.getAddressLine(this);

                final Context context = this;

                locRegisterTask = new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        // Register on our server
                        // On server creates a new user
                        LocationServerUtilities.register(context, stringLatitude, stringLongitude, email,mPhoneNumber, city, country, addressLine);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        mRegisterTask = null;
                    }

                };
                locRegisterTask.execute(null, null, null);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
                return;
            }
        }

        else {
            new GetShopGroup().execute();
        }
        */

        /*
        String loc_set = preferences.getString("Pref_Location_Set",null);
        if (loc_set != null) {
            if (loc_set.equalsIgnoreCase("1") ) {
                new GetShopGroup().execute();
            }
        }
        */

    }


    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }



    class GetLOC extends AsyncTask<String, String, String> {
        String xml="";
        ConnectXML parser2 = new ConnectXML();
        LinearLayout PBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            PBarLayout.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_shop_group", Shop_GRP_Id));

            // getting JSON string from URL
            JSONObject json_shop_loc = jParser.makeHttpRequest(URL_SHOP_LOC, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_shop_loc.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // shops found
                    // Getting Array of shops
                    shop_locs = json_shop_loc.getJSONArray(TAG_SHOPS);

                     for (int i = 0; i < shop_locs.length(); i++) {
                        JSONObject c = shop_locs.getJSONObject(i);

                        String Id_Shop = c.getString(TAG_ID_SHOP);
                        String Shop_Name = c.getString(TAG_SHOP_NAME);
                        String Shop_Url = c.getString(TAG_SHOP_URL);

                        shopNames.add(Shop_Name);
                        shopIds.add(Id_Shop);
                        shopUrls.add(Shop_Url);
                     }

                } else {

                    // no products found
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            }
            catch (Throwable t) {
            }
            return xml;
        }

        protected void onPostExecute(final String xml) {
            //if (pDialog.isShowing()){
            //    pDialog.dismiss();
           // }

            PBarLayout.setVisibility(View.GONE);

            runOnUiThread(new Runnable() {
                public void run() {

                    ChooseLoc2();
                } });
        }
    }


    class GetShopGroup extends AsyncTask<String, String, String> {
        String xml="";
        String active="1";
        ConnectXML parser2 = new ConnectXML();

        LinearLayout PBarLayout = (LinearLayout) findViewById(R.id.progressBarLayout);

        @Override
        protected void onPreExecute() {
     //pBar = findViewById(R.id.progressBar);
            super.onPreExecute();
            //pDialog = new ProgressDialog(SplashScreen.this);
            //pDialog.setTitle("Loading");
            //pDialog.setMessage("Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
            PBarLayout.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... args)
        {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("active", active));

            // getting JSON string from URL
            JSONObject json_shop_grp = jParser.makeHttpRequest(URL_SHOP_GROUP, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_shop_grp.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // shops found
                    // Getting Array of shops
                    shop_grps = json_shop_grp.getJSONArray(TAG_SHOP_CITY);

                    // looping through All Sohops
                    for (int i = 0; i < shop_grps.length(); i++) {
                        JSONObject c = shop_grps.getJSONObject(i);

                        // Storing each json item in variable
                        String Id_Shop_Group = c.getString(TAG_ID_SHOP_GROUP);
                        String Shop_Group_Name = c.getString(TAG_SHOP_GROUP_NAME);
                        shopGroupIds.add(Id_Shop_Group);
                        shopGroupNames.add(Shop_Group_Name);
                    }

                } else {

                    // no products found
                    runOnUiThread(new Runnable() {
                        public void run() {
                        }
                    });
                }
            }
            catch (Throwable t) {
                if (MyDebug.LOG) {
                    Log.d("Exception: ", t.toString());
                }
            }
            return null;
        }

        protected void onPostExecute(final String xml) {
          //  if (pDialog.isShowing()){
          //      pDialog.dismiss();
          //  }
            PBarLayout.setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                public void run() {
                    ChooseShopGroup();
                } });
        }
    }




    private void ChooseLoc2() {

        if (shopNames.size()>1){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Locality");
        final CharSequence[] selectLocality = shopNames.toArray(new CharSequence[shopNames.size()]);
        final CharSequence[] id_shop = shopIds.toArray(new CharSequence[shopIds.size()]);
        final CharSequence[] shop_url = shopUrls.toArray(new CharSequence[shopUrls.size()]);

        builder.setItems(selectLocality, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (shopNames.size()>1) {
                    SelectedLOC.setText(selectLocality[which]);
                }

                final StringBuilder selected_location = new StringBuilder(selectLocality[which].length());
                final StringBuilder selected_id_shop = new StringBuilder(id_shop[which].length());
                final StringBuilder selected_shop_url = new StringBuilder(shop_url[which].length());

                selected_location.append(selectLocality[which]);
                selected_id_shop.append(id_shop[which]);
                selected_shop_url.append(shop_url[which]);

                String Shop_Location = selected_location.toString();
                String Shop_Id = selected_id_shop.toString();
                String Shop_URL_HTTP = "http://";
                String SHOP_URL_PRE = Shop_URL_HTTP.concat(selected_shop_url.toString());
                String SHOP_URL = SHOP_URL_PRE.substring(0, SHOP_URL_PRE.length() - 1);


                String OFFER_PRE_URL = SHOP_URL.substring(0, 24); // http://www.exfresh.co.in/Mansarovar
                String CERT_PRE_URL = SHOP_URL.substring(0, 24);

                String offer_locality = "";
                String OFFER_GO_URL = "";
                String CERT_GO_URL = "";
                if (SHOP_URL.length() > 24) {
                    offer_locality = SHOP_URL.substring(25, SHOP_URL.length());
                    OFFER_GO_URL = OFFER_PRE_URL.concat("/Offer/").concat(offer_locality);
                    CERT_GO_URL = CERT_PRE_URL.concat("/Certification/").concat(offer_locality);
                } else {
                    OFFER_GO_URL = SHOP_URL.concat("/Offer/");
                    CERT_GO_URL = SHOP_URL.concat("/Certification/");
                }

                if (MyDebug.LOG) {
                    Log.d("shop location:", Shop_Location);
                    Log.d("shop id:", Shop_Id);
                    Log.d("shop url:", SHOP_URL);

                    Log.d("OFFER_PRE_URL", OFFER_PRE_URL);
                }

                editor.putString(KEY_LOC, Shop_Location);
                editor.putString(KEY_SHOP_ID, Shop_Id);
                editor.putString(MAIN_URL, SHOP_URL);
                editor.putString(OFFER_URL, OFFER_GO_URL);
                editor.putString(CERT_URL, CERT_GO_URL);
                editor.commit();

                Shop_LOC = preferences.getString("Pref_shop_loc", null);

                if (shopNames.size()>1){
                SelectedLOC.setText(Shop_LOC);
                ChangeLOC.setVisibility(View.VISIBLE);
                }

                Toast.makeText(SplashScreen.this, "Shop Now", Toast.LENGTH_SHORT).show();

                dialog.dismiss();

                shopNames.clear();
                shopIds.clear();
                shopUrls.clear();


                if (!isLogin()) {
                    //if login failed then-->
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    //Intent intent = new Intent(SplashScreen.this, NewLoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();

                }
                else {
                    Intent intent = new Intent(SplashScreen.this, Category.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            }
        });
        AlertDialog alert = builder.create();
        //setFinishOnTouchOutside(false);
        //setCanceledOnTouchOutside(false);
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        alert.show();
      }
      else {


            String Shop_Location = shopNames.get(0).toString();
            String Shop_Id = shopIds.get(0).toString();
            String Shop_URL_HTTP = "http://";
            String SHOP_URL_PRE = Shop_URL_HTTP.concat(shopUrls.get(0).toString());
            String SHOP_URL = SHOP_URL_PRE.substring(0, SHOP_URL_PRE.length() - 1);

            String OFFER_PRE_URL = SHOP_URL.substring(0, 24); // http://www.exfresh.co.in/Mansarovar
            //String CERT_PRE_URL = SHOP_URL.substring(0, 24);
            String CERT_PRE_URL = SHOP_URL;

            String offer_locality = "";
            String OFFER_GO_URL = "";
            String CERT_GO_URL = "";
            if (SHOP_URL.length() > 30) {
                offer_locality = SHOP_URL.substring(25, SHOP_URL.length());
                OFFER_GO_URL = OFFER_PRE_URL.concat("/Offer/").concat(offer_locality);
                CERT_GO_URL = CERT_PRE_URL.concat("/Certification/").concat(offer_locality);
            } else {
                OFFER_GO_URL = SHOP_URL.concat("/Offer/");
                CERT_GO_URL = SHOP_URL.concat("/Certification/");
            }

            if (MyDebug.LOG) {
                Log.d("shop location:", Shop_Location);
                Log.d("shop id:", Shop_Id);
                Log.d("shop url:", SHOP_URL);

                Log.d("OFFER_PRE_URL", OFFER_PRE_URL);
            }

            editor.putString(KEY_LOC, Shop_Location);
            editor.putString(KEY_SHOP_ID, Shop_Id);
            editor.putString(MAIN_URL, SHOP_URL);
            editor.putString(OFFER_URL, OFFER_GO_URL);
            editor.putString(CERT_URL, CERT_GO_URL);
            editor.commit();

            Shop_LOC = preferences.getString("Pref_shop_loc", null);

            shopNames.clear();
            shopIds.clear();
            shopUrls.clear();


            if (!isLogin()) {
                //if login failed then-->
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                //Intent intent = new Intent(SplashScreen.this, NewLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            }
            else {
                Intent intent = new Intent(SplashScreen.this, Category.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }



        }
    }






    private void ChooseShopGroup() {

        if (shopGroupNames.size() > 1) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose City");
        final CharSequence[] selectCity = shopGroupNames.toArray(new CharSequence[shopGroupNames.size()]);
        final CharSequence[] id_shop_group = shopGroupIds.toArray(new CharSequence[shopGroupIds.size()]);

        builder.setItems(selectCity, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (shopGroupNames.size() > 1) {
                    SelectedLOC.setText(selectCity[which]);
                }

                final StringBuilder selected_city = new StringBuilder(selectCity[which].length());
                final StringBuilder selected_id_shop_group = new StringBuilder(id_shop_group[which].length());

                selected_city.append(selectCity[which]);
                selected_id_shop_group.append(id_shop_group[which]);

                String Shop_City = selected_city.toString();
                String Shop_Group_Id = selected_id_shop_group.toString();

                if (MyDebug.LOG) {
                    Log.d("shop location:", Shop_City);
                    Log.d("shop id:", Shop_Group_Id);
                }

                    editor.putString(KEY_SHOP_CITY, Shop_City);
                    editor.putString(KEY_SHOP_GROUP_ID, Shop_Group_Id);
                    editor.commit();

                    Shop_GRP_Id = preferences.getString("Pref_shop_group_id", null);

                    dialog.dismiss();

                    shopGroupIds.clear();
                    shopGroupNames.clear();

                    new GetLOC().execute();
                }
            }

            );
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        }
            else{

            String Shop_City = shopGroupNames.get(0).toString();
            String Shop_Group_Id = shopGroupIds.get(0).toString();

            if (MyDebug.LOG) {
                Log.d("shop location:", Shop_City);
                Log.d("shop id:", Shop_Group_Id);
            }

            editor.putString(KEY_SHOP_CITY, Shop_City);
            editor.putString(KEY_SHOP_GROUP_ID, Shop_Group_Id);
            editor.commit();

            Shop_GRP_Id = preferences.getString("Pref_shop_group_id", null);

            shopGroupIds.clear();
            shopGroupNames.clear();

            new GetLOC().execute();
        }

    }

/*
  public void addListenerOnButton()
    {
        ShopNow = (Button)findViewById(R.id.shopnowbtn);

        ShopNow.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {

                cd = new ConnectionDetector(getApplicationContext());


                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(SplashScreen.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }

                if (Shop_GRP_Id == null) {
                new GetShopGroup().execute();
                    return;
                }

                if (Shop_LOC==null){
                    new GetLOC().execute();
                    return;
                }

                if (!isLogin()) {
                    //if login failed then-->
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    String test = preferences.getString("Pref_shop_group_id", null);
                    startActivity(intent);

                }
                else {
                    Intent intent = new Intent(SplashScreen.this, Category.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    String test = preferences.getString("Pref_shop_group_id", null);
                    startActivity(intent);
                }
            }

        });
    }

    */
    private boolean isLogin() {

        return preferences.getBoolean(KEY_LOGIN_NAME, false);

    }

    /*

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                }).setNegativeButton("No", null).show();
    }

   */

    class GetKey extends AsyncTask<String, String, String> {

        String Service_Key;
        String get_status="";

        // Before starting background thread Show Progress Dialog

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(SplashScreen.this);
            pDialog.setTitle("Loading");
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_webservice_account", "1"));

            // getting JSON string from URL
            //JSONObject json_key = jParser.makeHttpRequest(url_get_key, "POST", params);
            JSONObject json_key = jParser.makeHttpRequest(URL_GET_KEY_MSG, "POST", params);


            try {
                int success = json_key.getInt(TAG_SUCCESS);

                if (success == 1) {
                    Service_Key = json_key.getString(TAG_KEY);

                    if (MyDebug.LOG) {
                    Log.d("WebService Key = ", Service_Key);
                    }

                    get_status = "1";
                }
                else
                {
                    get_status="0";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return get_status;
        }


        protected void onPostExecute(final String get_status) {

            runOnUiThread(new Runnable() {
                public void run() {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    if (get_status.equalsIgnoreCase("1")){

                        DEFAULT_USER = Service_Key;

                        editor.putString(KEY_WEBSERVICE, DEFAULT_USER);
                        editor.commit();

                        //new ShowPopup().execute();

                    }
                    else{
                        Toast.makeText(SplashScreen.this,"Server Error", Toast.LENGTH_SHORT).show();
                    }

                } });
        }
    }



    class ShowPopup extends AsyncTask<String, String, String> {

        String get_status="";

        // Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_webservice_account", "1"));

            JSONObject json_key = jParser.makeHttpRequest(URL_GET_MSG, "POST", params);

            if (json_key != null){
                try {
                    int success = json_key.getInt(TAG_SUCCESS);

                    if (success == 1) {

                        msg_ids = json_key.getJSONArray(TAG_MSGS);

                        for (int i = 0; i < msg_ids.length(); i++) {
                            JSONObject c = msg_ids.getJSONObject(i);

                            String Splash_Message = c.getString(TAG_SINGLE_MSG);
                            msg_list.add(Splash_Message);

                        }

                        get_status = "1";
                    } else {
                        get_status = "0";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return get_status;

        }


        protected void onPostExecute(final String get_status) {

            runOnUiThread(new Runnable() {
                public void run() {

                    String popuid = preferences.getString("Pref_Webservice_Key", null);
                    if (get_status.equalsIgnoreCase("1")){

                        int len = msg_list.size();

                        String msg0="";
                        String msg1="";
                        String msg2="";
                        String msg3="";
                        String msg4="";


                        final Dialog dialog = new Dialog(SplashScreen.this,R.style.ThemeWithCorners3);
                        dialog.setContentView(R.layout.dialog_features);
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);

                        TextView text0 = (TextView) dialog.findViewById(R.id.TextView00);
                        TextView text1 = (TextView) dialog.findViewById(R.id.TextView01);
                        TextView text2 = (TextView) dialog.findViewById(R.id.TextView02);
                        TextView text3 = (TextView) dialog.findViewById(R.id.TextView03);
                        TextView text4 = (TextView) dialog.findViewById(R.id.TextView04);

                        TextView bullet0 = (TextView) dialog.findViewById(R.id.bullet0);
                        TextView bullet1 = (TextView) dialog.findViewById(R.id.bullet1);
                        TextView bullet2 = (TextView) dialog.findViewById(R.id.bullet2);
                        TextView bullet3 = (TextView) dialog.findViewById(R.id.bullet3);
                        TextView bullet4 = (TextView) dialog.findViewById(R.id.bullet4);

                        if (msg_list.size()>0){
                            //Message1.setText(msg_list.get(0));
                            msg0=msg_list.get(0);
                            text0.setText(msg0);
                            bullet0.setVisibility(View.VISIBLE);
                        }
                        if (msg_list.size()>1){
                            //Message2.setText(msg_list.get(1));
                            msg1=msg_list.get(1);
                            text1.setText(msg1);
                            bullet1.setVisibility(View.VISIBLE);
                        }
                        if (msg_list.size()>2){
                            //Message3.setText(msg_list.get(2));
                            msg2=msg_list.get(2);
                            text2.setText(msg2);
                            bullet2.setVisibility(View.VISIBLE);
                        }
                        if (msg_list.size()>3){
                            //Message4.setText(msg_list.get(3));
                            msg3=msg_list.get(3);
                            text3.setText(msg3);
                            bullet3.setVisibility(View.VISIBLE);
                        }
                        if (msg_list.size()>4){
                            //Message5.setText(msg_list.get(4));
                            msg4=msg_list.get(4);
                            text4.setText(msg4);
                            bullet4.setVisibility(View.VISIBLE);
                        }

                        Button dismissbtn = (Button) dialog.findViewById(R.id.dismiss_btn);
                        dismissbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        //dialog.show();
                        if(!((Activity) SplashScreen.this).isFinishing())
                        {
                            dialog.show();
                        }

                    }
                    else if (get_status.equalsIgnoreCase("0")){
                        Toast.makeText(SplashScreen.this,"Something went wrong !!", Toast.LENGTH_SHORT).show();
                    }

                } });

        }

    }

    /*
    @Override
    protected void onRestart() {
        new GetShopGroup().execute();
        super.onRestart();
    }
    */


    /*
    @Override
    protected void onResume() {

        super.onResume();

        Log.d("ON RESUME","called onresume");

        //new GetShopGroup().execute();
    }
    */

}