package com.exfresh.exfreshapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.SignInButton;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Mahendra on 3/21/2015.
 */
public class LoginActivity extends ActionBarActivity {

    Button btn_login;
    //Button g_btn_login;
    //private SignInButton btnSignIn;
    String KEY_LOGIN_NAME = "LoginStatus";
    String KEY_ID = "Pref_Customer_Id";
    String KEY_EMAIL ="Pref_Cus_Email";
    String KEY_FNAME ="Pref_Cus_First_Name";
    String KEY_LNAME = "Pref_Cus_Last_Name";
    String KEY_PASSWORD = "Pref_Customer_Password";

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String MAIN_URL;
    String LOGIN_URL;


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();


    String f_Customer_Id = null;


    String[] avail_accounts;
    private AccountManager mAccountManager;
    ListView list;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        addListenerOnButton();

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        Intent intent ;
        editor = preferences.edit();

        MAIN_URL = preferences.getString("Pref_main_url", null);

        TextView registerScreen = (TextView) findViewById(R.id.link_to_register);
        TextView forgotpassword = (TextView)findViewById(R.id.forgot_password);

        // Listening to Registration Screen link
        registerScreen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent i = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        // Listening to Registration Screen link
        forgotpassword.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });
    }

    public void addListenerOnButton() {
        btn_login = (Button) findViewById(R.id.btnLogin);

        /*
        g_btn_login = (Button) findViewById(R.id.btnGlogin);

        g_btn_login.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0){
                Intent intent = new Intent(LoginActivity.this, NewLoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        });
        */

        btn_login.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View arg0) {

                EditText login_email,login_password;

                login_email = (EditText) findViewById(R.id.login_email_txt);

                String cust_email_addr = login_email.getText().toString();
                if (!isValidEmail(cust_email_addr)) {
                    login_email.setError("Invalid Email");
                }
                if (login_email.getText().toString().length() == 0)
                    login_email.setError("Email is required!");


                login_password = (EditText) findViewById(R.id.login_password_txt);

                String pass_frm_app = login_password.getText().toString();
                if (login_password.getText().toString().length() == 0)
                    login_password.setError("Password cannot be blank");

                if ((login_password.getText().toString().length() > 0) && (login_password.getText().toString().length() < 5))
                    login_password.setError("Password must be atleast 5 characters long");
                // in config folder  setting.inc.php
                //String cookie = "kb0fdkn2mxjwxjaknobi8oe6t9gykacxnml4wzqgfrofxufot02pbh4d";
                //String cookie = "pRAw9G311V8QiXJ5HDJim2shrRqQR5eyZCCulyzM1zMOSkFrHknPm2k7spHkEfYf"; //of exfresh
                //String cookie = "DzkgG8QWIGa86XOVG2rLZBPPpmp4An0CmBMWVe9ZXLDkJyiLU73WoHI8"; //of exfresh2
                String cookie = "aoxIdFphWR8tXPT1TZicx7GeR7sdOPPWJwGZ54ZFh9tKsj0eCBteZU0b"; //of exfresh3




                if (login_email.getText().toString().length() > 0 && !isValidEmail(cust_email_addr)==false ){
                String final_pass_frm_app = cookie + pass_frm_app;
                String md5_passwd = computeMD5Hash(final_pass_frm_app);

                String URL_POST = "/api/customers/?display=[id,passwd,email,firstname,lastname]&filter[email]=[" + login_email.getText().toString() + "]";
                LOGIN_URL = MAIN_URL.concat(URL_POST);
                String xml2 = "";

                ConnectXML parser2 = new ConnectXML();
                try {
                    xml2 = parser2.execute(LOGIN_URL).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                Document doc = parser2.getDomElement(xml2);


                Element element = doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("customer");


                if (nList.getLength() != 0) {
                    for (int i = 0; i < nList.getLength(); i++) {
                        Node node = nList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                            Element element2 = (Element) node;
                            String Customer_Id = getValue("id", element2);
                            f_Customer_Id = Customer_Id;


                            String Customer_email = getValue("email", element2);
                            String Customer_password_frm_xml = getValue("passwd", element2);
                            String Customer_first_name = getValue("firstname", element2);
                            String Customer_last_name = getValue("lastname", element2);

                            if (md5_passwd.equals(Customer_password_frm_xml)) {
                                editor.putBoolean(KEY_LOGIN_NAME, true);
                                editor.putString(KEY_ID, Customer_Id);
                                editor.putString(KEY_EMAIL, Customer_email);
                                editor.putString(KEY_PASSWORD, md5_passwd);
                                editor.putString(KEY_FNAME, Customer_first_name);
                                editor.putString(KEY_LNAME, Customer_last_name);
                                editor.commit();

                                String get_id = preferences.getString("Pref_Customer_Id", null);

                                Toast.makeText(getApplicationContext(), "Login Successful...", Toast.LENGTH_SHORT).show();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent j = new Intent(getApplicationContext(), Category.class);
                                        startActivity(j);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                }, 1400);

                            } else {
                                alert.showAlertDialog(LoginActivity.this, "Login failed..", "Email/Password is incorrect", false);
                            }

                        }
                    }

                }
                else {
                    alert.showAlertDialog(LoginActivity.this, "Login failed..", "Email/Password is incorrect", false);
                }
            }

            }


        });
    }


    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static String getValue(String tag
        , Element element) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {

            case R.id.new_user_registration:
                // Check for internet connection
                Intent intent = new Intent(LoginActivity.this, RegActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

