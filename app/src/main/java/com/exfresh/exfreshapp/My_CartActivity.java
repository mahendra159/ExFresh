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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
 * Created by Mahendra on 3/26/2015.
 */
public class My_CartActivity extends ActionBarActivity implements LazyAdapter_Cart.customButtonListener {

    int global_position_to_delete =0;

    public static int cart_counter;
    public static int mNotificationsCount = 0;

    public static int Total_Items =0;

    public static int Total_Price = 0;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    public String C_Cart_Id;

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser_cart jParser_gt_crt = new JSONParser_cart();

    ArrayList<HashMap<String, String>> productsList;
    ArrayList<HashMap<String, String>> Total_Price_List;

    ArrayList<HashMap<String, String>> tot_price_list;

    String MAIN_URL;
    String ATTRIBUTE_URL;
    String URL;
    String URL_PHP;
    String URL_GET_PRODUCT;
    String URL_GET_PRODUCT2;
    String URL_CHECK_IN_PS_ORDER;
    String URL_EMPTY_CART;
    String URL_PRODUCT_DELETE;

    String Del_Product_Id ;
    String Del_Attribute_Id ;
    String Del_Cart_Id;
    int Del_Minus_Price;

    String Update_Product_Id;
    String Update_Attribute_Id;
    String Update_Cart_Id;
    String Update_Product_Quantity;

    int updated_quantity;
    float Product_Base_Price;



    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCTS = "products";

    static final String TAG_ID_PRODUCT = "id_product";
    static final String TAG_QUANTITY = "quantity";
    static final String TAG_ID_PRODUCT_ATTRIBUTE = "id_product_attribute";
    static final String TAG_ID_ATTRIBUTE = "id_attribute";

    static final String TAG_PRODUCT_NAME = "name";

    static final String TAG_ATTRIBUTE_NAME = "attribute_name";
    static final String TAG_IMPACT = "impact";
    static final String TAG_BASE_PRICE = "base_price";
    static final String TAG_FINAL_PRICE = "final_price";
    static final String TAG_IMPACTED_UNIT_PRICE = "impacted_unit_price";

    static final String TAG_IMAGE_ID="id_image";

    static final String TAG_TOTAL_PRICE = "total_price";

    static final String KEY_PRODUCT_OPTION_VALUE_NAME = "language";

    String Attribute_Value;
    String Price_Impact;
    String Attribute_Id;

    // products JSONArray
    JSONArray products = null;
    JSONArray tot_price =null;

    // XML node keys
    static final String KEY_PRODUCT = "product"; //parent node
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_CONDITION = "condition";
    static final String KEY_PRICE = "price";
    static final String KEY_IMAGE_URL = "image";
    static final String KEY_CATEGORY = "id_category_default";
    static final String KEY_QUANTITY = "quantity";
    static final String KEY_ATTRIBUTE_VAL = "attribute_value_name";
    static final String KEY_ATTRIBUTE_ID = "attribute_id";
    static final String KEY_PRICE_IMPACT = "price_impact";


    static final String KEY_TOTAL_PRICE = "totalprice";
    static final String KEY_TOTAL_ITEMS = "totalitems";

    static final String KEY_PRODUCT_OPTION_VALUE = "product_option_value";

    static final String TAG_IMAGE_URL = "image";

    public static String tt_prce ;
    public static String tt_itms ;
    public static int deletion =2 ;


    ListView list;
    ListView list2;
    LazyAdapter_Cart adapter;
    ShowTotalPriceAdapter total_price_adapter;

    Button chkoutbtn;
    Button homebtn;
    Button mycartbtn;

    int pr_len=0;
    String CART_FLAG = "Pref_Cart_Flag";


    TextView Total_Price_Text;
    TextView Total_Items_Text;

    public String url_inc_dec_product;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        View view = findViewById(android.R.id.content);
        Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
        mLoadAnimation.setDuration(500);
        view.startAnimation(mLoadAnimation);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);



        Total_Price_Text = (TextView)findViewById(R.id.Total_Price);
        Total_Items_Text = (TextView)findViewById(R.id.Total_Items);

        cd = new ConnectionDetector(getApplicationContext());

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        String Cart_Id = preferences.getString("Pref_Cart_Id", null);
        final String CART_FLAG = preferences.getString("Pref_Cart_Flag", null);

        chkoutbtn = (Button)findViewById(R.id.bchkout);
        homebtn = (Button)findViewById(R.id.bHome);
        mycartbtn = (Button)findViewById(R.id.bMyCart);


        View.OnClickListener listnr_chkout=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(My_CartActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(CART_FLAG!=null && CART_FLAG.equals("1")){
                    Intent m = new Intent(My_CartActivity.this, CheckOutAddressActivity.class);
                    String send_total_price = String.valueOf(Total_Price);
                    //temporary sending total price from here
                    m.putExtra("Total_Order_Price", send_total_price);
                    startActivity(m);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
                    alert.showAlertDialog(My_CartActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(My_CartActivity.this,Category.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "You are in My Cart only !!", Toast.LENGTH_SHORT).show();
            }
        };

        chkoutbtn.setOnClickListener(listnr_chkout);
        homebtn.setOnClickListener(listnr_home);
        mycartbtn.setOnClickListener(listnr);

        C_Cart_Id=Cart_Id;

        MAIN_URL = preferences.getString("Pref_main_url", null);
        ATTRIBUTE_URL = MAIN_URL.concat("/api/combinations/");
        URL = MAIN_URL.concat("/api/products?display=full");

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_GET_PRODUCT = URL_PHP.concat("Get_Product.php");
        URL_GET_PRODUCT2 = URL_PHP.concat("Get_Product2.php");
        URL_CHECK_IN_PS_ORDER = URL_PHP.concat("Check_Cart_In_PSORDER.php");
        URL_EMPTY_CART = URL_PHP.concat("Empty_Cart.php");
        URL_PRODUCT_DELETE = URL_PHP.concat("delete_product.php");

        // Hashmap for ListView
        productsList = new ArrayList<HashMap<String, String>>();
        Total_Price_List= new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new GetProducts().execute();

        new FetchCountTask().execute();
    }




    //Background Async Task to Load all product by making HTTP Request
    class GetProducts extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(My_CartActivity.this);
            pDialog.setTitle("Cart");
            pDialog.setMessage("Loading !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cart", C_Cart_Id));

            JSONObject json_ps_order_check = jParser_gt_crt.makeHttpRequest(URL_CHECK_IN_PS_ORDER, "POST", params);

            try{
                int success_pre = json_ps_order_check.getInt(TAG_SUCCESS);

                if (success_pre == 1) {

                    try {
                        // getting JSON string from URL
                        JSONObject json_cart = jParser_gt_crt.makeHttpRequest(URL_GET_PRODUCT2, "POST", params);
                        // Checking for SUCCESS TAG
                        int success = json_cart.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            // products found
                            // Getting Array of Products
                            products = json_cart.getJSONArray(TAG_PRODUCTS);

                            // looping through All Products
                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);

                                String Image_Id = c.getString(TAG_IMAGE_ID);
                                String Image_Url = MAIN_URL.concat("/api/images/products/").concat(c.getString(TAG_ID_PRODUCT).concat("/").concat(Image_Id));

                                // creating new HashMap
                                HashMap<String, String> map2 = new HashMap<String, String>();

                                // adding each child node to HashMap key => value
                                map2.put(TAG_ID_PRODUCT, c.getString(TAG_ID_PRODUCT));
                                map2.put(TAG_QUANTITY, c.getString(TAG_QUANTITY));
                                map2.put(TAG_ID_PRODUCT_ATTRIBUTE, c.getString(TAG_ID_PRODUCT_ATTRIBUTE));

                                map2.put(TAG_PRODUCT_NAME,c.getString(TAG_PRODUCT_NAME) );
                                map2.put(TAG_ID_ATTRIBUTE,c.getString(TAG_ID_ATTRIBUTE) );
                                map2.put(TAG_ATTRIBUTE_NAME,c.getString(TAG_ATTRIBUTE_NAME));
                                map2.put(TAG_IMPACT, c.getString(TAG_IMPACT));
                                map2.put(TAG_BASE_PRICE,c.getString(TAG_BASE_PRICE) );
                                map2.put(TAG_IMPACTED_UNIT_PRICE,c.getString(TAG_IMPACTED_UNIT_PRICE) );
                                map2.put(TAG_FINAL_PRICE,c.getString(TAG_FINAL_PRICE) );
                                map2.put(TAG_IMAGE_URL,Image_Url);


                                // adding HashList to ArrayList
                                productsList.add(map2);
                                mNotificationsCount = productsList.size();
                                if (MyDebug.LOG) {
                                    Log.d("Products List ", ": " + productsList);
                                }

                                Total_Items = productsList.size();
                                String pr_price = c.getString(TAG_FINAL_PRICE);
                                String pr_price_trimd = pr_price.substring(0,pr_price.length()-4);
                            }
                            double temp = Double.parseDouble(json_cart.getString(TAG_TOTAL_PRICE));
                            Total_Price = (int)temp;
                        } else {
                            // no products found
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    //Toast.makeText(getApplicationContext(), "Cart Empty ! Redirecting to Categories Menu ...", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                else{

                }}catch(JSONException e){e.printStackTrace();}
            return null;
        }

        //  After completing background task Dismiss the progress dialog

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    pr_len = productsList.size();
                    cart_counter = productsList.size();
                    if (pr_len == 0) {
                        //Toast.makeText(getApplicationContext(), "Cart Empty ! Redirecting to Login Menu ...", Toast.LENGTH_SHORT).show();
                    } else
                    {

                        Total_Items_Text.setText("Total Items - "+String.valueOf(Total_Items));
                        Total_Price_Text.setText("Total Price - Rs."+String.valueOf(Total_Price));
                        list = (ListView) findViewById(R.id.product_list);

                        adapter = new LazyAdapter_Cart(My_CartActivity.this, productsList);
                        adapter.setCustomButtonListner(My_CartActivity.this);
                        list.setAdapter(adapter);
                    }
                } });
        }
    }

    //Background Async Task to Load all product by making HTTP Request
    class LoadTotalPrice extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        protected String doInBackground(String... args)
        {

            return null;
        }

        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                public void run() {

                    LazyAdapter_Cart rt = new LazyAdapter_Cart();
                    HashMap<String, String> map_tot = new HashMap<String, String>();

                    map_tot.put(KEY_TOTAL_PRICE, String.valueOf(Total_Price));
                    map_tot.put(KEY_TOTAL_ITEMS,String.valueOf(Total_Items));

                    Total_Price_List.add(map_tot);

                    list2 = (ListView) findViewById(R.id.listView2);

                    // Getting adapter by passing xml data ArrayList
                    total_price_adapter = new ShowTotalPriceAdapter(My_CartActivity.this, Total_Price_List);
                    list2.setAdapter(total_price_adapter);
                    ListUtils.setDynamicHeight(list2);

                } });
        }
    }


    public static class ListUtils {
        public static void setDynamicHeight(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
            for (int i = 0; i < listAdapter.getCount(); i++) {
                View listItem = listAdapter.getView(i, null, listView);
                if (listItem instanceof ViewGroup) {
                    listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);

        // Get the notifications MenuItem and
        // its LayerDrawable (layer-list)
        MenuItem item = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        // Update LayerDrawable's BadgeDrawable
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
                                    //Yes button clicked
                                    new EmptyCart().execute();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
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
            //return 5;
        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }



    class EmptyCart extends AsyncTask<String, String, Integer> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(My_CartActivity.this);
            pDialog.setTitle("Cart");
            pDialog.setMessage("Emptying Cart !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // deleting all products frm cart url
        protected Integer doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cart", C_Cart_Id));

            // getting JSON string from URL
            JSONObject json_cart = jParser_gt_crt.makeHttpRequest(URL_EMPTY_CART, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_cart.getInt(TAG_SUCCESS);

                if (success == 1) {
                    LazyAdapter_Cart rt = new LazyAdapter_Cart();
                    rt.Tot_amount=null;
                    rt.Tot_items=null;

                    editor.remove(CART_FLAG).apply();

                    deletion = 1;
                    // emptying cart successful

                } else {

                    deletion=0;
                    // emptying cart not successful

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return deletion;
        }

        //  After completing background task Dismiss the progress dialog
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
                                My_CartActivity.this.finish();
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



    @Override
    public void onButtonClickListner(int position, String product_id,String attribute_id,String cart_id, int minus_price) {
        Del_Product_Id = product_id;
        Del_Attribute_Id = attribute_id;
        Del_Cart_Id = cart_id;
        Del_Minus_Price = minus_price;
        global_position_to_delete=position;
        new DeleteProduct().execute();
    }

    //Background Async Task to Load all product by making HTTP Request
    class DeleteProduct extends AsyncTask<String, String, String> {
        String deleted;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(My_CartActivity.this);
            pDialog.setTitle("Deleting From Cart");
            pDialog.setMessage("Removing this product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cart", Del_Cart_Id));
            params.add(new BasicNameValuePair("id_product", Del_Product_Id));
            params.add(new BasicNameValuePair("id_product_attribute", Del_Attribute_Id));

            JSONObject json_delete = jParser_gt_crt.makeHttpRequest(URL_PRODUCT_DELETE, "POST", params);

            try{
                int success_pre = json_delete.getInt(TAG_SUCCESS);

                if (success_pre == 1) {
                    deleted = "1";
                }
                else{
                    deleted ="0";
                }
            }catch(JSONException e){
                e.printStackTrace();}

            return deleted;
        }

        protected void onPostExecute(String deleted) {
            pDialog.dismiss();
            final String del_success = deleted;

            runOnUiThread(new Runnable() {
                public void run() {

                    if (del_success.equalsIgnoreCase("1")){

                        int index_to_delete = global_position_to_delete-list.getFirstVisiblePosition();

                        Animation anim = AnimationUtils.loadAnimation(
                                My_CartActivity.this, android.R.anim.slide_out_right
                        );
                        anim.setDuration(500);

                        list.getChildAt(index_to_delete).startAnimation(anim);

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Product deleted", Toast.LENGTH_SHORT).show();

                                mNotificationsCount=mNotificationsCount-1;
                                updateNotificationsBadge(mNotificationsCount);

                                Products.mNotificationsCount=mNotificationsCount;
                                Products notifyupdate = new Products();
                                notifyupdate.updateNotificationsBadge(notifyupdate.mNotificationsCount);

                                Category.mNotificationsCount=mNotificationsCount;
                                Category notifyupdate_cat = new Category();
                                notifyupdate_cat.updateNotificationsBadge(notifyupdate_cat.mNotificationsCount);

                                SearchResultsActivity.mNotificationsCount = mNotificationsCount;
                                SearchResultsActivity notifyupdate_search = new SearchResultsActivity();
                                notifyupdate_search.updateNotificationsBadge(notifyupdate_search.mNotificationsCount);

                                productsList.remove(global_position_to_delete);
                                adapter.notifyDataSetChanged();
                                Total_Items=Total_Items-1;
                                Total_Items_Text.setText("Total Items - " + String.valueOf(Total_Items));

                                Total_Price= Total_Price-Del_Minus_Price;
                                Total_Price_Text.setText("Total Price - Rs."+(My_CartActivity.Total_Price));

                                cart_counter = cart_counter -1;
                                if (cart_counter ==0){
                                    editor.remove(CART_FLAG).apply();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent i = new Intent(My_CartActivity.this, Category.class);
                                            startActivity(i);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }
                                    }, 1400);
                                }
                            }
                        }, anim.getDuration());
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Something went wrong.Deletion fail", Toast.LENGTH_SHORT).show();
                    }
                } });
        }
    }
}
