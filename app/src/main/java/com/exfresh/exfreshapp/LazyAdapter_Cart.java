package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahendra on 3/28/2015.
 */
public class LazyAdapter_Cart extends BaseAdapter {



    public interface customButtonListener {
        public void onButtonClickListner(int position,String value, String value2,String value3, int value4);
    }

    customButtonListener customListner;

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    // listener for plus button /////////////////////////////////////////////////////////////////
  /*
    public interface PlusButtonListener {
        public int onPlusButtonClickListner(int position,String value, String value2,String value3, String value4, String value5);
    }

    PlusButtonListener pluscustomListner;

    public void setPlusButtonListner(PlusButtonListener listener) {
        this.pluscustomListner = listener;

    }

*/
    //////////////////////////////////////////////////////////////////////////////////////////////

    private Context context;


    //private final Context context;
    private static SharedPreferences prefs;
    SharedPreferences.Editor editor;

    float tp =0;

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public String C2_cart_id;
    public String C2_product_id;
    public String C2_pr_quantity;
    public String C2_id_pr_attribute;

    //public static String id_attribute_value;

    String product_quantity;

    int updated_quantity2=0;

    public String url_inc_dec_product;
    public static String prc_id;
    public static String crt_id;
    public static String pr_qty;
    public static String cus_id;
    public static String pr_price;
    public static String pr_base_price;
    public static String Tot_amount;
    public static String Tot_items;
    public static String pr_impact;

    public static String pr_impacted_unit_price;




//    TextView Total_PRICE;

    //float adjusted_price;

   // String update_status;

    //ImageButton minusbtn;
    Button minusbtn;
    //ImageButton plusbtn;
    Button plusbtn;
    Button removebtn;

    String MAIN_URL;
    String URL_PHP;
    String URL_DECREASE;
    String URL_INCREASE;

    //public static String test;

    public float Total_AMT =0;
    public int Total_ITM=0;

    String KEY_CART_ID = "Pref_Cart_Id";

    //String TAG_SUCCESS="success";
    private static final String TAG_SUCCESS = "success";

    //GlobalVar global = new GlobalVar();


    public LazyAdapter_Cart(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());


    }

    public LazyAdapter_Cart (){  };

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }






    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_cart_row, null);

        prefs = activity.getApplicationContext().getSharedPreferences("PREF_NAME", 0);
        editor = prefs.edit();

        MAIN_URL = prefs.getString("Pref_main_url", null);
        URL_PHP = prefs.getString("Pref_url_php", null);
        final String Cart_Id = prefs.getString("Pref_Cart_Id", null);
        //URL_DECREASE = URL_PHP.concat("update_product_decrease.php");
        //URL_INCREASE = URL_PHP.concat("update_product_increase.php");

        //final String temp = getItem(position);

        //PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        prefs = activity.getApplicationContext().getSharedPreferences("PREF_NAME", 0);
        editor = prefs.edit();

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        //TextView attrib = (TextView)vi.findViewById(R.id.attribute);
        TextView Attribute_Quantity = (TextView)vi.findViewById(R.id.attribute);
        TextView Product_Quantity = (TextView)vi.findViewById(R.id.pr_quantity_value);
        TextView Final_Price = (TextView) vi.findViewById(R.id.final_price);


        // these are additional
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        TextView price_mul = (TextView) vi.findViewById(R.id.price_mul);
        final TextView quan_mul = (TextView) vi.findViewById(R.id.quantity_mul);
        final TextView multiplication_result = (TextView) vi.findViewById(R.id.mul_result);
        final TextView qty = (TextView)vi.findViewById(R.id.quantity);  // pehle final textview qty tha

        //calling reference of main activity textview in custom adapter
        final TextView Total_PRICE = (TextView)(activity).findViewById(R.id.Total_Price);

        //TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        //TextView Product_Main_Price = (TextView)vi.findViewById(R.id.pr_main_price); // duration
        //ImageButton img_addtocart_btn=(ImageButton)vi.findViewById(R.id.img_add_to_cart_btn);
        //TextView pr_quantity_val = (TextView)vi.findViewById(R.id.pr_quantity_value);


        //minusbtn = (ImageButton)vi.findViewById(R.id.minusButton);
        minusbtn = (Button)vi.findViewById(R.id.minusButton);
        //plusbtn = (ImageButton)vi.findViewById(R.id.plusButton);
        plusbtn = (Button)vi.findViewById(R.id.plusButton);
        removebtn = (Button)vi.findViewById(R.id.remove_btn);


        HashMap<String, String> song = new HashMap<String, String>();
        song = data.get(position);

        final String id = song.get(My_CartActivity.TAG_ID_PRODUCT);
        final String id_attribute_value = song.get(My_CartActivity.TAG_ID_PRODUCT_ATTRIBUTE);
        String Product_Image_Url = song.get(My_CartActivity.TAG_IMAGE_URL);

        //pr_impact = song.get(My_CartActivity.KEY_PRICE_IMPACT);
        //String pp_qty = song.get(My_CartActivity.KEY_QUANTITY);
        //String pr_impact_trimd = pr_impact.substring(0,pr_price.length()-4);
        //final float adjusted_price = Float.parseFloat(pr_price_trimd)+Float.parseFloat(pr_impact_trimd);
        //float final_price_per_item = adjusted_price*Float.parseFloat(pp_qty);
        //String final_price_p_item = String.valueOf(final_price_per_item);

        //pr_price = song.get(My_CartActivity.KEY_PRICE);
        pr_price = song.get(My_CartActivity.TAG_FINAL_PRICE);
        final String pr_price_trimd = pr_price.substring(0,pr_price.length()-4);
        int pr_price_trimd_int = (int)Float.parseFloat(pr_price_trimd);
        pr_base_price = song.get(My_CartActivity.TAG_BASE_PRICE);
        pr_impacted_unit_price = song.get(My_CartActivity.TAG_IMPACTED_UNIT_PRICE);

        final String pr_base_price_trimd = pr_base_price.substring(0,pr_price.length()-4);  //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        final String pr_impacted_unit_price_trimd = pr_impacted_unit_price.substring(0, pr_price.length() - 4);
        final int pr_impacted_unit_price_int = (int)Float.parseFloat(pr_impacted_unit_price_trimd);


        tp = tp+Float.parseFloat(pr_price_trimd);

        //final float adjusted_price = Float.parseFloat(pr_base_price_trimd);  //@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        final float adjusted_price = Float.parseFloat(pr_impacted_unit_price_trimd);

        title.setText(song.get(My_CartActivity.TAG_PRODUCT_NAME));
        qty.setText(song.get(My_CartActivity.TAG_QUANTITY));
        //attrib.setText(song.get(My_CartActivity.KEY_ATTRIBUTE_VAL));
        Attribute_Quantity.setText(song.get(My_CartActivity.TAG_ATTRIBUTE_NAME));

        //price_mul.setText(pr_base_price_trimd);
        price_mul.setText(String.valueOf(pr_impacted_unit_price_int));
        quan_mul.setText(song.get(My_CartActivity.KEY_QUANTITY));
        //multiplication_result.setText("Rs."+final_price_p_item);
        multiplication_result.setText("Rs."+pr_price_trimd_int);
        imageLoader.DisplayImage(Product_Image_Url, thumb_image);

        //Total_AMT = Total_AMT+final_price_per_item;
        //Tot_amount = String.valueOf(Total_AMT);
        //artist.setText(song.get(My_CartActivity.KEY_CONDITION));
        //Product_Main_Price.setText("Rs." + pr_price_trimd);


        //Total_ITM=Total_ITM+1;
        //Tot_items = String.valueOf(Total_ITM);




        removebtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (customListner != null) {

                    String minus_price_pre = multiplication_result.getText().toString();
                    if (MyDebug.LOG) {
                        Log.d("minus_price_pre", minus_price_pre);
                        Log.d("minus_price_length", String.valueOf(minus_price_pre.length()));
                    }
                    String minus_pr = minus_price_pre.substring(3, minus_price_pre.length());
                    if (MyDebug.LOG) {
                        Log.d("minus_pr", minus_pr);
                    }
                    int minus_price = Integer.parseInt(minus_pr);

                    customListner.onButtonClickListner(position,id,id_attribute_value,Cart_Id,minus_price);

                }

            }
        });

        minusbtn.setOnClickListener(new View.OnClickListener() {


                                                 @Override
                                                 public void onClick(View vi) {
                                                     //do stuff when clicked
                                                     String product_quantity = qty.getText().toString();

                                                     int quan_check = Integer.parseInt(product_quantity);
                                                     if (quan_check > 1){
                                                         C2_cart_id = Cart_Id;
                                                     C2_product_id = id;
                                                     C2_pr_quantity = product_quantity;
                                                     C2_id_pr_attribute = id_attribute_value;

                                                     // url to create new product
                                                     url_inc_dec_product = URL_PHP.concat("update_product_decrease.php");

                                                     // creating new product in background thread
                                                     new UpdateProduct().execute();

                                                     Toast.makeText(activity.getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();

                                                     int updated_quantity = Integer.parseInt(product_quantity) - 1;

                                                     qty.setText(String.valueOf(updated_quantity));
                                                     quan_mul.setText(String.valueOf(updated_quantity));
                                                     float final_price_per_item = adjusted_price * updated_quantity;
                                                     int final_price_per_item_int = (int) final_price_per_item;
                                                     multiplication_result.setText("Rs."+String.valueOf(final_price_per_item_int));
                                                     My_CartActivity.Total_Price = My_CartActivity.Total_Price - pr_impacted_unit_price_int;
                                                     Total_PRICE.setText("Total Price - Rs." + (My_CartActivity.Total_Price));
                                                 } else {
                                                         Toast.makeText(activity.getApplicationContext(), "To remove product, Tap on Remove", Toast.LENGTH_SHORT).show();
                                                     }

                                                 }
           });

        plusbtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View vi) {
                //do stuff when clicked
                product_quantity = qty.getText().toString();
                C2_cart_id = Cart_Id;
                C2_product_id = id;
                C2_pr_quantity = product_quantity;
                C2_id_pr_attribute = id_attribute_value;

                // url to create new product
                url_inc_dec_product = URL_PHP.concat("update_product_increase.php");

                new UpdateProduct().execute();

                int updated_quantity = Integer.parseInt(product_quantity) + 1;

                qty.setText(String.valueOf(updated_quantity));
                quan_mul.setText(String.valueOf(updated_quantity));
                float final_price_per_item = adjusted_price * updated_quantity;
                int final_price_per_item_int = (int)final_price_per_item;
                multiplication_result.setText("Rs."+String.valueOf(final_price_per_item_int));
                My_CartActivity.Total_Price = My_CartActivity.Total_Price + pr_impacted_unit_price_int;
                Total_PRICE.setText("Total Price - Rs." + (My_CartActivity.Total_Price));

                Toast.makeText(activity.getApplicationContext(), "Successfully Updated", Toast.LENGTH_SHORT).show();

          }
       });




        return vi;



    }

    public String get_pr_id(){
        return prc_id;
    }
    public String get_crt_id(){return crt_id; }
    public String get_cus_id(){return cus_id;}
    public String get_price() {return pr_price;}
    public String get_qty(){return pr_qty;}
   // public String get_total_cart_items() {return Tot_items;}
    //public String get_total_cart_amount(){return Tot_amount;}


    class UpdateProduct extends AsyncTask<String, String, String> {
        ProgressDialog aProgressDialogL = new ProgressDialog(activity);
          String update_status;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //aProgressDialogL.setTitle("Processing");
            //aProgressDialogL.setMessage("Updating product quantity..");
            //aProgressDialogL.show();

        }
        /*** updating product* */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cart", C2_cart_id));
            params.add(new BasicNameValuePair("id_product", C2_product_id));
            params.add(new BasicNameValuePair("quantity", C2_pr_quantity));
            params.add(new BasicNameValuePair("id_product_attribute", C2_id_pr_attribute));

            // getting JSON Object
            // Note that create product url accepts POST method
            JSONParser_cart r = new JSONParser_cart();
            JSONObject json = r.makeHttpRequest(url_inc_dec_product,"POST", params);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    update_status = "1";
                } else {
                    update_status = "0";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return update_status;
        }

        protected void onPostExecute(String update_status) {
            //pDialog.dismiss();
            final String success_p = update_status;

            if (success_p.equals("1")){

            }
            else{

            }

        }

    }



}

/*

Take Product Quantity, Customer_ID, Product_Id and Attribute_value

Now fetching cart_id corresponding to customer_id
            If not exist, then create new cart in ps_cart table. And then get CART_ID and then update products
			If exist, then get CART_ID, and then check in ps_order table.
			                If exist in ps_order, then post new cart and get CART_ID and then update products
							If not exist in ps_order, then cart_id will remain same and update products in ps_cart_product


USE CASES for updating cart
Fetch cart_id corrspnding to customer_id
     	     CART_ID EXIST					EXIST IN PS_ORDER TABLE				POSTING NEW CART				UPDATING PRODUCTS IN PS_CART_PRODUCT
				yes      ------------------>		yes     --------------> 	    post      ----------------->			update
				yes      ------------------>		no		--------------------------------------------------->			update
				no       ------------------------------------------------->			post      ----------------->			update

 */