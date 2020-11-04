package com.tang.xu.formwork.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class WindowHelper {

    private Context context;
    private WindowManager windowManager;
    private WindowManager.LayoutParams lp;
    private Handler mHandler = new Handler();

    public static volatile  WindowHelper windowHelper = null;

    private WindowHelper() {
    }

    public static WindowHelper getInstance(){
        if (windowHelper==null){
            synchronized (WindowHelper.class){
                if (windowHelper==null){
                    windowHelper=new WindowHelper();
                }
            }
        }
        return windowHelper;
    }

    public void initView(Context context){
        this.context = context;
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        lp = createParms(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, Gravity.CENTER);
    }

    public WindowManager.LayoutParams createParms(int width,int height,int gravity){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //设置宽高
        layoutParams.width = width;
        layoutParams.height = height;

        //设置标志位
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        //设置格式
        layoutParams.format = PixelFormat.TRANSLUCENT;

        //设置位置
        layoutParams.gravity = gravity;

        //设置类型
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.O){
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        return layoutParams;
    }

    public View getView(int id){
        return View.inflate(context,id,null);
    }

    public void show(View view){
        if (view!=null){
            if (view.getParent()==null){
                windowManager.addView(view,lp);
            }
        }
    }

    public void show(View view, WindowManager.LayoutParams layoutParams){
        if (view!=null){
            if (view.getParent()==null){
                windowManager.addView(view,layoutParams);
            }
        }
    }

    public void hide(View view){
        if (view!=null){
            if (view.getParent()!=null){
                windowManager.removeView(view);
            }
        }
    }

    /**
     * 更新View的布局
     *
     * @param view
     * @param layoutParams
     */
    public void updateView(final View view, final WindowManager.LayoutParams layoutParams) {
        if (view != null && layoutParams != null) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    windowManager.updateViewLayout(view, layoutParams);
                }
            });
        }
    }

}
