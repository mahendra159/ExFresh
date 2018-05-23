package com.exfresh.exfreshapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


public class RegActivity extends ActionBarActivity {


    Button btn_submit;
    ProgressDialog pDialog;


    String KEY_FNAME ="Pref_Cus_First_Name";
    String KEY_LNAME = "Pref_Cus_Last_Name";

    String Customer_First_Name;
    String Customer_Last_Name;


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    //http://www.exfresh.co.in


    //static final String GET_URL = "http://192.168.1.31/prestashop16/api/customers?schema=blank";
    static final String KEY_FIRST_NAME = "fname"; //parent node
    static final String KEY_LAST_NAME = "lname";
    //static final String KEY_EMAIL = "email";
    static final String KEY_PASSWORD = "password";

    // for autologin

    //for autologin
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String KEY_LOGIN_NAME = "LoginStatus";
    String KEY_ID = "Pref_Customer_Id";
    String KEY_EMAIL ="Pref_Cus_Email";

    // String declarations
    String E_MAIL;
    String Customer_Id;
    //private static String url_fetch_cust_id = "http://www.exfresh.co.in/fetch_cust_id.php";
    //private static String url_fetch_cust_id = "http://192.168.1.31/prestashop16/fetch_cust_id.php";

    String MAIN_URL;
    String POST_URL;
    String FETCH_CUST_ID_URL;
    String URL_PHP;
    String ADMIN_NOTIFY_MAIL_URL;

    // Creating JSON Parser object
    JSONParser_cart jParser = new JSONParser_cart();


    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_CUSTOMER_ID = "customer_id";
    //static final String TAG_CUSTOMER_ID = "id_customer";


    JSONParser_cart jparser_fetch_cust_id = new JSONParser_cart();

    // products JSONArray
    JSONArray cust_ids = null;

    String DEFAULT_USER;

    EditText email;
    String emailacc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
            //do something here



            email = (EditText) findViewById(R.id.reg_email);
            emailacc = getEmail(this);
            email.setText(emailacc);
            //fname=(EditText)findViewById(R.id.reg_firstname);
            //fname.setKeyListener(DigitsKeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZ"));

            //Initialise sharedPreferences Object
            preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
            editor = preferences.edit();

            DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);
            MAIN_URL = preferences.getString("Pref_main_url", null);
            POST_URL = MAIN_URL.concat("/api/customers?schema=synopsis");
            URL_PHP = preferences.getString("Pref_url_php", null);
            FETCH_CUST_ID_URL = URL_PHP.concat("fetch_cust_id.php");
            ADMIN_NOTIFY_MAIL_URL = URL_PHP.concat("Admin_Customer_Notify_Mail.php");
            //PRODUCT_URL = MAIN_URL.concat("/api/products?display=full");


            addListenerOnButton();

            TextView loginScreen = (TextView) findViewById(R.id.link_to_login);

            // Listening to login link
            loginScreen.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    //Switching to Login screen
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            });
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

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_settings) {
        //    return true;
        // }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnButton()
    {
        btn_submit = (Button)findViewById(R.id.btnRegister);

        btn_submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {

                String URL_POST = "/api/customers/?display=[email]&filter[email]=[" + email.getText().toString() + "]";
                String Reg_Check_URL = MAIN_URL.concat(URL_POST);
                String xml2 = "";

                ConnectXML parser2 = new ConnectXML();
                try {
                    xml2 = parser2.execute(Reg_Check_URL).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Document doc = parser2.getDomElement(xml2);

                Element element = doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("customer");

                try {

                    if (nList.getLength() != 0){

                        alert.showAlertDialog(RegActivity.this,"Email already exist", "This email is already registered with us. If you forgot password then choose 'forgot password' from login screen.", false);
                    }
                    else{
                        EditText fname, lname, password, password_confirm;
                        fname = (EditText) findViewById(R.id.reg_firstname);
                        lname = (EditText) findViewById(R.id.reg_lastname);

                        password = (EditText) findViewById(R.id.reg_password);
                        password_confirm = (EditText) findViewById((R.id.reg_password_retype));
                        String passwd = password.getText().toString();
                        String passwd_retype = password_confirm.getText().toString();

                        Customer_First_Name = fname.getText().toString();
                        Customer_Last_Name = lname.getText().toString();

                        String F_Name = fname.getText().toString();
                        if (!isValidName(F_Name)) {
                            fname.setError("Only Characters allowed");
                        }
                    /*
                    fname.setFilters(new InputFilter[]{
                            new InputFilter() {
                                public CharSequence filter(CharSequence src, int start,
                                                           int end, Spanned dst, int dstart, int dend) {
                                    if (src.equals("")) { // for backspace
                                        return src;
                                    }
                                    if (src.toString().matches("[a-zA-Z ]+")) {
                                        return src;
                                    }
                                    return "";
                                }
                            }
                    });
                    */
                        if (F_Name.length() == 0) {
                            fname.setError("First name cannot be blank");
                        }

                        String L_Name = lname.getText().toString();
                        if (!isValidName(L_Name)) {
                            lname.setError("Only Characters allowed");
                        }
                        if (L_Name.length() == 0) {
                            lname.setError("Last name cannot be blank");
                        }

                        E_MAIL = email.getText().toString();
                        if (!isValidEmail(E_MAIL)) {
                            email.setError("Invalid Email");
                        }
                        if (E_MAIL.length() == 0) {
                            email.setError("Email cannot be blank");
                        }

                        if (passwd.length() == 0) {
                            password.setError("Password cannot be left blank");
                        }
                        if (passwd.length() > 0 && passwd.length() < 6) {
                            password.setError("Minimum password length is 6");
                        }
                        if (!isValidPassword(passwd, passwd_retype)) {
                            password_confirm.setError("Password Mismatch");
                            //alert.showAlertDialog(RegActivity.this,"Password Mismatch", "The two Passwords do not match", false);
                            //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Email/Password is incorrect", false);
                        }

//&& isValidName(F_Name)==true

                        if (F_Name.length() > 0 && L_Name.length() > 0 && E_MAIL.length() > 0 && isValidName(L_Name) == true && isValidName(F_Name) == true
                                && isValidPassword(passwd, passwd_retype) == true && passwd.length() > 5 && isValidEmail(E_MAIL) == true) {

                            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                            Document document = documentBuilder.newDocument();
                            Element rootElement = document.createElement("prestashop");
                            document.appendChild(rootElement);
                            Element customerElement = document.createElement("customer");
                            rootElement.appendChild(customerElement);

                            //ID Default Group
                            Element id_default_grpElement = document.createElement("id_default_group");
                            customerElement.appendChild(id_default_grpElement);
                            id_default_grpElement.appendChild(document.createTextNode("3"));

                            //String Firstname = fname.getText().toString();
                            Element fnameElement = document.createElement("firstname");
                            customerElement.appendChild(fnameElement);
                            fnameElement.appendChild(document.createTextNode(fname.getText().toString()));

                            Element lnameElement = document.createElement("lastname");
                            customerElement.appendChild(lnameElement);
                            lnameElement.appendChild(document.createTextNode(lname.getText().toString()));

                            Element emailElement = document.createElement("email");
                            customerElement.appendChild(emailElement);
                            emailElement.appendChild(document.createTextNode(email.getText().toString()));

                            Element passwdElement = document.createElement("passwd");
                            customerElement.appendChild(passwdElement);
                            passwdElement.appendChild(document.createTextNode(password.getText().toString()));


                            // Active node in xml for status in pres.customer tabel
                            Element active_Element = document.createElement("active");
                            customerElement.appendChild(active_Element);
                            active_Element.appendChild(document.createTextNode("1"));


                            TransformerFactory transformerFactory = TransformerFactory.newInstance();
                            Transformer transformer = transformerFactory.newTransformer();
                            DOMSource source = new DOMSource(document.getDocumentElement());
                            StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
                                    .getAbsolutePath() + "/" + "file.xml"));
                            transformer.transform(source, result);
                            new Connect_Post().execute();
                        }else{
                            alert.showAlertDialog(RegActivity.this,"Wrong Entries", "Please correct all the entries and submit again. ", false);
                        }

                        //byte[] data = FileOperator.readBytesFromFile(f);
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

            class Connect_Post extends AsyncTask<String, String, String> {

                protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog = new ProgressDialog(RegActivity.this);
                    pDialog.setTitle("New Customer Registration");
                    pDialog.setMessage("Registering !! Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                protected String doInBackground(String... args) {
                    String status ;

                    try

                    {
                        //             ****post your file to server*****

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
                            status ="1";
                        } else {
                            status ="0";
                        }
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                        return "Exception";
                    }
                    return status;
                }
                @Override
                protected void onPostExecute(String status) {
                    //super.onPostExecute(status);
                    pDialog.dismiss();

                    if(status.equals("0"))
                    {
                        Toast.makeText(getApplicationContext(), "Unknown error occurred..", Toast.LENGTH_LONG).show();
                    }
                    else if(status.equals("1")) {
                        Toast.makeText(getApplicationContext(), "New Customer Registered Successfully", Toast.LENGTH_SHORT).show();

                        /*
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent j = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(j);
                                RegActivity.this.finish();
                            }
                        }, 1400);
                        */

                        //Code for autologin from here

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new SetLogin().execute();
                            }
                        }, 2400);


                    }
                    //contact us
                    new Send_Welcome_Mail().execute();
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

    // Background Async Task for AutoLogin
    class SetLogin extends AsyncTask<String, String, String> {

        String status;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(RegActivity.this);
            pDialog.setTitle("Logging In");
            pDialog.setMessage("Taking you to home !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", E_MAIL));

            // getting JSON string from URL
            JSONObject json_fetch_cust_id = jparser_fetch_cust_id.makeHttpRequest(FETCH_CUST_ID_URL, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_fetch_cust_id.getInt(TAG_SUCCESS);

                if (success == 1) {

                    try{
                        Customer_Id = json_fetch_cust_id.getString(TAG_CUSTOMER_ID);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // Storing email,cust_id in pref
                    editor.putBoolean(KEY_LOGIN_NAME, true);
                    editor.putString(KEY_ID, Customer_Id);
                    editor.putString(KEY_EMAIL, E_MAIL);
                    editor.putString(KEY_FNAME, Customer_First_Name);
                    editor.putString(KEY_LNAME, Customer_Last_Name);
                    editor.commit();

                    status = "1";
                }
                else {

                    status = "0";

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    if (status.equalsIgnoreCase("1"))
                    {
                        Toast.makeText(getApplicationContext(), "Login Successfull, taking you home...", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent j = new Intent(getApplicationContext(), Category.class);
                                startActivity(j);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                RegActivity.this.finish();
                            }
                        }, 1400);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "AutoLogin error !! Please close app and run again", Toast.LENGTH_SHORT).show();
                    }

                } });


        }

    }



    class Send_Welcome_Mail extends AsyncTask<String, String, String> {
        String status;
        //String CS_MESSAGE = "Hi! A new customer just signed up for an account. Here are the details\nEmail -"+E_MAIL+"\n Name -"+Customer_First_Name+Customer_Last_Name;
        protected void onPreExecute() {
            super.onPreExecute();
            //pDialog = new ProgressDialog(RegActivity.this);
            //pDialog.setTitle("Contact Us");
            //pDialog.setMessage("Please wait...");
            //pDialog.setIndeterminate(false);
            //pDialog.setCancelable(false);
            //pDialog.show();
        }

        protected String doInBackground(String... args) {

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", E_MAIL ));
            params.add(new BasicNameValuePair("fname",Customer_First_Name  ));
            params.add(new BasicNameValuePair("lname",Customer_Last_Name));
            //params.add(new BasicNameValuePair("msg",CS_MESSAGE  ));
            //params.add(new BasicNameValuePair("order_ref",ORDER_REF  ));

            // getting JSON string from URL
            JSONObject json_mail = jParser.makeHttpRequest(ADMIN_NOTIFY_MAIL_URL, "POST", params);

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
            //pDialog.dismiss();

            if (status.equalsIgnoreCase("1")){
                if (MyDebug.LOG) {
                    Log.d("Admin Notify Mail Sent", "PASS");
                }
            }
            else{
                if (MyDebug.LOG) {
                    Log.d("Admin Notify Mail Sent", "FAIL");
                }
            }
        }
    }


}
