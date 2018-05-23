package com.exfresh.exfreshapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import android.widget.AdapterView.OnItemSelectedListener;

/**
 * Created by Mahendra on 4/3/2015.
 */
public class CheckOutAddressActivity extends ActionBarActivity{

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private ProgressDialog pDialog;

    ConnectionDetector cd;

    AlertDialogManager alert = new AlertDialogManager();

    public int status_flag;
    String URL_PHP;
    String URL_CHK_ADD;

    JSONArray add_ids = null;
    JSONArray delivery_carriers = null;
    JSONArray delivery_ranges = null;
    JSONArray products = null;

    ArrayList<HashMap<String, String>> productsList;
    ArrayList<HashMap<String, String>> AddList;
    ArrayList<HashMap<String, String>> DeliveryTimeList;
    ArrayList<HashMap<String, String>> DeliveryCarrierList;
    ArrayList<HashMap<String, String>> DelChrgList;

    List<String> CarrierNameList = new ArrayList<String>();
    List<String> CarrierIdList = new ArrayList<String>();

    JSONParser_cart jParser_check_add_exist = new JSONParser_cart();
    JSONParser_cart jParser_chkout = new JSONParser_cart();

    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_ID_PRODUCT = "id_product";
    private static final String TAG_QUANTITY = "quantity";
    static final String TAG_ID_PRODUCT_ATTRIBUTE = "id_product_attribute";
    static final String TAG_PRODUCT_NAME = "name";
    static final String TAG_ID_ATTRIBUTE = "id_attribute";
    static final String TAG_ATTRIBUTE_NAME = "attribute_name";
    static final String TAG_IMPACT = "impact";
    static final String TAG_BASE_PRICE = "base_price";
    static final String TAG_FINAL_PRICE = "final_price";


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


    private static final String TAG_DELIVERY_RANGE = "delivery_range";
    static final String TAG_DELIVERY_PRICE_START = "price_start";
    static final String TAG_DELIVERY_PRICE_END = "price_end";
    static final String TAG_DELIVERY_CHARGE = "delivery_charge";

    private static final String TAG_DEL_CARRIERS = "carriers";
    private static final String TAG_DEL_ID_CARRIER = "id_carrier";
    private static final String TAG_DEL_DESCRIPTION = "description";

    private static final String TAG_SUCCESS_DEL_CARRIER = "success_del_carrier";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_SUCCESS_UPDATE = "success_update";

    String CART_FLAG = "Pref_Cart_Flag";

    final String output[] = new String[1];
    String URL_UPDATE_PS_CART ;
    String URL_APPLY_DELIVERY_CHARGE;
    String Cust_Id;
    String Cart_Id;
    String Id_Shop;

    public String Id_Address;
    String Total_Order_Price;
    String DEFAULT_USER;
    public  String selected_alias;
    String MAIN_URL;
    String URL_CHKOUT;
    int gn_tot_int;
    int grand_tot;
    int Delivery_Charges =0;
    int spinner_check=0;

    ListView list;
    ChkoutAddAdapter adapter;

    private Button buybtn;
    private TextView SubTotal;
    private TextView DeliveryCharges;
    private TextView GrandTotal;
    Spinner delivery_slot_spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ckout_address);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        setTitle("ExFresh");

        cd = new ConnectionDetector(getApplicationContext());

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        Total_Order_Price = extras.getString("Total_Order_Price");

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        Cust_Id = preferences.getString("Pref_Customer_Id", null);
        Cart_Id= preferences.getString("Pref_Cart_Id", null);
        Id_Shop = preferences.getString("Pref_shop_id", null);

        MAIN_URL = preferences.getString("Pref_main_url", null);
        URL_PHP = preferences.getString("Pref_url_php", null);

        URL_CHKOUT = URL_PHP.concat("Check_Out3.php");
        URL_CHK_ADD = URL_PHP.concat("ChkAddExist.php");

        URL_UPDATE_PS_CART = URL_PHP.concat("update_ps_cart.php");
        URL_APPLY_DELIVERY_CHARGE = URL_PHP.concat("DeliveryCharge.php");

        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);

        productsList = new ArrayList<HashMap<String, String>>();
        DeliveryTimeList = new ArrayList<HashMap<String, String>>();
        DeliveryCarrierList = new ArrayList<HashMap<String, String>>();

        SubTotal = (TextView)findViewById(R.id.SubTotalValue);
        DeliveryCharges = (TextView)findViewById(R.id.Delivery_Charges_Value);
        GrandTotal = (TextView)findViewById(R.id.GrandTot_Value);


        SubTotal.setText("Rs." + Total_Order_Price);
        DeliveryCharges.setText("Rs."+Delivery_Charges);
        GrandTotal.setText("Rs."+Total_Order_Price);

        AddList = new ArrayList<HashMap<String, String>>();
        DelChrgList = new ArrayList<HashMap<String, String>>();

        new ChkAddressExist().execute();

        addListenerOnButton();

    }

    class ChkAddressExist extends AsyncTask<String, String, String> {

        String id_carrier;
        String carrier_desc;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(CheckOutAddressActivity.this);
            pDialog.setTitle("Getting Address");
            pDialog.setMessage("Retrieving your address !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_customer", Cust_Id));
            params.add(new BasicNameValuePair("id_shop", Id_Shop));

            JSONObject json_chkaddexist = jParser_check_add_exist.makeHttpRequest(URL_CHK_ADD, "POST", params);

            try {
                int success = json_chkaddexist.getInt(TAG_SUCCESS);

                if (success == 1) {
                    add_ids = json_chkaddexist.getJSONArray(TAG_ADD_DTL);

                    for (int i = 0; i < add_ids.length(); i++) {
                        JSONObject c = add_ids.getJSONObject(i);

                        String id_add = c.getString(TAG_ID_ADDRESS);
                        String Alias = c.getString(TAG_ALIAS);
                        String FName = c.getString(TAG_FNAME);
                        String LName = c.getString(TAG_LNAME);
                        String Add1 = c.getString(TAG_ADD1);
                        String Add2 = c.getString(TAG_ADD2);
                        String City = c.getString(TAG_CITY);
                        String Postcode = c.getString(TAG_POSTCODE);
                        String Phone = c.getString(TAG_PHONE);
                        String Phone_Mobile = c.getString(TAG_PHONE_MOBILE);


                        HashMap<String, String> map2 = new HashMap<String, String>();

                        map2.put(TAG_ID_ADDRESS, id_add);
                        map2.put(TAG_ALIAS, Alias);
                        map2.put(TAG_FNAME,FName);
                        map2.put(TAG_LNAME,LName);
                        map2.put(TAG_ADD1,Add1);
                        map2.put(TAG_ADD2,Add2);
                        map2.put(TAG_CITY,City);
                        map2.put(TAG_POSTCODE,Postcode);
                        map2.put(TAG_PHONE,Phone);
                        map2.put(TAG_PHONE_MOBILE,Phone_Mobile);

                        AddList.add(map2);
                    }

                }
                int success_del_carrier = json_chkaddexist.getInt(TAG_SUCCESS_DEL_CARRIER);


                if (success_del_carrier == 1){
                    delivery_carriers = json_chkaddexist.getJSONArray(TAG_DEL_CARRIERS);

                    CarrierIdList.add("0");
                    CarrierNameList.add("Select");

                    for (int i = 0; i < delivery_carriers.length(); i++) {
                        JSONObject d = delivery_carriers.getJSONObject(i);

                        id_carrier = d.getString(TAG_DEL_ID_CARRIER);
                        carrier_desc = d.getString(TAG_DEL_DESCRIPTION);

                        CarrierNameList.add(carrier_desc);
                        CarrierIdList.add(id_carrier);

                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            delivery_slot_spinner = (Spinner) findViewById(R.id.SpinnerTimeSlot);

            runOnUiThread(new Runnable() {
                public void run() {


                    int pr_len = AddList.size();
                    if (pr_len == 0) {

                        Intent i = new Intent(getApplicationContext(), AddressRegistrationActivity.class);
                        status_flag = 2;
                        i.putExtra("status_flag", status_flag);
                        i.putExtra("Total_Order_Price", Total_Order_Price);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        CheckOutAddressActivity.this.finish();

                    } else {

                        list = (ListView) findViewById(R.id.add_list);

                        adapter = new ChkoutAddAdapter(CheckOutAddressActivity.this, AddList);
                        list.setAdapter(adapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                                view.setSelected(true);
                                TextView clickedTextView = (TextView) view.findViewById(R.id.id_address); // Or get child from view arg1
                                TextView clickedTextView_alias = (TextView) findViewById(R.id.alias);
                                Id_Address = clickedTextView.getText().toString();
                                selected_alias = clickedTextView_alias.getText().toString();
                                delivery_slot_spinner.setClickable(true);
                            }
                        });
                    }

                    ArrayAdapter<String> timeslot_dataAdapter = new ArrayAdapter<String>(CheckOutAddressActivity.this, R.layout.spinner_item, CarrierNameList) {
                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = null;

                            // If this is the initial dummy entry, make it hidden
                            if (position == 0) {
                                TextView tv = new TextView(getContext());
                                tv.setHeight(0);
                                tv.setVisibility(View.GONE);
                                v = tv;
                            } else {
                                // Pass convertView as null to prevent reuse of special case views
                                v = super.getDropDownView(position, null, parent);
                            }

                            // Hide scroll bar because it appears sometimes unnecessarily, this does not prevent scrolling
                            parent.setVerticalScrollBarEnabled(false);
                            return v;

                        }
                    };
                    timeslot_dataAdapter.setDropDownViewResource(R.layout.spinner_item);
                    delivery_slot_spinner.setAdapter(timeslot_dataAdapter);
                    delivery_slot_spinner.setOnItemSelectedListener(onItemSelectedListener);

                    delivery_slot_spinner.getSelectedView();
                    delivery_slot_spinner.setClickable(false);
                }
            });
        }


        OnItemSelectedListener onItemSelectedListener =
                new OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {

                        spinner_check = spinner_check + 1;

                        if (spinner_check > 1){
                            if (Id_Address == null) {
                                //
                                alert.showAlertDialog(CheckOutAddressActivity.this, "Choose delivery address",
                                        "Please select the delivery address from above", false);

                                return;
                            }

                        for (int i = 0; i < CarrierIdList.size(); i++) {
                            if (position == i) {
                                final String id_carrier_value = CarrierIdList.get(i);
                                output[0] = id_carrier_value;
                                new UpdtePS_Cart_and_go_2_Finalcheckout().execute();
                            }
                        }

                    }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                };

    }

    class UpdtePS_Cart_and_go_2_Finalcheckout extends AsyncTask<String, String, String> {
        String status;
        String id_car = output[0];

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_address", Id_Address));
            params.add(new BasicNameValuePair("id_cart", Cart_Id));
            params.add(new BasicNameValuePair("id_carrier", id_car));
            params.add(new BasicNameValuePair("total_order_price", Total_Order_Price));

            JSONParser_cart r = new JSONParser_cart();
            JSONObject json = r.makeHttpRequest(URL_UPDATE_PS_CART, "POST", params);

            try {
                int success_update = json.getInt(TAG_SUCCESS_UPDATE);

                if (success_update == 1) {

                    status = "1";

                } else {

                    status = "0";
                }
                int success = json.getInt(TAG_SUCCESS);


                if (id_car.equals("0")) {

                }

                else {

                    if (success == 1) {
                        delivery_ranges = json.getJSONArray(TAG_DELIVERY_RANGE);

                        for (int i = 0; i < delivery_ranges.length(); i++) {
                            JSONObject c = delivery_ranges.getJSONObject(i);

                            String Price_Start = c.getString(TAG_DELIVERY_PRICE_START);
                            String Price_End = c.getString(TAG_DELIVERY_PRICE_END);
                            String Delivery_Charge = c.getString(TAG_DELIVERY_CHARGE);


                            HashMap<String, String> map_delivery_charges = new HashMap<String, String>();

                            map_delivery_charges.put(TAG_DELIVERY_PRICE_START, Price_Start);
                            map_delivery_charges.put(TAG_DELIVERY_PRICE_END, Price_End);
                            map_delivery_charges.put(TAG_DELIVERY_CHARGE, Delivery_Charge);

                            DelChrgList.add(map_delivery_charges);
                        }

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return status;
        }

        protected void onPostExecute(String file_url) {

            runOnUiThread(new Runnable() {
                public void run() {

                    if (id_car.equals("0")) {

                    } else {

                        List<String> pr_chrg = new ArrayList<String>();
                        for (int a = 0; a < DelChrgList.size(); a++) {

                            String d_chrg = DelChrgList.get(a).get(TAG_DELIVERY_CHARGE);
                            pr_chrg.add(d_chrg);
                        }
                        String d_charge="";
                        if (pr_chrg.size()==0) {
                            d_charge = "0";
                        } else {
                            d_charge = pr_chrg.get(0);
                        }
                        float de_charge = Float.parseFloat(d_charge);
                        Delivery_Charges = (int) de_charge;

                        float gn_tot = Float.parseFloat(Total_Order_Price);
                        gn_tot_int = (int) gn_tot;
                        grand_tot = gn_tot_int + Delivery_Charges;

                        SubTotal.setText("Rs." + Total_Order_Price);
                        DeliveryCharges.setText("Rs." + Delivery_Charges);
                        GrandTotal.setText("Rs." + grand_tot);

                    }

                }
            });

        }


    }



 public void addListenerOnButton() {

        buybtn = (Button) findViewById(R.id.BuyButton);
        buybtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

            String Id_Carrier = output[0];

               if (Id_Address==null){
                    alert.showAlertDialog(CheckOutAddressActivity.this, "Choose delivery address",
                            "Please select the delivery address from above", false);
                    return;
                }


                if (Id_Carrier==null || Id_Carrier.equals("0")){

                    alert.showAlertDialog(CheckOutAddressActivity.this, "Choose delivery",
                            "Please select the delivery", false);

                    return;
                }


                new ParseMakeConnectPost().execute();

            }

            private boolean isValidPassword(String pass, String pass_retype) {
                if ( pass.equals(pass_retype)) {
                    return true;
                }
                return false;
            }

            class ParseMakeConnectPost extends AsyncTask<String, String, String> {
                String post_status;
                protected void onPreExecute() {
                    super.onPreExecute();
                    pDialog = new ProgressDialog(CheckOutAddressActivity.this);
                    pDialog.setTitle("Order Generation");
                    pDialog.setMessage("Placing your order !! Please wait...");
                    pDialog.setIndeterminate(false);
                    pDialog.setCancelable(false);
                    pDialog.show();
                }

                protected String doInBackground(String... args) {

                    double Final_Price = 0;

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("id_cart", Cart_Id));

                    JSONObject json_chkout = jParser_chkout.makeHttpRequest(URL_CHKOUT, "POST", params);

                    String rt = json_chkout.toString();

                    try {
                        int success = json_chkout.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            products = json_chkout.getJSONArray(TAG_PRODUCTS);

                            for (int i = 0; i < products.length(); i++) {
                                JSONObject c = products.getJSONObject(i);

                                String product_id_f_ps_crt = c.getString(TAG_ID_PRODUCT);
                                String quantity_f_ps_crt = c.getString(TAG_QUANTITY);

                                String id_f_ps_product_attribute = c.getString(TAG_ID_PRODUCT_ATTRIBUTE);

                                String product_f_ps_name = c.getString(TAG_PRODUCT_NAME);
                                String id_f_ps_attribute = c.getString(TAG_ID_ATTRIBUTE);
                                String attribute_f_ps_name = c.getString(TAG_ATTRIBUTE_NAME);
                                String price_f_ps_impact = c.getString(TAG_IMPACT);
                                String base_f_ps_price = c.getString(TAG_BASE_PRICE);
                                String final_f_ps_price = c.getString(TAG_FINAL_PRICE);


                                HashMap<String, String> map2 = new HashMap<String, String>();

                                map2.put(TAG_ID_PRODUCT, product_id_f_ps_crt);
                                map2.put(TAG_QUANTITY, quantity_f_ps_crt);

                                map2.put(TAG_ID_PRODUCT_ATTRIBUTE, id_f_ps_product_attribute);

                                map2.put(TAG_PRODUCT_NAME,c.getString(TAG_PRODUCT_NAME) );
                                map2.put(TAG_ID_ATTRIBUTE,c.getString(TAG_ID_ATTRIBUTE) );
                                map2.put(TAG_ATTRIBUTE_NAME,c.getString(TAG_ATTRIBUTE_NAME));
                                map2.put(TAG_IMPACT, c.getString(TAG_IMPACT));
                                map2.put(TAG_BASE_PRICE,c.getString(TAG_BASE_PRICE) );
                                map2.put(TAG_FINAL_PRICE,c.getString(TAG_FINAL_PRICE) );

                                productsList.add(map2);
                            }

                        }
                       } catch (JSONException e) {
                         e.printStackTrace();
                         }

                    try {

                        String Id_Carrier = output[0];

                        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                        Document document = documentBuilder.newDocument();

                        Element rootElement = document.createElement("prestashop");
                        document.appendChild(rootElement);

                        Element orderElement = document.createElement("order");
                        rootElement.appendChild(orderElement);

                        //id_address_delivery
                        Element id_add_delElement = document.createElement("id_address_delivery");
                        orderElement.appendChild(id_add_delElement);
                        id_add_delElement.appendChild(document.createTextNode(Id_Address));

                        //id_address_invoice
                        Element id_add_invElement = document.createElement("id_address_invoice");
                        orderElement.appendChild(id_add_invElement);
                        id_add_invElement.appendChild(document.createTextNode(Id_Address));

                        //id_cart
                        Element id_cartElement = document.createElement("id_cart");
                        orderElement.appendChild(id_cartElement);
                        id_cartElement.appendChild(document.createTextNode(Cart_Id));

                        //id_currency
                        Element id_currencyElement = document.createElement("id_currency");
                        orderElement.appendChild(id_currencyElement);
                        id_currencyElement.appendChild(document.createTextNode("1"));

                        //id_lang
                        Element id_langElement = document.createElement("id_lang");
                        orderElement.appendChild(id_langElement);
                        id_langElement.appendChild(document.createTextNode("1"));


                        // id_customer
                        Element id_customerElement = document.createElement("id_customer");
                        orderElement.appendChild(id_customerElement);
                        id_customerElement.appendChild(document.createTextNode(Cust_Id));

                        // id_carrier
                        Element id_carrierElement = document.createElement("id_carrier");
                        orderElement.appendChild(id_carrierElement);
                        id_carrierElement.appendChild(document.createTextNode(Id_Carrier));

                        //module
                        Element moduleElement = document.createElement("module");
                        orderElement.appendChild(moduleElement);
                        moduleElement.appendChild(document.createTextNode("cashondelivery"));

                        //payment
                        Element paymentElement = document.createElement("payment");
                        orderElement.appendChild(paymentElement);
                        paymentElement.appendChild(document.createTextNode("Cash on delivery (COD)"));

                        //total_paid
                        Element total_paidElement = document.createElement("total_paid");
                        orderElement.appendChild(total_paidElement);
                        //total_paidElement.appendChild(document.createTextNode((String.valueOf(Final_Price))));
                        total_paidElement.appendChild(document.createTextNode(String.valueOf(grand_tot)));

                        //total_paid_real
                        Element total_paid_realElement = document.createElement("total_paid_real");
                        orderElement.appendChild(total_paid_realElement);
                        //total_paid_realElement.appendChild(document.createTextNode((String.valueOf(Final_Price))));
                        total_paid_realElement.appendChild(document.createTextNode(String.valueOf(grand_tot)));

                        //total_products
                        Element total_productsElement = document.createElement("total_products");
                        orderElement.appendChild(total_productsElement);
                        // total_productsElement.appendChild(document.createTextNode((String.valueOf(Final_Price))));
                        total_productsElement.appendChild(document.createTextNode(Total_Order_Price));

                        //total_products_wt
                        Element total_products_wtElement = document.createElement("total_products_wt");
                        orderElement.appendChild(total_products_wtElement);
                        // total_products_wtElement.appendChild(document.createTextNode((String.valueOf(Final_Price))));
                        total_products_wtElement.appendChild(document.createTextNode(Total_Order_Price));

                        //conversion_rate
                        Element conversion_rateElement = document.createElement("conversion_rate");
                        orderElement.appendChild(conversion_rateElement);
                        conversion_rateElement.appendChild(document.createTextNode("1"));

                        //associations
                        Element associationsElement = document.createElement("associations");
                        orderElement.appendChild(associationsElement);

                        //order_rows   set attribute also
                        Element order_rowsElement = document.createElement("order_rows");
                        associationsElement.appendChild(order_rowsElement);

                        for (Map<String, String> map : productsList) {
                            //order_row
                            Element order_rowElement = document.createElement("order_row");
                            order_rowsElement.appendChild(order_rowElement);

                            String tag_quantity = map.get(TAG_QUANTITY);
                            String tag_id = map.get(TAG_ID_PRODUCT);
                            String tag_product_attribute_id = map.get(TAG_ID_PRODUCT_ATTRIBUTE);
                            String tag_product_name = map.get(TAG_PRODUCT_NAME);

                            //product_quantity in order row
                            Element or_product_quantityElement = document.createElement("product_quantity");
                            order_rowElement.appendChild(or_product_quantityElement);
                            or_product_quantityElement.appendChild(document.createTextNode(tag_quantity));

                            //product_attribute_id in order row
                            Element product_attribute_idElement = document.createElement("product_attribute_id");
                            order_rowElement.appendChild(product_attribute_idElement);
                            product_attribute_idElement.appendChild(document.createTextNode(tag_product_attribute_id));

                            //product_name in order row
                            Element product_nameElement = document.createElement("product_name");
                            order_rowElement.appendChild(product_nameElement);
                            product_nameElement.appendChild(document.createTextNode(tag_product_name));

                            //product_id    in order row
                            Element or_product_idElement = document.createElement("product_id");
                            order_rowElement.appendChild(or_product_idElement);
                            or_product_idElement.appendChild(document.createTextNode(tag_id));
                        }

                        TransformerFactory transformerFactory = TransformerFactory.newInstance();
                        Transformer transformer = transformerFactory.newTransformer();
                        DOMSource source = new DOMSource(document.getDocumentElement());
                        StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
                                .getAbsolutePath() + "/" + "order_post.xml"));

                        transformer.transform(source, result);
                    }catch (ParserConfigurationException e) {
                    } catch (TransformerConfigurationException e) {
                    } catch (TransformerException e) {
                    }

                    try

                    {
                        //             ****post your file to server*****

                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        String URL2 = MAIN_URL.concat("/api/orders?schema=synopsis");

                        HttpPost httppost = new HttpPost(URL2);
                        String WbSr_password = "";// leave it empty
                        String authToBytes = DEFAULT_USER + ":" + WbSr_password;
                        byte authBytes[] = Base64.encode(authToBytes.getBytes(), Base64.NO_WRAP);
                        String authBytesString = new String(authBytes);
                        httppost.setHeader("Authorization", "Basic " + authBytesString);
                        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                        File f=new File(filePath,"order_post.xml");

                        String content = getFileContent(f);

                        StringEntity se = new StringEntity(content, HTTP.UTF_8);
                        se.setContentType("text/xml");
                        httppost.setEntity(se);
                        f.delete();

                        HttpResponse httpresponse = httpClient.execute(httppost);
                        int post_status_code = httpresponse.getStatusLine().getStatusCode();

                        if (post_status_code == 200 || post_status_code == 201) {
                            post_status="1";
                        } else {
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
                        editor.remove(CART_FLAG).apply();

                        Category.mNotificationsCount=0;
                        Category notifyupdate_cat = new Category();
                        notifyupdate_cat.updateNotificationsBadge(notifyupdate_cat.mNotificationsCount);

                        Products.mNotificationsCount=0;
                        preferences.edit().remove("Pref_Cart_Id").commit();

                        final Dialog dialog = new Dialog(CheckOutAddressActivity.this,R.style.ThemeWithCorners);
                        dialog.setContentView(R.layout.dialog_usage);
                        dialog.setTitle("Order Placed");
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);

                        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                        text.setText(R.string.order_place);

                        Button button = (Button) dialog.findViewById(R.id.Button01);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialog.dismiss();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent j = new Intent(getApplicationContext(), Category.class);

                                        j.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(j);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    }
                                }, 400);
                            }
                        });
                        dialog.show();
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
        });
    }
}