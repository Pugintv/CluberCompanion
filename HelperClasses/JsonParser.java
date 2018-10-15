package com.lendasoft.clubercompanion.HelperClasses;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class JsonParser {


    /*public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            return readJsonFromStream(in);
        }
        finally {
            urlConnection.disconnect();
        }

    }*/

    public static JSONObject readJsonFromStream(InputStream is) throws IOException, JSONException {
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        String jsonText = readAll(rd);
        System.out.println(jsonText);
        return new JSONObject(jsonText);
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {

        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        InputStream is = new BufferedInputStream(urlConnection.getInputStream());

        try  {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //System.out.println(jsonText);
            JSONObject jsonObject = new JSONObject(jsonText);
            return jsonObject;
        } finally {
            is.close();
            int status = urlConnection.getResponseCode();
            System.out.println("Status:" + status);
            urlConnection.disconnect();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        if (sb.toString().startsWith("[")){
            return sb.toString().substring(1,sb.toString().length());
        }
        return sb.toString();
    }

    public static JSONArray readJsonArrayFromURL(String url) throws IOException,JSONException{
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        InputStream is = new BufferedInputStream(urlConnection.getInputStream());
        //InputStream is = new URL(url).openStream();
        try  {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAllforArray(rd);
            //System.out.println(jsonText);
            return new JSONArray(jsonText);
        } finally {
            is.close();
        }
    }

    private static String readAllforArray(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject PostRequest(String url) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        urlConnection.setRequestMethod("POST");
        InputStream is = new BufferedInputStream(urlConnection.getInputStream());

        try  {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            JSONObject jsonObject = new JSONObject(jsonText);
            return jsonObject;
        } finally {
            is.close();
            int status = urlConnection.getResponseCode();
            System.out.println("Status:" + status);
            urlConnection.disconnect();
        }
    }

    public static JSONObject PostdeleteDevice(String url) throws IOException,JSONException{
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        urlConnection.setRequestMethod("POST");
        //urlConnection.setRequestProperty("Content-Type", "application/json");


        int status = urlConnection.getResponseCode();
        System.out.println("Status Deletedevice:" + status);

        InputStream is = urlConnection.getInputStream(); //new BufferedInputStream(urlConnection.getInputStream());
        System.out.println(is);
        try  {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            JSONObject jsonObject = new JSONObject(jsonText);
            return jsonObject;
        } finally {
            is.close();
            //int status = urlConnection.getResponseCode();
            //System.out.println("Status:" + status);
            urlConnection.disconnect();
        }


    }

    public static String PostRegisterWaitPersonDevice(String url,String Handle,String Tags) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        urlConnection.setRequestMethod("POST");
        //urlConnection.setRequestProperty("Content-Type", "application/json");


        //Agregamos Parametros
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        String platform = "gcm";
        params.add(new BasicNameValuePair("Platform", platform));
        params.add(new BasicNameValuePair("Handle", Handle));
        //params.add(new BasicNameValuePair("Tags",Tags));

        OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(params));
        writer.flush();
        writer.close();
        os.close();

        int status = urlConnection.getResponseCode();
        System.out.println("Status Handle:" + status);

        InputStream is = urlConnection.getInputStream(); //new BufferedInputStream(urlConnection.getInputStream());
        System.out.println(is);
        try  {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            System.out.println(jsonText);
            //JSONObject jsonObject = new JSONObject(jsonText);
                return jsonText;
        } finally {
            is.close();
            //int status = urlConnection.getResponseCode();
            //System.out.println("Status:" + status);
            urlConnection.disconnect();
        }
    }

    public static JSONObject PostImage(String url,String id,String base64) throws IOException, JSONException {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
        urlConnection.addRequestProperty("ApiKey", "exampleKey");
        urlConnection.setRequestMethod("POST");

        //Agregamos Parametros
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("WaitpersonId", id));
        params.add(new BasicNameValuePair("Base64EncodedImageContent", base64));

        OutputStream os = urlConnection.getOutputStream();
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(os, "UTF-8"));
        writer.write(getQuery(params));
        writer.flush();
        writer.close();
        os.close();

        //urlConnection.disconnect();

        InputStream is = new BufferedInputStream(urlConnection.getInputStream());

        try  {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            //System.out.println(jsonText);
            JSONObject jsonObject = new JSONObject(jsonText);
            return jsonObject;
        } finally {
            is.close();
            int status = urlConnection.getResponseCode();
            System.out.println("Status:" + status);
            urlConnection.disconnect();
        }
    }

    public static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

