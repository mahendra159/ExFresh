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
 * Created by Mahendra on 4/20/2015.
 */
public class My_Returns extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    // Progress Dialog
    private ProgressDialog pDialog;


    public String C_Cust_Id;

    //    private static String url_get_order_id = "http://www.exfresh.co.in/Get_Order_Id.php";

    String URL_PHP;
    String URL_GET_RETURN;

    //private static String URL_GET_RETURN = "http://www.exfresh.co.in/Get_Return_Id.php";
    //static final String URL_PRE = "http://192.168.1.31/prestashop16/api/orders?display=full&filter[id_customer]=[";



    // products JSONArray
    JSONArray return_ids = null;

    // Creating JSON Parser object
    JSONParser_cart jParser_gt_return_id = new JSONParser_cart();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";



    private static final String TAG_RETURNS = "returns";


    static final String TAG_ID_ORDER_RETURN = "id_order_return";
    static final String TAG_ID_ORDER = "id_order";
    static final String TAG_STATE="state";
    static final String TAG_DATE_ADD="date_add";
    static final String TAG_ORDER_REFERENCE = "reference";
    //static final String TAG_ORDER_STATUS="current_state";
    //static final String TAG_DELIVERY_DATE="delivery_date";

    ArrayList<HashMap<String, String>> returnsList;

    ListView list;
    ReturnHistoryAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returns);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        // Got cust_id from SharedPreferences
        C_Cust_Id = preferences.getString("Pref_Customer_Id", null);

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_GET_RETURN = URL_PHP.concat("Get_Return_Id.php");

        // Hashmap for ListView
        returnsList = new ArrayList<HashMap<String, String>>();

        // Loading products in Background Thread
        new GetReturnDetail().execute();

    }


    // Background Async Task to fetch order_ids corresponding to customer ids
    class GetReturnDetail extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(My_Returns.this);
            pDialog.setTitle("Your Returns");
            pDialog.setMessage("Retrieving your returns !! Please wait...");
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
            JSONObject json_return = jParser_gt_return_id.makeHttpRequest(URL_GET_RETURN, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_return.getInt(TAG_SUCCESS);
                //Log.d("Inside Try Block : "+success");
                //Log.d("MainActivity", "INSIDE TRY block, value of success :" + success);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    return_ids = json_return.getJSONArray(TAG_RETURNS);
                    if (MyDebug.LOG) {
                        Log.d("Return Ids ", ": " + return_ids);
                    }


                    // looping through All Orders
                    for (int i = 0; i < return_ids.length(); i++) {
                        JSONObject c = return_ids.getJSONObject(i);

                        // Storing each json item in variable
                        String Id_Order_Return = c.getString(TAG_ID_ORDER_RETURN);
                        String Id_Order = c.getString(TAG_ID_ORDER);
                        String Return_State_numeric = c.getString(TAG_STATE);
                        String Return_State="";
                        if (Return_State_numeric.equalsIgnoreCase("1")){
                            Return_State = "Waiting for confirmation";
                        } else if (Return_State_numeric.equalsIgnoreCase("4")){
                            Return_State = "Return Denied";
                        } else if (Return_State_numeric.equalsIgnoreCase("5")){
                            Return_State = "Return completed";
                        } else if (Return_State_numeric.equalsIgnoreCase("2")||Return_State_numeric.equalsIgnoreCase("3")){
                            Return_State = "Processing";
                        }
                        String Return_date_add = c.getString(TAG_DATE_ADD);
                        String Order_Reference = c.getString(TAG_ORDER_REFERENCE);
                        //String Order_Status = c.getString(TAG_ORDER_STATUS);
                        //String Delivery_Date = c.getString(TAG_DELIVERY_DATE);

                        //String quantity_f_ps_crt = c.getString(TAG_QUANTITY);
                        //Log.d("MainActivity", "INSIDE TRY->IF block->FOR, value of product_id_ps_crt :" +Order_Id);
                        //code for setting flag for status delivered


                        // creating new HashMap
                        HashMap<String, String> map2 = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map2.put(TAG_ID_ORDER_RETURN, Id_Order_Return);
                        map2.put(TAG_ID_ORDER,Id_Order);
                        map2.put(TAG_STATE,Return_State);
                        map2.put(TAG_DATE_ADD,Return_date_add);
                        map2.put(TAG_ORDER_REFERENCE,Order_Reference);
                        //map2.put(TAG_ORDER_STATUS,Order_Status);
                        //map2.put(TAG_DELIVERY_DATE,Delivery_Date);
                        //map2.put(TAG_QUANTITY, quantity_f_ps_crt);

                        // adding HashList to ArrayList
                        returnsList.add(map2);
                        //map2.entrySet().toArray();
                        //Log.d("MainActivity", "INSIDE TRY->IF block->FOR-->, value of ordersList :" +ordersList);
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
                    int pr_len = returnsList.size();
                    if (pr_len == 0) {
                        Toast.makeText(getApplicationContext(), "No Returns ...", Toast.LENGTH_SHORT).show();
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

                        list = (ListView) findViewById(R.id.returns_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new ReturnHistoryAdapter(My_Returns.this, returnsList);
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
                                final Intent GoToDisplay = new Intent(My_Returns.this,Return_Detail.class);
                                //GoToDisplay.putExtra("position", CategoryList.get(position));
                               TextView id_ord_return = (TextView)view.findViewById(R.id.id_order_return);



                                GoToDisplay.putExtra("id_order_return", id_ord_return.getText().toString());
                                //GoToDisplay.putExtra("status_flag", status_flag);
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