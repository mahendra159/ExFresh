package com.exfresh.exfreshapp;

import android.app.Application;
import android.content.Context;

/**
 * Created by Mahendra on 4/6/2015.
 */
public class MyApplication extends Application {

    private static Context mContext;

    private String key;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }

    public static Context getContext() {

        return mContext;
    }

    public String getKey(){
        return this.key;
    }

    public void setKey(String k){
        this.key=k;
    }

    private int spin_load_check=0;

    public int getSpin_load_check() {
        return spin_load_check;
    }

    public void setSpin_load_check(int spin_load_check) {
        this.spin_load_check = spin_load_check;
    }

}
