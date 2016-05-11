package com.welling.kinghacker.tools;

import android.content.Context;
import android.nfc.Tag;
import android.util.Log;

import com.loopj.android.http.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.HttpEntityWrapper;
import cz.msebera.android.httpclient.util.EntityUtils;


/**
 * Created by KingHacker on 3/31/2016.
**/
public class MTHttpManager {
    private String url = PublicRes.IP;
    static public int ECG=0,BO=1,BP=2,BS=3;
    private AsyncHttpClient client;
    private HttpResponseListener httpResponseListener;
    private int requestID;
    private final int TIMEOUT = 3000;

    public MTHttpManager(String url) {
        this.url = url;
        init();
    }
    public MTHttpManager(){
        init();
    }
    private void init(){
        client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);
        client.setEnableRedirects(true);

        requestID = 0;

    }


//上传文件
    public boolean post(String fileKey,File file,int requestId,String url){
        if (fileKey == null || fileKey.equals("")) {
            fileKey = "file";
        }
        if (file == null) return false;
        RequestParams requestParams = new RequestParams();
        try {
            requestParams.put(fileKey,file);
            return post(requestParams,requestId,url);
        } catch (FileNotFoundException e) {
            return false;
        }

    }
//    post参数的
    public boolean post(HashMap<String,String> params,int requestId,String url){
        if (params == null) return false;
        RequestParams requestParams = new RequestParams(params);
        return post(requestParams, requestId,url);
    }
    public boolean post( RequestParams requestParams,int requestId,String url){
        if (requestParams == null) return post(requestId,url);

        final int id = requestId;
        Log.i("http",this.url+url);
        client.post(this.url+url, requestParams, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                Log.i("http","sss"+ responseBody.toString());

                for (Header header:headers){
                    Log.i("http",header.getValue());
                }

                httpResponseListener.onSuccess(id,responseBody);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseBody,Throwable error) {
                Log.i("http",error.toString());
                for (Header header:headers){
                    Log.i("http",header.getValue());
                }
                httpResponseListener.onFailure(id, statusCode);
            }
            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable error, JSONObject responseBody) {
                if (headers!=null) {
                    for (Header header : headers) {
                        Log.i("http", header.getValue());
                    }

                }
                httpResponseListener.onFailure(id, statusCode);
            }
        });
        return true;
    }
    public boolean post(int requestId,String url){
        final int id = requestId;
        client.post(this.url+url,new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject responseBody) {
                Log.i("http","sss"+ responseBody.toString());
                httpResponseListener.onSuccess(id, responseBody);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers,Throwable error, JSONObject responseBody) {
                for (Header header:headers){
                    Log.i("http",header.getValue());
                }
                Log.i("http",responseBody.toString());
                httpResponseListener.onFailure(id,statusCode);
            }

        });
        return true;
    }
    public void updateToCloud(Context context,String data,int type,int requestID){
        Log.i("update","fileType "+type);
        String account = SystemTool.getSystem(context).getStringValue(PublicRes.ACCOUNT);
        RequestParams params = new RequestParams();
        params.put("username",account);
        params.put("data",data);
        params.put("type",type);
        post(params,requestID,"uploadHdRecord.do");
    }

    public int getRequestID(){
        return requestID++;
    }


    //设置回调监听接口
    public void setHttpResponseListener(HttpResponseListener httpResponseListener) {
        this.httpResponseListener = httpResponseListener;
    }

    public interface HttpResponseListener{
        void onSuccess(int requestId,JSONObject JSONResponse);
        void onFailure(int requestId,int errorCode);
    }
}
