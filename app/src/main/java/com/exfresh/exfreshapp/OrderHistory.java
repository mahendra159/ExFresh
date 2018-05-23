package com.exfresh.exfreshapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahendra on 3/31/2015.
 */
public class OrderHistory extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public int status_flag;

    // Progress Dialog
    private ProgressDialog pDialog;

    // products JSONArray
    JSONArray order_ids = null;

    // Creating JSON Parser object
    JSONParser_cart jParser_gt_ord_id = new JSONParser_cart();

    ArrayList<HashMap<String, String>> ordersList;

    private static final String TAG_ORDERS = "orders";
    static final String TAG_ORDER_ID = "id_order";
    static final String TAG_ORDER_REFERENCE = "reference";
     static final String TAG_ORDER_DATE="date_add";

     static final String TAG_ORDER_STATUS="current_state";
    static final String TAG_DELIVERY_DATE="delivery_date";

    static final String TAG_ORDER_GRAND_TOTAL="Grand_Total";
    static final String TAG_ORDER_SUB_TOTAL="Sub_Total";
    static final String TAG_ORDER_Shipping="Shipping";
    static final String TAG_SHIPPING_CARRIER_ID="id_carrier";
    static final String TAG_SHIPPING_CARRIER_NAME="Carrier_Name";



    // XML node keys
    static final String KEY_ORDER_ID = "order_id";
    static final String KEY_ORDER_REFERENCE = "order_reference";
    static final String KEY_ORDER_DATE = "order_date";
    static final String KEY_ORDER_TOTAL_PRICE = "order_price";
    static final String KEY_ORDER_STATUS = "order_status";
    static final String KEY_DELIVERY_DATE = "delivery_date";

    public String C_Cust_Id;

    //private static String url_get_order_id = "http://www.exfresh.co.in/Get_Order_Id.php";
    //private static String url_get_order_id = "http://192.168.1.31/prestashop16/Get_Order_Id.php";
    //static final String URL_PRE = "http://192.168.1.31/prestashop16/api/orders?display=full&filter[id_customer]=[";
    String MAIN_URL;
    String URL;
    //String url_get_order_id;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    ListView list;
    OrderHistoryAdapter adapter;

    String URL_PHP;
    String URL_GET_ORDER;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    Button homebtn;
    Button mycartbtn;
    Button offersbtn;

    String cart_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderhistory);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        mycartbtn = (Button)findViewById(R.id.bMyCart);
        homebtn = (Button)findViewById(R.id.bHome);
        offersbtn = (Button)findViewById(R.id.bOffers);

        cart_flag = preferences.getString("Pref_Cart_Flag",null);
        // Got cust_id from SharedPreferences
        C_Cust_Id = preferences.getString("Pref_Customer_Id", null);

        MAIN_URL = preferences.getString("Pref_main_url", null);

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_GET_ORDER = URL_PHP.concat("Get_Order_Id.php");

         //String URL_POST = C_Cust_Id+"]";
         //final String URL = URL_PRE.concat(URL_POST);
        URL = MAIN_URL.concat("/api/orders?display=full&filter[id_customer]=["+C_Cust_Id+"]");
        //url_get_order_id = MAIN_URL.concat();
        //http://192.168.1.31/prestashop16/Get_Order_Id.php
        // Hashmap for ListView
        ordersList = new ArrayList<HashMap<String, String>>();

        View.OnClickListener listnr_home=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(OrderHistory.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(OrderHistory.this,Category.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };


        View.OnClickListener listnr_cart=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(OrderHistory.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent i= new Intent(OrderHistory.this,My_CartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }


            }
        };

        View.OnClickListener listnr_offers=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(getApplicationContext(), "Offers are about to launch", Toast.LENGTH_SHORT).show();

                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(OrderHistory.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent k= new Intent(OrderHistory.this,OffersActivity.class);
                startActivity(k);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        };

        homebtn.setOnClickListener(listnr_home);
        mycartbtn.setOnClickListener(listnr_cart);
        offersbtn.setOnClickListener(listnr_offers);


        // Loading products in Background Thread
        new GetOrderId().execute();

    }

    // Background Async Task to fetch order_ids corresponding to customer ids
    class GetOrderId extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OrderHistory.this);
            pDialog.setTitle("Order History");
            pDialog.setMessage("Retrieving your order !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", C_Cust_Id));

            // getting JSON string from URL
            JSONObject json_order = jParser_gt_ord_id.makeHttpRequest(URL_GET_ORDER, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_order.getInt(TAG_SUCCESS);
                //Log.d("Inside Try Block : "+success");
                //Log.d("MainActivity", "INSIDE TRY block, value of success :" + success);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    order_ids = json_order.getJSONArray(TAG_ORDERS);


                    // looping through All Orders
                    for (int i = 0; i < order_ids.length(); i++) {
                        JSONObject c = order_ids.getJSONObject(i);

                        // Storing each json item in variable
                        String Order_Id = c.getString(TAG_ORDER_ID);
                        String Order_Reference = c.getString(TAG_ORDER_REFERENCE);
                        String Order_Date = c.getString(TAG_ORDER_DATE);
                        String Order_Grand_Total = c.getString(TAG_ORDER_GRAND_TOTAL);
                        String Order_Sub_Total = c.getString(TAG_ORDER_SUB_TOTAL);
                        String Order_Shipping = c.getString(TAG_ORDER_Shipping);
                        String Order_Status = c.getString(TAG_ORDER_STATUS);
                        String Delivery_Date = c.getString(TAG_DELIVERY_DATE);

                        String Carrier_Id = c.getString(TAG_SHIPPING_CARRIER_ID);
                        String Carrier_Name = c.getString(TAG_SHIPPING_CARRIER_NAME);


                        // creating new HashMap
                        HashMap<String, String> map2 = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map2.put(TAG_ORDER_ID, Order_Id);
                        map2.put(TAG_ORDER_REFERENCE,Order_Reference);
                        map2.put(TAG_ORDER_DATE,Order_Date);
                        map2.put(TAG_ORDER_GRAND_TOTAL,Order_Grand_Total);
                        map2.put(TAG_ORDER_SUB_TOTAL,Order_Sub_Total);
                        map2.put(TAG_ORDER_Shipping,Order_Shipping);
                        map2.put(TAG_ORDER_STATUS,Order_Status);
                        map2.put(TAG_DELIVERY_DATE,Delivery_Date);

                        map2.put(TAG_SHIPPING_CARRIER_ID,Carrier_Id);
                        map2.put(TAG_SHIPPING_CARRIER_NAME,Carrier_Name);
                        //map2.put(TAG_QUANTITY, quantity_f_ps_crt);

                        // adding HashList to ArrayList
                        ordersList.add(map2);

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
                        /*
                        Thread thread = new Thread(){
                            @Override
                            public void run() {

                                try {
                                    Thread.sleep(2500); // As I am using LENGTH_LONG in Toast
                                    My_CartActivity.this.finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                           }
                        };
                        */
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
                    //for(Map<String, String> map : productsList) {
                    //  String tag_quantity = map.get(TAG_QUANTITY);
                    //String tag_id = map.get(TAG_ID_PRODUCT);
                    // pids = pids.concat(tag_id);
                    // pids = pids.concat(",");
                    //  }
                    int pr_len = ordersList.size();
                    if (pr_len == 0) {
                        Toast.makeText(getApplicationContext(), "No Orders ...", Toast.LENGTH_SHORT).show();
                    } else
                    {
/*

                        ArrayList<HashMap<String, String>> OrdersList = new ArrayList<HashMap<String, String>>();

                        // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();


                            for (Map<String, String> map3 : ordersList) {
                                String tag_quantity2 = map3.get(TAG_ORDER_ID);
                                String tag_id2 = map3.get(TAG_ORDER_REFERENCE);

                                    //Total_ITEMS=Total_ITEMS+1;
                                    map.put(KEY_ORDER_REFERENCE, map3.get(TAG_ORDER_REFERENCE));
                                    map.put(KEY_ORDER_DATE, map3.get(TAG_ORDER_DATE));
                                    map.put(KEY_ORDER_TOTAL_PRICE, map3.get(TAG_ORDER_Total_Price));
                                    map.put(KEY_ORDER_STATUS, map3.get(TAG_ORDER_STATUS));


                                    OrdersList.add(map);

                            }
                        */

                        list = (ListView) findViewById(R.id.order_hist_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new OrderHistoryAdapter(OrderHistory.this, ordersList);
                        list.setAdapter(adapter);
                       // list.addHeaderView(OrderHistoryAdapter.header, null, false);




                    /*


                        ArrayList<HashMap<String, String>> ProductsList = new ArrayList<HashMap<String, String>>();

                        XMLParser parser = new XMLParser();
                        //String xml = parser.getXmlFromUrl(PRODUCT_URL); // getting XML from URL
                        String xml = parser.getXmlFromUrl(URL); // getting XML from URL
                        Document doc = parser.getDomElement(xml); // getting DOM element


                        NodeList nList = doc.getElementsByTagName(KEY_PRODUCT);


                        //NodeList nl = doc.getElementsByTagName(KEY_SONG);
                        // looping through all product nodes <product>
                        for (int i = 0; i < nList.getLength(); i++) {
                            // creating new HashMap
                            HashMap<String, String> map = new HashMap<String, String>();
                            Element e = (Element) nList.item(i);
                            Node Product_Id = e.getFirstChild().getNextSibling();
                            String Value_Product_Id = Product_Id.getFirstChild().getNodeValue();


                            for (Map<String, String> map3 : productsList) {
                                String tag_quantity2 = map3.get(TAG_QUANTITY);
                                String tag_id2 = map3.get(TAG_ID_PRODUCT);
                                if (Value_Product_Id.equalsIgnoreCase(tag_id2)) {
                                    //Total_ITEMS=Total_ITEMS+1;
                                    map.put(KEY_CATEGORY, parser.getValue(e, KEY_CATEGORY));
                                    map.put(KEY_IMAGE_URL, parser.getValue(e, KEY_IMAGE_URL));
                                    map.put(KEY_ID, parser.getValue(e, KEY_ID));
                                    map.put(KEY_NAME, parser.getValue(e, KEY_NAME));
                                    map.put(KEY_CONDITION, parser.getValue(e, KEY_CONDITION));
                                    map.put(KEY_PRICE, parser.getValue(e, KEY_PRICE));
                                    map.put(KEY_QUANTITY,tag_quantity2);

                                    ProductsList.add(map);


                                }
                            }

                        }

                        list = (ListView) findViewById(R.id.product_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new LazyAdapter_Cart(My_CartActivity.this, ProductsList);
                        list.setAdapter(adapter);
                        ListUtils.setDynamicHeight(list);
//                        adapter.notifyDataSetChanged();


                        */

                        // Click event for single list row
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                final Intent GoToDisplay = new Intent(OrderHistory.this,OrderDetail.class);
                                //GoToDisplay.putExtra("position", CategoryList.get(position));
                                TextView clickedTextView = (TextView)view.findViewById(R.id.order_id);
                                TextView clicked_Grand_Total = (TextView)view.findViewById(R.id.order_total_price);
                                TextView clicked_Sub_Total = (TextView)view.findViewById(R.id.sub_total);
                                TextView clicked_Shipping = (TextView)view.findViewById(R.id.shipping);

                                TextView clicked_Carrier_Id = (TextView)view.findViewById(R.id.carrier_id);
                                TextView clicked_Carrier_Name = (TextView)view.findViewById(R.id.carrier_name);

                                TextView clickedStatus = (TextView)view.findViewById(R.id.order_status);// Or get child from view arg1
                                if (clickedStatus.getText().toString().equalsIgnoreCase("Delivered")){

                                    TextView clickedDeliveryDate = (TextView)view.findViewById(R.id.delivery_date);
                                    String d_date = clickedDeliveryDate.getText().toString();
                                    Date D_Date_Formatted = convertDate(d_date);

                                    Date cur_date = new Date();

                                    long dif_dates = cur_date.getTime()-D_Date_Formatted.getTime();
                                    long secs = dif_dates/1000;
                                    long mins = secs/60;
                                    long hours = mins/60;
                                    long days = hours/24;
                                    if (MyDebug.LOG) {
                                        Log.d("seconds :", String.valueOf(secs));
                                        Log.d("minutes :", String.valueOf(mins));
                                        Log.d("hours :", String.valueOf(hours));
                                        Log.d("days :", String.valueOf(days));
                                    }

                                    if ((days<1) && (hours<=2)) {
                                         status_flag = 1;
                                        if (MyDebug.LOG) {
                                            Log.d("Inside If-Status_Flag", String.valueOf(status_flag));
                                        }
                                    }
                                    else {
                                        status_flag=0;
                                        if (MyDebug.LOG) {
                                            Log.d("Inside if->else status", String.valueOf(status_flag));
                                        }
                                    }
                                }
                                else {
                                    status_flag = 0;
                                    if (MyDebug.LOG) {
                                        Log.d("Inside else", String.valueOf(status_flag));
                                    }
                                }






                                GoToDisplay.putExtra("order_id", clickedTextView.getText().toString());
                                GoToDisplay.putExtra("grand_total", clicked_Grand_Total.getText().toString());
                                GoToDisplay.putExtra("sub_total", clicked_Sub_Total.getText().toString());
                                GoToDisplay.putExtra("shipping", clicked_Shipping.getText().toString());

                                GoToDisplay.putExtra("carrier_id", clicked_Carrier_Id.getText().toString());
                                GoToDisplay.putExtra("carrier_name", clicked_Carrier_Name.getText().toString());

                                GoToDisplay.putExtra("status_flag", status_flag);
                                startActivity(GoToDisplay);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


                            }
                        });





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



}
///
