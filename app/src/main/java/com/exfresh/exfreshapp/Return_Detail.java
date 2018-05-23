package com.exfresh.exfreshapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
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
 * Created by Mahendra on 4/21/2015.
 */
public class Return_Detail extends ActionBarActivity {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();


    // Progress Dialog
    private ProgressDialog pDialog;

    // JSON Node names for order detail
    private static final String TAG_SUCCESS = "success";

    //http://www.exfresh.co.in
    private static String url_get_return_detail = "http://www.exfresh.co.in/Return_Detail.php";

    public static String Id_Order_Return;

    // Creating JSON Parser object
    JSONParser_cart jParser_gt_ret_dtl = new JSONParser_cart();

    // products JSONArray
    JSONArray return_dtls= null;

    private static final String TAG_RETURN_DETAILS = "return_details";
    //static final String TAG_ORDER_ID = "id_order";
    //static final String TAG_ORDER_INVOICE="id_order_invoice";
    static final String TAG_PRODUCT_NAME = "product_name";
    static final String TAG_PRODUCT_QUANTITY="product_quantity";
    static final String TAG_PRODUCT_PRICE="product_price";
    static final String TAG_TOTAL_PRICE_TAX_INCLUSIVE="total_price_tax_incl";
    //static final String TAG_UNIT_PRICE_TAX_INCLUSIVE="unit_price_tax_incl";

    ArrayList<HashMap<String, String>> returndetailList;

    String cart_flag;


    ListView list;
    ReturnDetailAdapter adapter;

    Button myaccbtn;
    Button mycartbtn;
    Button chkoutbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returndetail);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            return;
        }

        Id_Order_Return = extras.getString("id_order_return");

        // Hashmap for ListView
        returndetailList = new ArrayList<HashMap<String, String>>();

        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();


        cart_flag = preferences.getString("Pref_Cart_Flag",null);

        myaccbtn = (Button)findViewById(R.id.bMyAccount);
        mycartbtn = (Button)findViewById(R.id.bMyCart);
        chkoutbtn = (Button)findViewById(R.id.bchkout);


        View.OnClickListener listnr_myacc=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Return_Detail.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(Return_Detail.this,My_AccountActivity.class);
                startActivity(j);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        View.OnClickListener listnr_cart=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Return_Detail.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent i= new Intent(Return_Detail.this,My_CartActivity.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(Return_Detail.this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                }


            }
        };



        View.OnClickListener listnr_chkout=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*
                if (pr_len == 0) {
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent m = new Intent(My_CartActivity.this, CheckoutActivity.class);
                    startActivity(m);
                } */

                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(Return_Detail.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent k= new Intent(Return_Detail.this,CheckoutActivity.class);
                    startActivity(k);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }

            }
        };

        myaccbtn.setOnClickListener(listnr_myacc);
        mycartbtn.setOnClickListener(listnr_cart);
        chkoutbtn.setOnClickListener(listnr_chkout);





        // Loading products in Background Thread
        new ReturnDetail().execute();
    }

    // Background Async Task to fetch order_ids corresponding to customer ids
    class ReturnDetail extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(Return_Detail.this);
            pDialog.setTitle("Return Details");
            pDialog.setMessage("Retrieving your return details !! Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // getting All products from url

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_order_return", Id_Order_Return));

            // getting JSON string from URL
            JSONObject json_order = jParser_gt_ret_dtl.makeHttpRequest(url_get_return_detail, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json_order.getInt(TAG_SUCCESS);
                //Log.d("Inside Try Block : "+success");
                //Log.d("MainActivity", "INSIDE TRY block, value of success :" + success);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    return_dtls = json_order.getJSONArray(TAG_RETURN_DETAILS);
                    //Log.d("MainActivity", "INSIDE TRY->IF block, value of products :" +order_ids);


                    // looping through All Orders
                    for (int i = 0; i < return_dtls.length(); i++) {
                        JSONObject c = return_dtls.getJSONObject(i);

                        // Storing each json item in variable
                        String Product_Name = c.getString(TAG_PRODUCT_NAME);
                        String Product_Quantity = c.getString(TAG_PRODUCT_QUANTITY);
                        String Product_Price = c.getString(TAG_PRODUCT_PRICE);
                        String Total_Price_Tax_Incl = c.getString(TAG_TOTAL_PRICE_TAX_INCLUSIVE);
                        //String Order_Status = c.getString(TAG_ORDER_STATUS);
                        //String Delivery_Date = c.getString(TAG_DELIVERY_DATE);

                        //String quantity_f_ps_crt = c.getString(TAG_QUANTITY);
                        //Log.d("MainActivity", "INSIDE TRY->IF block->FOR, value of product_id_ps_crt :" +Order_Id);
                        //code for setting flag for status delivered


                        // creating new HashMap
                        HashMap<String, String> map2 = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map2.put(TAG_PRODUCT_NAME, Product_Name);
                        map2.put(TAG_PRODUCT_QUANTITY,Product_Quantity);
                        map2.put(TAG_PRODUCT_PRICE,Product_Price);
                        map2.put(TAG_TOTAL_PRICE_TAX_INCLUSIVE,Total_Price_Tax_Incl);
                        //map2.put(TAG_ORDER_STATUS,Order_Status);
                        //map2.put(TAG_DELIVERY_DATE,Delivery_Date);
                        //map2.put(TAG_QUANTITY, quantity_f_ps_crt);

                        // adding HashList to ArrayList
                        returndetailList.add(map2);
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
                    int pr_len = returndetailList.size();
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

                        list = (ListView) findViewById(R.id.return_detail_list);

                        // Getting adapter by passing xml data ArrayList
                        adapter = new ReturnDetailAdapter(Return_Detail.this, returndetailList);
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

                        /*
                        // Click event for single list row
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                final Intent GoToDisplay = new Intent(OrderHistory.this,OrderDetail.class);
                                //GoToDisplay.putExtra("position", CategoryList.get(position));
                                TextView clickedTextView = (TextView)view.findViewById(R.id.order_id);
                                TextView clickedStatus = (TextView)view.findViewById(R.id.order_status);// Or get child from view arg1
                                if (clickedStatus.getText().toString().equalsIgnoreCase("Delivered")){

                                    TextView clickedDeliveryDate = (TextView)view.findViewById(R.id.delivery_date);
                                    String d_date = clickedDeliveryDate.getText().toString();
                                    Date D_Date_Formatted = convertDate(d_date);

                                    Date cur_date = new Date();

                                    long dif_dates = cur_date.getTime()-D_Date_Formatted.getTime();
                                    long secs = dif_dates/1000;
                                    long mins = secs/60;
                                    long hours = mins/60;
                                    long days = hours/24;
                                    Log.d("seconds :",String.valueOf(secs));
                                    Log.d("minutes :",String.valueOf(mins));
                                    Log.d("hours :",String.valueOf(hours));
                                    Log.d("days :",String.valueOf(days));

                                    if ((days<1) && (hours<=2)) {
                                        status_flag = 1;
                                        Log.d("Inside if->if",String.valueOf(status_flag));
                                    }
                                    else {
                                        status_flag=0;
                                        Log.d("Inside if->else",String.valueOf(status_flag));
                                    }
                                }
                                else {
                                    status_flag = 0;
                                    Log.d("Inside else",String.valueOf(status_flag));
                                }






                                GoToDisplay.putExtra("order_id", clickedTextView.getText().toString());
                                GoToDisplay.putExtra("status_flag", status_flag);
                                startActivity(GoToDisplay);


                            }
                        });


*/


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
