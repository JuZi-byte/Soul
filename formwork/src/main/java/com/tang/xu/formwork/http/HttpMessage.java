package com.tang.xu.formwork.http;

import com.tang.xu.formwork.Sha;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpMessage {

    private static final String TokenUrl="https://api-cn.ronghub.com/user/getToken.json";
    private static final String App_Key="0vnjpoad0i0gz";
    private static final String Secret="rWbrW3hF39";
    private OkHttpClient okHttpClient;
    public HttpMessage() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * 请求融云Token
     * @param map
     * @return
     */
    public String postToken(HashMap<String,String> map){

        String timestamp = String.valueOf(System.currentTimeMillis()/1000);
        String Nonce = String.valueOf(Math.floor(Math.random()*100000));
        String Signature = Sha.sha1(Secret+Nonce+timestamp);

        FormBody.Builder builder = new FormBody.Builder();
        for (String key:map.keySet()) {
            builder.add(key,map.get(key));
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(TokenUrl)
                .addHeader("Timestamp",timestamp)
                .addHeader("App-Key",App_Key)
                .addHeader("Nonce",Nonce)
                .addHeader("Signature",Signature)
                .addHeader("Content-Type","application/x-www-form-urlencoded")
                .post(requestBody)
                .build();

        try {
            return okHttpClient.newCall(request).execute().body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
