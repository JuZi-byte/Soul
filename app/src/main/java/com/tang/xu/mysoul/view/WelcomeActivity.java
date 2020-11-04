package com.tang.xu.mysoul.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.TextView;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.utils.SpUtils;
import com.tang.xu.mysoul.R;

public class WelcomeActivity extends BaseUiActivity {

    private static final int SKIP = 1000;
    private Intent intent = new Intent();

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000:
                    startMain();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        handler.sendEmptyMessageDelayed(SKIP,3000);
    }

    private void startMain() {
        boolean soul = SpUtils.getInstance().getBoolean(Contants.first_play, true);
        String token = SpUtils.getInstance().getString(Contants.token, "");
        if (soul){
            intent.setClass(this,AnimotionActivity.class);
            SpUtils.getInstance().putBoolean(Contants.first_play,false);
        }else if (TextUtils.isEmpty(token)){
            if (FormWork.getInstance().islogin()){
                intent.setClass(this,MainActivity.class);
            }else{
                intent.setClass(this,LoginActivity.class);
            }
        }else{
            intent.setClass(this,MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}