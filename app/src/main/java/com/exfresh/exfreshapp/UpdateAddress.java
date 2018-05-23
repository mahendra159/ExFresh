package com.exfresh.exfreshapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mahendra on 4/22/2015.
 */
public class UpdateAddress extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    public String Cust_Id;
    public String Id_Address;


    // Progress Dialog
    private ProgressDialog pDialog;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Creating JSON Parser object
    JSONParser_cart jParser_add_update = new JSONParser_cart();

    JSONParser_cart jParser_gt_add_id = new JSONParser_cart();

    private static final String TAG_ADDRESS = "addrs";
    //http://www.exfresh.co.in
//    private static String URL_UPDATE_ADD = "http://www.exfresh.co.in/UpdateAddress.php";

    //private static String url_get_add_id = "http://192.168.1.31/prestashop16/Get_Address_Sync_No.php";

//    private static String url_get_add_id = "http://www.exfresh.co.in/Get_Address_Sync_No.php";



    // JSON Node names
    private static final String TAG_SUCCESS = "success";


    EditText fname,lname,address,address2,zip,home_phone,mobile,alias;

    Button update_address_btn;

    // Connection detector
    ConnectionDetector cd;

    String Update_DateTime ;

    String MAIN_URL;
    String URL_PHP;
    String URL_GET_ADD_ID;
    String URL_UPDATE_ADD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_update);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        Id_Address = extras.getString("id_address");

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();



        update_address_btn = (Button)findViewById(R.id.ButtonAddUpdate);

        MAIN_URL = preferences.getString("Pref_main_url", null);
        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_GET_ADD_ID = URL_PHP.concat("Get_Address_Sync_No.php");
        URL_UPDATE_ADD = URL_PHP.concat("UpdateAddress.php");

        // Got cust_id from SharedPreferences
        Cust_Id = preferences.getString("Pref_Customer_Id", null);

        fname=(EditText)findViewById(R.id.EditFirstName);
        lname=(EditText)findViewById(R.id.EditLastName);
        address=(EditText)findViewById(R.id.EditAddress);
        address2=(EditText)findViewById(R.id.EditAddress2);
        zip=(EditText)findViewById((R.id.EditZipPostal));

        home_phone=(EditText)findViewById((R.id.EditHomePhone));
        mobile=(EditText)findViewById(R.id.EditMobilePhone);
        //additional_info=(EditText)findViewById(R.id.EditAdditionalInformation);
        alias=(EditText)findViewById((R.id.EditAliasAdd));

        String First_Name = alias.getText().toString();



        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Update_DateTime = sdf.format(new Date());



        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(UpdateAddress.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }

                fname=(EditText)findViewById(R.id.EditFirstName);
                lname=(EditText)findViewById(R.id.EditLastName);
                address=(EditText)findViewById(R.id.EditAddress);
                address2=(EditText)findViewById(R.id.EditAddress2);
                zip=(EditText)findViewById((R.id.EditZipPostal));
                home_phone=(EditText)findViewById((R.id.EditHomePhone));
                mobile=(EditText)findViewById(R.id.EditMobilePhone);
                alias=(EditText)findViewById((R.id.EditAliasAdd));



                //String Address1;
                //String Address2;
                //String Zip;
                //String Home_Phone;
                //String Mobile;
                //String Alias;

                String F_Name = fname.getText().toString();
                if (!isValidName(F_Name)) {
                    fname.setError("Only Characters allowed");
                }
                if (F_Name.length()==0){
                    fname.setError("First name cannot be blank");
                }

                String L_Name = lname.getText().toString();
                if (!isValidName(L_Name)){
                    lname.setError("Only Characters allowed");
                }
                if (L_Name.length()==0) {
                    lname.setError("Last name cannot be blank");
                }

                String Address1 = address.getText().toString();
                if (Address1.length()==0){
                    address.setError("Address cannot be blank");
                }

                String Zip = zip.getText().toString();
                if (Zip.length()==0){
                    zip.setError("Pin Code cannot be blank");
                }

                if (Zip.length()!=6){
                    zip.setError("Incorrect Pin");
                }

                String Mobile = mobile.getText().toString();
                if (Mobile.length()==0){
                    mobile.setError("Mobile 1 cannot be blank");
                }
                if (Mobile.length()<10 || Mobile.length()>10){
                    mobile.setError("Invalid mobile number");
                }

                String Mobile2 = home_phone.getText().toString();
                if (Mobile2.length()>10){
                    mobile.setError("Invalid number");
                }

                String Alias = alias.getText().toString();
                if (Alias.length()==0){
                    alias.setError("Alias cannot be blank");
                }

                if ( F_Name.length() > 0 && L_Name.length() > 0 && Address1.length() > 0 && isValidName(L_Name)==true && isValidName(F_Name)==true
                        && Zip.length() > 0  && Zip.length()==6 && Mobile.length()>0 && Mobile.length()==10 && Mobile2.length()<11 && Alias.length()>0 ) {

                    new Address_Update().execute();
                }

                //Intent i= new Intent(MyAddressActivity.this,AddressRegistrationActivity.class);
                //status_flag = 1;
                //i.putExtra("status_flag", status_flag);
                //startActivity(i);
                //MyAddressActivity.this.finish();
            }
        };


       update_address_btn.setOnClickListener(listnr);

        new GetAddId().execute();

    }

    // validating name
    private boolean isValidName(String name) {
        String NAME_PATTERN = "^[a-z_A-Z]*$";

        Pattern pattern = Pattern.compile(NAME_PATTERN);
        Matcher matcher = pattern.matcher(name);
        return matcher.matches();
    }

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    class Address_Update extends AsyncTask<String, String, String> {

        public String id_address;
        String status;


        /**
         * Before starting background thread Show Progress Dialog *
         */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateAddress.this);
            pDialog.setTitle("Update Address");
            pDialog.setMessage("Updating address !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //params.add(new BasicNameValuePair("id_customer", Cust_Id));
            params.add(new BasicNameValuePair("id_address", Id_Address));
            params.add(new BasicNameValuePair("firstname",fname.getText().toString() ));
            params.add(new BasicNameValuePair("lastname",lname.getText().toString() ));
            params.add(new BasicNameValuePair("address1",address.getText().toString()));
            params.add(new BasicNameValuePair("address2",address2.getText().toString()));
            params.add(new BasicNameValuePair("postcode",zip.getText().toString()));
            params.add(new BasicNameValuePair("phone",home_phone.getText().toString()));
            params.add(new BasicNameValuePair("phone_mobile",mobile.getText().toString()));
            //params.add(new BasicNameValuePair("other",additional_info.getText().toString()));
            params.add(new BasicNameValuePair("alias",alias.getText().toString()));
            params.add(new BasicNameValuePair("date_upd",Update_DateTime));
            //Update_DateTime

            // getting JSON string from URL
            JSONObject json_addupdte = jParser_add_update.makeHttpRequest(URL_UPDATE_ADD, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_addupdte.getInt(TAG_SUCCESS);

                if (success == 1) {

                    status="1";
                    // products found
                    // Getting Array of Products
                    //add_ids = json_chkaddexist.getJSONArray(TAG_ADD_DTL);
                    //Log.d("MainActivity", "INSIDE TRY->IF block, value of products :" +add_ids);

/*
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
                    */

                } else {

                    status = "0";

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

            return status;
        }

        //  After completing background task Dismiss the progress dialog


        protected void onPostExecute(final String status) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {
                    //for(Map<String, String> map : productsList) {
                    //  String tag_quantity = map.get(TAG_QUANTITY);
                    //String tag_id = map.get(TAG_ID_PRODUCT);
                    // pids = pids.concat(tag_id);
                    // pids = pids.concat(",");
                    //  }
                    //int pr_len = AddList.size();
                    if (status.equalsIgnoreCase("1")){
                        Toast.makeText(getApplicationContext(), "Successfully Updated...", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                final Intent GoToMyAddress = new Intent(UpdateAddress.this,MyAddressActivity.class);
                                startActivity(GoToMyAddress);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                UpdateAddress.this.finish();
                            }
                        }, 600);

                    } else {
                        Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
                    }

                    /*if (pr_len == 0) {
                        //Toast.makeText(getApplicationContext(), "No Address for given Customer Id...", Toast.LENGTH_SHORT).show();
                        //present here address registration form

                        Intent i = new Intent(getApplicationContext(), AddressRegistrationActivity.class);
                        status_flag = 1;
                        i.putExtra("status_flag", status_flag);
                        startActivity(i);

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
                            }
                        });


                    }
                    */


                }
            });

        }

    }


    // Background Async Task to fetch order_ids corresponding to customer ids
    class GetAddId extends AsyncTask<String, String, String> {

        JSONArray addrs_dtls =null;

        String orig_fname,orig_lname,orig_add1,orig_add2,orig_pin,orig_mob,orig_phone,orig_alias;

        // Before starting background thread Show Progress Dialog
        @Override
        protected void onPreExecute() {

            super.onPreExecute();

            pDialog = new ProgressDialog(UpdateAddress.this);
            pDialog.setTitle("Fetching Address");
            pDialog.setMessage("Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        // getting All products from url

        protected String doInBackground(String... args)
        {
            String Address_Id="";
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", Cust_Id));
            params.add(new BasicNameValuePair("id_address", Id_Address));

            // getting JSON string from URL
            JSONObject json_add_id = jParser_gt_add_id.makeHttpRequest(URL_GET_ADD_ID, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_add_id.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    Address_Id = json_add_id.getString(TAG_ADDRESS);

                }

                int success_add = json_add_id.getInt("success_add") ;

                if(success_add==1){
                    addrs_dtls = json_add_id.getJSONArray("address");

                    // looping through All Orders
                    for (int i = 0; i < addrs_dtls.length(); i++) {
                        JSONObject c = addrs_dtls.getJSONObject(i);

                        // Storing each json item in variable
                        orig_fname = c.getString("firstname");
                        orig_lname = c.getString("lastname");
                        orig_add1 = c.getString("address1");
                        orig_add2 = c.getString("address2");
                        orig_pin = c.getString("postcode");
                        orig_mob = c.getString("phone_mobile");
                        orig_phone = c.getString("phone");
                        orig_alias = c.getString("alias");

                    }


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Address_Id;
        }


        protected void onPostExecute(String Address_Id) {
            pDialog.dismiss();
            final String Add_id = Address_Id;

            runOnUiThread(new Runnable() {
                public void run() {
                    int add_count = Integer.parseInt(Add_id)+1;
                    //alias.setText("Home "+add_count);

                    fname.setText(orig_fname);
                    lname.setText(orig_lname);
                    address.setText(orig_add1);
                    address2.setText(orig_add2);
                    zip.setText(orig_pin);
                    home_phone.setText(orig_phone);
                    mobile.setText(orig_mob);
                    alias.setText(orig_alias);

                } });


        }


    }

}