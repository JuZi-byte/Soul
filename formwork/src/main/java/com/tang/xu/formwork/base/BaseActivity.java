package com.tang.xu.formwork.base;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tang.xu.formwork.event.EventMessage;
import com.tang.xu.formwork.event.EventUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

public class BaseActivity extends AppCompatActivity {

    private String[] permission={
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.VIBRATE
    };

    private ArrayList<String> mPermission = new ArrayList<>();
    private ArrayList<String> mPermissionAll = new ArrayList<>();
    private OnAllpermission allpermission;
    private int recode;

    //调用入口
    protected void request(int requestCode,OnAllpermission allpermission){
        if (!checkAllPremission()){
            requestAllPermisstion(requestCode,allpermission);
        }
    }
    /**
     * 请求单个权限
     * @param permisstion
     * @return
     */
    protected boolean checkPremission(String permisstion){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            int check  = checkSelfPermission(permisstion);
            return check == PackageManager.PERMISSION_GRANTED;
        }
       return false;
    }

    /**
     * 请求多个权限
     */
    protected void requestPermisstion(String [] permisstion,int resultCode){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermissions(permisstion,resultCode);
        }

    }

    /**
     * 请求未授权所有权限
     * @param
     * @return
     */
    protected boolean checkAllPremission(){
        mPermission.clear();
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            for (int i = 0; i < permission.length; i++) {
                boolean b = checkPremission(permission[i]);
                if (!b){
                    mPermission.add(permission[i]);
                }
            }
        }
        return mPermission.size()>0 ? false : true;
    }

    /**
     * 请求所有权限
     */
    protected void requestAllPermisstion(int resultCode,OnAllpermission allpermission){
        this.allpermission = allpermission;
        this.recode=resultCode;
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            requestPermisstion(mPermission.toArray(new String[mPermission.size()]),resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionAll.clear();
        if (requestCode==requestCode){
            if (grantResults.length>0){
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i]==PackageManager.PERMISSION_DENIED){
                        //有失败的授权
                        mPermissionAll.add(permission[i]);
                    }
                }
                if (allpermission!=null){
                    if (mPermissionAll.size()==0){
                        allpermission.onSuccess();
                    }else{
                        allpermission.onFail(mPermissionAll);
                    }
                }
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected interface OnAllpermission{
        void onSuccess();
        void onFail(ArrayList<String> mPermissionAll);
    }

    /**
     * 判断是否授予窗口权限
     */
    protected boolean checkWindowPerssion(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            return Settings.canDrawOverlays(this);
        }
        return true;
    }

    /**
     * 请求窗口权限
     */
    protected void requestWindowPerssion(int recode){
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
        startActivityForResult(intent,recode);
    }

    /**
     * EventBus
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventUtils.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventUtils.getInstance().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        switch (event.getType()){

        }
    }
}
