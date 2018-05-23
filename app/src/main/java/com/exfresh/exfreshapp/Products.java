package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Mahendra on 3/31/2015.
 */
public class Products extends ActionBarActivity implements LazyAdapter.customButtonListener {

    public static int mNotificationsCount = 0;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ShareActionProvider mShareActionProvider;

    // Connection detector
    ConnectionDetector cd;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    public int status_flag;

    // Progress Dialog
    private ProgressDialog pDialog;

    // products JSONArray
    JSONArray product_ids = null;

    JSONArray attribute_ids = null;

    // Creating JSON Parser object
    JSONParser_cart jParser = new JSONParser_cart();

    ArrayList<Multimap<String, String>> ProductsList;

    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_ATTRIBUTES = "attributes";

    static final String TAG_ID_PRODUCT = "id_product";
    static final String TAG_PRODUCT_NAME = "name";
    static final String TAG_Price="price";
    static final String TAG_IMAGE_ID="id_image";

    static final String TAG_ID_PRODUCT_ATTRIBUTE = "id_product_attribute";
    static final String TAG_PRICE_IMPACT = "impact";
    static final String TAG_ID_ATTRIBUTE ="id_attribute";
    static final String TAG_ATTRIBUTE_NAME="attribute_name";
    static final String TAG_IMAGE_URL = "image";

    static final String TAG_CART_ID = "cart_id";
    static final String TAG_CART_COUNT = "item_count";

    static final String TAG_ITEM_QTY_IN_CART="item_qty_cart";


    public String C_Cust_Id;
    String Id_Product;
    String Category_Id_pul_frm_Cat;
    String Category_Name;
    String Cart_Id;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SUCCESS_CART_COUNT = "success_cart_count";
    private static final String TAG_STOCK_AVAIL = "stock";
    private static final String TAG_QUANTITY_AVAILABLE = "quantity_available";

    private static final String TAG_COUNT = "total_products";
    private static final String TAG_SUCCESS_PRO_COUNT = "success_count";

    private static final String TAG_SUCCESS_PR_IN_CART = "success_pr_count_in_cart";
    private static final String TAG_PR_IN_CART = "pr_count_in_cart";

    ListView list;
    LazyAdapter adapter;

    String KEY_CART_FLAG = "Pref_Cart_Flag";

    String cart_item_counter;

    String Id_Shop;
    String Id_Shop_Group;

    public static String Id_Product_Attribute ;
    public static String Impact ;
    public static String Id_Attribute ;
    public static String Attribute_Name ;

    String cart_flag;

    String DEFAULT_USER;

    Button homebtn;
    Button mycartbtn;
    Button offersbtn;

    String MAIN_URL;
    String URL_PHP;
    String PRODUCT_URL;
    String ATTRIBUTE_URL;
    String URL_GET_CART_ID;
    String URL_PRODUCT_INSUPDT;
    String TOTAL_COUNT_URL;

    String Add_Customer_Id;
    String Add_Product_Id ;
    String Add_Attribute_Id ;
    String Add_Product_Quantity;
    String Add_Id_Shop;

    String KEY_CART_ID = "Pref_Cart_Id";

    private TextView title;
    private TextView no_product_disp;
    private TextView no_product_disp2;

    private ArrayList<String> data;
    ArrayAdapter<String> sd;


    public int TOTAL_LIST_ITEMS = 0;
    public int NUM_ITEMS_PAGE   = 5;

    private int noOfBtns;
    private Button[] btns;

    int first_time_load=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        title	 = (TextView)findViewById(R.id.title);
        no_product_disp = (TextView)findViewById(R.id.no_product_disp);
        no_product_disp2 = (TextView)findViewById(R.id.no_product_disp2);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        Category_Id_pul_frm_Cat = extras.getString("categ_id");
        Category_Name = extras.getString("category_name");

        setTitle(Category_Name);

        /*
        if (Category_Id_pul_frm_Cat.equalsIgnoreCase("13")||Category_Id_pul_frm_Cat.equalsIgnoreCase("17")){

            //set up dialog
            final Dialog dialog = new Dialog(Products.this, R.style.ThemeWithCorners);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_usage);

            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            TextView text = (TextView) dialog.findViewById(R.id.TextView01);
            if(Category_Id_pul_frm_Cat.equalsIgnoreCase("13")) {
                text.setText("Certification of Fruits is in progress. Very soon, certified & safe fruits will be available to our esteemed customers.");
            }
            else if (Category_Id_pul_frm_Cat.equalsIgnoreCase("17")){
                text.setText("Organic certification is in progress. Very soon, certified organic products will be available to our esteemed customers at very competitive price.");
            }
            //set up button
            Button button = (Button) dialog.findViewById(R.id.Button01);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent i= new Intent(Products.this,Category.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            });


            //now that the dialog is set up, it's time to show it
            dialog.show();

            return;
        }
        */

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        cd = new ConnectionDetector(getApplicationContext());

        // Got cust_id from SharedPreferences
        C_Cust_Id = preferences.getString("Pref_Customer_Id", null);
        cart_flag = preferences.getString("Pref_Cart_Flag",null);

        MAIN_URL = preferences.getString("Pref_main_url", null);
        URL_PHP = preferences.getString("Pref_url_php", null);
        PRODUCT_URL = URL_PHP.concat("Master_Product.php");
        ATTRIBUTE_URL = URL_PHP.concat("Master_Attribute.php");
        URL_GET_CART_ID = URL_PHP.concat("Get_Cart_Id.php");
        URL_PRODUCT_INSUPDT = URL_PHP.concat("update_product.php");
        TOTAL_COUNT_URL = URL_PHP.concat("List_View_Pagination_Count.php");

        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);
        Id_Shop = preferences.getString("Pref_shop_id", null);
        Id_Shop_Group = preferences.getString("Pref_shop_group_id", null);

        Cart_Id = preferences.getString("Pref_Cart_Id", null);


        mycartbtn = (Button)findViewById(R.id.bMyCart);
        homebtn = (Button)findViewById(R.id.bHome);
        offersbtn = (Button)findViewById(R.id.boffers);

        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Products.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(preferences.getString("Pref_Cart_Flag",null)!=null && preferences.getString("Pref_Cart_Flag",null).equals("1")){
                    Intent i= new Intent(Products.this,My_CartActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }


            }
        };

        View.OnClickListener listnr_home=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Products.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(Products.this,Category.class);
                startActivity(j);
            }
        };

        View.OnClickListener listnr_offers=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(), "Offers are about to launch", Toast.LENGTH_SHORT).show();
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Products.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent k= new Intent(Products.this,OffersActivity.class);
                startActivity(k);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        };

        mycartbtn.setOnClickListener(listnr);
        homebtn.setOnClickListener(listnr_home);
        offersbtn.setOnClickListener(listnr_offers);

        // Hashmap for ListView
        ProductsList = new ArrayList<Multimap<String, String>>();


        // Loading products in Background Thread
        // new GetTotalCount().execute();

        // Btnfooter();

        new GetProduct().execute(0);

       // CheckBtnBackGroud(0);

        new FetchCountTask().execute();

    }

    //for pagination
    private void Btnfooter()
    {
        int val = TOTAL_LIST_ITEMS%NUM_ITEMS_PAGE;   //total_list_items = 47     num_items_page = 7   remainder = 0 i.e val = 0
        val = val==0?0:1;  //val =1
        noOfBtns=TOTAL_LIST_ITEMS/NUM_ITEMS_PAGE+val;

        LinearLayout ll = (LinearLayout)findViewById(R.id.btnLay);

        btns	=new Button[noOfBtns];

        for(int i=0;i<noOfBtns;i++)
        {
            btns[i]	=	new Button(this);
            btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
            btns[i].setText(""+(i+1));

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ll.addView(btns[i], lp);

            final int j = i;
            btns[j].setOnClickListener(new View.OnClickListener() {

                public void onClick(View v)
                {
                    //loadList(j);
                    ProductsList.clear();
                    adapter.notifyDataSetChanged();
                    new GetProduct().execute(j);
                    CheckBtnBackGroud(j);
                }
            });
        }

    }

    /**
     * Method for Checking Button Backgrounds
     */
    private void CheckBtnBackGroud(int index)
    {
        title.setText("Page "+(index+1)+" of "+noOfBtns);
        for(int i=0;i<noOfBtns;i++)
        {
            if(i==index)
            {
                //btns[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_btn_check_material));
                btns[i].setTextColor(getResources().getColor(android.R.color.holo_red_light));
            }
            else
            {
                //btns[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
                btns[i].setTextColor(getResources().getColor(R.color.light_grey));
            }
        }

    }



    //getting toal count
    class GetTotalCount extends AsyncTask<Integer, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            //pDialog = new ProgressDialog(Products.this);
            //pDialog.setTitle(Category_Name);
            //pDialog.setMessage("Loading...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        protected String doInBackground(Integer... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_category", Category_Id_pul_frm_Cat));
            params.add(new BasicNameValuePair("id_cart", Cart_Id));
            params.add(new BasicNameValuePair("id_shop", Id_Shop));

            JSONObject json_total = jParser.makeHttpRequest(TOTAL_COUNT_URL, "POST", params);

            try {
                int success = json_total.getInt(TAG_SUCCESS);

                if (success == 1) {
                    TOTAL_LIST_ITEMS = json_total.getInt(TAG_COUNT);
                    if (MyDebug.LOG) {
                        Log.d("total count fetch", String.valueOf(TOTAL_LIST_ITEMS));
                    }

                } else {

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

                    Btnfooter();

                    CheckBtnBackGroud(0);

                } });

        }

    }

    // getting total count ends

    // Background Async Task to fetch order_ids corresponding to customer ids
    class GetProduct extends AsyncTask<Integer, String, String> {

        int help_display_check;
        int help_display_check2;
        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(Products.this);
            //pDialog.setTitle(Category_Name);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(Integer... args) {
            int start_count = args[0];
            help_display_check=start_count;

            //int start = start_count * NUM_ITEMS_PAGE;
            int start = start_count * NUM_ITEMS_PAGE;

                    // Building Parameters
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("id_category", Category_Id_pul_frm_Cat));
                params.add(new BasicNameValuePair("id_cart", Cart_Id));
                params.add(new BasicNameValuePair("id_shop", Id_Shop));
                params.add(new BasicNameValuePair("start_count", String.valueOf(start)));


                // getting JSON string from URL
                JSONObject json_order = jParser.makeHttpRequest(PRODUCT_URL, "POST", params);

                try {
                    // Checking for SUCCESS TAG
                    int success = json_order.getInt(TAG_SUCCESS);

                    int success_pro_count = json_order.getInt(TAG_SUCCESS_PRO_COUNT);
                    if (success_pro_count == 1) {
                        TOTAL_LIST_ITEMS = json_order.getInt(TAG_COUNT);
                        if (MyDebug.LOG) {
                            Log.d("total count fetch", String.valueOf(TOTAL_LIST_ITEMS));
                        }

                    }


                    if (success == 1) {
                        // products found
                        // Getting Array of Products
                        product_ids = json_order.getJSONArray(TAG_PRODUCTS);
                        if (MyDebug.LOG) {
                            Log.d("MainActivity", "Products from exfresh :" + product_ids);
                        }


                        //getting item count in cart
                        int success_cart_count = json_order.getInt(TAG_SUCCESS_CART_COUNT);

                        if (success_cart_count == 1) {
                            cart_item_counter = json_order.getString(TAG_CART_COUNT);
                            mNotificationsCount = Integer.parseInt(cart_item_counter);
                            updateNotificationsBadge(mNotificationsCount);
                            if (MyDebug.LOG) {
                                Log.d("Cart Counter ", cart_item_counter);
                            }
                        }


                        // int start = number * NUM_ITEMS_PAGE;

                        for (int i = 0; i < product_ids.length(); i++) {
                            JSONObject c = product_ids.getJSONObject(i);

                            Multimap<String, String> multiMap = ArrayListMultimap.create();

                            Id_Product = c.getString(TAG_ID_PRODUCT);
                            String Name = c.getString(TAG_PRODUCT_NAME);
                            String Price = c.getString(TAG_Price);
                            String Image_Id = c.getString(TAG_IMAGE_ID);
                            String Image_Url = MAIN_URL.concat("/api/images/products/").concat(Id_Product.concat("/").concat(Image_Id));

                            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                            params2.add(new BasicNameValuePair("id_product", Id_Product));
                            params2.add(new BasicNameValuePair("id_cart", Cart_Id));

                            JSONObject json_attb = jParser.makeHttpRequest(ATTRIBUTE_URL, "POST", params2);

                            try {
                                // Checking for SUCCESS TAG
                                int success2 = json_attb.getInt(TAG_SUCCESS);

                                if (success2 == 1) {
                                    attribute_ids = json_attb.getJSONArray(TAG_ATTRIBUTES);
                                    for (int j = 0; j < attribute_ids.length(); j++) {
                                        JSONObject d = attribute_ids.getJSONObject(j);

                                        Id_Product_Attribute = d.getString(TAG_ID_PRODUCT_ATTRIBUTE);
                                        Impact = d.getString(TAG_PRICE_IMPACT);
                                        Id_Attribute = d.getString(TAG_ID_ATTRIBUTE);
                                        Attribute_Name = d.getString(TAG_ATTRIBUTE_NAME);

                                        multiMap.put(TAG_ID_PRODUCT_ATTRIBUTE, Id_Product_Attribute);
                                        multiMap.put(TAG_PRICE_IMPACT, Impact);
                                        multiMap.put(TAG_ID_ATTRIBUTE, Id_Attribute);
                                        multiMap.put(TAG_ATTRIBUTE_NAME, Attribute_Name);

                                    }

                                } else {
                                }

                                // for getting count of product in respective cart
                                int success_chk_pr_in_cart = json_attb.getInt(TAG_SUCCESS_PR_IN_CART);

                                if (success_chk_pr_in_cart == 1) {
                                    String product_check_count_in_cart = json_attb.getString(TAG_PR_IN_CART);

                                        multiMap.put(TAG_ITEM_QTY_IN_CART,product_check_count_in_cart);

                                }else{
                                    multiMap.put(TAG_ITEM_QTY_IN_CART,"0");
                                }
                                // for getting count of product in respective cart ends

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            multiMap.put(TAG_ID_PRODUCT, Id_Product);
                            multiMap.put(TAG_PRODUCT_NAME, Name);
                            multiMap.put(TAG_Price, Price);
                            multiMap.put(TAG_IMAGE_URL, Image_Url);

                            ProductsList.add(multiMap);

                        }

                    } else {

                        // no products found
                        runOnUiThread(new Runnable() {
                            public void run() {
                                //Toast.makeText(getApplicationContext(), "Cart Empty ! Redirecting to Categories Menu ...", Toast.LENGTH_SHORT).show();
                                //thread.start();
                                //Intent i = new Intent(getApplicationContext(),Category.class);
                                //startActivity(i);
                            }

                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            return null;
        }



        //  After completing background task Dismiss the progress dialog


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    int pr_len = ProductsList.size();
                    help_display_check2=pr_len;
                    no_product_disp.setVisibility(View.GONE);
                    no_product_disp2.setVisibility(View.GONE);
                    if (pr_len == 0) {
                        //Toast.makeText(getApplicationContext(), "No Products ...", Toast.LENGTH_SHORT).show();


                        if (help_display_check==0){
                        HorizontalScrollView btnscroll = (HorizontalScrollView)findViewById(R.id.btnScrollView);
                        btnscroll.setVisibility(View.GONE);
                        title.setVisibility(View.GONE);}


                        if (Category_Id_pul_frm_Cat.equalsIgnoreCase("15")){

                            SpannableString ss = new SpannableString("To avail certified "+Category_Name+", sms/whatsapp '" +Category_Name+"' to 9828696426");
                            int ss_len = ss.length();
                            int ss_len_minus = ss_len-15;
                            int ct_len = Category_Name.length();
                            int ct_len_minus = ss_len_minus-ct_len;

                            ClickableSpan clickableSpan = new ClickableSpan() {
                                @Override
                                public void onClick(View textView) {
                                  Intent sendIntent = new Intent();
                                  sendIntent.setAction(Intent.ACTION_SEND);
                                  sendIntent.putExtra(Intent.EXTRA_TEXT, Category_Name);
                                  sendIntent.setType("text/plain");
                                  startActivity(sendIntent);

                                }
                            };
                            ss.setSpan(clickableSpan, ct_len_minus, ss_len_minus, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                            /*
                            no_product_disp.setMovementMethod(LinkMovementMethod.getInstance());
                            no_product_disp.setText(ss);

                            no_product_disp2.setText("Alternatively email us at cs@exfresh.co.in");

                            no_product_disp.setVisibility(View.VISIBLE);
                            no_product_disp2.setVisibility(View.VISIBLE);
                            */





                            final Dialog dialog = new Dialog(Products.this, R.style.ThemeWithCorners);
                            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_usage);

                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                            /*if(Category_Id_pul_frm_Cat.equalsIgnoreCase("13")) {
                                text.setText("Certification of Fruits is in progress. Very soon, certified & safe fruits will be available to our esteemed customers.");
                            }
                            else if (Category_Id_pul_frm_Cat.equalsIgnoreCase("17")){
                                text.setText("Organic certification is in progress. Very soon, certified organic products will be available to our esteemed customers at very competitive price.");
                            }*/
                            text.setText(ss+"\n\nAlternatively email us at cs@exfresh.co.in");
                            //set up button
                            Button button = (Button) dialog.findViewById(R.id.Button01);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent i= new Intent(Products.this,Category.class);
                                    startActivity(i);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                }
                            });


                            //now that the dialog is set up, it's time to show it
                            dialog.show();


                        }
                        else {

                            //no_product_disp.setText("No Products Available");
                           // no_product_disp.setText("Certification of " + Category_Name + " is in progress.\n\nVery soon, safe & certified " + Category_Name+" will be available to our esteemed customers.\n\nTill then please look for other available certified products.");
                           //         no_product_disp.setVisibility(View.VISIBLE);

                            //set up dialog
                            final Dialog dialog = new Dialog(Products.this, R.style.ThemeWithCorners);
                            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.dialog_usage);

                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                            TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                            /*if(Category_Id_pul_frm_Cat.equalsIgnoreCase("13")) {
                                text.setText("Certification of Fruits is in progress. Very soon, certified & safe fruits will be available to our esteemed customers.");
                            }
                            else if (Category_Id_pul_frm_Cat.equalsIgnoreCase("17")){
                                text.setText("Organic certification is in progress. Very soon, certified organic products will be available to our esteemed customers at very competitive price.");
                            }*/
                            text.setText("Certification of " + Category_Name + " is in progress.\n\nVery soon, safe & certified " + Category_Name+" will be available to our esteemed customers.\n\nTill then please look for other available certified products.");
                            //set up button
                            Button button = (Button) dialog.findViewById(R.id.Button01);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    Intent i= new Intent(Products.this,Category.class);
                                    startActivity(i);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                }
                            });


                            //now that the dialog is set up, it's time to show it
                            dialog.show();


                        }

                    } else {

                        if (first_time_load==0){
                        Btnfooter();
                        CheckBtnBackGroud(0);
                            first_time_load = first_time_load+1;
                        }

                        list = (ListView) findViewById(R.id.product_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new LazyAdapter(Products.this, ProductsList);
                        adapter.setCustomButtonListner(Products.this);
                        list.setAdapter(adapter);


                        final MyApplication globalVariable = (MyApplication) getApplicationContext();

                        int spin_check= globalVariable.getSpin_load_check();
                        if (help_display_check==0 && help_display_check2!=0 && spin_check==0) {

                            globalVariable.setSpin_load_check(1);
                        //first_time_load = first_time_load+1;
                        //spinner_load=spinner_load+1;



                        //set up dialog
                        final Dialog dialog = new Dialog(Products.this, R.style.ThemeWithCorners);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_usage);

                        //dialog.setTitle("How to order");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        //there are a lot of settings, for dialog, check them all out!
                        //set up text
                        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                        text.setText(R.string.usage);

                        //set up button
                        Button button = (Button) dialog.findViewById(R.id.Button01);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();

                                //finish();
                            }
                        });

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(!((Activity) Products.this).isFinishing())
                                {
                                    dialog.show();
                                }


                                //FinalCheckoutActivity.this.finish();
                            }
                        }, 6400);
                        //now that the dialog is set up, it's time to show it
                        //dialog.show();

                    }

                        // list.addHeaderView(OrderHistoryAdapter.header, null, false);


                    }

                } });

        }


        private Date convertDate(String date) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date d = format.parse(date);
                SimpleDateFormat convertedFormat = new SimpleDateFormat("dd/M/yyyy hh:mm:ss");
                return d;
                //return convertedFormat.format(d);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
            //return "";
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_products, menu);

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

    private void setShareIntent() {
        // BEGIN_INCLUDE(update_sap)
        if (mShareActionProvider != null) {
            // Get the currently selected item, and retrieve it's share intent
            //ContentItem item = mItems.get(position);
            Intent shareIntent = new Intent(Intent.ACTION_VIEW);
            shareIntent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.android.example"));
            startActivity(shareIntent);


            // Now update the ShareActionProvider with the new share intent
            mShareActionProvider.setShareIntent(shareIntent);
        }
        // END_INCLUDE(update_sap)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.chkout:
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Products.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    //return;
                }


                else if(preferences.getString("Pref_Cart_Flag",null)!=null && preferences.getString("Pref_Cart_Flag",null).equals("1")){
                    Intent k= new Intent(Products.this,CheckoutActivity.class);
                    startActivity(k);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                return true;


            case R.id.action_search:

                onSearchRequested();

                return true;



            case R.id.menu_share:

                try
                {
                 /*   Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.android.example"));
                    startActivity(intent);
                   */
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

            case R.id.action_notifications:

                if (mNotificationsCount>0){
                    Intent intent_cart = new Intent(Products.this, My_CartActivity.class);
                    startActivity(intent_cart);
                } else {
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                //    NavUtils.navigateUpFromSameTask(this);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
    Updates the count of notifications in the ActionBar.
     */
    public void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
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
            //return 5;
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }


    @Override
    public void onButtonClickListner(int position, String customer_id,String product_id ,String attribute_id, String product_quantity) {

        Add_Customer_Id = customer_id;
        Add_Product_Id = product_id;
        Add_Attribute_Id = attribute_id;
        Add_Product_Quantity = product_quantity;
        Add_Id_Shop = Id_Shop;

        new GetCartId().execute();

    }


    //Background Async Task to Load all product by making HTTP Request
    class GetCartId extends AsyncTask<String, String, String> {

        int quantity_difference;
        String post_status;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(Products.this);
            pDialog.setTitle("Processing");
            pDialog.setMessage("Adding/updating product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", Add_Customer_Id));
            params.add(new BasicNameValuePair("id_shop", Add_Id_Shop));
            params.add(new BasicNameValuePair("id_product", Add_Product_Id));
            params.add(new BasicNameValuePair("id_product_attribute", Add_Attribute_Id));

            JSONObject json_get_cart_id = jParser.makeHttpRequest(URL_GET_CART_ID, "POST", params);

            try {
                int success_stock = json_get_cart_id.getInt(TAG_STOCK_AVAIL);
                if (MyDebug.LOG) {
                    Log.d("success_stock", String.valueOf(success_stock));
                }
                if (success_stock == 1) {

                    //post_status = "555";

                    int quantity_avail = json_get_cart_id.getInt(TAG_QUANTITY_AVAILABLE);
                    int quantity_demanded = Integer.parseInt(Add_Product_Quantity);


                    if (quantity_avail >= quantity_demanded) {


                    int success = json_get_cart_id.getInt(TAG_SUCCESS);

                    // use existing cart and insert/update product quantity
                    if (success == 1) {
                        Cart_Id = json_get_cart_id.getString(TAG_CART_ID);
                        if (MyDebug.LOG) {
                            Log.d("CART ID from php", Cart_Id);
                            Log.d("Product ID", Add_Product_Id);
                            Log.d("Quantity", Add_Product_Quantity);
                            Log.d("ID Attrib", Add_Attribute_Id);
                            Log.d("Id Shop", Add_Id_Shop);
                        }

                        editor.putString(KEY_CART_ID, Cart_Id);
                        editor.commit();


                        // code for inserting /updating product
                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        params2.add(new BasicNameValuePair("id_cart", Cart_Id));
                        params2.add(new BasicNameValuePair("id_product", Add_Product_Id));
                        params2.add(new BasicNameValuePair("quantity", Add_Product_Quantity));
                        params2.add(new BasicNameValuePair("id_product_attribute", Add_Attribute_Id));
                        params2.add(new BasicNameValuePair("id_shop", Add_Id_Shop));

                        //JSONParser_cart r = new JSONParser_cart();
                        JSONObject json_insupdt = jParser.makeHttpRequest(URL_PRODUCT_INSUPDT, "POST", params2);


                        // check for success tag
                        try {
                            int success_insupt = json_insupdt.getInt(TAG_SUCCESS);
                            if (success_insupt == 10) {
                                if (MyDebug.LOG) {
                                    Log.d("Prduct Inserted", String.valueOf(success));
                                }
                                //editor.putString(CART_FLAG, "1");
                                //editor.commit();
                                post_status = "11";

                            } else if (success_insupt == 11) {
                                if (MyDebug.LOG) {
                                    Log.d("Prduct Updated", String.valueOf(success));
                                }
                                post_status = "111";
                            } else {
                                if (MyDebug.LOG) {
                                    Log.d("Prdct Insrt/Updte fail", String.valueOf(success));
                                }
                                post_status = "01";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // code end for product insert/update

                    } else {
                        // post new and get cart
                        String URL_POST_NEW_CART = MAIN_URL.concat("/api/carts?display=full&filter[id_customer]=[" + Add_Customer_Id + "]");

                        try {
                            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                            Document document = documentBuilder.newDocument();

                            //prestashop
                            Element rootElement = document.createElement("prestashop");
                            document.appendChild(rootElement);

                            //cart
                            Element cartElement = document.createElement("cart");
                            rootElement.appendChild(cartElement);

                            //ID Shop Group
                            Element id_shop_grpElement = document.createElement("id_shop_group");
                            cartElement.appendChild(id_shop_grpElement);
                            id_shop_grpElement.appendChild(document.createTextNode(Id_Shop_Group));

                            //ID Shop
                            Element id_shop_Element = document.createElement("id_shop");
                            cartElement.appendChild(id_shop_Element);
                            id_shop_Element.appendChild(document.createTextNode(Id_Shop));

                            //Currency ID
                            Element id_currencyElement = document.createElement("id_currency");
                            cartElement.appendChild(id_currencyElement);
                            id_currencyElement.appendChild(document.createTextNode("1"));

                            //Customer ID
                            Element id_customerElement = document.createElement("id_customer");
                            cartElement.appendChild(id_customerElement);
                            id_customerElement.appendChild(document.createTextNode(Add_Customer_Id));

                            //id_lang
                            Element id_langElement = document.createElement("id_lang");
                            cartElement.appendChild(id_langElement);
                            id_langElement.appendChild(document.createTextNode("1"));

                            //associations
                            Element associationsElement = document.createElement("associations");
                            cartElement.appendChild(associationsElement);

                            //cart_rows
                            Element cart_rowsElement = document.createElement("cart_rows");
                            associationsElement.appendChild(cart_rowsElement);
                            cart_rowsElement.setAttribute("nodeType", "cart_row");
                            cart_rowsElement.setAttribute("virtualEntity", "true");

                            //cart_row
                            Element cart_rowElement = document.createElement("cart_row");
                            cart_rowsElement.appendChild(cart_rowElement);

                            //id_product
                            Element id_productElement = document.createElement("id_product");
                            cart_rowElement.appendChild(id_productElement);
                            id_productElement.appendChild(document.createTextNode(Add_Product_Id));

                            //id_product_attribute
                            Element id_product_attribute_Element = document.createElement("id_product_attribute");
                            cart_rowElement.appendChild(id_product_attribute_Element);
                            id_product_attribute_Element.appendChild(document.createTextNode(Add_Attribute_Id));

                            //id_address_delivery
                            Element id_address_delivery_Element = document.createElement("id_address_delivery");
                            cart_rowElement.appendChild(id_address_delivery_Element);
                            id_address_delivery_Element.appendChild(document.createTextNode("0"));

                            //quantity
                            Element quantity_Element = document.createElement("quantity");
                            cart_rowElement.appendChild(quantity_Element);
                            quantity_Element.appendChild(document.createTextNode(Add_Product_Quantity));

                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            DOMSource source = new DOMSource(document.getDocumentElement());
                            StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/" + "file.xml"));
                            transformer.transform(source, result);
                            //****post your file to server*****
                            DefaultHttpClient httpClient = new DefaultHttpClient();
                            String URL_ps_cart = MAIN_URL.concat("/api/carts?schema=synopsis");
                            HttpPost httpost = new HttpPost(URL_ps_cart);
                            String WbSr_password = "";// leave it empty
                            String authToBytes = DEFAULT_USER + ":" + WbSr_password;
                            byte authBytes[] = Base64.encode(authToBytes.getBytes(), Base64.NO_WRAP);
                            String authBytesString = new String(authBytes);
                            httpost.setHeader("Authorization", "Basic " + authBytesString);
                            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                            File f = new File(filePath, "file.xml");
                            try {
                                String content = getFileContent(f);
                                StringEntity se = new StringEntity(content, HTTP.UTF_8);
                                se.setContentType("text/xml");
                                httpost.setEntity(se);
                                f.delete();
                                HttpResponse httpresponse = httpClient.execute(httpost);
                                int status_code = httpresponse.getStatusLine().getStatusCode();
                                int sta = status_code;
                                if (httpresponse.getStatusLine().getStatusCode() == 201) {
                                    if (MyDebug.LOG) {
                                        Log.d("response ok", "ok response :/");
                                        Log.d("Connect_post(0)", "OK");
                                    }
                                    post_status = "10";

                                    // fetch cart_id here and set to preferences
                                    JSONObject json_get_cart_id2 = jParser.makeHttpRequest(URL_GET_CART_ID, "POST", params);
                                    int success2 = json_get_cart_id2.getInt(TAG_SUCCESS);
                                    if (success2 == 1) {
                                        Cart_Id = json_get_cart_id2.getString(TAG_CART_ID);

                                        editor.putString(KEY_CART_ID, Cart_Id);
                                        editor.commit();
                                    }

                                } else {
                                    if (MyDebug.LOG) {
                                        Log.d("response not ok", "Something went wrong :/");
                                        Log.d("Connect_post(0)", "NOT OK");
                                    }
                                    post_status = "00";
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (ParserConfigurationException e) {
                        } catch (TransformerConfigurationException e) {
                        } catch (TransformerException e) {
                        }
                    }
                }else{
                        post_status="0001";
                        quantity_difference = quantity_demanded-quantity_avail;
                    }
            }
            else{
                    post_status = "000";// stock not avail
                }

                if (MyDebug.LOG) {
                    Log.d("post_status", post_status);
                }
            }// block of stock avail
            catch (JSONException e) {
                e.printStackTrace();
            }

            return post_status;
        }

        protected void onPostExecute(final String post_status) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    if (post_status.equals("000")) {
                        alert.showAlertDialog(Products.this, "Out of stock",
                                "Oops !! this product is currently out of stock", false);
                        return;
                    } else if (post_status.equals("0001")) {
                        alert.showAlertDialog(Products.this, "Information",
                                "Requested quantity not available, Please reduce quantity by "+quantity_difference, false);
                        return;
                    } else {
                        {
                            if (post_status.equals("11")) {
                                editor.putString(KEY_CART_FLAG, "1");
                                editor.commit();

                                Products.mNotificationsCount = Products.mNotificationsCount + 1;
                                updateNotificationsBadge(Products.mNotificationsCount);

                                Category.mNotificationsCount = mNotificationsCount;
                                Category notifyupdate_cat = new Category();
                                notifyupdate_cat.updateNotificationsBadge(notifyupdate_cat.mNotificationsCount);

                                SearchResultsActivity.mNotificationsCount = mNotificationsCount;
                                SearchResultsActivity notifyupdate_search = new SearchResultsActivity();
                                notifyupdate_search.updateNotificationsBadge(notifyupdate_search.mNotificationsCount);



                                Toast.makeText(Products.this, "Product added", Toast.LENGTH_LONG).show();
                            } else if (post_status.equals("01")) {
                                Toast.makeText(Products.this, "Product insertion/updation fails", Toast.LENGTH_LONG).show();
                            } else if (post_status.equals("111")) {
                                Toast.makeText(Products.this, "Quantity updated", Toast.LENGTH_LONG).show();
                                editor.putString(KEY_CART_FLAG, "1");
                                editor.commit();
                            } else if (post_status.equals("10")) {

                                Products.mNotificationsCount = Products.mNotificationsCount + 1;
                                updateNotificationsBadge(Products.mNotificationsCount);

                                Category.mNotificationsCount = mNotificationsCount;
                                Category notifyupdate_cat = new Category();
                                notifyupdate_cat.updateNotificationsBadge(notifyupdate_cat.mNotificationsCount);

                                editor.putString(KEY_CART_FLAG, "1");
                                editor.commit();

                                //cart_flag = preferences.getString("Pref_Cart_Flag",null);

                                Toast.makeText(Products.this, "Product added", Toast.LENGTH_SHORT).show();


                            } else if (post_status.equals("00")) {
                                // http post fail
                                Toast.makeText(Products.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }


                }
                }

                }
            });

        }
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

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        //cart_flag = preferences.getString("Pref_Cart_Flag",null);
        updateNotificationsBadge(Products.mNotificationsCount);

    }

}
