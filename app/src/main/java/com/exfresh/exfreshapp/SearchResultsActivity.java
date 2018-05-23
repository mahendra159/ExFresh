package com.exfresh.exfreshapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
 * Created by Mahendra on 3/24/2015.
 */
public class SearchResultsActivity extends ActionBarActivity implements LazyAdapter_Searchable.customButtonListener {

    public static int mNotificationsCount = 0;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private TextView txtQuery;
    public String search_query;

    // Connection detector
    ConnectionDetector cd;
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Progress Dialog
    private ProgressDialog pDialog;

    ArrayList<Multimap<String, String>> ProductsList;

    JSONArray product_ids = null;

    JSONArray attribute_ids = null;

    private static final String TAG_SUCCESS = "success";

    JSONParser_cart jParser = new JSONParser_cart();


    static String URL;
    static final String KEY_PRODUCT = "product"; //parent node
    static final String KEY_ID = "id";
    static final String KEY_NAME = "name";
    static final String KEY_CONDITION = "condition";
    static final String KEY_PRICE = "price";
    static final String KEY_IMAGE_URL = "image";
    static final String KEY_CATEGORY = "id_category_default";

    ListView search_list;
    LazyAdapter_Searchable adapter;

    String MAIN_URL;
    String URL_PHP;
    String SEARCH_URL;
    String ATTRIBUTE_URL;

    String search_id_product;

    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_ATTRIBUTES = "attributes";

    static final String TAG_ID_PRODUCT = "id_product";
    static final String TAG_PRODUCT_NAME = "name";
    //static final String TAG_ID_SHOP_DEFAULT ="id_shop_default";
    static final String TAG_Price="price";
    //static final String TAG_UNITY="unity";
    static final String TAG_IMAGE_ID="id_image";
    static final String TAG_IMAGE_URL = "image";
    //static final String TAG_DELIVERY_DATE="delivery_date";

    static final String TAG_ID_PRODUCT_ATTRIBUTE = "id_product_attribute";
    static final String TAG_PRICE_IMPACT = "impact";
    static final String TAG_ID_ATTRIBUTE ="id_attribute";
    static final String TAG_ATTRIBUTE_NAME="attribute_name";


    public static String Id_Product_Attribute ;
    public static String Impact ;
    public static String Id_Attribute ;
    public static String Attribute_Name ;

//added from products
String Add_Customer_Id;
    String Add_Product_Id ;
    String Add_Attribute_Id ;
    String Add_Product_Quantity;
    String Add_Id_Shop;

    String Id_Shop;
    String Id_Shop_Group;
    String DEFAULT_USER;

    String URL_GET_CART_ID;
    String Cart_Id;

    static final String TAG_CART_ID = "cart_id";
    String KEY_CART_ID = "Pref_Cart_Id";

    String URL_PRODUCT_INSUPDT;

    String KEY_CART_FLAG = "Pref_Cart_Flag";
    String cart_flag;

    private static final String TAG_STOCK_AVAIL = "stock";
    private static final String TAG_QUANTITY_AVAILABLE = "quantity_available";

    Button homebtn;
    Button mycartbtn;
    Button offersbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setDisplayShowHomeEnabled(true);
        menu.setLogo(R.mipmap.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        setTitle("Search");
        // get the action bar
//        ActionBar actionBar = getActionBar();

        // Enabling Back navigation on Action Bar icon
    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cd = new ConnectionDetector(getApplicationContext());




        preferences = getSharedPreferences("PREF_NAME", MODE_PRIVATE);
        editor = preferences.edit();

        cart_flag = preferences.getString("Pref_Cart_Flag", null);

        MAIN_URL = preferences.getString("Pref_main_url", null);

        URL_PHP = preferences.getString("Pref_url_php", null);
        SEARCH_URL = URL_PHP.concat("Master_Product_Search.php");
        ATTRIBUTE_URL = URL_PHP.concat("Master_Attribute_Search.php");
        URL_GET_CART_ID = URL_PHP.concat("Get_Cart_Id.php");
        URL_PRODUCT_INSUPDT = URL_PHP.concat("update_product.php");

        Cart_Id = preferences.getString("Pref_Cart_Id", null);

        Id_Shop = preferences.getString("Pref_shop_id", null);
        Id_Shop_Group = preferences.getString("Pref_shop_group_id", null);
        DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);

        mycartbtn = (Button)findViewById(R.id.bMyCart);
        homebtn = (Button)findViewById(R.id.bHome);
        offersbtn = (Button)findViewById(R.id.boffers);

        View.OnClickListener listnr=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for internet connection
                if (!cd.isConnectingToInternet()) {
                    // Internet Connection is not present
                    alert.showAlertDialog(SearchResultsActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                else if(cart_flag!=null && cart_flag.equals("1")){
                    Intent i= new Intent(SearchResultsActivity.this,My_CartActivity.class);
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
                    alert.showAlertDialog(SearchResultsActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent j= new Intent(SearchResultsActivity.this,Category.class);
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
                    alert.showAlertDialog(SearchResultsActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                    return;
                }
                Intent k= new Intent(SearchResultsActivity.this,OffersActivity.class);
                startActivity(k);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


            }
        };

        mycartbtn.setOnClickListener(listnr);
        homebtn.setOnClickListener(listnr_home);
        offersbtn.setOnClickListener(listnr_offers);


        // Hashmap for ListView
        ProductsList = new ArrayList<Multimap<String, String>>();


        //txtQuery = (TextView) findViewById(R.id.txtQuery);

        handleIntent(getIntent());


        ArrayList<HashMap<String, String>> Pro_SerList = new ArrayList<HashMap<String, String>>();

        XMLParser parser = new XMLParser();

        String xml="";
        ConnectXML parser_z = new ConnectXML();
        try{
            xml = parser_z.execute(URL).get();
        }

        catch (InterruptedException e){e.printStackTrace();

        }catch(ExecutionException e){e.printStackTrace();}

        Document doc = parser_z.getDomElement(xml);



        NodeList nList = doc.getElementsByTagName(KEY_PRODUCT);

        Node node = nList.item(0);
        Element fstElmnt = (Element) node;
        if (fstElmnt == null) {
            Toast.makeText(getApplicationContext(), "No such product available", Toast.LENGTH_SHORT).show();
            finish();

        }

        else {
            String search_url_product = fstElmnt.getAttributes().getNamedItem("xlink:href").getNodeValue();
            search_id_product = fstElmnt.getAttributes().getNamedItem("id").getNodeValue();

            new GetProduct().execute();




        }






    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Handling intent data
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            search_query = query;

            //URL = "http://192.168.1.31/prestashop16/api/search?query="+search_query+"&language=1";
            URL = MAIN_URL.concat("/api/search?query="+search_query+"&language=1");
            //Intent i = new Intent(getApplicationContext(), Products.class);
            //i.putExtra("search_query",search_query);
            //startActivity(i);
            //return true;

            /**
             * Use this query to display search results like
             * 1. Getting the data from SQLite and showing in listview
             * 2. Making webrequest and displaying the data
             * For now we just display the query only
             */
            //txtQuery.setText("Search Query: " + query);

        }

    }

    class GetProduct extends AsyncTask<String, String, String> {

        int help_display_check2;
        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(SearchResultsActivity.this);
            pDialog.setTitle("Product Search");
            pDialog.setMessage("Searching...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_product", search_id_product));
            params.add(new BasicNameValuePair("id_shop", Id_Shop));

            // getting JSON string from URL
            JSONObject json_search = jParser.makeHttpRequest(SEARCH_URL, "POST", params);

            try {
                int success = json_search.getInt(TAG_SUCCESS);

                if (success == 1) {
                    product_ids = json_search.getJSONArray(TAG_PRODUCTS);
                    if (MyDebug.LOG) {
                        Log.d("MainActivity", "search :" + product_ids);
                    }

                    for (int i = 0; i < product_ids.length(); i++) {
                        JSONObject c = product_ids.getJSONObject(i);


                        Multimap<String, String> multiMap = ArrayListMultimap.create();


                        String Id_Product = c.getString(TAG_ID_PRODUCT);
                        String Name = c.getString(TAG_PRODUCT_NAME);

                        String Price = c.getString(TAG_Price);

                        String Image_Id = c.getString(TAG_IMAGE_ID);
                        String Image_Url = MAIN_URL.concat("/api/images/products/").concat(Id_Product.concat("/").concat(Image_Id));


                        List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                        params2.add(new BasicNameValuePair("id_product", Id_Product));

                        JSONObject json_attb = jParser.makeHttpRequest(ATTRIBUTE_URL, "POST", params2);

                        try {

                            int success2 = json_attb.getInt(TAG_SUCCESS);

                            if (success2 == 1) {
                                attribute_ids = json_attb.getJSONArray(TAG_ATTRIBUTES);
                                for (int j=0;j < attribute_ids.length();j++ ) {
                                    JSONObject d = attribute_ids.getJSONObject(j);




                                    Id_Product_Attribute = d.getString(TAG_ID_PRODUCT_ATTRIBUTE);
                                    Impact = d.getString(TAG_PRICE_IMPACT);
                                    Id_Attribute = d.getString(TAG_ID_ATTRIBUTE);
                                    Attribute_Name = d.getString(TAG_ATTRIBUTE_NAME);

                                    multiMap.put(TAG_ID_PRODUCT_ATTRIBUTE,Id_Product_Attribute);
                                    multiMap.put(TAG_PRICE_IMPACT,Impact);
                                    multiMap.put(TAG_ID_ATTRIBUTE,Id_Attribute);
                                    multiMap.put(TAG_ATTRIBUTE_NAME,Attribute_Name);


                                }

                            } else {}
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        multiMap.put(TAG_ID_PRODUCT,Id_Product);
                        multiMap.put(TAG_PRODUCT_NAME,Name);
                        multiMap.put(TAG_Price,Price);
                        multiMap.put(TAG_IMAGE_URL, Image_Url);




                        ProductsList.add(multiMap);


                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    int pr_len = ProductsList.size();
                    help_display_check2=pr_len;
                    if (pr_len == 0) {
                        Toast.makeText(getApplicationContext(), "No Products ...", Toast.LENGTH_SHORT).show();
                    } else {

                        search_list = (ListView) findViewById(R.id.search_pr_list);


                        adapter = new LazyAdapter_Searchable(SearchResultsActivity.this, ProductsList);
                        adapter.setCustomButtonListner(SearchResultsActivity.this);
                        search_list.setAdapter(adapter);



                        if (help_display_check2!=0)
                        {

                        final Dialog dialog = new Dialog(SearchResultsActivity.this, R.style.ThemeWithCorners);
                        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_usage);


                            dialog.setCancelable(false);
                            dialog.setCanceledOnTouchOutside(false);
                        TextView text = (TextView) dialog.findViewById(R.id.TextView01);
                        text.setText(R.string.usage);


                        Button button = (Button) dialog.findViewById(R.id.Button01);
                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();


                            }
                        });

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (!((Activity) SearchResultsActivity.this).isFinishing()) {
                                    dialog.show();
                                }


                            }
                        }, 6400);
                    }



                        search_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {

                            }
                        });

                    }

                } });

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_searchable, menu);

        MenuItem item = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) item.getIcon();

        Utils2.setBadgeCount(this, icon, mNotificationsCount);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.chkout:
                if (!cd.isConnectingToInternet()) {
                    alert.showAlertDialog(SearchResultsActivity.this, "Internet Connection Error",
                            "Please connect to working Internet connection", false);
                }


                else if(preferences.getString("Pref_Cart_Flag",null)!=null && preferences.getString("Pref_Cart_Flag",null).equals("1")){
                    Intent k= new Intent(SearchResultsActivity.this,CheckoutActivity.class);
                    startActivity(k);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                return true;


            case R.id.action_notifications:

                if (mNotificationsCount>0){
                    Intent intent_cart = new Intent(SearchResultsActivity.this, My_CartActivity.class);
                    startActivity(intent_cart);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(getApplicationContext(), "Your cart is empty", Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



//Updates the count of notifications in the ActionBar.

    public void updateNotificationsBadge(int count) {
        mNotificationsCount = count;

        // force the ActionBar to relayout its MenuItems.
        // onCreateOptionsMenu(Menu) will be called again.
        invalidateOptionsMenu();
    }


    class FetchCountTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... params) {

            return mNotificationsCount;

        }

        @Override
        public void onPostExecute(Integer count) {
            updateNotificationsBadge(count);
        }
    }


    @Override
    public void onButtonClickListner(int position, String customer_id,String product_id ,String attribute_id, String product_quantity) {

        Add_Customer_Id = customer_id;
        Add_Product_Id = product_id;
        Add_Attribute_Id = attribute_id;
        Add_Product_Quantity = product_quantity;
        Add_Id_Shop = Id_Shop;

        new GetCartId().execute();
    }


    class GetCartId extends AsyncTask<String, String, String> {

        int quantity_difference;
        String post_status;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(SearchResultsActivity.this);
            pDialog.setTitle("Processing");
            pDialog.setMessage("Adding/updating product..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", Add_Customer_Id));
            params.add(new BasicNameValuePair("id_shop", Add_Id_Shop));
            params.add(new BasicNameValuePair("id_product", Add_Product_Id));
            params.add(new BasicNameValuePair("id_product_attribute", Add_Attribute_Id));

            JSONObject json_get_cart_id = jParser.makeHttpRequest(URL_GET_CART_ID, "POST", params);

            try {
                int success_stock = json_get_cart_id.getInt(TAG_STOCK_AVAIL);
                if (MyDebug.LOG) {
                    Log.d("success_stock", String.valueOf(success_stock));
                }
                if (success_stock == 1) {

                    int quantity_avail = json_get_cart_id.getInt(TAG_QUANTITY_AVAILABLE);
                    int quantity_demanded = Integer.parseInt(Add_Product_Quantity);


                    if (quantity_avail >= quantity_demanded) {


                        int success = json_get_cart_id.getInt(TAG_SUCCESS);

                        if (success == 1) {
                            Cart_Id = json_get_cart_id.getString(TAG_CART_ID);


                            editor.putString(KEY_CART_ID, Cart_Id);
                            editor.commit();


                            List<NameValuePair> params2 = new ArrayList<NameValuePair>();
                            params2.add(new BasicNameValuePair("id_cart", Cart_Id));
                            params2.add(new BasicNameValuePair("id_product", Add_Product_Id));
                            params2.add(new BasicNameValuePair("quantity", Add_Product_Quantity));
                            params2.add(new BasicNameValuePair("id_product_attribute", Add_Attribute_Id));
                            params2.add(new BasicNameValuePair("id_shop", Add_Id_Shop));

                            JSONObject json_insupdt = jParser.makeHttpRequest(URL_PRODUCT_INSUPDT, "POST", params2);


                            try {
                                int success_insupt = json_insupdt.getInt(TAG_SUCCESS);
                                if (success_insupt == 10) {

                                    post_status = "11";

                                } else if (success_insupt == 11) {
                                    post_status = "111";
                                } else {
                                    post_status = "01";
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            String URL_POST_NEW_CART = MAIN_URL.concat("/api/carts?display=full&filter[id_customer]=[" + Add_Customer_Id + "]");

                            try {
                                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                                Document document = documentBuilder.newDocument();

                                //prestashop
                                Element rootElement = document.createElement("prestashop");
                                document.appendChild(rootElement);

                                //cart
                                Element cartElement = document.createElement("cart");
                                rootElement.appendChild(cartElement);

                                //ID Shop Group
                                Element id_shop_grpElement = document.createElement("id_shop_group");
                                cartElement.appendChild(id_shop_grpElement);
                                id_shop_grpElement.appendChild(document.createTextNode(Id_Shop_Group));

                                //ID Shop
                                Element id_shop_Element = document.createElement("id_shop");
                                cartElement.appendChild(id_shop_Element);
                                id_shop_Element.appendChild(document.createTextNode(Id_Shop));

                                //Currency ID
                                Element id_currencyElement = document.createElement("id_currency");
                                cartElement.appendChild(id_currencyElement);
                                id_currencyElement.appendChild(document.createTextNode("1"));

                                //Customer ID
                                Element id_customerElement = document.createElement("id_customer");
                                cartElement.appendChild(id_customerElement);
                                id_customerElement.appendChild(document.createTextNode(Add_Customer_Id));

                                //id_lang
                                Element id_langElement = document.createElement("id_lang");
                                cartElement.appendChild(id_langElement);
                                id_langElement.appendChild(document.createTextNode("1"));

                                //associations
                                Element associationsElement = document.createElement("associations");
                                cartElement.appendChild(associationsElement);

                                //cart_rows
                                Element cart_rowsElement = document.createElement("cart_rows");
                                associationsElement.appendChild(cart_rowsElement);
                                cart_rowsElement.setAttribute("nodeType", "cart_row");
                                cart_rowsElement.setAttribute("virtualEntity", "true");

                                //cart_row
                                Element cart_rowElement = document.createElement("cart_row");
                                cart_rowsElement.appendChild(cart_rowElement);

                                //id_product
                                Element id_productElement = document.createElement("id_product");
                                cart_rowElement.appendChild(id_productElement);
                                id_productElement.appendChild(document.createTextNode(Add_Product_Id));

                                //id_product_attribute
                                Element id_product_attribute_Element = document.createElement("id_product_attribute");
                                cart_rowElement.appendChild(id_product_attribute_Element);
                                id_product_attribute_Element.appendChild(document.createTextNode(Add_Attribute_Id));

                                //id_address_delivery
                                Element id_address_delivery_Element = document.createElement("id_address_delivery");
                                cart_rowElement.appendChild(id_address_delivery_Element);
                                id_address_delivery_Element.appendChild(document.createTextNode("0"));

                                //quantity
                                Element quantity_Element = document.createElement("quantity");
                                cart_rowElement.appendChild(quantity_Element);
                                quantity_Element.appendChild(document.createTextNode(Add_Product_Quantity));

                                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                                Transformer transformer = transformerFactory.newTransformer();
                                DOMSource source = new DOMSource(document.getDocumentElement());
                                StreamResult result = new StreamResult(new File(Environment.getExternalStorageDirectory()
                                        .getAbsolutePath() + "/" + "file.xml"));
                                transformer.transform(source, result);

                                DefaultHttpClient httpClient = new DefaultHttpClient();
                                String URL_ps_cart = MAIN_URL.concat("/api/carts?schema=synopsis");
                                HttpPost httpost = new HttpPost(URL_ps_cart);
                                String WbSr_password = "";// leave it empty
                                String authToBytes = DEFAULT_USER + ":" + WbSr_password;
                                byte authBytes[] = Base64.encode(authToBytes.getBytes(), Base64.NO_WRAP);
                                String authBytesString = new String(authBytes);
                                httpost.setHeader("Authorization", "Basic " + authBytesString);
                                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
                                File f = new File(filePath, "file.xml");
                                try {
                                    String content = getFileContent(f);
                                    StringEntity se = new StringEntity(content, HTTP.UTF_8);
                                    se.setContentType("text/xml");
                                    httpost.setEntity(se);
                                    f.delete();
                                    HttpResponse httpresponse = httpClient.execute(httpost);
                                    int status_code = httpresponse.getStatusLine().getStatusCode();
                                    int sta = status_code;
                                    if (httpresponse.getStatusLine().getStatusCode() == 201) {
                                        post_status = "10";

                                        JSONObject json_get_cart_id2 = jParser.makeHttpRequest(URL_GET_CART_ID, "POST", params);
                                        int success2 = json_get_cart_id2.getInt(TAG_SUCCESS);
                                        if (success2 == 1) {
                                            Cart_Id = json_get_cart_id2.getString(TAG_CART_ID);


                                            editor.putString(KEY_CART_ID, Cart_Id);
                                            editor.commit();
                                        }

                                    } else {
                                        post_status = "00";
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            } catch (ParserConfigurationException e) {
                            } catch (TransformerConfigurationException e) {
                            } catch (TransformerException e) {
                            }
                        }
                    }else{
                        post_status="0001";
                        quantity_difference = quantity_demanded-quantity_avail;
                    }
                }
                else{
                    post_status = "000";// stock not avail
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }



            return post_status;
        }

        protected void onPostExecute(final String post_status) {
            pDialog.dismiss();

            runOnUiThread(new Runnable() {
                public void run() {

                    if (post_status.equals("000")) {
                        alert.showAlertDialog(SearchResultsActivity.this, "Out of stock",
                                "Oops !! this product is currently out of stock", false);
                        return;
                    } else if (post_status.equals("0001")) {
                        alert.showAlertDialog(SearchResultsActivity.this, "Information",
                                "Requested quantity not available, Please reduce quantity by "+quantity_difference, false);
                        return;
                    } else {
                        {
                            if (post_status.equals("11")) {
                                editor.putString(KEY_CART_FLAG, "1");
                                editor.commit();

                                mNotificationsCount=mNotificationsCount+1;
                                updateNotificationsBadge(mNotificationsCount);


                                Products.mNotificationsCount = mNotificationsCount;
                                Products notifyupdate_products = new Products();
                                notifyupdate_products.updateNotificationsBadge(notifyupdate_products.mNotificationsCount);

                                Category.mNotificationsCount = mNotificationsCount;
                                Category notifyupdate_cat = new Category();
                                notifyupdate_cat.updateNotificationsBadge(notifyupdate_cat.mNotificationsCount);

                                Toast.makeText(SearchResultsActivity.this, "Product added", Toast.LENGTH_LONG).show();
                            } else if (post_status.equals("01")) {
                                Toast.makeText(SearchResultsActivity.this, "Product insertion/updation fails", Toast.LENGTH_LONG).show();
                            } else if (post_status.equals("111")) {
                                Toast.makeText(SearchResultsActivity.this, "Quantity updated", Toast.LENGTH_LONG).show();
                            } else if (post_status.equals("10")) {

                                Products.mNotificationsCount = Products.mNotificationsCount + 1;
                                updateNotificationsBadge(Products.mNotificationsCount);

                                Category.mNotificationsCount = mNotificationsCount;
                                Category notifyupdate_cat = new Category();
                                notifyupdate_cat.updateNotificationsBadge(notifyupdate_cat.mNotificationsCount);

                                editor.putString(KEY_CART_FLAG, "1");
                                editor.commit();

                                Toast.makeText(SearchResultsActivity.this, "Product added", Toast.LENGTH_SHORT).show();


                            } else if (post_status.equals("00")) {
                                Toast.makeText(SearchResultsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }


                        }
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
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
        reader.close();
        inputStream.close();

        return stringBuilder.toString();
    }

    @Override
    public void onResume(){
        super.onResume();
        // put your code here...
        //cart_flag = preferences.getString("Pref_Cart_Flag",null);
        updateNotificationsBadge(Products.mNotificationsCount);

    }


}
