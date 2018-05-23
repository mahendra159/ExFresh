package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/**
 * Created by Mahendra on 3/23/2015.
 */
public class LazyAdapter_Searchable extends BaseAdapter {



    customButtonListener customListner;

    public interface customButtonListener {
        public void onButtonClickListner(int position,String value, String value2, String value3, String value4);
    }



    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    private Context context;

    //this counts how many Gallery's are on the UI
    private int mGalleryCount = 0;

    //this counts how many Gallery's have been initialized
    private int mGalleryInitializedCount = 1;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Activity activity;
    private ArrayList<Multimap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    double calc_price;

    //public static TextView Product_Cart_Indicator;

    // Progress Dialog
    private ProgressDialog pDialog;

    Spinner spinner_dropdown;

    Button minusbtn;
    Button plusbtn;
    ImageButton certbtn;

    Button addtocart_btn;


    public static String prc_id;
    public static String crt_id;
    public static String pr_qty;
    public static String cus_id;
    public static String pr_price;
    String MAIN_URL;
    String URL_PHP;


    String Id_Shop;
    String DEFAULT_USER;

    String TAG_SUCCESS;

    Double Calculated_Price;
    //String id_attribute_value;

    String CART_FLAG = "Pref_Cart_Flag";
    String KEY_CART_ID = "Pref_Cart_Id";
    String get_dp_price;
    Double Impact_Price;
    String PRODUCT_URL;
    public String url_create_product;

    public String C2_cart_id;
    public String C2_product_id;
    public String C2_pr_quantity;
    public String C2_id_pr_attribute;
    public String C2_id_shop;





    public LazyAdapter_Searchable(Activity a, ArrayList<Multimap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());

        mGalleryCount = data.size();
    }

    public LazyAdapter_Searchable() { };

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.activity_products_row, null);

        prefs = activity.getApplicationContext().getSharedPreferences("PREF_NAME", 0);
        editor = prefs.edit();

        MAIN_URL = prefs.getString("Pref_main_url", null);
        URL_PHP = prefs.getString("Pref_url_php", null);
        Id_Shop = prefs.getString("Pref_shop_id", null);
        DEFAULT_USER = prefs.getString("Pref_Webservice_Key", null);

        TextView P_Id_Product = (TextView) vi.findViewById(R.id.id_product);
        TextView P_Id_Shop_Default = (TextView) vi.findViewById(R.id.id_shop_default);
        TextView P_Name = (TextView) vi.findViewById(R.id.pr_name); // title
        final TextView P_Default_Price = (TextView) vi.findViewById(R.id.pr_default_price); // duration
        //Product_Cart_Indicator = (TextView) vi.findViewById(R.id.product_cart_indicator);
        addtocart_btn = (Button) vi.findViewById(R.id.add_to_cart_btn);
        final TextView qty = (TextView) vi.findViewById(R.id.quantity);
        spinner_dropdown = (Spinner) vi.findViewById(R.id.spinner1);
        //minusbtn = (ImageButton) vi.findViewById(R.id.minusButton);
        minusbtn = (Button) vi.findViewById(R.id.minusButton);
        //plusbtn = (ImageButton) vi.findViewById(R.id.plusButton);
        plusbtn = (Button) vi.findViewById(R.id.plusButton);
        certbtn = (ImageButton) vi.findViewById(R.id.certbtn);

        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image

        Multimap<String, String> song = ArrayListMultimap.create();
        song = data.get(position);


        Collection<String> myCollection = new HashSet<String>();
        myCollection = song.get("id_product");
        String Id_Product = myCollection.toString();
        String Id_Product_F = Id_Product.substring(1, Id_Product.length() - 1);
        final String id = Id_Product_F;

        final String output[] = new String[1];

        //final String id_attribute_value;


        Collection<String> myCollection2 = new HashSet<String>();
        myCollection2 = song.get("name");
        String Product_Name = myCollection2.toString();
        String Product_Name_F = Product_Name.substring(1, Product_Name.length() - 1);

        Collection<String> myCollection3 = new HashSet<String>();
        myCollection3 = song.get("price");
        String Product_Price = myCollection3.toString();
        final String Product_Price_F = Product_Price.substring(1, Product_Price.length() - 5);

        Collection<String> myCollection4 = new HashSet<String>();
        myCollection4 = song.get("id_shop_default");
        String Id_Shop_Default = myCollection4.toString();
        String Id_Shop_Default_F = Id_Shop_Default.substring(1, Id_Shop_Default.length() - 1);

        Collection<String> myCollection5 = new HashSet<String>();
        myCollection5 = song.get("unity");
        String Unity = myCollection5.toString();
        String Unity_F = Unity.substring(1, Unity.length() - 1);

        Collection<String> myCollection6 = new HashSet<String>();
        myCollection6 = song.get("id_product_attribute");
        String Id_Product_Attribute = myCollection6.toString();

        final List<String> id_product_attribute_spin_list = new ArrayList<String>();
        for (String value : song.get("id_product_attribute")) {
            //Log.d("multimap",value);
            id_product_attribute_spin_list.add(value);
        }


        Collection<String> myCollection7 = new HashSet<String>();
        myCollection7 = song.get("id_attribute");
        String Id_Attribute = myCollection7.toString();

        List<String> id_attribute_spin_list = new ArrayList<String>();
        for (String value : song.get("id_attribute")) {
            //Log.d("multimap",value);
            id_attribute_spin_list.add(value);
        }


        Collection<String> myCollection8 = new HashSet<String>();
        myCollection8 = song.get("impact");
        String Price_Impact = myCollection8.toString();

        final List<String> impact_spin_list = new ArrayList<String>();
        for (String value : song.get("impact")) {
            //Log.d("multimap",value);
            impact_spin_list.add(value);
        }

        Collection<String> myCollection9 = new HashSet<String>();
        myCollection9 = song.get("attribute_name");
        String Attribute_Name = myCollection9.toString();

        List<String> attribute_name_spin_list = new ArrayList<String>();
        attribute_name_spin_list.add("Select Qty");
        for (String value : song.get("attribute_name")) {
            attribute_name_spin_list.add(value);
        }

        Collection<String> myCollection10 = new HashSet<String>();
        myCollection10 = song.get("image");
        String Temp_Image_Url = myCollection10.toString();
        String Product_Image_Url = Temp_Image_Url.substring(1, Temp_Image_Url.length() - 1);


        P_Name.setText(Product_Name_F);
        P_Id_Product.setText(Id_Product_F);
        //P_Id_Shop_Default.setText(Id_Shop_Default_F);
        imageLoader.DisplayImage(Product_Image_Url, thumb_image);

        if (impact_spin_list.size() > 0) {
            spinner_dropdown.setVisibility(View.VISIBLE);
            String temp_Impact_price0 = impact_spin_list.get(0);
            String temp_price_f0 = temp_Impact_price0.substring(0, temp_Impact_price0.length() - 4);
            calc_price = Double.parseDouble(Product_Price_F) + Double.parseDouble(temp_price_f0);
            int Calculated_Price = (int) calc_price;
            P_Default_Price.setText("Rs." + String.valueOf(Calculated_Price));

        } else {
            spinner_dropdown.setVisibility(View.GONE);
            P_Default_Price.setText("Rs." + String.valueOf(Product_Price_F));
        }


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(activity.getApplicationContext(), R.layout.spinner_item, attribute_name_spin_list) {
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

        dataAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner_dropdown.setAdapter(dataAdapter);

        spinner_dropdown.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // if (mGalleryInitializedCount < mGalleryCount)
                        // {
                        //     mGalleryInitializedCount++;
                        // }
                        // else {
                        String temp_Impact_price;
                        String temp_price_f;
                        int real_position = position - 1;

                        for (int i = 0; i < impact_spin_list.size(); i++) {
                            if (real_position == i) {
                                temp_Impact_price = impact_spin_list.get(i);
                                temp_price_f = temp_Impact_price.substring(0, temp_Impact_price.length() - 4);

                                Calculated_Price = Double.parseDouble(Product_Price_F) + Double.parseDouble(temp_price_f);
                                P_Default_Price.setText("Rs." + String.valueOf(Calculated_Price));
                            }
                        }

                        for (int i = 0; i < id_product_attribute_spin_list.size(); i++) {
                            if (real_position == i) {
                                String id_attribute_value = id_product_attribute_spin_list.get(i);
                                output[0] = id_attribute_value;
                                //id_attribute_value = id_product_attribute_spin_list.get(i);
                                //Log.d("Attribute Value set :", id_attribute_value);

                            }

                        }
                        //}

                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

        minusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {

                String quan = qty.getText().toString();
                int val = Integer.parseInt(quan) - 1;
                if (val > 0) {
                    qty.setText(String.valueOf(val));
                }

            }
        });
        plusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {

                String quan = qty.getText().toString();
                int val = Integer.parseInt(quan) + 1;
                qty.setText(String.valueOf(val));
            }
        });


        certbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {

                String pr_id = id;
                final Intent GoToDisplay = new Intent(activity.getApplication(),CertActivity.class);
                GoToDisplay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                GoToDisplay.putExtra("product_id", pr_id);
                activity.getApplication().startActivity(GoToDisplay);

            }
        });

        addtocart_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customListner != null) {
                    //Toast.makeText(activity.getApplicationContext(), "button clicked ", Toast.LENGTH_SHORT).show();
                    String product_quantity = qty.getText().toString();
                    String cs_id = prefs.getString("Pref_Customer_Id", null);
                    String pr_id = id;

                    String id_attribute_value = output[0];

                    if (id_attribute_value == null) {
                        Toast.makeText(activity.getApplicationContext(), "Please select quantity of product", Toast.LENGTH_SHORT).show();
                    } else {

                        // if (null_cndtn==0){
                        // set cart id to zero
                        //require
                        // id_customer
                        // product_id
                        // id_attribute_value
                        // product_quantity
                        //
                        // }
                        customListner.onButtonClickListner(position, cs_id, pr_id, id_attribute_value, product_quantity);

                    }

                }
            }

        });

        return vi;
    }

    public String get_pr_id() {
        return prc_id;
    }

    public String get_crt_id() {
        return crt_id;
    }

    public String get_qty() {
        return pr_qty;
    }

    public String get_cus_id() {
        return cus_id;
    }

    public String get_price() {
        return pr_price;
    }


}
    /*
    private void SingleChoice() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Single Choice");
        final CharSequence[] selectFruit = {"Choice I", "Choice II", "Choice III", "Choice IV", "Choice V"};
        builder.setItems(selectFruit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(activity.getApplicationContext(), selectFruit[which] + " Selected", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    */





/*
        // get all the set of keys
       // Set<String> keys = song.keySet();

//        for (String key : keys) {
  //          Log.d("Key = ",  key);
    //        Log.d("Values = " , song.get(key) + "n");
            //String ty = (song.get(key));
      //  }
// Iterating over entire Mutlimap
        for(String value : song.get("name")) {
            Log.d("multimap",value);
        }
        */

//  ArrayList<String> A_Product_Id = new ArrayList<String>();
//  ArrayList<String> A_Product_Name = new ArrayList<String>();
//  ArrayList<String> A_Id_Shop = new ArrayList<String>();
//  ArrayList<String> A_Product_Price = new ArrayList<String>();
//  ArrayList<String> A_Unity = new ArrayList<String>();
//  ArrayList<String> A_Id_Product_Attb = new ArrayList<String>();
//  ArrayList<String> A_Price_Impact = new ArrayList<String>();
//  ArrayList<String> A_Id_Attb = new ArrayList<String>();
//  ArrayList<String> A_Attb_Name = new ArrayList<String>();
