package com.exfresh.exfreshapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by Mahendra on 3/21/2015.
 */
public class XMLParser {






    // constructor
    public XMLParser() {

    }

    Context context = MyApplication.getContext();


    SharedPreferences preferences =MyApplication.getContext().getSharedPreferences("PREF_NAME", context.MODE_PRIVATE);
    String DEFAULT_USER = preferences.getString("Pref_Webservice_Key", null);


    /**
     * Getting XML from URL making HTTP request
     * @param url string
     * */
    public String getXmlFromUrl(String url) {
        String xml = null;



        try {
            HttpGet request = new HttpGet(url);

            //getContext().getApplicationContext().getString(R.string.WebService_Key);
            //(R.string.WebService_Key);

            //app.getContext().getResources().getString(R.string.WebService_Key);
            String DEFAULT_PASS = "";// leave it empty
            String auth = DEFAULT_USER + ":" + DEFAULT_PASS;
            //byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
            byte[] encodedAuth = Base64.encode(auth.getBytes(Charset.forName("US-ASCII")), Base64.NO_WRAP);
            String authHeader = "Basic " + new String(encodedAuth);
            request.setHeader("AUTHORIZATION", authHeader);


            HttpClient client = new DefaultHttpClient();//HttpClientBuilder.create().build();
            HttpResponse httpResponse = client.execute(request);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            HttpEntity httpEntity = httpResponse.getEntity();
            xml = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // return XML
        return xml;
    }

    /**
     * Getting XML DOM element
     * @param XML string
     * */
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

    /** Getting node value
     * @param elem element
     */

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    public final String getElementValue( Node elem ) {
        Node child;


        if( elem != null){

            if (elem.hasAttributes())
            {

                /*
                Node node = n.item(0);
                Element fstElmnt = (Element) Node;
                Attr marakim = fstElmnt.getAttributeNode("soups")
                Element attri;
                Attr rt = elem.getAttributes("XLINK");

                if (child2.getNodeType() == Node.TEXT_NODE || child2.getNodeType() == Node.CDATA_SECTION_NODE) {
                    return child2.getNodeValue();



            }

                if (elem.hasChildNodes()) {
                    //////////////////////////////////////////
                for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){

                    if( child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE  ){
                        return child.getNodeValue();
                    }

                    if(child.getNodeName().equalsIgnoreCase("description"))
                    {
                        return child.getTextContent();
                    }

                   // if( child.getNodeType() == Node.TEXT_NODE  ){
                   //     return child.getTextContent();
                   // }
               // }

                    child = elem.getFirstChild();

                    while (child != null) {
                        /////
                        Node child2;
                        child2 = child.getFirstChild();
                        while (child2 != null) {
                            if (child2.getNodeType() == Node.TEXT_NODE || child2.getNodeType() == Node.CDATA_SECTION_NODE) {
                                return child2.getNodeValue();
                            }
                        }
                        ////
                        if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE) {
                            return child.getNodeValue();
                        }
                        child = child.getNextSibling();
                    }

                    //String P_name=element2.getElementsByTagName("name").item(0).getTextContent();


                    ///////////////////////////////////////////////////////
                }

        }
        return "";
    }*/

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public final String getElementValue( Node elem ) {  // vo n hi yahan elem hai....n=elem=typesList
        Node node = elem;
        if (elem == null) {
            return null;
        }
        String str = elem.getNodeName();
        if (str=="id_category_default"){

            String category_id = elem.getFirstChild().getNodeValue();
        }



        Element imgElemnt = (Element) node;
        Attr img_url = imgElemnt.getAttributeNode("xlink:href");

        Node child;

        if (elem != null) {
            if (elem.hasChildNodes()) {
                //child=elem.getAttributes()
                for (child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
                    Node child2;
                    child2 = child.getFirstChild();
                    Node child3;
                    child3 = null;

                    if (child2 != null) {
                        child3 = child2.getFirstChild();
                    }
                    if (img_url != null) {
                        return img_url.getNodeValue();
                    }

                    if (child.getNodeType() == Node.TEXT_NODE || child.getNodeType() == Node.CDATA_SECTION_NODE
                            || child.getNodeType() == Node.ATTRIBUTE_NODE && img_url.getNodeValue() == null) {

                        return child.getNodeValue();

                    }


                    while (child2 != null) {
                        if (child2.getNodeType() == Node.TEXT_NODE || child2.getNodeType() == Node.CDATA_SECTION_NODE) {

                            return child2.getNodeValue();

                        }
                    }

                    while (child3 != null) {
                        if (child3.getNodeType() == Node.TEXT_NODE || child2.getNodeType() == Node.CDATA_SECTION_NODE || child3.getNodeType() == Node.ATTRIBUTE_NODE ) {
                            return child2.getNodeValue();
                        }
                    }

                }
            }
        }
        return "";
    }
    //////////////////////////////////////////////////////


    /**
     * Getting node value
     * @param Element node
     * @param key string
     * */
    public String getValue(Element item, String str) { // e and image tag bheja hai  e mein poora product dom hai
        NodeList n = item.getElementsByTagName(str);   // yahan ye execute ho rahi hai NodeList typesList = doc.getElementsByTagName("image");

        return this.getElementValue(n.item(0));
    }



    //Node node = typesList.item(0);
    // Element fstElmnt = (Element) node;
    // Attr marakim = fstElmnt.getAttributeNode("xlink:href");
    //NodeList marakimList = marakim.getChildNodes();
    //Element nameElement = (Element) marakimList.item(0);
    //marakimList = nameElement.getChildNodes();
    // String test = marakim.getNodeValue();



}
