package com.exfresh.exfreshapp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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


/**
 * Created by Mahendra on 4/12/2015.
 */
public class UpdateProfileActivity extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String KEY_EMAIL ="Pref_Cus_Email";
    String KEY_FNAME ="Pref_Cus_First_Name";
    String KEY_PASSWORD = "Pref_Customer_Password";


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    String C_Cust_Id;
    String C_Cust_Passwd;

    Calendar myCalendar = Calendar.getInstance();

    EditText fname,lname,email,dob,cur_password,chg_password;

    String MAIN_URL;
    String URL_PRE;
    String URL_UPDT_PRE;

    //static final String URL_PRE = "http://192.168.1.31/prestashop16/api/customers?display=[id,firstname,lastname,email,birthday" +
      //      ",passwd,date_add,date_upd,id_shop,id_shop_group,id_default_group,id_lang]&filter[id]=[";

    //static final String URL_UPDT_PRE = "http://192.168.1.31/prestashop16/api/customers/";

    static String URL_UPDATE;
    // Progress Dialog
    private ProgressDialog pDialog;

    String Customer_first_name ;
    String Customer_last_name ;
    String Customer_email ;
    String Customer_dob ;
    String Customer_Cur_password ;
    String Cr_DateTime ;
    String Update_DateTime ;
    String Customer_id_shop ;
    String Customer_id_shop_grp ;
    String Customer_id_default_grp ;
    String Customer_id_lang ;
    String Up_DateTime;

    String current_passwd ;

    String DEFAULT_USER ;
    //String cookie ;
    //String final_pass_frm_app ;
    //String md5_passwd ;

    Button btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updateprofile);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        addListenerOnButton();


        //Log.d("value of datetime",currentDateandTime);

        //Initialise sharedPreferences Object
        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        Intent intent ;
        editor = preferences.edit();

        // Got cust_id from SharedPreferences
        C_Cust_Id = preferences.getString("Pref_Customer_Id", null);
        C_Cust_Passwd = preferences.getString("Pref_Customer_Password",null);
        MAIN_URL = preferences.getString("Pref_main_url", null);

        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);

        URL_PRE = MAIN_URL.concat("/api/customers?display=[id,firstname,lastname,email,birthday" +
                ",passwd,date_add,date_upd,id_shop,id_shop_group,id_default_group,id_lang]&filter[id]=[");
        URL_UPDT_PRE = MAIN_URL.concat("/api/customers/");

        String URL_POST = C_Cust_Id+"]";
        String URL_UPDT_POST = C_Cust_Id;

        final String URL = URL_PRE.concat(URL_POST);
        URL_UPDATE = URL_UPDT_PRE.concat(URL_UPDT_POST);


        //static final String URL_UPDT_PRE = "http://192.168.1.31/prestashop16/api/customers/";


        dob = (EditText)findViewById(R.id.update_dob);
        cur_password =(EditText)findViewById(R.id.cur_password);

        dob.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    dob.setInputType(InputType.TYPE_NULL);
                    new DatePickerDialog(UpdateProfileActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                    dob.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            dob.setInputType(InputType.TYPE_NULL);
                            new DatePickerDialog(UpdateProfileActivity.this, date, myCalendar
                                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                        }
                    });


                } else {
                    //Hide your calender here
                }
            }
        });
        ////////////////next focus

        ///////////////////next focus end


    // Making DOM

    fname=(EditText)findViewById(R.id.update_firstname);
    lname=(EditText)findViewById(R.id.update_lastname);
    email=(EditText)findViewById(R.id.update_email);
    dob=(EditText)findViewById(R.id.update_dob);
    current_passwd = cur_password.getText().toString();
    chg_password=(EditText)findViewById((R.id.chg_password));
    //chg_password_confirm=(EditText)findViewById((R.id.chg_password_retype));



    //fetching and displaying value in editboxes
    String xml="";
    ConnectXML parser = new ConnectXML();
    try{
        xml = parser.execute(URL).get();
    }

    catch (InterruptedException e){e.printStackTrace();

    }catch(ExecutionException e){e.printStackTrace();}

    Document doc = parser.getDomElement(xml);


    Element element = doc.getDocumentElement();
    element.normalize();

    NodeList nList = doc.getElementsByTagName("customer");


    if (nList.getLength() != 0) {
        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element2 = (Element) node;

                Customer_first_name = getValue("firstname", element2);
                Customer_last_name = getValue("lastname", element2);
                Customer_email = getValue("email", element2);
                Customer_dob = getValue("birthday", element2);
                Customer_Cur_password = getValue("passwd", element2);

                Cr_DateTime = getValue("date_add", element2);
                Customer_id_shop = getValue("id_shop", element2);
                Customer_id_shop_grp = getValue("id_shop_group", element2);
                Customer_id_default_grp = getValue("id_default_group", element2);
                Customer_id_lang = getValue("id_lang", element2);
                Up_DateTime =getValue("date_upd", element2);

                Log.d("creae date", Cr_DateTime);
                Log.d("update date", Up_DateTime);
                Log.d("bday ",Customer_dob);

            }
        }
        String format_dob = convertDate(Customer_dob);

        fname.setText(Customer_first_name);
        lname.setText(Customer_last_name);
        email.setText(Customer_email);
        dob.setText(format_dob);
        //cur_password.setText(Customer_Cur_password);
    }










 }

    public void addListenerOnButton()
    {
        btn_update = (Button)findViewById(R.id.btnUpdate);

        btn_update.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {

try {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Update_DateTime = sdf.format(new Date());

    //String pp = dob.getText().toString();
    String modified_dob = convertDate2(dob.getText().toString());
    String current_password = cur_password.getText().toString();
    String Chg_password_confirm = chg_password.getText().toString();

    if( fname.getText().toString().length() == 0  )
        fname.setError( "First name is required!" );

    if (!isValidName(fname.getText().toString()))
        fname.setError("Only Characters allowed");


    if( lname.getText().toString().length() == 0  )
        lname.setError( "Last name is required!" );

    if (!isValidName(lname.getText().toString()))
        lname.setError("Only Characters allowed");

    if( email.getText().toString().length() == 0  )
        email.setError( "Email is required!" );


    if (!isValidEmail(email.getText().toString())) {
        email.setError("Invalid Email");
    }

    if( cur_password.getText().toString().length() == 0  )
        cur_password.setError( "Password is required!" );

    if( chg_password.getText().toString().length() == 0  )
        chg_password.setError( "Confirm Password is required!" );

    if (cur_password.getText().toString().length()<6){
        cur_password.setError("Minimum password length is 6");
    }

    if (!isValidPassword(current_password,Chg_password_confirm)) {
        chg_password.setError("Password Mismatch");
        //alert.showAlertDialog(RegActivity.this,"Password Mismatch", "The two Passwords do not match", false);
        //alert.showAlertDialog(LoginActivity.this, "Login failed..", "Email/Password is incorrect", false);
    }



    if (fname.getText().toString().length() > 0 && lname.getText().toString().length() > 0 && email.getText().toString().length() > 0 && isValidName(fname.getText().toString())==true && isValidName(lname.getText().toString())==true
            && isValidPassword(current_password,Chg_password_confirm)==true && cur_password.getText().toString().length()>5 && isValidEmail(email.getText().toString())==true)
    {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.newDocument();

    Element rootElement = document.createElement("prestashop");
    document.appendChild(rootElement);

    Element customerElement = document.createElement("customer");
    rootElement.appendChild(customerElement);

    //Calendar c = Calendar.getInstance();
    //int seconds = c.get(Calendar.HOUR);

//    Time now = new Time();
    //   now.setToNow();


    //ID
    Element id_Element = document.createElement("id");
    customerElement.appendChild(id_Element);
    id_Element.appendChild(document.createTextNode(C_Cust_Id));

    //ID Default Group
    Element id_default_group_Element = document.createElement("id_default_group");
    customerElement.appendChild(id_default_group_Element);
    id_default_group_Element.appendChild(document.createTextNode(Customer_id_default_grp));

    //ID Lang
    Element id_lang_Element = document.createElement("id_lang");
    customerElement.appendChild(id_lang_Element);
    id_lang_Element.appendChild(document.createTextNode(Customer_id_lang));

    //ID Shop
    Element id_shop_Element = document.createElement("id_shop");
    customerElement.appendChild(id_shop_Element);
    id_shop_Element.appendChild(document.createTextNode(Customer_id_shop));

    //ID Shop Group
    Element id_shop_group_Element = document.createElement("id_shop_group");
    customerElement.appendChild(id_shop_group_Element);
    id_shop_group_Element.appendChild(document.createTextNode(Customer_id_shop_grp));

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

    Element dob_Element = document.createElement("birthday");
    customerElement.appendChild(dob_Element);
    dob_Element.appendChild(document.createTextNode(modified_dob));


    Element passwdElement = document.createElement("passwd");
    customerElement.appendChild(passwdElement);
    passwdElement.appendChild(document.createTextNode(cur_password.getText().toString()));


    // Active node in xml for status in pres.customer tabel
    Element active_Element = document.createElement("active");
    customerElement.appendChild(active_Element);
    active_Element.appendChild(document.createTextNode("1"));

    // Date Time node in xml for status in pres.customer tabel
    Element cr_datetime_Element = document.createElement("date_add");
    customerElement.appendChild(cr_datetime_Element);
    cr_datetime_Element.appendChild(document.createTextNode(Cr_DateTime));

    // Date Time node in xml for status in pres.customer tabel
    Element upd_datetime_Element = document.createElement("date_upd");
    customerElement.appendChild(upd_datetime_Element);
    upd_datetime_Element.appendChild(document.createTextNode(Update_DateTime));


    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer = transformerFactory.newTransformer();
    DOMSource source = new DOMSource(document.getDocumentElement());
    StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
            .getAbsolutePath() + "/" + "update_profile.xml"));
    transformer.transform(source, result);

    new UpdateCustInfo().execute();
   }


            }
            catch (ParserConfigurationException e) {
            } catch (TransformerConfigurationException e) {
            } catch (TransformerException e) {
            }




            }
        });

    }
//////////////////////////////////////////////////////////////////

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

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();

    }


    public String computeMD5Hash(String password) {
        String md_ret = null;

        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            md_ret = MD5Hash.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return md_ret;

    }

    private String convertDate(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d = format.parse(date);
            SimpleDateFormat convertedFormat = new SimpleDateFormat("dd-MMM, yyyy");
            return convertedFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    private String convertDate2(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM, yyyy");
            Date d = format.parse(date);
            SimpleDateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd");
            return convertedFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }




    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };



    private void updateLabel() {

        String myFormat = "dd-MMM, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dob.setText(sdf.format(myCalendar.getTime()));
    }


    // Background Async Task to fetch order_ids corresponding to customer ids
    class UpdateCustInfo extends AsyncTask<String, String, Integer> {

        /** Before starting background thread Show Progress Dialog * */
     Integer status =0;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(UpdateProfileActivity.this);
            pDialog.setTitle("Your Account Details");
            pDialog.setMessage("Updating your account details !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected Integer doInBackground(String... args)
        {
            try
            {    //****post your file to server*****

                DefaultHttpClient httpClient = new DefaultHttpClient();

                HttpPut httpput = new HttpPut(URL_UPDATE);
                //String WebSer_username = "9KRJBZFZN224WMFSLFJMDKS449ULFU49";
                String WbSr_password = "";// leave it empty
                String authToBytes = DEFAULT_USER + ":" + WbSr_password;
                byte authBytes[] = Base64.encode(authToBytes.getBytes(), Base64.NO_WRAP);
                String authBytesString = new String(authBytes);
                httpput.setHeader("Authorization", "Basic " + authBytesString);
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File f=new File(filePath,"update_profile.xml");

                String content = getFileContent(f);

                StringEntity se = new StringEntity(content, HTTP.UTF_8);
                se.setContentType("text/xml");
                httpput.setEntity(se);
                f.delete();

                HttpResponse httpresponse = httpClient.execute(httpput);
                int st_code = httpresponse.getStatusLine().getStatusCode();
                if (MyDebug.LOG) {
                    Log.d("status code ", String.valueOf(st_code));
                }

                if (httpresponse.getStatusLine().getStatusCode() == 200) {
                    if (MyDebug.LOG) {
                        Log.d("response ok", "ok response :/");
                    }
                    status=1;
                    editor.putString(KEY_EMAIL, email.getText().toString());
                    editor.putString(KEY_PASSWORD,cur_password.getText().toString());
                    editor.putString(KEY_FNAME, fname.getText().toString());
                    editor.commit();


                    //Toast.makeText(getApplicationContext(), "User Created", Toast.LENGTH_SHORT).show();
                } else {
                    if (MyDebug.LOG) {
                        Log.d("response not ok", "Something went wrong :/");
                    }
                    status=0;
                    //Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                status =3;    //
                return status;
            }

            return status;

        }

        //  After completing background task Dismiss the progress dialog


        protected void onPostExecute(Integer statusop) {
           final int status_post = statusop;
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    /*
                    if (status_post == 0) {
                        Toast.makeText(getApplicationContext(), "No Orders ...", Toast.LENGTH_SHORT).show();
                    }
                    */

                    switch (status_post){

                        case 0:
                            Toast.makeText(getApplicationContext(), "Something went wrong ...", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(getApplicationContext(), "Connection Error ...", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            Toast.makeText(getApplicationContext(), "Successfully Updated...", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Category.this.finish();
                                    // Staring Categories Activity
                                    Intent i = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                                    startActivity(i);
                                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    finish();
                                }
                            }, 1400);
                            break;
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
            }
            catch(IOException e) {
                e.printStackTrace();

            }

        }
        reader.close();
        inputStream.close();

        return stringBuilder.toString();

    }

}


/*

    private Date convertDate2(String date) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date d = format.parse(date);
            SimpleDateFormat convertedFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            return d;
            //return convertedFormat.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
        //return "";
    }
 */