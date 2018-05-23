package com.exfresh.exfreshapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mahendra on 4/2/2015.
 */
public class CheckoutActivity extends ActionBarActivity {

    public static int mNotificationsCount = 0;

    public int Total_Items =0;

    public int Total_Price = 0;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public String C_Cart_Id;

    String CART_FLAG = "Pref_Cart_Flag";

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser_cart jParser_gt_crt = new JSONParser_cart();

    ArrayList<HashMap<String, String>> productsList;


    String MAIN_URL;
    String PRODUCT_URL;
    String ATTRIB_URL;

    String URL_PHP;
    String URL_GET_PRODUCT;
    String URL_GET_PRODUCT2;
    String URL_CHECK_IN_ORDER;
    String URL_EMPTY_CART;

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";

     static final String TAG_ID_PRODUCT = "id_product";
     static final String TAG_QUANTITY = "quantity";
     static final String TAG_ID_PRODUCT_ATTRIBUTE = "id_product_attribute";

     static final String TAG_PRODUCT_NAME = "name";
     static final String TAG_ID_ATTRIBUTE = "id_attribute";
     static final String TAG_ATTRIBUTE_NAME = "attribute_name";
     static final String TAG_IMPACT = "impact";
     static final String TAG_BASE_PRICE = "base_price";
     static final String TAG_FINAL_PRICE = "final_price";

    static final String TAG_IMAGE_ID="id_image";

    static final String TAG_IMAGE_URL = "image";

    JSONArray products = null;

    static final String KEY_TOTAL_PRICE = "totalprice";
    static final String KEY_TOTAL_ITEMS = "totalitems";

    public static int deletion =2 ;


    ListView list;
    ListView list2;
    CheckoutAdapter adapter;
    CheckoutShowPriceAdapter total_price_adapter;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    Button myhomebtn;
    Button myaccbtn;
    Button chkoutbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());


        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();


        myhomebtn = (Button)findViewById(R.id.bHome);
        myaccbtn = (Button)findViewById(R.id.bMyAccount);
        chkoutbtn = (Button)findViewById(R.id.bchkout2);


        MAIN_URL = preferences.getString("Pref_main_url", null);
        ATTRIB_URL = MAIN_URL.concat("/api/combinations/");
        PRODUCT_URL = MAIN_URL.concat("/api/products?display=full");

        URL_PHP = preferences.getString("Pref_url_php", null);

        URL_GET_PRODUCT = URL_PHP.concat("Get_Product.php");
        URL_GET_PRODUCT2 = URL_PHP.concat("Get_Product2.php");
        URL_CHECK_IN_ORDER = URL_PHP.concat("Check_Cart_In_PSORDER.php");
        URL_EMPTY_CART = URL_PHP.concat("Empty_Cart.php");

        View.OnClickListener listnr_home=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cd.isConnectingToInternet()) {
                    alert.showAlertDialog(CheckoutActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(CheckoutActivity.this,Category.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };


        View.OnClickListener listnr_myaccnt=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cd.isConnectingToInternet()) {
                    alert.showAlertDialog(CheckoutActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(CheckoutActivity.this,My_AccountActivity.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };


        View.OnClickListener listnr_chkout=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        Intent m= new Intent(CheckoutActivity.this,CheckOutAddressActivity.class);
                        String send_total_price = String.valueOf(Total_Price);
                        m.putExtra("Total_Order_Price", send_total_price);
                        startActivity(m);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        };

        myhomebtn.setOnClickListener(listnr_home);
        myaccbtn.setOnClickListener(listnr_myaccnt);
        chkoutbtn.setOnClickListener(listnr_chkout);

        String Cart_Id = preferences.getString("Pref_Cart_Id", null);
        C_Cart_Id=Cart_Id;

        productsList = new ArrayList<HashMap<String, String>>();

        new GetProducts().execute();

        new FetchCountTask().execute();
    }




    class GetProducts extends AsyncTask<String, String, String> {

       TextView Total_Price_Text = (TextView)findViewById(R.id.Total_Price);
       TextView Total_Items_Text = (TextView)findViewById(R.id.Total_Items);

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(CheckoutActivity.this);
            pDialog.setTitle("Order Summary");
            pDialog.setMessage("Loading !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cart", C_Cart_Id));

            JSONObject json_ps_order_check = jParser_gt_crt.makeHttpRequest(URL_CHECK_IN_ORDER, "POST", params);

            try{
                int success_pre = json_ps_order_check.getInt(TAG_SUCCESS);
                if (success_pre == 1) {
                    try {
                        JSONObject json_cart = jParser_gt_crt.makeHttpRequest(URL_GET_PRODUCT2, "POST", params);
                        int success = json_cart.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            products = json_cart.getJSONArray(TAG_PRODUCTS);

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);

                                String Image_Id = c.getString(TAG_IMAGE_ID);
                                String Image_Url = MAIN_URL.concat("/api/images/products/").concat(c.getString(TAG_ID_PRODUCT).concat("/").concat(Image_Id));

                                HashMap<String, String> map2 = new HashMap<String, String>();

                                map2.put(TAG_ID_PRODUCT, c.getString(TAG_ID_PRODUCT));
                                map2.put(TAG_QUANTITY, c.getString(TAG_QUANTITY));
                                map2.put(TAG_ID_PRODUCT_ATTRIBUTE, c.getString(TAG_ID_PRODUCT_ATTRIBUTE));

                                map2.put(TAG_PRODUCT_NAME,c.getString(TAG_PRODUCT_NAME) );
                                map2.put(TAG_ID_ATTRIBUTE,c.getString(TAG_ID_ATTRIBUTE) );
                                map2.put(TAG_ATTRIBUTE_NAME,c.getString(TAG_ATTRIBUTE_NAME));
                                map2.put(TAG_IMPACT, c.getString(TAG_IMPACT));
                                map2.put(TAG_BASE_PRICE,c.getString(TAG_BASE_PRICE) );
                                map2.put(TAG_FINAL_PRICE,c.getString(TAG_FINAL_PRICE) );
                                map2.put(TAG_IMAGE_URL,Image_Url);


                                productsList.add(map2);
                                mNotificationsCount = productsList.size();
                                Total_Items = productsList.size();
                                String pr_price = c.getString(TAG_FINAL_PRICE);
                                String pr_price_trimd = pr_price.substring(0, pr_price.length() - 4);
                                double pr_price_trimd_temp = Double.parseDouble(pr_price_trimd);
                                int pr_price_trimd_int = (int)pr_price_trimd_temp;
                                Total_Price = Total_Price + pr_price_trimd_int;
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            catch(JSONException e)
            {e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {

                    int pr_len = productsList.size();
                    if (pr_len == 0) {
                    } else
                    {

                       Total_Items_Text.setText("Total Items - "+String.valueOf(Total_Items));
                       Total_Price_Text.setText("Total Price - Rs."+String.valueOf(Total_Price));
                        list = (ListView) findViewById(R.id.product_list);

                        adapter = new CheckoutAdapter(CheckoutActivity.this, productsList);
                        list.setAdapter(adapter);
                    }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checkout, menu);

        MenuItem item = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        Utils2.setBadgeCount(this, icon, mNotificationsCount);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_empty_cart:
                int IS_CART_EMPTY2=0;
                for (HashMap<String, String> map : productsList)
                    for (Map.Entry<String, String> entry : map.entrySet())
                        IS_CART_EMPTY2=IS_CART_EMPTY2+1;

                if (IS_CART_EMPTY2!=0) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    new EmptyCart().execute();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    dialog.dismiss();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Empty Cart ?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("Cancel", dialogClickListener).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Cart is already empty", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.action_search:
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    class EmptyCart extends AsyncTask<String, String, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CheckoutActivity.this);
            pDialog.setTitle("Cart");
            pDialog.setMessage("Emptying Cart !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected Integer doInBackground(String... args)
        {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cart", C_Cart_Id));

            JSONObject json_cart = jParser_gt_crt.makeHttpRequest(URL_EMPTY_CART, "POST", params);

            try {
                int success = json_cart.getInt(TAG_SUCCESS);

                if (success == 1) {
                    LazyAdapter_Cart rt = new LazyAdapter_Cart();
                    rt.Tot_amount=null;
                    rt.Tot_items=null;

                    editor.remove(CART_FLAG).apply();

                    deletion = 1;

                } else {
                    deletion=0;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return deletion;
        }


        protected void onPostExecute(final Integer deletion) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    if (deletion==1){

                        Toast.makeText(getApplicationContext(), "Emptying Cart Successful. Taking you to home ...", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent j = new Intent(getApplicationContext(), Category.class);
                                startActivity(j);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                CheckoutActivity.this.finish();
                            }
                        }, 1400);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Emptying Cart Failed ...", Toast.LENGTH_SHORT).show();
                    }
                }

            } );
        }

    }

    /*
    Updates the count of notifications in the ActionBar.
     */
    private void updateNotificationsBadge(int count) {
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
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }

}
