package com.exfresh.exfreshapp;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Mahendra on 5/19/2015.
 */
public final class CommonUtilities {

    // give your server registration url here
    //static final String SERVER_URL = "http://www.exfresh.co.in/gcm_server_php/register.php";
    static final String SERVER_URL = "http://exfresh.online/exfresh3/gcm_server_php/register.php";

    //for location register
    //static final String SERVER_LOC_URL = "http://www.exfresh.co.in/location_server_php/register.php";
    //static final String SERVER_LOC_URL = "http://www.exfresh.co.in/location_server_php/register.php"; //for exfresh
    static final String SERVER_LOC_URL = "http://exfresh.online/exfresh3/location_server_php/register.php"; //for exfresh2

    // Google project id
    static final String SENDER_ID = "899946396905";

    /**
     * Tag used on log messages.
     */
    static final String TAG = "ExFresh GCM";

    static final String DISPLAY_MESSAGE_ACTION =
            "com.exfresh.exfresh.DISPLAY_MESSAGE";

    static final String EXTRA_MESSAGE = "message";

    /**
     * Notifies UI to display a message.
     * <p>
     * This method is defined in the common helper because it's used both by
     * the UI and the background service.
     *
     * @param context application's context.
     * @param message message to be displayed.
     */
    static void displayMessage(Context context, String message) {
        Intent intent = new Intent(DISPLAY_MESSAGE_ACTION);
        intent.putExtra(EXTRA_MESSAGE, message);
        context.sendBroadcast(intent);
    }
}
