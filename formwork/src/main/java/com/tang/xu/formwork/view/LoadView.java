package com.tang.xu.formwork.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.tang.xu.formwork.R;
import com.tang.xu.formwork.utils.DilogMessage;

public class LoadView  {
    private DilogViewRoom dilogViewRoom;
    private ImageView imageView;
    private TextView textView;
    private ObjectAnimator objectAnimator;

    public LoadView(Context context) {
        dilogViewRoom = DilogMessage.getInstance().createViewRoom(context,R.layout.load_myview, Gravity.CENTER);
        imageView = dilogViewRoom.findViewById(R.id.load_image);
        textView = dilogViewRoom.findViewById(R.id.load_text);
        //旋转动画
        objectAnimator = ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        objectAnimator.setDuration(2*1000);
        //设置动画一直循环
        //objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setInterpolator(new LinearInterpolator());
        objectAnimator.start();
    }

    public void setTextViewMsg(String s){
        if (!TextUtils.isEmpty(s)){
            textView.setText(s);
        }
    }

    public void show(String s){
        setTextViewMsg(s);
        DilogMessage.getInstance().show(dilogViewRoom);
    }

    public void hide(){
        DilogMessage.getInstance().hide(dilogViewRoom);
    }
}
