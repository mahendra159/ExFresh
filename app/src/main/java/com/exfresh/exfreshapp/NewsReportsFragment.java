package com.exfresh.exfreshapp;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahendra on 7/14/2015.
 */
public class NewsReportsFragment extends Fragment {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // products JSONArray
    JSONArray news_ids = null;

    // Creating JSON Parser object
    JSONParser_cart jParser = new JSONParser_cart();

    ArrayList<HashMap<String, String>> newsList;

    private static final String TAG_NEWS_URLS = "news_urls";

    static final String TAG_NEWS_LINK = "link";

    String MAIN_URL;
    String URL;

    private static final String TAG_SUCCESS = "success";


    ListView list;
    NewsAdapter adapter;

    String URL_PHP;
    String URL_NEWS;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Progress Dialog
    private ProgressDialog pDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_news_reports, container, false);

        cd = new ConnectionDetector(this.getActivity().getApplicationContext());

        preferences = this.getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        editor = preferences.edit();


        MAIN_URL = preferences.getString("Pref_main_url", null);

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_NEWS = URL_PHP.concat("Get_Media_News.php");

        newsList = new ArrayList<HashMap<String, String>>();

        new GetNews().execute();


        return rootView;
    }



    // Background Async Task to fetch order_ids corresponding to customer ids
    class GetNews extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            //pDialog.setTitle("Order History");
            //pDialog.setMessage("Retrieving your order !! Please wait...");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        // getting All products from url

        protected String doInBackground(String... args)
        {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", "news"));

            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(URL_NEWS, "POST", params);

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                //Log.d("Inside Try Block : "+success");
                //Log.d("MainActivity", "INSIDE TRY block, value of success :" + success);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    news_ids = json.getJSONArray(TAG_NEWS_URLS);


                    // looping through All Orders
                    for (int i = 0; i < news_ids.length(); i++) {
                        JSONObject c = news_ids.getJSONObject(i);

                        // Storing each json item in variable
                        String news_url = c.getString(TAG_NEWS_LINK);
                        String news_name = c.getString("name");
                        String thumbnail_url=c.getString("thumbnail");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_NEWS_LINK, news_url);
                        map.put("name", news_name);
                        map.put("thumbnail", thumbnail_url);

                        // adding HashList to ArrayList
                        newsList.add(map);
                    }

                } else {

                    // no products found
                    /*
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(getApplicationContext(), "Cart Empty ! Redirecting to Categories Menu ...", Toast.LENGTH_SHORT).show();
                            //thread.start();
                            //Intent i = new Intent(getApplicationContext(),Category.class);
                            //startActivity(i);
                        }
                    }); */
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }



        //  After completing background task Dismiss the progress dialog


        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            super.onPostExecute(file_url);

            int news_count = newsList.size();
            if (news_count == 0) {
                Toast.makeText(getActivity().getApplicationContext(), "No news available ...", Toast.LENGTH_SHORT).show();
            } else
            {

                list = (ListView) getActivity().findViewById(R.id.news_list);

                // Getting adapter by passing xml data ArrayList
                adapter = new NewsAdapter(getActivity(), newsList);
                list.setAdapter(adapter);

                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        // Check for internet connection
                        if (!cd.isConnectingToInternet()) {
                            // Internet Connection is not present
                            alert.showAlertDialog(getActivity(), "Internet Connection Error",
                                    "Please connect to working Internet connection", false);
                            return;
                        }


                        new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_info)
                                .setMessage("View in browser.")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        TextView clicked_news_link = (TextView)view.findViewById(R.id.news_link);
                                        String newsId = clicked_news_link.getText().toString();
                                        //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + newsId));
                                        //intent.putExtra("NEWS_ID", newsId);
                                        //startActivity(intent);
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(newsId));
                                        startActivity(i);
                                    }
                                }).setNegativeButton("No", null).show();


                    }
                });

            }

        }

    }

}
