package com.exfresh.exfreshapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import android.widget.AdapterView.OnItemSelectedListener;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
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
import java.util.ArrayList;
import java.util.List;

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
 * Created by Mahendra on 4/4/2015.
 */
public class AddressRegistrationActivity extends ActionBarActivity implements OnItemSelectedListener  {

    Button btnsubmit;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String DEFAULT_USER;

    public int status_flag=0;

    public String Cust_Id;
    public String Cart_Id;

    // Creating JSON Parser object
    JSONParser_cart jParser_gt_add_id = new JSONParser_cart();

    // Progress Dialog
    private ProgressDialog pDialog;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    //JSONArray add_ids = null;
    private static final String TAG_ADDRESS = "addrs";

    String MAIN_URL;
    String GET_URL;
    String POST_URL;

    static public String City_frm_spinner;

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    EditText alias;

    String URL_PHP;
    String GET_ADD_URL;

    String Total_Order_Price;

    String Customer_First_Name;
    String Customer_Last_Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_registration);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        status_flag = extras.getInt("status_flag");

        if (status_flag ==2){

            Total_Order_Price = extras.getString("Total_Order_Price");
        }

        alias=(EditText)findViewById((R.id.EditAliasAdd));

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        Cust_Id = preferences.getString("Pref_Customer_Id", null);
        Cart_Id = preferences.getString("Pref_Cart_Id", null);

        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);

        MAIN_URL = preferences.getString("Pref_main_url", null);
        GET_URL = MAIN_URL.concat("/api/customers?schema=blank");
        POST_URL = MAIN_URL.concat("/api/addresses?schema=blank");

        URL_PHP = preferences.getString("Pref_url_php", null);
        GET_ADD_URL = URL_PHP.concat("Get_Address_Sync_No.php");


        Customer_First_Name = preferences.getString("Pref_Cus_First_Name", "");
        Customer_Last_Name = preferences.getString("Pref_Cus_Last_Name", "");


        addListenerOnButton();


        // Spinner element
        Spinner city_spinner = (Spinner) findViewById(R.id.SpinnerCity);

        // Spinner click listener
        city_spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements for city
        List<String> cities = new ArrayList<String>();
        cities.add("Jaipur");
        cities.add("Boca Raton");

        // Creating city adapter for spinner city
        ArrayAdapter<String> city_dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item2, cities);

        // Drop down layout style - list view with radio button
        city_dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching city data adapter to spinner city
        city_spinner.setAdapter(city_dataAdapter);

        new GetAddId().execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        // Showing selected spinner item
        switch (parent.getId()) {
            case R.id.SpinnerCity:
                // do stuffs with you spinner 1
                City_frm_spinner = parent.getItemAtPosition(position).toString();
                break;
            default:
                break;
        }

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reg, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }


    public void addListenerOnButton()
    {
        btnsubmit = (Button)findViewById(R.id.ButtonAddSubmit);

        btnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    EditText address, home_phone, mobile;
                    address = (EditText) findViewById(R.id.EditAddress);
                    home_phone = (EditText) findViewById((R.id.EditHomePhone));
                    mobile = (EditText) findViewById(R.id.EditMobilePhone);

                    String Zip = "302018";
                    String Address1 = address.getText().toString();
                    if (Address1.length() == 0) {
                        address.setError("Address name cannot be blank");
                    }

                    String Mobile = mobile.getText().toString();
                    if (Mobile.length() == 0) {
                        mobile.setError("Mobile cannot be blank");
                    }
                    if (Mobile.length() < 10 || Mobile.length() > 10) {
                        mobile.setError("Invalid mobile number");
                    }

                    String Mobile2 = home_phone.getText().toString();
                    if (Mobile2.length() > 10) {
                        mobile.setError("Invalid number");
                    }

                    String Alias = alias.getText().toString();
                    if (Alias.length() == 0) {
                        alias.setError("Alias cannot be blank");
                    }

                    if (Address1.length() > 0 && Mobile.length() > 0 && Mobile.length() == 10
                            && Mobile2.length() < 11 && Alias.length() > 0)
                    {
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.newDocument();
                    Element rootElement = document.createElement("prestashop");
                    document.appendChild(rootElement);
                    Element customerElement = document.createElement("address");
                    rootElement.appendChild(customerElement);

                    //ID Customer Group
                    Element id_customerElement = document.createElement("id_customer");
                    customerElement.appendChild(id_customerElement);
                    id_customerElement.appendChild(document.createTextNode(Cust_Id));

                    //Id_Country
                    Element id_countryElement = document.createElement("id_country");
                    customerElement.appendChild(id_countryElement);
                    id_countryElement.appendChild(document.createTextNode("110"));

                    //Id_State
                    Element id_stateElement = document.createElement("id_state");
                    customerElement.appendChild(id_stateElement);
                    id_stateElement.appendChild(document.createTextNode("334"));


                    //Alias
                    Element aliasElement = document.createElement("alias");
                    customerElement.appendChild(aliasElement);
                    aliasElement.appendChild(document.createTextNode(alias.getText().toString()));


                    // Last Name
                    Element lnameElement = document.createElement("lastname");
                    customerElement.appendChild(lnameElement);
                    lnameElement.appendChild(document.createTextNode(Customer_Last_Name));


                    //String Firstname = fname.getText().toString();
                    Element fnameElement = document.createElement("firstname");
                    customerElement.appendChild(fnameElement);
                    fnameElement.appendChild(document.createTextNode(Customer_First_Name));


                    //Address-1
                    Element address1Element = document.createElement("address1");
                    customerElement.appendChild(address1Element);
                    address1Element.appendChild(document.createTextNode(address.getText().toString()));

                    //PostCode
                    Element postcodeElement = document.createElement("postcode");
                    customerElement.appendChild(postcodeElement);
                    postcodeElement.appendChild(document.createTextNode(Zip));

                    //City
                    Element cityElement = document.createElement("city");
                    customerElement.appendChild(cityElement);
                    cityElement.appendChild(document.createTextNode(City_frm_spinner));


                    //Phone
                    Element phoneElement = document.createElement("phone");
                    customerElement.appendChild(phoneElement);
                    phoneElement.appendChild(document.createTextNode(home_phone.getText().toString()));

                    //Mobile
                    Element mobileElement = document.createElement("phone_mobile");
                    customerElement.appendChild(mobileElement);
                    mobileElement.appendChild(document.createTextNode(mobile.getText().toString()));

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    DOMSource source = new DOMSource(document.getDocumentElement());
                    StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/" + "file.xml"));
                    transformer.transform(source, result);
                    new Connect_Post().execute();

                }

                } catch (ParserConfigurationException e) {
                } catch (TransformerConfigurationException e) {
                } catch (TransformerException e) {
                }
            }

            // validating password with retype password
            private boolean isValidPassword(String pass, String pass_retype) {
                if ( pass.equals(pass_retype)) {
                    return true;
                }
                return false;
            }

            class Connect_Post extends AsyncTask<String, String, String> {
               String post_status;
                protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog = new ProgressDialog(AddressRegistrationActivity.this);
                    pDialog.setTitle("Address Registration");
                    pDialog.setMessage("Registering your address !! Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                protected String doInBackground(String... args) {
                    try
                    {
                        DefaultHttpClient httpClient = new DefaultHttpClient();

                        HttpPost httppost = new HttpPost(POST_URL);
                        String WbSr_password = "";// leave it empty
                        String authToBytes = DEFAULT_USER + ":" + WbSr_password;
                        byte authBytes[] = Base64.encode(authToBytes.getBytes(), Base64.NO_WRAP);
                        String authBytesString = new String(authBytes);
                        httppost.setHeader("Authorization", "Basic " + authBytesString);
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File f=new File(filePath,"file.xml");

                        String content = getFileContent(f);

                        StringEntity se = new StringEntity(content, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        f.delete();

                        HttpResponse httpresponse = httpClient.execute(httppost);

                        if (httpresponse.getStatusLine().getStatusCode() == 201) {

                            if (MyDebug.LOG) {

                                Log.d("response ok", "ok response :/");
                            }

                            post_status="1";
                        } else {
                            if (MyDebug.LOG) {
                                Log.d("response not ok", "Something went wrong :/");
                            }
                            post_status="0";
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        return "Exception";
                    }
                    return post_status;
                }
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    pDialog.dismiss();

                    if(result.equals("0"))
                    {
                        Toast.makeText(getApplicationContext(), "Unknown error occurred..", Toast.LENGTH_LONG).show();
                    }
                    else if(result.equals("1")) {


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (status_flag==1){
                                Toast.makeText(getApplicationContext(), "Address Registered Successfully!! Redirecting to address menu", Toast.LENGTH_SHORT).show();
                                Intent k = new Intent(getApplicationContext(), MyAddressActivity.class);
                                startActivity(k);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();

                                }
                                else if (status_flag==2){
                                Toast.makeText(getApplicationContext(), "Address Registered Successfully!! Redirecting to choose address", Toast.LENGTH_SHORT).show();

                                    Intent j = new Intent(getApplicationContext(), CheckOutAddressActivity.class);
                                    j.putExtra("Total_Order_Price", Total_Order_Price);
                                    startActivity(j);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    AddressRegistrationActivity.this.finish();
                                }
                            }
                        }, 1400);
                    }
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
                    }
                    catch(IOException e) {
                        e.printStackTrace();

                    }

                }
                reader.close();
                inputStream.close();

                return stringBuilder.toString();

            }

        });

    }


    class GetAddId extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args)
        {
            String Address_Id="";

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", Cust_Id));

            // getting JSON string from URL
            JSONObject json_add_id = jParser_gt_add_id.makeHttpRequest(GET_ADD_URL, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_add_id.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    Address_Id = json_add_id.getString(TAG_ADDRESS);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return Address_Id;
        }


        protected void onPostExecute(String Address_Id) {
            final String Add_id = Address_Id;

            runOnUiThread(new Runnable() {
                public void run() {
                    if (Add_id.equalsIgnoreCase("")){
                        alias.setText("Home 1");
                    }else{
                    int add_count = Integer.parseInt(Add_id)+1;
                    alias.setText("Home "+add_count);
                    }
                }
            });
        }
    }

}
