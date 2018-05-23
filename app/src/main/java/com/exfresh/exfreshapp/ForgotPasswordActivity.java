package com.exfresh.exfreshapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
 * Created by Mahendra on 5/2/2015.
 */
public class ForgotPasswordActivity extends ActionBarActivity {

    Button submit;
    EditText email;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Progress Dialog
    private ProgressDialog pDialog;

    String MAIN_URL;
    String FORGOT_URL;

    String DEFAULT_USER;

    String new_password;

    String Customer_email;
    String Customer_Id;
    String Customer_first_name;
    String Customer_last_name;

    String Customer_id_shop_group;
    String Customer_id_shop;
    String Customer_id_default_group;
    String Customer_id_lang;
    String Customer_date_add;
    String Customer_birthday;
    String Customer_secure_key;

    // Creating JSON Parser object
    JSONParser_cart jParser_mail = new JSONParser_cart();

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    String URL_PHP;
    String URL_SEND_MAIL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpasswd);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        email = (EditText)findViewById(R.id.forgot_email);
        submit = (Button)findViewById(R.id.forgot_submit_btn);

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        Intent intent ;
        editor = preferences.edit();

        MAIN_URL = preferences.getString("Pref_main_url", null);
        URL_PHP = preferences.getString("Pref_url_php", null);

        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);
        URL_SEND_MAIL = URL_PHP.concat("testmail2.php");

        FORGOT_URL = MAIN_URL.concat("/api/customers?schema=synopsis");


        addListenerOnButton();
    }

    public void addListenerOnButton() {

        submit.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {

                String cust_email_addr = email.getText().toString();
                if (!isValidEmail(cust_email_addr)) {
                    email.setError("Invalid Email");
                }
                if (email.getText().toString().length() == 0)
                    email.setError("Email is required!");


                if (email.getText().toString().length() > 0 && !isValidEmail(cust_email_addr)==false ) {
                String URL_POST = "/api/customers/?display=[id,firstname,lastname,email,id_shop_group,id_shop,id_default_group,id_lang,birthday,date_add,secure_key]&filter[email]=[" + email.getText().toString() + "]";
                FORGOT_URL = MAIN_URL.concat(URL_POST);
                String xml2 = "";

                ConnectXML parser2 = new ConnectXML();
                try {
                    xml2 = parser2.execute(FORGOT_URL).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Document doc = parser2.getDomElement(xml2);

                Element element = doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("email");

                if (nList.getLength() != 0) {
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node node = nList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element2 = (Element) node;

                            Node node_email = element2.getFirstChild();
                            Customer_email = node_email.getNodeValue();

                            //getting id,firstname,lastname
                            NodeList nList2 = doc.getElementsByTagName("customer");

                            for (int j = 0; j < nList2.getLength(); j++) {
                                Node node2 = nList2.item(j);
                                if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                    Element element3 = (Element) node2;
                                    Customer_Id = getValue("id", element3);
                                    Customer_first_name = getValue("firstname", element3);
                                    Customer_last_name = getValue("lastname", element3);

                                    Customer_id_shop_group = getValue("id_shop_group", element3);
                                    Customer_id_shop = getValue("id_shop", element3);
                                    Customer_id_default_group = getValue("id_default_group", element3);
                                    Customer_id_lang = getValue("id_lang", element3);
                                    Customer_birthday = getValue("birthday", element3);
                                    Customer_date_add = getValue("date_add", element3);

                                }
                            }

                            if (cust_email_addr.equals(Customer_email)) {

                                RandomPassword r_password = new RandomPassword(10);
                                new_password = r_password.GeneratePassword();

                                try {
                                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                                    Document document = documentBuilder.newDocument();
                                    Element rootElement = document.createElement("prestashop");
                                    document.appendChild(rootElement);
                                    Element customerElement = document.createElement("customer");
                                    rootElement.appendChild(customerElement);

                                    //Customer ID
                                    Element id_Element = document.createElement("id");
                                    customerElement.appendChild(id_Element);
                                    id_Element.appendChild(document.createTextNode(Customer_Id));

                                    //String Firstname = fname.getText().toString();
                                    Element fnameElement = document.createElement("firstname");
                                    customerElement.appendChild(fnameElement);
                                    fnameElement.appendChild(document.createTextNode(Customer_first_name));

                                    Element lnameElement = document.createElement("lastname");
                                    customerElement.appendChild(lnameElement);
                                    lnameElement.appendChild(document.createTextNode(Customer_last_name));

                                    Element emailElement = document.createElement("email");
                                    customerElement.appendChild(emailElement);
                                    emailElement.appendChild(document.createTextNode(Customer_email));

                                    Element passwdElement = document.createElement("passwd");
                                    customerElement.appendChild(passwdElement);
                                    passwdElement.appendChild(document.createTextNode(new_password));

                                    // Active node in xml for status in pres.customer tabel
                                    Element active_Element = document.createElement("active");
                                    customerElement.appendChild(active_Element);
                                    active_Element.appendChild(document.createTextNode("1"));

                                    //ID Default Group
                                    Element id_default_group_Element = document.createElement("id_default_group");
                                    customerElement.appendChild(id_default_group_Element);
                                    id_default_group_Element.appendChild(document.createTextNode(Customer_id_default_group));

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
                                    id_shop_group_Element.appendChild(document.createTextNode(Customer_id_shop_group));


                                    //birthday
                                    Element dob_Element = document.createElement("birthday");
                                    customerElement.appendChild(dob_Element);
                                    dob_Element.appendChild(document.createTextNode(Customer_birthday));


                                    // Date Time node in xml for status in pres.customer tabel
                                    Element cr_datetime_Element = document.createElement("date_add");
                                    customerElement.appendChild(cr_datetime_Element);
                                    cr_datetime_Element.appendChild(document.createTextNode(Customer_date_add));

                                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                    Transformer transformer = transformerFactory.newTransformer();
                                    DOMSource source = new DOMSource(document.getDocumentElement());
                                    StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
                                            .getAbsolutePath() + "/" + "file.xml"));
                                    transformer.transform(source, result);
                                    //****post your file to server*****
                                    new Connect_Put().execute();

                                } catch (ParserConfigurationException e) {
                                } catch (TransformerConfigurationException e) {
                                } catch (TransformerException e) {
                                }


                            } else {
                                alert.showAlertDialog(ForgotPasswordActivity.this, "Email Match Error", "Email is incorrect", false);
                            }

                        }
                    }
                }
                else {
                    alert.showAlertDialog(ForgotPasswordActivity.this, "Email Incorrect", "This email is not registered with us.", false);
                }
            }

            }


        });
    }



    private static String getValue(String tag
            , Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node.getNodeValue();
    }


    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }



    public class RandomPassword {

        private final char[] symbols;

         {
            StringBuilder tmp = new StringBuilder();
            for (char ch = '0'; ch <= '9'; ++ch)
                tmp.append(ch);
            for (char ch = 'a'; ch <= 'z'; ++ch)
                tmp.append(ch);
            symbols = tmp.toString().toCharArray();
        }

        private final Random random = new Random();

        private final char[] buf;

        public RandomPassword(int length) {
            if (length < 1)
                throw new IllegalArgumentException("length < 1: " + length);
            buf = new char[length];
        }

        public String GeneratePassword() {
            for (int idx = 0; idx < buf.length; ++idx)
                buf[idx] = symbols[random.nextInt(symbols.length)];
            return new String(buf);
        }

    }


    class Connect_Put extends AsyncTask<String, String, String> {
        String status;
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ForgotPasswordActivity.this);
            pDialog.setTitle("Password Reset");
            pDialog.setMessage("Your password is being reset !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try

            {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPut httpput = new HttpPut(FORGOT_URL);
                String WbSr_password = "";
                String authToBytes = DEFAULT_USER + ":" + WbSr_password;
                byte authBytes[] = Base64.encode(authToBytes.getBytes(), Base64.NO_WRAP);
                String authBytesString = new String(authBytes);
                httpput.setHeader("Authorization", "Basic " + authBytesString);
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                File f=new File(filePath,"file.xml");

                String content = getFileContent(f);

                StringEntity se = new StringEntity(content, HTTP.UTF_8);
                se.setContentType("text/xml");
                httpput.setEntity(se);
                f.delete();

                HttpResponse httpresponse = httpClient.execute(httpput);

                if (httpresponse.getStatusLine().getStatusCode() == 200) {
                    if (MyDebug.LOG) {
                        Log.d("response ok", "ok response :/");
                    }
                } else {
                    if (MyDebug.LOG) {
                        Log.d("response not ok", "Something went wrong :/");
                    }
                }
            } catch (IOException e)
            {
                e.printStackTrace();
                return "Exception";
            }

            String complete_name = Customer_first_name.concat(" ").concat(Customer_last_name);
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", Customer_email ));
            params.add(new BasicNameValuePair("password", new_password ));
            params.add(new BasicNameValuePair("cust_name", complete_name ));

            // getting JSON string from URL
            JSONObject json_mail = jParser_mail.makeHttpRequest(URL_SEND_MAIL, "POST", params);

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
            pDialog.dismiss();

            if (status.equalsIgnoreCase("1")){

                new AlertDialog.Builder(ForgotPasswordActivity.this).setIcon(R.drawable.fail).setTitle("Password Change")
                        .setMessage("New password has been sent to your registered email id.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final Intent GoToLogin = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(GoToLogin);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                ForgotPasswordActivity.this.finish();
                            }
                        }).show();

            }
            else{
                alert.showAlertDialog(ForgotPasswordActivity.this, "Email Send Error", "Email can't ne send to your email id. Please contact helpline. ", false);
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
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        ForgotPasswordActivity.this.finish();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}
