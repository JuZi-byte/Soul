package com.tang.xu.formwork.utils;

public class TimeUtils {
    public static String formData(long ms){
        long hour = (ms%(1000*60*60*24))/(1000*60*60);
        long minute = (ms%(1000*60*60))/(1000*60);
        long second = (ms%(1000*60))/(1000);
        String hours = hour+"";
        if (hour<10){
            hours="0"+hour;
        }
        String minutes = minute+"";
        if (minute<10){
            minutes="0"+minute;
        }
        String seconds = second+"";
        if (second<10){
            seconds="0"+second;
        }

        return hours+":"+minutes+":"+seconds;
    }
}
