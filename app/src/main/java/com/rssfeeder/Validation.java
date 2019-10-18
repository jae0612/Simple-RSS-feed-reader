/**
 *
 *
 * @author Jae Shin (u6858132)
 * @author Jihwan Bae (u6871659)
 */

package com.rssfeeder;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class Validation {



    public static boolean isValueFeeder(String url){
        if(!url.startsWith("http://") && !url.startsWith("https://")){
            // the url must start with http:// or https://
            return false;
        }

        // Can execute on the main thread
        /*
        String xml = getHTTP(url);
        if(xml == null || !xml.startsWith("<?xml")){
            // the site must return xml document

            return false;
        }
        */

        return true;

    }

    /*
    public static String getHTTP(String url_string) {
        String get = null;

        try {
            URL url = new URL(url_string);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(in);

            get = br.readLine();
            System.out.println("GET: " +get);

            br.close();
            urlConnection.disconnect();
        } catch(Exception e){
            e.printStackTrace();
        }

        return get;

    }
     */



}
