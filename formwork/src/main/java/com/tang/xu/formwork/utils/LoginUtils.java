package com.tang.xu.formwork.utils;

import android.text.TextUtils;
import android.util.Log;

import com.tang.xu.formwork.BuildConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginUtils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH-mm-ss");

    public static void LogI(String msg){
        if (BuildConfig.DEBUG){
            if (!TextUtils.isEmpty(msg)){
                Log.i("BuildConfig.DEBUG",msg);
                writeToFile(msg);
            }
        }
    }

    public static void LogE(String msg){
        if (BuildConfig.DEBUG){
            if (!TextUtils.isEmpty(msg)){
                Log.e("BuildConfig.DEBUG",msg);
                writeToFile(msg);
            }
        }
    }

    private static void writeToFile(String msg){
        String fileName = "/sdcard/MySoul/mysoul.text";
        String logMsg = simpleDateFormat.format(System.currentTimeMillis())+ " " + msg + "\n";;
        File file = new File("/sdcard/MySoul/");
        if (!file.exists()){
            file.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = new FileOutputStream(fileName,true);
            bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(fileOutputStream, Charset.forName("gbk")));
            bufferedWriter.write(logMsg);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (bufferedWriter!=null){
                    bufferedWriter.close();
                }
                if (fileOutputStream!=null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
