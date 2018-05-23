package com.exfresh.exfreshapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahendra on 3/26/2015.
 */
public class My_AccountActivity extends ActionBarActivity {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    ListView acclist;
    MyAccountAdapter accadapter;

    private String[] Acc_List = {"View Order History","Update My Profile","My Addresses","My Credit Slips","My Returns","My Vouchers"};

    private String[] Acc_List_URL = {
                                    "http://exfresh.online/exfresh2/icons/order_history.png",
                                    "http://exfresh.online/exfresh2/icons/updateProfile.png",
                                    "http://exfresh.online/exfresh2/icons/address.png",
                                    "http://exfresh.online/exfresh2/icons/credit.png",
                                    "http://exfresh.online/exfresh2/icons/returns.png",
                                    "http://exfresh.online/exfresh2/icons/voucher.png"};


    // map node keys
    static final String KEY_ITEM = "item";
    static final String KEY_IMAGE_URL = "image_url";


    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Connection detector
    ConnectionDetector cd;

    Button homebtn;
    Button mycartbtn;
    Button offersbtn;

    String cart_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);


        cd = new ConnectionDetector(getApplicationContext());

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        cart_flag = preferences.getString("Pref_Cart_Flag",null);

        mycartbtn = (Button)findViewById(R.id.bMyCart);
        homebtn = (Button)findViewById(R.id.bHome);
        offersbtn = (Button)findViewById(R.id.bOffers);

        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(My_AccountActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent i= new Intent(My_AccountActivity.this,My_CartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }


            }
        };

        View.OnClickListener listnr_home=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(My_AccountActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(My_AccountActivity.this,Category.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        View.OnClickListener listnr_offers=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(My_AccountActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent k= new Intent(My_AccountActivity.this,OffersActivity.class);
                startActivity(k);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            }
        };

        mycartbtn.setOnClickListener(listnr);
        homebtn.setOnClickListener(listnr_home);
        offersbtn.setOnClickListener(listnr_offers);


        final ArrayList<HashMap<String, String>> MyAccList = new ArrayList<HashMap<String, String>>();

        for(int i=0;i<Acc_List.length;i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(KEY_ITEM,Acc_List[i] );
            map.put(KEY_IMAGE_URL,Acc_List_URL[i]);
            MyAccList.add(map);
        }


        acclist = (ListView) findViewById(R.id.accnt_list);

        // Getting adapter by passing xml data ArrayList
        accadapter = new MyAccountAdapter(this, MyAccList);
        acclist.setAdapter(accadapter);


        // Click event for single list row
        acclist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(My_AccountActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }

                switch (position) {

                    case 0:
                        Intent j = new Intent(getApplicationContext(), OrderHistory.class);
                        startActivity(j);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case 1:
                        Intent i = new Intent(getApplicationContext(), UpdateProfileActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case 2:
                        Intent m = new Intent(getApplicationContext(), MyAddressActivity.class);
                        startActivity(m);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;

                    case 3:
                        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                        //Intent x = new Intent(getApplicationContext(), MyLocation2.class);
                        //startActivity(x);
                        break;

                    case 4:
                        Intent k = new Intent(getApplicationContext(), My_Returns.class);
                        startActivity(k);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                        break;

                    case 5:
                        Toast.makeText(getApplicationContext(), "Coming Soon", Toast.LENGTH_SHORT).show();
                        //Intent l = new Intent(getApplicationContext(), MyLocation.class);
                        //startActivity(l);
                        break;

                }

            }
        });

    }


    @Override public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); }

}
