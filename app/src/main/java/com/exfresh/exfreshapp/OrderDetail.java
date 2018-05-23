package com.exfresh.exfreshapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
 * Created by Mahendra on 4/1/2015.
 */
public class OrderDetail extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    // Progress Dialog
    private ProgressDialog pDialog;

//*******************************************************************************
    // products JSONArray
    JSONArray order_dtls= null;
//    JSONArray ship_dtls= null;

    // Creating JSON Parser object for getting order details
    JSONParser_cart jParser_gt_ord_detail = new JSONParser_cart();

    // Creating JSON Parser object for order return
    JSONParser_cart jParser_ret_post = new JSONParser_cart();

    ArrayList<HashMap<String, String>> ordersdetailList;
//    ArrayList<HashMap<String, String>> shipdetailList;

    // JSON Node names for order detail
    private static final String TAG_SUCCESS = "success";
//    private static final String TAG_SUCCESS_SHIPPING = "success_shipping";

    // JSON Node names for order return
    private static final String TAG_SUCCESS2 = "success";


    //*************************************************************************

    private static final String TAG_ORDER_DETAILS = "orderdetails";
    static final String TAG_ORDER_ID = "id_order";
    static final String TAG_ORDER_INVOICE="id_order_invoice";
    static final String TAG_PRODUCT_NAME = "product_name";
    static final String TAG_PRODUCT_QUANTITY="product_quantity";
    static final String TAG_PRODUCT_PRICE="product_price";
    static final String TAG_TOTAL_PRICE_TAX_INCLUSIVE="total_price_tax_incl";
    static final String TAG_UNIT_PRICE_TAX_INCLUSIVE="unit_price_tax_incl";
    static final String TAG_ID_ORDER_DETAIL="id_order_detail";
    static final String TAG_PRODUCT_ID="product_id";

    static int status_flag;


//    private static final String TAG_SHIPPING_DETAILS = "shipping_details";
//    static final String TAG_CARRIER_NAME = "Carrier_Name";
//    static final String TAG_SHIP_CHARGE_TAX_INCL="Shipping_Charge_TAX_INCL";
//    static final String TAG_SHIP_CHARGE_TAX_EXCL = "Shipping_Charge_TAX_EXCL";



    // XML node keys
    static final String KEY_ORDER_ID = "order_id";
    static final String KEY_ORDER_INVOICE="id_order_invoice";
    static final String KEY_PRODUCT_NAME = "product_name";
    static final String KEY_PRODUCT_QUANTITY = "product_quantity";
    static final String KEY_PRODUCT_PRICE = "product_price";
    static final String KEY_TOTAL_PRICE_TAX_INCLUSIVE = "total_price_tax_incl";
    static final String KEY_UNIT_PRICE_TAX_INCLUSIVE="unit_price_tax_incl";
    static final String KEY_ID_ORDER_DETAIL="id_order_detail";
    static final String KEY_PRODUCT_ID="product_id";

    //http://www.exfresh.co.in/Get_Order_Id.php

    //private static String url_get_order_detail = "http://www.exfresh.co.in/Get_Order_Detail.php";
    //private static String url_return = "http://www.exfresh.co.in/order_return.php";
    //private static String url_get_order_detail = "http://192.168.1.31/prestashop16/Get_Order_Detail.php";
    //private static String url_return = "http://192.168.1.31/prestashop16/order_return.php";

    public static String Order_Id;
    public static String Grand_Total;
    public static String Sub_Total;
    public static String Shipping;

    public static String Carrier_Id;
    public static String Carrier_Name;

    public static String Cust_Id;
//static final String URL_PRE = "http://192.168.1.31/prestashop16/api/orders?display=full&filter[id_customer]=[";

    public static String O_Order_Id_Order_Detail ;
    public static String O_Order_Product_Quantity ;


    ListView list;
    //ListView shiplist;
    OrderDetailAdapter adapter;
    //OrderShipAdapter shipadapter;

    private TextView Ship_Charge_disp,Ship_Charge,Grand_Total_Text,Sub_Total_Text;



    String URL_PHP;
    String URL_GET_ORDER_DTL;
    String URL_ORDER_RETURN;

    Button homebtn;
    Button mycartbtn;
    Button offersbtn;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    String cart_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdetail);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }



        Order_Id = extras.getString("order_id");
        if (MyDebug.LOG) {
            Log.d("Order Id = ", Order_Id);
        }

        Grand_Total = extras.getString("grand_total");
        Sub_Total = extras.getString("sub_total");
        Shipping = extras.getString("shipping");

        Carrier_Id = extras.getString("carrier_id");
        Carrier_Name = extras.getString("carrier_name");

        if (MyDebug.LOG) {
            Log.d("Grand_Total = ", Grand_Total);
        }

        status_flag = extras.getInt("status_flag");
        int pp =status_flag;
//        O_Order_Id = Integer.parseInt(Order_Id);

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();



        mycartbtn = (Button)findViewById(R.id.bMyCart);
        homebtn = (Button)findViewById(R.id.bHome);
        offersbtn = (Button)findViewById(R.id.bOffers);


        cart_flag = preferences.getString("Pref_Cart_Flag",null);
        // Got cust_id from SharedPreferences
        Cust_Id = preferences.getString("Pref_Customer_Id", null);
  //      C_Cust_Id = Integer.parseInt(Cust_Id);
        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_GET_ORDER_DTL = URL_PHP.concat("Get_Order_Detail.php");
        URL_ORDER_RETURN = URL_PHP.concat("order_return.php");


        View.OnClickListener listnr_home=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(OrderDetail.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(OrderDetail.this,Category.class);
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
                    alert.showAlertDialog(OrderDetail.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent i= new Intent(OrderDetail.this,My_CartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(OrderDetail.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }


            }
        };

        View.OnClickListener listnr_offers=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(OrderDetail.this, "Offers are about to launch", Toast.LENGTH_SHORT).show();
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(OrderDetail.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent k= new Intent(OrderDetail.this,OffersActivity.class);
                startActivity(k);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            }
        };

        homebtn.setOnClickListener(listnr_home);
        mycartbtn.setOnClickListener(listnr_cart);
        offersbtn.setOnClickListener(listnr_offers);


        // Hashmap for ListView
        ordersdetailList = new ArrayList<HashMap<String, String>>();
  //      shipdetailList= new ArrayList<HashMap<String, String>>();




        // Loading products in Background Thread
        new GetOrderDetail().execute();

    }

    // Background Async Task to fetch order_ids corresponding to customer ids
    class GetOrderDetail extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog *
         */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OrderDetail.this);
            pDialog.setTitle("Order Details");
            pDialog.setMessage("Retrieving your order details !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_order", Order_Id));

            // getting JSON string from URL
            JSONObject json_order_detail = jParser_gt_ord_detail.makeHttpRequest(URL_GET_ORDER_DTL, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_order_detail.getInt(TAG_SUCCESS);
                //Log.d("Inside Try Block : "+success");
                //Log.d("MainActivity", "INSIDE TRY block, value of success :" + success);

                if (success == 1) {
                    // order detail found
                    // Getting order details
                    order_dtls = json_order_detail.getJSONArray(TAG_ORDER_DETAILS);


                    // looping through All Orders
                    for (int i = 0; i < order_dtls.length(); i++) {
                        JSONObject c = order_dtls.getJSONObject(i);

                        // Storing each json item in variable
                        String Order_Id = c.getString(TAG_ORDER_ID);
                        String Order_Invoice = c.getString(TAG_ORDER_INVOICE);
                        String Order_Product_Name = c.getString(TAG_PRODUCT_NAME);
                        String Order_Product_Quantity = c.getString(TAG_PRODUCT_QUANTITY);
                        String Order_Product_Price = c.getString(TAG_PRODUCT_PRICE);
                        String Order_Total_Price = c.getString(TAG_TOTAL_PRICE_TAX_INCLUSIVE);
                        String Order_Total_Unit_Price = c.getString(TAG_UNIT_PRICE_TAX_INCLUSIVE);
                        String Order_Id_Order_Detail = c.getString(TAG_ID_ORDER_DETAIL);
                        String Order_Product_Id = c.getString(TAG_PRODUCT_ID);

                        O_Order_Id_Order_Detail = Order_Id_Order_Detail;
                        O_Order_Product_Quantity = Order_Product_Quantity;


                        // creating new HashMap
                        HashMap<String, String> map2 = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map2.put(TAG_ORDER_ID, Order_Id);
                        map2.put(TAG_ORDER_INVOICE, Order_Invoice);
                        map2.put(TAG_PRODUCT_NAME, Order_Product_Name);
                        map2.put(TAG_PRODUCT_QUANTITY, Order_Product_Quantity);
                        map2.put(TAG_PRODUCT_PRICE, Order_Product_Price);
                        map2.put(TAG_TOTAL_PRICE_TAX_INCLUSIVE, Order_Total_Price);
                        map2.put(TAG_UNIT_PRICE_TAX_INCLUSIVE, Order_Total_Unit_Price);
                        map2.put(TAG_ID_ORDER_DETAIL, Order_Id_Order_Detail);
                        map2.put(TAG_PRODUCT_ID, Order_Product_Id);
                        //map2.put(TAG_QUANTITY, quantity_f_ps_crt);


                        // adding HashList to ArrayList
                        ordersdetailList.add(map2);
                    }

                }

                ////////shipping start
  /*
                int success_shipping = json_order_detail.getInt(TAG_SUCCESS_SHIPPING);

                if (success_shipping == 1) {
                    // order detail found
                    // Getting order details
                    ship_dtls = json_order_detail.getJSONArray(TAG_SHIPPING_DETAILS);
                    Log.d("MainActivity", "INSIDE TRY->IF block, order_details :" + ship_dtls);


                    // looping through All Orders
                    for (int i = 0; i < ship_dtls.length(); i++) {
                        JSONObject c = ship_dtls.getJSONObject(i);

                        Log.d("MainActivity", "INSIDE TRY->IF block->FOR, value of c :" + c);

                        // Storing each json item in variable
                        String Carrier_Name = c.getString(TAG_CARRIER_NAME);
                        String Shipping_Charge_Tax_incl = c.getString(TAG_SHIP_CHARGE_TAX_INCL);
                        String Shipping_Charge_Tax_excl = c.getString(TAG_SHIP_CHARGE_TAX_EXCL);


                        Log.d("MainActivity", "INSIDE TRY->IF block->FOR, value of  :" + Carrier_Name);


                        // creating new HashMap
                        HashMap<String, String> map3 = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map3.put(TAG_CARRIER_NAME, Carrier_Name);
                        map3.put(TAG_SHIP_CHARGE_TAX_INCL, Shipping_Charge_Tax_incl);
                        map3.put(TAG_SHIP_CHARGE_TAX_EXCL, Shipping_Charge_Tax_excl);

                        // adding HashList to ArrayList
                        shipdetailList.add(map3);
                        //map2.entrySet().toArray();
                        Log.d("M", "value of ordersdetailList :" + shipdetailList);
                    }

                }

*/

                ////////shipping

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

                    if (ordersdetailList.size() == 0) {
                        Toast.makeText(getApplicationContext(), "No Order Details...", Toast.LENGTH_SHORT).show();
                    } else {

                        list = (ListView) findViewById(R.id.order_detail_list);
                        adapter = new OrderDetailAdapter(OrderDetail.this, ordersdetailList);
                        list.setAdapter(adapter);
                        // list.addHeaderView(OrderHistoryAdapter.header, null, false);

                    }


                        //shiplist = (ListView) findViewById(R.id.ship_detail_list);
                        //shipadapter = new OrderShipAdapter(OrderDetail.this, shipdetailList);
                        //shiplist.setAdapter(shipadapter);

                        TextView Ship_Charge_disp = (TextView)findViewById(R.id.shipping_prc);
                        TextView Ship_Charge = (TextView)findViewById(R.id.Shipping_Charge);
                        TextView Grand_Total_Text = (TextView)findViewById(R.id.grand_tot);
                        TextView Sub_Total_Text = (TextView)findViewById(R.id.sub_tot);

                       // String Carrier_Name = shipdetailList.get(0).get(TAG_CARRIER_NAME);
                        //String Shipping_Charge_Pre = shipdetailList.get(0).get(TAG_SHIP_CHARGE_TAX_INCL);
                        //String Ship_charges_tax_incl = "Rs."+Shipping_Charge_Pre;

                        Spannable spanSubTotal = Spannable.Factory.getInstance().newSpannable(Sub_Total);
                        spanSubTotal.setSpan(new BackgroundColorSpan(0xffb7d1ff), 0, spanSubTotal.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        Spannable spanShippingCharges = Spannable.Factory.getInstance().newSpannable(Shipping);
                        spanShippingCharges.setSpan(new BackgroundColorSpan(0xffb7d1ff), 0, spanShippingCharges.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        Spannable spanGrandTotal = Spannable.Factory.getInstance().newSpannable(Grand_Total);
                        spanGrandTotal.setSpan(new BackgroundColorSpan(0xffb7d1ff), 0, spanGrandTotal.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);



                        Ship_Charge_disp.setText(Carrier_Name);
                        Ship_Charge.setText(spanShippingCharges);
                        Sub_Total_Text.setText(spanSubTotal);
                        Grand_Total_Text.setText(spanGrandTotal);






                }
            });

        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orderdetail, menu);

        return super.onCreateOptionsMenu(menu);

        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        int id = item.getItemId();

        switch (id) {

            case R.id.return_order:
                //editor.clear();
                //editor.commit();
//                new ReturnPost().execute();

                Toast.makeText(OrderDetail.this, "Return feature is coming soon!!", Toast.LENGTH_SHORT).show();
            case R.id.action_search:

                onSearchRequested();

                return true;

            //case android.R.id.home:
            //    NavUtils.navigateUpFromSameTask(this);
            //   return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    //Background Async Task to Load all product by making HTTP Request

    class ReturnPost extends AsyncTask<String, String, String> {

        String status_ret;
        String quest2 = "so what";

        OrderDetailAdapter m1 = new OrderDetailAdapter();
        ;
        public ArrayList<String> mylist2 = m1.mylist;
        //public String quest2 = m1.ret_ques;
        //ArrayList<Map<String, String>> pplist;
        public ArrayList<Map<String, String>> mylist56 = m1.pplist;
        //public String[] rt = mylist56.toArray();
        Map<String,String> myMap2 = m1.myMap;

        // Before starting background thread Show Progress Dialog

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OrderDetail.this);
            pDialog.setTitle("Return");
            pDialog.setMessage("Creating your return !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(String... args)
        {
            String ipo="";
    //        StringBuilder ipo2 = new StringBuilder();
            for (int i = 0; i < mylist2.size(); i++) {
                ipo = ipo.concat(mylist2.get(i));
                ipo = ipo.concat(",");

//                ipo2.append(mylist2.get(i));
  //              c.append("b");
            }

           // Iterator<Map.Entry<String,String>> iterator = myMap2.entrySet().iterator();
           // while (iterator.hasNext()) {
           //     Map.Entry<String,String> entry = iterator.next();

           // }


            // Building Parameters
            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            params2.add(new BasicNameValuePair("id_cust", Cust_Id));
            params2.add(new BasicNameValuePair("id_order", Order_Id));
            //params2.add(new BasicNameValuePair("id_order_detail",ipo ));
            //params2.add(new BasicNameValuePair("id_product_quantity", O_Order_Product_Quantity));
            params2.add(new BasicNameValuePair("question", quest2));

            for (Map<String, String> map : mylist56)
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    // view.append(entry.getKey() + " => " + entry.getValue());
                    params2.add(new BasicNameValuePair("id_ord_dtl[]", entry.getKey()));
                    params2.add(new BasicNameValuePair("id_ord_dtl_qty[]", entry.getValue()));
                }



            params2.add(new BasicNameValuePair("quesd", mylist56.toString()));
            // url_check_in_ps_order
            // getting JSON string from URL
            JSONObject json_ord_return = jParser_ret_post.makeHttpRequest(URL_ORDER_RETURN, "POST", params2);

            try{


                int success = json_ord_return.getInt(TAG_SUCCESS2);

                if (success == 1) {

                    status_ret = "1";
                }

                else{

                    status_ret = "0";

                }}catch(JSONException e){e.printStackTrace();}



            return status_ret;
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

                    //new PS_Order_Return_Detail().execute();

                    if (status_ret.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Return creation successful ...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Something went wrong ...", Toast.LENGTH_SHORT).show();
                    }


                } });

        }
    }



    /*
    class PS_Order_Return_Detail extends AsyncTask<ArrayList<Map<String, String>>, Void, ArrayList<Map<String, String>>> {

        String status_ret;
        String quest2 = " so wat to do i dont know wat to do";

        OrderDetailAdapter m1 = new OrderDetailAdapter();
        ;
        public ArrayList<String> mylist2 = m1.mylist;
        //ArrayList<Map<String, String>> pplist;
        public ArrayList<Map<String, String>> mylist56 = m1.pplist;
        //public String[] rt = mylist56.toArray();
        Map<String,String> myMap2 = m1.myMap;



//        int id_detail_array_length = mylist2.size();




        // Before starting background thread Show Progress Dialog

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(OrderDetail.this);
            pDialog.setTitle("Return");
            pDialog.setMessage("Creating your return !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected ArrayList<Map<String, String>> doInBackground(ArrayList<Map<String, String>>... args)
        {
            String ipo="";
            //        StringBuilder ipo2 = new StringBuilder();
            for (int i = 0; i < mylist2.size(); i++) {
                ipo = ipo.concat(mylist2.get(i));
                ipo = ipo.concat(",");

//                ipo2.append(mylist2.get(i));
                //              c.append("b");
            }

            Iterator<Map.Entry<String,String>> iterator = myMap2.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String,String> entry = iterator.next();

            }


            // Building Parameters
            ArrayList<NameValuePair> params3 = new ArrayList<NameValuePair>();
            //List<NameValuePair> params2 = new ArrayList<NameValuePair>();
            //params2.add(new BasicNameValuePair("id_cust", Cust_Id));
            //params2.add(new BasicNameValuePair("id_order", Order_Id));
            //params2.add(new BasicNameValuePair("id_order_detail",ipo ));
            //params2.add(new BasicNameValuePair("id_product_quantity", O_Order_Product_Quantity));
            //params2.add(new BasicNameValuePair("question", quest2));
            //params2.add(new BasicNameValuePair("id_ord_dtl_qty", myMap2.toString()));
            params3.add(new BasicNameValuePair("quesd", mylist56);
            // url_check_in_ps_order
            // getting JSON string from URL
            JSONObject json_ord_return = jParser_ret_post.makeHttpRequest(url_return, "POST", params2);

            try{


                int success = json_ord_return.getInt(TAG_SUCCESS2);
                //Log.d("Inside Try Block : "+success");
                Log.d("MainActivity", "INSIDE ps_order_return, value of success :" +success);

                if (success == 1) {

                    status_ret = "1";
                }

                else{

                    status_ret = "0";

                }}catch(JSONException e){e.printStackTrace();}



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

                    new PS_Order_Return_Detail().execute();

                    if (status_ret.equals("1")) {
                        Toast.makeText(getApplicationContext(), "Return creation successful ...", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Something went wrong ...", Toast.LENGTH_SHORT).show();
                    }


                } });

        }
    }
*/

}




