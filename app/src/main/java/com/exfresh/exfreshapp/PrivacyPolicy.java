package com.exfresh.exfreshapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * Created by Mahendra on 4/18/2018.
 */

public class PrivacyPolicy extends Activity {

    //TextView tv1;
    private WebView wv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pp);

        //tv1 = (TextView)findViewById(R.id.textview_pp);
       // tv1.setText(getResources().getString(R.string.privacy_policy_v2));


        wv1=(WebView)findViewById(R.id.webview);
       // wv1.setBackgroundColor(0);
        //wv1.setBackgroundColor(Color.BLACK);


        String str = "<html>\n" +
                "<body>\n" +
                "<head>"+"<style  type=\"text/css\">"+
                "body,h1{color: #bebab9\n;"+
                "background-color: #474645 ;}"+
                "</style></head>"+
                "<h2>Privacy Policy</h2>\n" +
                "<p>We as ExFresh built the ExFresh app as a free app. This SERVICE is provided by ExFresh at no cost and is intended\n" +
                "    for use as is.</p>\n" +
                "<p>This page is used to inform website visitors regarding our policies with the collection, use, and\n" +
                "    disclosure of Personal Information if anyone decided to use our Service.</p>\n" +
                "<p>If you choose to use our Service, then you agree to the collection and use of information in\n" +
                "    relation with this policy. The Personal Information that we collect are used for providing and\n" +
                "    improving the Service. We will not use or share your information with anyone except as described\n" +
                "    in this Privacy Policy.</p>\n" +
                "<p>The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions,\n" +
                "    which is accessible at ExFresh, unless otherwise defined in this Privacy Policy.</p>\n" +
                "\n" +
                "<p><strong>Information Collection and Use</strong></p>\n" +
                "<p>For a better experience while using our Service, we may require you to provide us with certain\n" +
                "    personally identifiable information, including but not limited to email, users name, address, location. \n" +
                "\tThe information that we request is retained by us and used as described in this privacy policy.</p>\n" +
                "<p>The app does use third party services that may collect information used to identify you.\n" +
                "\n" +
                "<p><strong>Log Data</strong></p>\n" +
                "<p>We want to inform you that whenever you use our Service, in case of an error in the app we collect\n" +
                "    data and information (through third party products) on your phone called Log Data. This Log Data\n" +
                "    may include information such as your devices’s Internet Protocol (“IP”) address, device name,\n" +
                "    operating system version, configuration of the app when utilising our Service, the time and date\n" +
                "    of your use of the Service, and other statistics.</p>\n" +
                "\n" +
                "<p><strong>Cookies</strong></p>\n" +
                "<p>Cookies are files with small amount of data that is commonly used an anonymous unique identifier.\n" +
                "    These are sent to your browser from the website that you visit and are stored on your devices’s\n" +
                "    internal memory. Our app is not using cookies in any mode.</p>\n" +
                "\n" +
                "\n" +
                "<p><strong>Service Providers</strong></p> <!-- This part need seem like it's not needed, but if you use any Google services, or any other third party libraries, chances are, you need this. -->\n" +
                "<p>We may employ third-party companies and individuals due to the following reasons:</p>\n" +
                "<ul>\n" +
                "    <li>To facilitate our Service;</li>\n" +
                "    <li>To provide the Service on our behalf;</li>\n" +
                "    <li>To perform Service-related services; or</li>\n" +
                "    <li>To assist us in analyzing how our Service is used.</li>\n" +
                "</ul>\n" +
                "<p>We want to inform users of this Service that these third parties have access to your Personal\n" +
                "    Information. The reason is to perform the tasks assigned to them on our behalf. However, they\n" +
                "    are obligated not to disclose or use the information for any other purpose.</p>\n" +
                "\n" +
                "<p><strong>Security</strong></p>\n" +
                "<p>We value your trust in providing us your Personal Information, thus we are striving to use\n" +
                "    commercially acceptable means of protecting it. But remember that no method of transmission over\n" +
                "    the internet, or method of electronic storage is 100% secure and reliable, and we cannot\n" +
                "    guarantee its absolute security.</p>\n" +
                "\n" +
                "<p><strong>Links to Other Sites</strong></p>\n" +
                "<p>This Service may contain links to other sites like youtube. If you click on a third-party link, you will be\n" +
                "    directed to that site. Note that these external sites are not operated by us. Therefore, I\n" +
                "    strongly advise you to review the Privacy Policy of these websites. I have no control over, and\n" +
                "    assume no responsibility for the content, privacy policies, or practices of any third-party\n" +
                "    sites or services.</p>\n" +
                "\n" +
                "<p><strong>Children’s Privacy</strong></p>\n" +
                "<p>This Services do not address anyone under the age of 13. We do not knowingly collect personal\n" +
                "    identifiable information from children under 13. In the case we discover that a child under 13\n" +
                "    has provided us with personal information, we immediately delete this from our servers. If you\n" +
                "    are a parent or guardian and you are aware that your child has provided us with personal\n" +
                "    information, please contact us so that we will be able to do necessary actions.</p>\n" +
                "\n" +
                "<p><strong>Changes to This Privacy Policy</strong></p>\n" +
                "<p>We may update our Privacy Policy from time to time. Thus, you are advised to review this page\n" +
                "    periodically for any changes. We will notify you of any changes by posting the new Privacy Policy\n" +
                "    on this page. These changes are effective immediately, after they are posted on this page.</p>\n" +
                "\n" +
                "<p><strong>Contact Us</strong></p>\n" +
                "<p>If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact\n" +
                "    us.</p>\n" +
                "</body>\n" +
                "</html>";

        wv1.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);

        /*
        tv1.setText(Html.fromHtml("<html>\n" +
                "<body>\n" +
                "<h2>Privacy Policy</h2>\n" +
                "<p>We as ExFresh built the ExFresh app as a free app. This SERVICE is provided by ExFresh at no cost and is intended\n" +
                "    for use as is.</p>\n" +
                "<p>This page is used to inform website visitors regarding our policies with the collection, use, and\n" +
                "    disclosure of Personal Information if anyone decided to use our Service.</p>\n" +
                "<p>If you choose to use our Service, then you agree to the collection and use of information in\n" +
                "    relation with this policy. The Personal Information that we collect are used for providing and\n" +
                "    improving the Service. We will not use or share your information with anyone except as described\n" +
                "    in this Privacy Policy.</p>\n" +
                "<p>The terms used in this Privacy Policy have the same meanings as in our Terms and Conditions,\n" +
                "    which is accessible at ExFresh, unless otherwise defined in this Privacy Policy.</p>\n" +
                "\n" +
                "<p><strong>Information Collection and Use</strong></p>\n" +
                "<p>For a better experience while using our Service, we may require you to provide us with certain\n" +
                "    personally identifiable information, including but not limited to email, users name, address, location. \n" +
                "\tThe information that we request is retained by us and used as described in this privacy policy.</p>\n" +
                "<p>The app does use third party services that may collect information used to identify you.\n" +
                "\n" +
                "<p><strong>Log Data</strong></p>\n" +
                "<p>We want to inform you that whenever you use our Service, in case of an error in the app we collect\n" +
                "    data and information (through third party products) on your phone called Log Data. This Log Data\n" +
                "    may include information such as your devices’s Internet Protocol (“IP”) address, device name,\n" +
                "    operating system version, configuration of the app when utilising our Service, the time and date\n" +
                "    of your use of the Service, and other statistics.</p>\n" +
                "\n" +
                "<p><strong>Cookies</strong></p>\n" +
                "<p>Cookies are files with small amount of data that is commonly used an anonymous unique identifier.\n" +
                "    These are sent to your browser from the website that you visit and are stored on your devices’s\n" +
                "    internal memory. Our app is not using cookies in any mode.</p>\n" +
                "\n" +
                "\n" +
                "<p><strong>Service Providers</strong></p> <!-- This part need seem like it's not needed, but if you use any Google services, or any other third party libraries, chances are, you need this. -->\n" +
                "<p>We may employ third-party companies and individuals due to the following reasons:</p>\n" +
                "<ul>\n" +
                "    <li>To facilitate our Service;</li>\n" +
                "    <li>To provide the Service on our behalf;</li>\n" +
                "    <li>To perform Service-related services; or</li>\n" +
                "    <li>To assist us in analyzing how our Service is used.</li>\n" +
                "</ul>\n" +
                "<p>We want to inform users of this Service that these third parties have access to your Personal\n" +
                "    Information. The reason is to perform the tasks assigned to them on our behalf. However, they\n" +
                "    are obligated not to disclose or use the information for any other purpose.</p>\n" +
                "\n" +
                "<p><strong>Security</strong></p>\n" +
                "<p>We value your trust in providing us your Personal Information, thus we are striving to use\n" +
                "    commercially acceptable means of protecting it. But remember that no method of transmission over\n" +
                "    the internet, or method of electronic storage is 100% secure and reliable, and we cannot\n" +
                "    guarantee its absolute security.</p>\n" +
                "\n" +
                "<p><strong>Links to Other Sites</strong></p>\n" +
                "<p>This Service may contain links to other sites like youtube. If you click on a third-party link, you will be\n" +
                "    directed to that site. Note that these external sites are not operated by us. Therefore, I\n" +
                "    strongly advise you to review the Privacy Policy of these websites. I have no control over, and\n" +
                "    assume no responsibility for the content, privacy policies, or practices of any third-party\n" +
                "    sites or services.</p>\n" +
                "\n" +
                "<p><strong>Children’s Privacy</strong></p>\n" +
                "<p>This Services do not address anyone under the age of 13. We do not knowingly collect personal\n" +
                "    identifiable information from children under 13. In the case we discover that a child under 13\n" +
                "    has provided us with personal information, we immediately delete this from our servers. If you\n" +
                "    are a parent or guardian and you are aware that your child has provided us with personal\n" +
                "    information, please contact us so that we will be able to do necessary actions.</p>\n" +
                "\n" +
                "<p><strong>Changes to This Privacy Policy</strong></p>\n" +
                "<p>We may update our Privacy Policy from time to time. Thus, you are advised to review this page\n" +
                "    periodically for any changes. We will notify you of any changes by posting the new Privacy Policy\n" +
                "    on this page. These changes are effective immediately, after they are posted on this page.</p>\n" +
                "\n" +
                "<p><strong>Contact Us</strong></p>\n" +
                "<p>If you have any questions or suggestions about our Privacy Policy, do not hesitate to contact\n" +
                "    us.</p>\n" +
                "</body>\n" +
                "</html>"));
                */
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
