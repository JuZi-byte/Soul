package com.tang.xu.mysoul.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.utils.DilogMessage;
import com.tang.xu.formwork.utils.SpUtils;
import com.tang.xu.formwork.view.DilogView;
import com.tang.xu.formwork.view.DilogViewRoom;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.R;

import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;

public class LoginActivity extends BaseUiActivity implements View.OnClickListener{

    private EditText username;
    private EditText password;
    private Button yan;
    private Button login;
    private DilogViewRoom viewRoom;
    private DilogView dilogView;
    private LoadView loadView;
    private int time = 60;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    time--;
                    yan.setText(time+"");
                    if (time<=0){
                        yan.setText("发送");
                        yan.setEnabled(true);
                        time=60;
                    }else{
                        handler.sendEmptyMessageDelayed(0,1000);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        String string = SpUtils.getInstance().getString("username", "");
        if (!TextUtils.isEmpty(string)){
            username.setText(""+string);
        }
        initLoadView();
        initDialogViewRoom();
    }

    private void initLoadView() {
        loadView = new LoadView(this);
    }

    private void initDialogViewRoom() {
        //在中间位置创建滑动验证的自定义View
        viewRoom = DilogMessage.getInstance().createViewRoom(this, R.layout.activity_view,Gravity.CENTER);
        //自定义验证View
        dilogView = viewRoom.findViewById(R.id.dilog);
        //成功返回  发送验证码
        dilogView.setOnResult(new DilogView.OnResult() {
            @Override
            public void onResult() {
                String phone = username.getText().toString().trim();
                if (!TextUtils.isEmpty(phone)){
                    FormWork.getInstance().loginByAccount(phone, "123456", new FormWork.UpdateList() {
                        @Override
                        public void onSuccess() {
                                yan.setEnabled(false);
                                handler.sendEmptyMessage(0);
                                Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                                DilogMessage.getInstance().hide(viewRoom);
                        }

                        @Override
                        public void onError(BmobException e) {
                            Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

//                if (!TextUtils.isEmpty(phone)){
//                    FormWork.getInstance().request(phone,new QueryListener<Integer>() {
//                        @Override
//                        public void done(Integer smsId, BmobException e) {
//                            if (e == null) {
//                                yan.setEnabled(false);
//                                handler.sendEmptyMessage(0);
//                                Toast.makeText(LoginActivity.this, "发送验证码成功，短信ID：" + smsId + "\n", Toast.LENGTH_SHORT).show();
//                                DilogMessage.getInstance().hide(viewRoom);
//                            } else {
//                                Toast.makeText(LoginActivity.this, "发送验证码失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }else{
//                    Toast.makeText(LoginActivity.this, "手机号不能为空", Toast.LENGTH_SHORT).show();
//                    return;
//                }
            }
        });
    }

    private void initView() {
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        yan = findViewById(R.id.yan);
        login = findViewById(R.id.login);
        yan.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.yan:
                DilogMessage.getInstance().show(viewRoom);
                break;
            case R.id.login:
                login();
                break;
        }
    }

    private void login() {
        loadView.show("正在登陆。。。");
        String trim1 = username.getText().toString().trim();
        String trim2 = password.getText().toString().trim();
        if (!TextUtils.isEmpty(trim1)){
            if (!TextUtils.isEmpty(trim2)){
                FormWork.getInstance().loginByAccount(trim1, trim2, new FormWork.UpdateList() {
                    @Override
                    public void onSuccess() {
                        loadView.hide();
                        Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        SpUtils.getInstance().putString("username",trim1);
                        SpUtils.getInstance().putString("password",trim2);
                        finish();
                    }

                    @Override
                    public void onError(BmobException e) {
                        Toast.makeText(LoginActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
                    }
                });
//                FormWork.getInstance().login(trim1, trim2, new LogInListener<UserBean>() {
//                    @Override
//                    public void done(UserBean userBean, BmobException e) {
//                        if (e == null) {
//                            loadView.hide();
//                            Toast.makeText(LoginActivity.this,"短信注册或登录成功：" + userBean.getUsername(), Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                            SpUtils.getInstance().putString("username",trim1);
//                            SpUtils.getInstance().putString("password",trim2);
//                            finish();
//                        } else {
//                            Toast.makeText(LoginActivity.this,"短信注册或登录失败：" + e.getErrorCode() + "-" + e.getMessage() + "\n", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
            }else{
                Toast.makeText(this, " 密码不可以为空", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "手机号不可以为空", Toast.LENGTH_SHORT).show();
        }

    }
}