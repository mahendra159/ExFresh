package com.exfresh.exfreshapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Created by Mahendra on 4/10/2015.
 */
public class ConnectXML extends AsyncTask<String, Void, String>  {

    private Activity activity;

    public ConnectXML(Activity a) {

        activity = a;
    }

    public ConnectXML() {

    }

    String xml ;
    protected void onPreExecute() {
        super.onPreExecute();

    }

    Context context = MyApplication.getContext();


    SharedPreferences preferences =MyApplication.getContext().getSharedPreferences("PREF_NAME", context.MODE_PRIVATE);
    String DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);



    protected String doInBackground(String... params){

        try {

            String url = params[0];
            HttpGet request = new HttpGet(url);

            String DEFAULT_PASS = "";// leave it empty
            String auth = DEFAULT_USER + ":" + DEFAULT_PASS;
            byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")), Base64.NO_WRAP);
            String authHeader = "Basic " + new String(encodedAuth);
            request.setHeader("AUTHORIZATION", authHeader);


            HttpClient client = new DefaultHttpClient();//HttpClientBuilder.create().build();
            HttpResponse httpResponse = client.execute(request);

            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        }


        catch (Throwable t) {
            //Toast.makeText(Category.this, "Exception :" + t.toString(), Toast.LENGTH_LONG).show();
        }

        return xml;
    }


    @Override
    protected void onPostExecute(String xml) {

        super.onPostExecute(xml);

    }



    public Document getDomElement(String xml){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (SAXException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        } catch (IOException e) {
            Log.e("Error: ", e.getMessage());
            return null;
        }

        return doc;
    }

}
