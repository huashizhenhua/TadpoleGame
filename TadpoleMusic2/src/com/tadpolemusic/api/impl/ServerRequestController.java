package com.tadpolemusic.api.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

interface ServerResultListener {
	public void onResult(String body);
}

class TransactionThread extends Thread
{ 
	private String mUrl;
	private Handler mControllerHandler = null;
	private ServerResultListener mServerResultListener = null;
	private boolean mIsGet = false;
	private String mPostBody = "";
	public TransactionThread(boolean isGet, String url, Handler controllerHandler, ServerResultListener serverResultListener, String postBody) {
		mIsGet = isGet;
		mUrl = url;
		mPostBody = postBody;
		mControllerHandler = controllerHandler;
		mServerResultListener = serverResultListener;
	}

	public void run() 
	{ 
		try {
		ServerResponse serverResponse;
		if (mIsGet)
			serverResponse = CommunicateImplGet(mUrl);
		else
			serverResponse = CommunicateImplPost(mUrl, mPostBody);
		if (isTransactionSuccess(serverResponse.mStatusCode)) {
			Message msg = mControllerHandler.obtainMessage(ServerRequestController.COMMUNICATESUCCESS);
			Bundle bundle = new Bundle();
			bundle.putString("body", serverResponse.mBody);
			msg.obj = mServerResultListener;
			msg.setData(bundle);
			mControllerHandler.sendMessage(msg);
		}
		else {
			Message msg = mControllerHandler.obtainMessage(ServerRequestController.COMMUNICATEFAILED);
			mControllerHandler.sendMessage(msg);
		}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isTransactionSuccess(int statusCode) {
		return (statusCode == 200);
	}
	
	public static ServerResponse CommunicateImplGet(String urlStr){
	 	   HttpGet httpGet = new HttpGet(urlStr);  
	        //生成一个Http客户端对象  
	        HttpClient httpClient = new DefaultHttpClient();  
	        //使用http客户端发送请求对象  
	        InputStream inputStream = null;  
	        StringBuilder result=new StringBuilder(); 
	        int statusCode = 400;
	        try {  
	            //httpResponse就是代表响应对象  
	            HttpResponse httpResponse=httpClient.execute(httpGet);  
	            //httpEntity包含的就是返回的消息内容
	            statusCode = httpResponse.getStatusLine().getStatusCode();
	            HttpEntity httpEntity = httpResponse.getEntity();  
	            
	            inputStream = httpEntity.getContent();  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));  
	             
	            String line = "";  
	            while((line=reader.readLine())!=null){  
	                result.append(line);  
	            }    
	        } catch (ClientProtocolException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        finally{  
	            try {  
	                inputStream.close();  
	            } catch (Exception e2) {  
	                e2.printStackTrace();  
	            }  
	        }  
	        
	        return new ServerResponse(statusCode, result.toString());
	    }  
	
	public static ServerResponse CommunicateImplPost(String urlStr, String body) throws UnsupportedEncodingException{  
	 	   HttpPost httpPost = new HttpPost("http://www.baidu.com");
	 	   httpPost.setEntity(new StringEntity(body));
	        //生成一个Http客户端对象  
	        HttpClient httpClient = new DefaultHttpClient();  
	        //使用http客户端发送请求对象  
	        InputStream inputStream = null;  
	        StringBuilder result=new StringBuilder(); 
	        int statusCode = 400;
	        try {  
	            //httpResponse就是代表响应对象  
	            HttpResponse httpResponse=httpClient.execute(httpPost);  
	            //httpEntity包含的就是返回的消息内容
	            statusCode = httpResponse.getStatusLine().getStatusCode();
	            HttpEntity httpEntity = httpResponse.getEntity();  
	            
	            inputStream = httpEntity.getContent();  
	            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));  
	             
	            String line = "";  
	            while((line=reader.readLine())!=null){  
	                result.append(line);  
	            }    
	        } catch (ClientProtocolException e) {  
	            e.printStackTrace();  
	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	        finally{  
	            try {  
	                inputStream.close();  
	            } catch (Exception e2) {  
	                e2.printStackTrace();  
	            }  
	        }  
	        
	        return new ServerResponse(400, result.toString());
	    }  
}

public class ServerRequestController {
	private Handler mHandler;
	private static ServerRequestController sServerRequestController = null;
	public static final int COMMUNICATESUCCESS = 0;
	public static final int COMMUNICATEFAILED = 1;
	public static ServerRequestController getInstance() {
		if (sServerRequestController == null)
		{
			sServerRequestController = new ServerRequestController();			
		}
		return sServerRequestController;
	}
	
	private ServerRequestController() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what)
				{
					case COMMUNICATESUCCESS:
						ServerResultListener listener = (ServerResultListener)msg.obj;
						String body = msg.getData().getString("body");
						listener.onResult(body);
						break;
					case COMMUNICATEFAILED:
						break;
				}
			
			}			
		};
	}
	
	public void dealTransaction(boolean isGet, String url, ServerResultListener listener, String postBody) {
		dealTransactionInNewThread(isGet, url, listener, postBody);
	}

	private void dealTransactionInNewThread(boolean isGet, String url, ServerResultListener listener, String postBody) {
		TransactionThread t = new TransactionThread(isGet, url, mHandler, listener, postBody);
		t.start();
	}
	
	
}

class ServerResponse {
	public ServerResponse(int statusCode, String body) {
		mStatusCode = statusCode;
		mBody = body;
	}
	public int mStatusCode;
	public String mBody;
}
