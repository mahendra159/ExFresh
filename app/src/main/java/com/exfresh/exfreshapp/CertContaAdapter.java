package com.exfresh.exfreshapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mahendra on 7/19/2015.
 */
public class CertContaAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;

    public CertContaAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public CertContaAdapter (){  };

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
            vi = inflater.inflate(R.layout.activity_cert_row_conta, null);


        TextView txt0 = (TextView)vi.findViewById(R.id.textView0);
        TextView txt1 = (TextView)vi.findViewById(R.id.textView1);
        TextView txt2 = (TextView)vi.findViewById(R.id.textView2);
        TextView txt3 = (TextView)vi.findViewById(R.id.textView3);
        TextView txt4 = (TextView)vi.findViewById(R.id.textView4);
        TextView txt5 = (TextView)vi.findViewById(R.id.textView5);
        TextView txt6 = (TextView)vi.findViewById(R.id.textView6);
        TextView txt7 = (TextView)vi.findViewById(R.id.textView7);

        TextView txt8 = (TextView)vi.findViewById(R.id.textView8);
        TextView txt9 = (TextView)vi.findViewById(R.id.textView9);
        TextView txt10 = (TextView)vi.findViewById(R.id.textView10);
        TextView txt11 = (TextView)vi.findViewById(R.id.textView11);
        TextView txt12 = (TextView)vi.findViewById(R.id.textView12);

        HashMap<String, String> listdata = new HashMap<String, String>();
        listdata = data.get(position);

        String pos = String.valueOf(position);
        if (MyDebug.LOG) {
            Log.d("Value of pos", pos);
        }


        txt0.setText(listdata.get(CertActivity.TAG_CONTA_NAME));
        txt1.setText(listdata.get(CertActivity.TAG_CONTA_TYPE));
        if(listdata.get(CertActivity.TAG_CONTA_WL).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_WL).equalsIgnoreCase("") ){
            txt2.setVisibility(View.GONE);
        }else{
            txt2.setText(listdata.get(CertActivity.TAG_CONTA_WL));
        }


        if(listdata.get(CertActivity.TAG_CONTA_DWL).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_DWL).equalsIgnoreCase("")){
            txt3.setVisibility(View.GONE);
        }else{
            txt3.setText(listdata.get(CertActivity.TAG_CONTA_DWL));
        }

        if(listdata.get(CertActivity.TAG_CONTA_SL).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_SL).equalsIgnoreCase("")){
            txt4.setVisibility(View.GONE);
        }else{
            txt4.setText(listdata.get(CertActivity.TAG_CONTA_SL));
        }

        if(listdata.get(CertActivity.TAG_CONTA_DSL).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_DSL).equalsIgnoreCase("")){
            txt5.setVisibility(View.GONE);
        }else{
            txt5.setText(listdata.get(CertActivity.TAG_CONTA_DSL));
        }

        txt6.setText(listdata.get(CertActivity.TAG_CONTA_VL));
        txt7.setText(listdata.get(CertActivity.TAG_CONTA_DVL));

        if(listdata.get(CertActivity.TAG_CONTA_CL8).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_CL8).equalsIgnoreCase("")){
            txt8.setVisibility(View.GONE);
        }else{
            txt8.setText(listdata.get(CertActivity.TAG_CONTA_CL8));
        }

        if(listdata.get(CertActivity.TAG_CONTA_CL9).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_CL9).equalsIgnoreCase("")){
            txt9.setVisibility(View.GONE);
        }else{
            txt9.setText(listdata.get(CertActivity.TAG_CONTA_CL9));
        }

        if(listdata.get(CertActivity.TAG_CONTA_CL10).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_CL10).equalsIgnoreCase("")){
            txt10.setVisibility(View.GONE);
        }else{
            txt10.setText(listdata.get(CertActivity.TAG_CONTA_CL10));
        }

        if(listdata.get(CertActivity.TAG_CONTA_CL11).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_CL11).equalsIgnoreCase("")){
            txt11.setVisibility(View.GONE);
        }else{
            txt11.setText(listdata.get(CertActivity.TAG_CONTA_CL11));
        }

        if(listdata.get(CertActivity.TAG_CONTA_CL12).equalsIgnoreCase("null") || listdata.get(CertActivity.TAG_CONTA_CL12).equalsIgnoreCase("")){
            txt12.setVisibility(View.GONE);
        }else{
            txt12.setText(listdata.get(CertActivity.TAG_CONTA_CL12));
        }

        return vi;
    }
}
