package com.exfresh.exfreshapp;

import static com.exfresh.exfreshapp.CommonUtilities.SERVER_LOC_URL;
import static com.exfresh.exfreshapp.CommonUtilities.TAG;
import static com.exfresh.exfreshapp.CommonUtilities.displayMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
/**
 * Created by Mahendra on 6/1/2015.
 */
public class LocationServerUtilities {

    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();



    Context context = MyApplication.getContext();
//    SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("PREF_NAME", context.MODE_PRIVATE);
    //SharedPreferences.Editor editor = preferences.edit();


    /**
     * Register this account/device pair within the server.
     *
     */
    static void register(final Context context, String latitude, String longitude, String email,String phone_number, String city, String country, String addressLine) {
        String serverUrl = SERVER_LOC_URL;
        Map<String, String> params = new HashMap<String, String>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        params.put("email", email);
        params.put("phone", phone_number);
        params.put("city", city);
        params.put("country", country);
        params.put("addressline", addressLine);

        long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            if (MyDebug.LOG) {
                Log.d(TAG, "Attempt #" + i + " to register");
            }
            try {
                displayMessage(context, context.getString(
                        R.string.server_loc_registering, i, MAX_ATTEMPTS));
                post(serverUrl, params);
                //GCMRegistrar.setRegisteredOnServer(context, true);
                String message = context.getString(R.string.server_loc_registered);
                CommonUtilities.displayMessage(context, message);

                String LOCATION_SET = "Pref_Location_Set";
                SharedPreferences preferences = MyApplication.getContext().getSharedPreferences("PREF_NAME", context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString(LOCATION_SET, "1");
                editor.commit();

                return;
            } catch (IOException e) {
                // Here we are simplifying and retrying on any error; in a real
                // application, it should retry only on unrecoverable errors
                // (like HTTP error code 503).
                Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                if (i == MAX_ATTEMPTS) {
                    break;
                }
                try {
                    if (MyDebug.LOG) {
                        Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                    }
                    Thread.sleep(backoff);
                } catch (InterruptedException e1) {
                    // Activity finished before we complete - exit.
                if (MyDebug.LOG) {
                    Log.d(TAG, "Thread interrupted: abort remaining retries!");
                }
                    Thread.currentThread().interrupt();
                    return;
                }
                // increase backoff exponentially
                backoff *= 2;
            }
        }
        String message = context.getString(R.string.server_register_error,
                MAX_ATTEMPTS);
        CommonUtilities.displayMessage(context, message);
    }



    /**
     * Issue a POST request to the server.
     *
     * @param endpoint POST address.
     * @param params request parameters.
     *
     * @throws IOException propagated from POST.
     */
    private static void post(String endpoint, Map<String, String> params)
            throws IOException {

        URL url;
        try {
            url = new URL(endpoint);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url: " + endpoint);
        }
        StringBuilder bodyBuilder = new StringBuilder();
        Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
        // constructs the POST body using the parameters
        while (iterator.hasNext()) {
            Entry<String, String> param = iterator.next();
            bodyBuilder.append(param.getKey()).append('=')
                    .append(param.getValue());
            if (iterator.hasNext()) {
                bodyBuilder.append('&');
            }
        }
        String body = bodyBuilder.toString();
        Log.v(TAG, "Posting '" + body + "' to " + url);
        byte[] bytes = body.getBytes();
        HttpURLConnection conn = null;
        try {
            Log.e("URL", "> " + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setFixedLengthStreamingMode(bytes.length);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded;charset=UTF-8");
            // post the request
            OutputStream out = conn.getOutputStream();
            out.write(bytes);
            out.close();
            // handle the response
            int status = conn.getResponseCode();
            if (status != 200) {
                throw new IOException("Post failed with error code " + status);
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

}
