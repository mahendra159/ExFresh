package com.exfresh.exfreshapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.GCMRegistrar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.exfresh.exfreshapp.CommonUtilities.SENDER_ID;
import static com.exfresh.exfreshapp.CommonUtilities.SERVER_URL;

/**
 * Created by Mahendra on 3/23/2015.
 */
public class Category extends ActionBarActivity {


    public static int mNotificationsCount = 0;

    XMLParser parser = new XMLParser();

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    String MAIN_URL;
    String CAT_URL;
    String URL_PHP;
    String URL_GET_CATEGORY;
    String CUST_MSG_POST_URL;

    String CART_ID;
    String SHOP_ID;

    String E_MAIL ;
    String CS_MESSAGE ;
    String ORDER_REF;

    ListView catlist;
    CategoryAdapter adapter;

    Button mycartbtn;
    Button myaccbtn;
    Button offersbtn;


    // Connection detector
    ConnectionDetector cd;

    // Progress Dialog
    private ProgressDialog pDialog;

    private ShareActionProvider mShareActionProvider;

    String cart_flag;
    String cart_item_counter;

    String DEFAULT_USER;

    // Asyntask
    AsyncTask<Void, Void, Void> mRegisterTask;

    public static String name;
    public static String email;

    // products JSONArray
    JSONArray category_ids = null;

    // Creating JSON Parser object
    JSONParser_cart jParser = new JSONParser_cart();

    ArrayList<HashMap<String, String>> categoryList;

    private static final String TAG_CATEGORIES = "categories";
    static final String TAG_CATEGORY_ID = "id_category";
    static final String TAG_CATEGORY_NAME = "name";
    static final String TAG_CART_COUNT = "item_count";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SUCCESS_CART_COUNT = "success_cart_count";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        cd = new ConnectionDetector(Category.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        //getSupportActionBar().setIcon(R.drawable.ico_actionbar);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();


        MAIN_URL = preferences.getString("Pref_main_url", null);
        CART_ID = preferences.getString("Pref_Cart_Id", null);
        SHOP_ID = preferences.getString("Pref_shop_id",null);

        CAT_URL = MAIN_URL.concat("/api/categories/?display=full");

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_GET_CATEGORY = URL_PHP.concat("Get_Category_Cart.php");
        CUST_MSG_POST_URL = URL_PHP.concat("contact_us_mail.php");


        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);

        mycartbtn = (Button)findViewById(R.id.bMyCart);
        myaccbtn = (Button)findViewById(R.id.bMyAccount);
        offersbtn = (Button)findViewById(R.id.boffers);

       cart_flag = preferences.getString("Pref_Cart_Flag",null);

        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Category.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent i= new Intent(Category.this,My_CartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(Category.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
            }
        };

        View.OnClickListener listnr_myacc=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Category.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(Category.this,My_AccountActivity.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        View.OnClickListener listnr_offers=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Category.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                    Intent k= new Intent(Category.this,OffersActivity.class);
                    startActivity(k);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        };

        mycartbtn.setOnClickListener(listnr);
        myaccbtn.setOnClickListener(listnr_myacc);
        offersbtn.setOnClickListener(listnr_offers);


        categoryList = new ArrayList<HashMap<String, String>>();

        new GetCategory().execute();

        new FetchCountTask().execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);

        SearchManager searchManager = (SearchManager)
        getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        MenuItem item = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        // Update LayerDrawable's BadgeDrawable
        Utils2.setBadgeCount(this, icon, mNotificationsCount);

        return true;
    }



    // this method is used for displaying icon along with text in menu
/*
    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e("TAG", "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }
    */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        // toggle nav drawer on selecting action bar app icon/title

        int id = item.getItemId();

        switch (id) {
            case R.id.chkout:
                if (!cd.isConnectingToInternet()) {
                    alert.showAlertDialog(Category.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }

                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent k= new Intent(Category.this,CheckoutActivity.class);
                    startActivity(k);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(Category.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.logout:

                new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Log Out")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                editor.clear();
                                editor.commit();
                                Toast.makeText(Category.this, "Logged Out Successfully", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(Category.this, SplashScreen.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                }, 1400);

                            }
                        }).setNegativeButton("No", null).show();

                return true;


            case R.id.media:

                Intent media_reports= new Intent(Category.this,MediaReports.class);
                startActivity(media_reports);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                return true;



            case R.id.action_search:

                onSearchRequested();

                return true;


            case R.id.contact_us:

                final Dialog dialog = new Dialog(Category.this);
                dialog.setContentView(R.layout.dialog_contact_us);
                dialog.setTitle("Contact us");
                dialog.setCancelable(true);

                final EditText email = (EditText) dialog.findViewById(R.id.email_txt);
                final EditText cs_msg = (EditText) dialog.findViewById(R.id.cs_msg_txt);
                final EditText order_ref = (EditText) dialog.findViewById(R.id.order_ref_txt);


                Button sendbtn = (Button) dialog.findViewById(R.id.send_btn);
                sendbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        E_MAIL = email.getText().toString();
                        CS_MESSAGE = cs_msg.getText().toString();
                        ORDER_REF = order_ref.getText().toString();

                        if (!isValidEmail(E_MAIL)) {
                            email.setError("Invalid Email");
                        }
                        if (E_MAIL.length()==0) {
                            email.setError("Email cannot be blank");
                        }



                        if (CS_MESSAGE.length()==0) {
                            cs_msg.setError("Message cannot be blank");
                        }

                        if ( E_MAIL.length() > 0 && isValidEmail(E_MAIL)==true && CS_MESSAGE.length()> 0 ) {

                            new Contact_Us().execute();

                            dialog.dismiss();
                        }



                    }
                });
                dialog.show();

                return true;

            case R.id.about_us:

                Intent abt_us= new Intent(Category.this,AboutUs.class);
                startActivity(abt_us);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            case R.id.action_notifications:

                if (mNotificationsCount>0){
                Intent intent_cart = new Intent(Category.this, My_CartActivity.class);
                startActivity(intent_cart);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(Category.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_share:

                try
                {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Transform the way you consume fruits, vegetables & other eatables");
                    String sAux = "\n\nDownload Now  - \nExFresh - Safe & Certified\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=com.exfresh.exfreshapp \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "Choose One"));
                }
                catch(Exception e)
                { //e.toString();
                }

                return true;

            case R.id.privacy_policy:

                Intent pp= new Intent(Category.this,PrivacyPolicy.class);
                startActivity(pp);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public String getFileContent(final File file) throws IOException {
        final InputStream inputStream = new FileInputStream(file);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder stringBuilder = new StringBuilder();

        boolean done = false;
        while (!done) {
            try {
                final String line = reader.readLine();
                done = (line == null);

                if (line != null) {
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }


    /*
Updates the count of notifications in the ActionBar.
 */
    public void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        //ActivityCompat.invalidateOptionsMenu(this);
        invalidateOptionsMenu();
    }

    /*
Sample AsyncTask to fetch the notifications count
*/
    class FetchCountTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {
            // example count. This is where you'd
            // query your data store for the actual count.
            return mNotificationsCount;
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
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

   /*
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        Category.this.finish();
    }
    */

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        cart_flag = preferences.getString("Pref_Cart_Flag",null);
        updateNotificationsBadge(Category.mNotificationsCount);

    }


    class Contact_Us extends AsyncTask<String, String, String> {
        String status;
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Category.this);
            pDialog.setTitle("Contact Us");
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", E_MAIL ));
            params.add(new BasicNameValuePair("msg",CS_MESSAGE  ));
            params.add(new BasicNameValuePair("order_ref",ORDER_REF  ));

            // getting JSON string from URL
            JSONObject json_mail = jParser.makeHttpRequest(CUST_MSG_POST_URL, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_mail.getInt(TAG_SUCCESS);

                if (success == 1) {
                    status = "1";
                } else {
                    status ="0";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog.dismiss();

            if (status.equalsIgnoreCase("1")){
                alert.showAlertDialog(Category.this, "Message Sent", "Thanks for writing to us. Our team will contact you soon", false);
            }
            else{
                alert.showAlertDialog(Category.this, "Message Send Error", "Something went wrong. Please try again !!", false);
            }
        }
    }



    public class GetCategory extends AsyncTask<String, Void, String> {

        private ProgressDialog pDialog;

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Category.this);
            pDialog.setTitle("Retrieving Categories");
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... params) {

            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("id_shop", SHOP_ID));
            params2.add(new BasicNameValuePair("id_cart", CART_ID));

            JSONObject json_category = jParser.makeHttpRequest(URL_GET_CATEGORY, "POST", params2);

            if (json_category != null){
                try {
                    int success = json_category.getInt(TAG_SUCCESS);

                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        category_ids = json_category.getJSONArray(TAG_CATEGORIES);

                        int success_cart_count = json_category.getInt(TAG_SUCCESS_CART_COUNT);

                        if (success_cart_count == 1) {
                            cart_item_counter = json_category.getString(TAG_CART_COUNT);
                            mNotificationsCount = Integer.parseInt(cart_item_counter);
                            updateNotificationsBadge(mNotificationsCount);
                            if (MyDebug.LOG) {
                                Log.d("Cart Counter ", cart_item_counter);
                            }
                        }

                        // looping through all categories
                        for (int i = 0; i < category_ids.length(); i++) {
                            JSONObject c = category_ids.getJSONObject(i);

                            // Storing each json item in variable
                            String Category_Id = c.getString(TAG_CATEGORY_ID);
                            String Category_Name = c.getString(TAG_CATEGORY_NAME);

                            // creating new HashMap
                            HashMap<String, String> map2 = new HashMap<String, String>();

                            // adding each child node to HashMap key => value
                            map2.put(TAG_CATEGORY_ID, Category_Id);
                            map2.put(TAG_CATEGORY_NAME, Category_Name);

                            // adding HashList to ArrayList
                            categoryList.add(map2);
                            if (MyDebug.LOG) {
                                Log.d("Category List " ,"is"+categoryList);
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
             }
         return null;

        }


        @Override
        protected void onPostExecute(final String xml) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    int pr_len = categoryList.size();
                    if (pr_len == 0) {
                        Toast.makeText(Category.this, "No Categories available ...", Toast.LENGTH_SHORT).show();





                    } else {
                        catlist = (ListView) findViewById(R.id.category_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new CategoryAdapter(Category.this, categoryList);
                        catlist.setAdapter(adapter);

                        catlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                // Check for internet connection
                                if (!cd.isConnectingToInternet()) {
                                    // Internet Connection is not present
                                    alert.showAlertDialog(Category.this, "Internet Connection Error",
                                            "Please connect to working Internet connection", false);
                                    return;
                                }
                                final Intent GoToDisplay = new Intent(Category.this, Products.class);
                                TextView clickedTextView = (TextView) view.findViewById(R.id.category_id);
                                TextView cat_name = (TextView) view.findViewById(R.id.category_name);
                                GoToDisplay.putExtra("categ_id", clickedTextView.getText().toString());
                                GoToDisplay.putExtra("category_name", cat_name.getText().toString());
                                startActivity(GoToDisplay);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                        });
                    }

                } });

        }
    }




 }