package com.itap.voiceemoticon.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.itap.voiceemoticon.VEApplication;

/**
 * @author Lukasz Wisniewski
 */
public class HttpCaller {

    /**
     * Cache for most recent request
     */
    private static RequestCache requestCache = null;

    /**
     * Performs HTTP GET using Apache HTTP Client v 4
     * 
     * @param url
     * @return
     * @throws WSError
     */
    public static String doGet(String url) throws WSError {

        String data = null;
        if (requestCache != null) {
            data = requestCache.get(url);
            if (data != null) {
                Log.d(VEApplication.TAG, "Caller.doGet [cached] " + url);
                return data;
            }
        }

        URI encodedUri = null;
        HttpGet httpGet = null;

        try {
            encodedUri = new URI(url);
            httpGet = new HttpGet(encodedUri);
        } catch (URISyntaxException e1) {
            // at least try to remove spaces
            String encodedUrl = url.replace(' ', '+');
            httpGet = new HttpGet(encodedUrl);
            e1.printStackTrace();
        }

        // initialize HTTP GET request objects
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse;

        try {
            // execute request
            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (UnknownHostException e) {
                throw new WSError("Unable to access " + e.getLocalizedMessage());
            } catch (SocketException e) {
                throw new WSError(e.getLocalizedMessage());
            }

            // request data
            HttpEntity httpEntity = httpResponse.getEntity();

            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                data = convertStreamToString(inputStream);
                // cache the result
                if (requestCache != null) {
                    requestCache.put(url, data);
                }
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(VEApplication.TAG, "Caller.doGet " + url);
        return data;
    }

    public static String doPost(String urlStr, String body) throws UnsupportedEncodingException {
        HttpPost httpPost = new HttpPost(urlStr);
        httpPost.setEntity(new StringEntity(body));
        //生成一个Http客户端对象  
        HttpClient httpClient = new DefaultHttpClient();
        //使用http客户端发送请求对象  
        InputStream inputStream = null;
        StringBuilder result = new StringBuilder();
        int statusCode = 400;
        try {
            //httpResponse就是代表响应对象  
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //httpEntity包含的就是返回的消息内容
            statusCode = httpResponse.getStatusLine().getStatusCode();
            HttpEntity httpEntity = httpResponse.getEntity();

            inputStream = httpEntity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    public static void setRequestCache(RequestCache requestCache) {
        HttpCaller.requestCache = requestCache;
    }

    public static String createStringFromIds(int[] ids) {
        if (ids == null)
            return "";

        String query = "";

        for (int id : ids) {
            query = query + id + "+";
        }

        return query;
    }

}
