package com.lendasoft.clubercompanion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by victorrosas on 2/11/16.
 */
public class HTTP_Request {
    String weburl =  "http://api.cluberservice.com/api/";

    /**
     * Login
     * @param s_mobile # de telefono con el que se registro al mesero
     * @param s_pin   # pin que se asigno al mesero
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public  JSONObject get_login(String s_mobile,String s_pin) throws IOException, JSONException {
        String url = weburl + "User/VerifyCredentials?mobilePhoneNumber="  +  s_mobile +"&pin=" + s_pin;
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("X-CLUBERAPP-ApiKey", "key_Y2x1YmVyYXBwOkNsdWIzcg==");
        InputStream is = new BufferedInputStream(urlConnection.getInputStream());
        //readJsonStream(is);
        return null;
    }



    public JSONObject IStoJSON(InputStream is) throws IOException, JSONException {
        BufferedReader BR = new BufferedReader(new InputStreamReader(is));
        String line = "";

        StringBuilder responseStrBuilder = new StringBuilder();
        while ((line = BR.readLine()) != null ){

            responseStrBuilder.append(line);
        }
        is.close();
        JSONObject result = new JSONObject(responseStrBuilder.toString());
        return result;
    }



}
