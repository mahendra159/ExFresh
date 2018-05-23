package com.exfresh.exfreshapp;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
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
public class LazyAdapter extends BaseAdapter implements Animation.AnimationListener {



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

    // Animation
    Animation animBlink;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    private Activity activity;
    private ArrayList<Multimap<String, String>> data;
    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;

    private ProgressDialog pDialog;

    Spinner spinner_dropdown;

    ImageButton certbtn;
    Button plusbtn;
    Button minusbtn;

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

    double calc_price;


    public LazyAdapter(Activity a, ArrayList<Multimap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());

        mGalleryCount = data.size();
    }

    public LazyAdapter() { };

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

        // load the animation
        animBlink = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                R.anim.blink);

        animBlink.setAnimationListener(this);

//        AlphaAnimation  blinkanimation= new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
//        blinkanimation.setDuration(300); // duration - half a second
//        blinkanimation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
//        blinkanimation.setRepeatCount(3); // Repeat animation infinitely
//        blinkanimation.setRepeatMode(Animation.REVERSE);

        TextView P_Id_Product = (TextView) vi.findViewById(R.id.id_product);
        TextView P_Name = (TextView) vi.findViewById(R.id.pr_name); // title
        final TextView P_Default_Price = (TextView) vi.findViewById(R.id.pr_default_price); // duration
        addtocart_btn = (Button) vi.findViewById(R.id.add_to_cart_btn);
        final TextView qty = (TextView) vi.findViewById(R.id.quantity);
        spinner_dropdown = (Spinner) vi.findViewById(R.id.spinner1);
        minusbtn = (Button) vi.findViewById(R.id.minusButton);
        plusbtn = (Button) vi.findViewById(R.id.plusButton);
        certbtn = (ImageButton) vi.findViewById(R.id.certbtn);

        final TextView P_Item_Cart = (TextView) vi.findViewById(R.id.pr_item_in_cart);

        ImageView thumb_image = (ImageView) vi.findViewById(R.id.list_image); // thumb image

        //TextView P_Id_Shop_Default = (TextView) vi.findViewById(R.id.id_shop_default);
        //minusbtn = (ImageButton) vi.findViewById(R.id.minusButton);
        //plusbtn = (ImageButton) vi.findViewById(R.id.plusButton);
        //Product_Cart_Indicator = (TextView) vi.findViewById(R.id.product_cart_indicator);

        Multimap<String, String> song = ArrayListMultimap.create();
        song = data.get(position);

        Collection<String> myCollection = new HashSet<String>();
        myCollection = song.get("id_product");
        String Id_Product = myCollection.toString();
        String Id_Product_F = Id_Product.substring(1, Id_Product.length() - 1);
        final String id = Id_Product_F;

        final String output[] = new String[1];

        Collection<String> myCollection2 = new HashSet<String>();
        myCollection2 = song.get("name");
        String Product_Name = myCollection2.toString();
        String Product_Name_F = Product_Name.substring(1, Product_Name.length() - 1);

        Collection<String> myCollection3 = new HashSet<String>();
        myCollection3 = song.get("price");
        String Product_Price = myCollection3.toString();
        final String Product_Price_F = Product_Price.substring(1, Product_Price.length() - 5);

        Collection<String> myCollection6 = new HashSet<String>();
        myCollection6 = song.get("id_product_attribute");
        String Id_Product_Attribute = myCollection6.toString();

        final List<String> id_product_attribute_spin_list = new ArrayList<String>();
        for (String value : song.get("id_product_attribute")) {
            id_product_attribute_spin_list.add(value);
        }

        Collection<String> myCollection7 = new HashSet<String>();
        myCollection7 = song.get("id_attribute");
        String Id_Attribute = myCollection7.toString();

        List<String> id_attribute_spin_list = new ArrayList<String>();
        for (String value : song.get("id_attribute")) {
            id_attribute_spin_list.add(value);
        }


        Collection<String> myCollection8 = new HashSet<String>();
        myCollection8 = song.get("impact");
        String Price_Impact = myCollection8.toString();

        final List<String> impact_spin_list = new ArrayList<String>();
        for (String value : song.get("impact")) {
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

        Collection<String> myCollection11 = new HashSet<String>();
        myCollection11 = song.get("item_qty_cart");
        String Item_In_Cart = myCollection11.toString();
        String Item_In_Cart_F = Item_In_Cart.substring(1, Item_In_Cart.length() - 1);

        int Item_In_Cart_int = Integer.parseInt(Item_In_Cart_F);

        if (Item_In_Cart_int>0){
            P_Item_Cart.setText("[In cart]");
        }

        P_Name.setText(Product_Name_F);
        P_Id_Product.setText(Id_Product_F);
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
                        /*
                         if (mGalleryInitializedCount < mGalleryCount)
                         {
                             mGalleryInitializedCount++;
                         }
                         else {
                        */
                        String temp_Impact_price;
                        String temp_price_f;
                        int real_position = position - 1;

                        for (int i = 0; i < impact_spin_list.size(); i++) {
                            if (real_position == i) {
                                temp_Impact_price = impact_spin_list.get(i);
                                temp_price_f = temp_Impact_price.substring(0, temp_Impact_price.length() - 4);

                                calc_price = Double.parseDouble(Product_Price_F) + Double.parseDouble(temp_price_f);
                                int Calculated_Price = (int) calc_price;
                                P_Default_Price.setText("Rs." + String.valueOf(Calculated_Price));
                            }
                        }

                        for (int i = 0; i < id_product_attribute_spin_list.size(); i++) {
                            if (real_position == i) {
                                String id_attribute_value = id_product_attribute_spin_list.get(i);
                                output[0] = id_attribute_value;
                            }
                        }
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
        certbtn.startAnimation(animBlink);
        //applyRotation(certbtn);
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
                    String product_quantity = qty.getText().toString();
                    String cs_id = prefs.getString("Pref_Customer_Id", null);
                    String pr_id = id;

                    String id_attribute_value = output[0];

                    if (id_attribute_value == null) {
                        Toast.makeText(activity.getApplicationContext(), "Please select quantity of product", Toast.LENGTH_SHORT).show();
                    } else {
                        customListner.onButtonClickListner(position, cs_id, pr_id, id_attribute_value, product_quantity);
                    }
                }
            }

        });

        return vi;
    }


    @Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation

        // check for fade in animation
        //if (animation == animFadein) {
        //    Toast.makeText(getApplicationContext(), "Animation Stopped",
        //            Toast.LENGTH_SHORT).show();
       // }

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }



/*
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
*/

}