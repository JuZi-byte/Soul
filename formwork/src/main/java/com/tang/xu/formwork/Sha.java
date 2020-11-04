package com.tang.xu.formwork;

import com.tang.xu.formwork.http.HttpMessage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.OkHttpClient;

public class Sha {

    public static String sha1(String data){
        StringBuffer stringBuffer = new StringBuffer();
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(data.getBytes());
            byte[] digest = sha1.digest();
            for (int i = 0; i < digest.length; i++) {
                int a = digest[i];
                if (a<0) a+=256;
                if (a<16) stringBuffer.append("0");
                stringBuffer.append(Integer.toHexString(a));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }
}
