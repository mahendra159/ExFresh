package com.exfresh.exfreshapp;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahendra on 4/21/2015.
 */
public class MyAddressActivity extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    public int status_flag;

    // Progress Dialog
    private ProgressDialog pDialog;


    //static final String URL_CHK_ADD = "http://www.exfresh.co.in/ChkAddExist.php";
    //static final String URL_CHK_ADD = "http://192.168.1.31/prestashop16/ChkAddExist.php";

    // products JSONArray
    JSONArray add_ids = null;

    ArrayList<HashMap<String, String>> AddList;

    // Creating JSON Parser object
    JSONParser_cart jParser_check_add_exist = new JSONParser_cart();

    private static final String TAG_ADD_DTL = "adddetails";
    static final String TAG_ID_ADDRESS = "id_address";
    static final String TAG_ALIAS = "alias";
    static final String TAG_FNAME = "firstname";
    static final String TAG_LNAME="lastname";
    static final String TAG_ADD1="address1";
    static final String TAG_ADD2="address2";
    static final String TAG_CITY="city";
    static final String TAG_POSTCODE="postcode";
    static final String TAG_PHONE="phone";
    static final String TAG_PHONE_MOBILE="phone_mobile";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";



    String Cust_Id;

    ListView list;
    MyAddressAdapter adapter;

    Button add_new_btn;
    Button home_btn;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    String URL_PHP;
    String URL_ADD_CHK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaddress);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());


        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        add_new_btn = (Button)findViewById(R.id.bAddNew);
        home_btn = (Button)findViewById(R.id.bHome);

        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(MyAddressActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }

                Intent i= new Intent(MyAddressActivity.this,AddressRegistrationActivity.class);
                status_flag = 1;
                i.putExtra("status_flag", status_flag);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                MyAddressActivity.this.finish();
            }
        };


        View.OnClickListener listnr_home=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(MyAddressActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }

                Intent i= new Intent(MyAddressActivity.this,Category.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                MyAddressActivity.this.finish();
            }
        };

        add_new_btn.setOnClickListener(listnr);
        home_btn.setOnClickListener(listnr_home);

        // Got cart_id from SharedPreferences
        Cust_Id = preferences.getString("Pref_Customer_Id", null);

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_ADD_CHK = URL_PHP.concat("ChkAddExist.php");


        // Hashmap for ListView
        AddList = new ArrayList<HashMap<String, String>>();

        new ChkAddressExist().execute();


    }

    // Background Async Task to fetch order_ids corresponding to customer ids
    class ChkAddressExist extends AsyncTask<String, String, String> {

        public String id_address;

        /**
         * Before starting background thread Show Progress Dialog *
         */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(MyAddressActivity.this);
            pDialog.setTitle("Fetching Address");
            pDialog.setMessage("Retrieving your address !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_customer", Cust_Id));

            // getting JSON string from URL
            JSONObject json_chkaddexist = jParser_check_add_exist.makeHttpRequest(URL_ADD_CHK, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_chkaddexist.getInt(TAG_SUCCESS);
                //Log.d("Inside Try Block : "+success");
                //Log.d("MainActivity", "INSIDE TRY block, value of success :" + success);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    add_ids = json_chkaddexist.getJSONArray(TAG_ADD_DTL);
                    //Log.d("MainActivity", "INSIDE TRY->IF block, value of products :" +add_ids);


                    // looping through All Orders
                    for (int i = 0; i < add_ids.length(); i++) {
                        JSONObject c = add_ids.getJSONObject(i);

                        //  Log.d("MainActivity", "INSIDE TRY->IF block->FOR, value of c :" +c);

                        // Storing each json item in variable
                        String Id_Address = c.getString(TAG_ID_ADDRESS);
                        String Alias = c.getString(TAG_ALIAS);
                        String FName = c.getString(TAG_FNAME);
                        String LName = c.getString(TAG_LNAME);
                        String Add1 = c.getString(TAG_ADD1);
                        String Add2 = c.getString(TAG_ADD2);
                        String City = c.getString(TAG_CITY);
                        String Postcode = c.getString(TAG_POSTCODE);
                        String Phone = c.getString(TAG_PHONE);
                        String Phone_Mobile = c.getString(TAG_PHONE_MOBILE);
                        //String quantity_f_ps_crt = c.getString(TAG_QUANTITY);
                        //Log.d("MainActivity", "INSIDE TRY->IF block->FOR, value of product_id_ps_crt :" +Order_Id);


                        // creating new HashMap
                        HashMap<String, String> map2 = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map2.put(TAG_ID_ADDRESS, Id_Address);
                        map2.put(TAG_ALIAS, Alias);
                        map2.put(TAG_FNAME, FName);
                        map2.put(TAG_LNAME, LName);
                        map2.put(TAG_ADD1, Add1);
                        map2.put(TAG_ADD2, Add2);
                        map2.put(TAG_CITY, City);
                        map2.put(TAG_POSTCODE, Postcode);
                        map2.put(TAG_PHONE, Phone);
                        map2.put(TAG_PHONE_MOBILE, Phone_Mobile);
                        //map2.put(TAG_QUANTITY, quantity_f_ps_crt);

                        // adding HashList to ArrayList
                        AddList.add(map2);
                        //map2.entrySet().toArray();
                        //Log.d("MainActivity", "INSIDE TRY->IF block->FOR-->, value of ordersList :" +AddList);
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
                    int pr_len = AddList.size();
                    if (pr_len == 0) {
                        //Toast.makeText(getApplicationContext(), "No Address for given Customer Id...", Toast.LENGTH_SHORT).show();
                        //present here address registration form

                        Intent i = new Intent(getApplicationContext(), AddressRegistrationActivity.class);
                        status_flag = 1;
                        i.putExtra("status_flag", status_flag);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        //new AddressRegistration().execute();

                    } else {

                        list = (ListView) findViewById(R.id.add_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new MyAddressAdapter(MyAddressActivity.this, AddList);
                        list.setAdapter(adapter);


                        // Click event for single list row
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {


                                final Intent GoToDisplay = new Intent(MyAddressActivity.this,UpdateAddress.class);

                                TextView clicked_Id_Address = (TextView) view.findViewById(R.id.id_address); // Or get child from view arg1
                                GoToDisplay.putExtra("id_address", clicked_Id_Address.getText().toString());
                                //id_address = clickedTextView.getText().toString();
                                //new UpdtePS_Cart_and_go_2_Finalcheckout().execute();

                                startActivity(GoToDisplay);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                MyAddressActivity.this.finish();
                            }
                        });


                    }


                }
            });

        }

    }



    }
