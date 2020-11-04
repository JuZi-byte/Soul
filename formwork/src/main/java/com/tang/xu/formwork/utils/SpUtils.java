package com.tang.xu.formwork.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.Key;

public class SpUtils {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SpUtils spUtils;

    private SpUtils() {
    }

    public static SpUtils getInstance(){
        if (spUtils==null){
            synchronized (SpUtils.class){
                if (spUtils==null){
                    spUtils = new SpUtils();
                }
            }
        }
        return spUtils;
    }

    public void init(Context context){
        sharedPreferences = context.getSharedPreferences("soul",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void putInt(String key,int values){
        editor.putInt(key, values);
        editor.commit();
    }

    public int getInt(String key,int values){
        return sharedPreferences.getInt(key,values);
    }

    public void putString(String key,String values){
        editor.putString(key,values);
        editor.commit();
    }

    public String getString(String key,String values){
        return sharedPreferences.getString(key,values);
    }

    public void putBoolean(String key,boolean values){
        editor.putBoolean(key,values);
        editor.commit();
    }

    public boolean getBoolean(String key,boolean values){
        return sharedPreferences.getBoolean(key, values);
    }

    public void remodveSp(String key){
        editor.remove(key);
        editor.commit();
    }

}
