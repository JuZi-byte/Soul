package com.tang.xu.formwork.utils;

import android.content.Context;
import android.view.Gravity;

import com.tang.xu.formwork.R;
import com.tang.xu.formwork.view.DilogView;
import com.tang.xu.formwork.view.DilogViewRoom;

public class DilogMessage {

   public static volatile  DilogMessage dilogMessage = null;

    private DilogMessage() {
    }

    public static DilogMessage getInstance(){
       if (dilogMessage==null){
           synchronized (DilogMessage.class){
               if (dilogMessage==null){
                   dilogMessage=new DilogMessage();
               }
           }
       }
       return dilogMessage;
   }

   public DilogViewRoom createViewRoom(Context context, int layout){
       return new DilogViewRoom(context,layout, R.style.Dilog_Room, Gravity.CENTER);
   }
    public DilogViewRoom createViewRoom(Context context, int layout,int gravity){
        return new DilogViewRoom(context,layout, R.style.Dilog_Room,gravity);
    }

    public void show(DilogViewRoom view){
        if (!view.isShowing()){
            view.show();
        }
    }

    public void hide(DilogViewRoom view){
        if (view.isShowing()){
            view.hide();
        }
    }
}
