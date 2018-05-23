package com.exfresh.exfreshapp;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Mahendra on 3/23/2015.
 */
public class JSONParser_cart {


    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";

    // constructor
    public JSONParser_cart() {

    }



    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String url, String method,
                                      List<NameValuePair> params) {

        // Making HTTP request
        try {

            // check for request method
            if(method == "POST"){
                // request method is POST
                // defaultHttpClient

                HttpParams httpParams = new BasicHttpParams();

                ConnManagerParams.setTimeout(httpParams, 50000);
                HttpConnectionParams.setConnectionTimeout(httpParams,50000);
                HttpConnectionParams.setSoTimeout(httpParams, 50000);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();

            }else if(method == "GET"){
                // request method is GET

                HttpParams httpParams = new BasicHttpParams();

                ConnManagerParams.setTimeout(httpParams, 50000);
                HttpConnectionParams.setConnectionTimeout(httpParams,50000);
                HttpConnectionParams.setSoTimeout(httpParams, 50000);

                DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            }

        }
        catch (final Throwable t) {


            try {
                new Thread() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        //Toast.makeText(MainActivity.this, str, Toast.LENGTH_LONG).show();
                        Toast.makeText(MyApplication.getContext(), "Server not responding !!!" , Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                }.start();
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
            }

            //Toast.makeText(MyApplication.getContext(), "Internet too slow !! Error Code:" + t.toString(), Toast.LENGTH_LONG).show();
        }
        /*
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }  */

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            json = sb.toString();
            Log.i("tagconvertstr", "[" + json + "]");
        } catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jObj;

    }


}
