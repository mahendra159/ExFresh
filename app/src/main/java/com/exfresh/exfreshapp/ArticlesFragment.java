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
public class ArticlesFragment extends Fragment {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    // products JSONArray
    JSONArray article_ids = null;

    // Creating JSON Parser object
    JSONParser_cart jParser = new JSONParser_cart();

    ArrayList<HashMap<String, String>> articleList;

    private static final String TAG_ARTICLE_URLS = "article_urls";

    static final String TAG_ARTICLE_LINK = "link";

    String MAIN_URL;
    String URL;

    private static final String TAG_SUCCESS = "success";


    ListView list;
    ArticleAdapter adapter;

    String URL_PHP;
    String URL_ARTICLE;

    // Connection detector
    ConnectionDetector cd;

    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // Progress Dialog
    private ProgressDialog pDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_articles, container, false);

        cd = new ConnectionDetector(this.getActivity().getApplicationContext());

        preferences = this.getActivity().getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        editor = preferences.edit();


        MAIN_URL = preferences.getString("Pref_main_url", null);

        URL_PHP = preferences.getString("Pref_url_php", null);
        URL_ARTICLE = URL_PHP.concat("Get_Article.php");

        articleList = new ArrayList<HashMap<String, String>>();

        new GetArticles().execute();

        return rootView;
    }


    class GetArticles extends AsyncTask<String, String, String> {

        /** Before starting background thread Show Progress Dialog * */

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        protected String doInBackground(String... args)
        {
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id_cust", "article"));

            JSONObject json = jParser.makeHttpRequest(URL_ARTICLE, "POST", params);

            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // products found
                    // Getting Array of Products
                    article_ids = json.getJSONArray(TAG_ARTICLE_URLS);

                    for (int i = 0; i < article_ids.length(); i++) {
                        JSONObject c = article_ids.getJSONObject(i);

                        // Storing each json item in variable
                        String article_url = c.getString(TAG_ARTICLE_LINK);
                        String article_name = c.getString("name");
                        String thumbnail_url=c.getString("thumbnail");

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ARTICLE_LINK, article_url);
                        map.put("name", article_name);
                        map.put("thumbnail", thumbnail_url);

                        // adding HashList to ArrayList
                        articleList.add(map);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();

            super.onPostExecute(file_url);

            int article_count = articleList.size();
            if (article_count == 0) {
                Toast.makeText(getActivity().getApplicationContext(), "No articles available ...", Toast.LENGTH_SHORT).show();
            } else
            {

                list = (ListView) getActivity().findViewById(R.id.article_list);

                // Getting adapter by passing xml data ArrayList
                adapter = new ArticleAdapter(getActivity(), articleList);
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
                                        TextView clicked_article_link = (TextView)view.findViewById(R.id.article_link);
                                        String articleId = clicked_article_link.getText().toString();
                                        Intent i = new Intent(Intent.ACTION_VIEW);
                                        i.setData(Uri.parse(articleId));
                                        startActivity(i);
                                    }
                                }).setNegativeButton("No", null).show();
                    }
                });
            }
        }
    }
}
