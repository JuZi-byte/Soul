package com.tang.xu.mysoul.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.SimulationData;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.http.HttpMessage;
import com.tang.xu.formwork.utils.DilogMessage;
import com.tang.xu.formwork.utils.SpUtils;
import com.tang.xu.formwork.view.DilogViewRoom;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.entity.CommonEntity;
import com.tang.xu.mysoul.entity.TokenEntity;
import com.tang.xu.mysoul.fragment.ChatFragment;
import com.tang.xu.mysoul.fragment.MeFragment;
import com.tang.xu.mysoul.fragment.SquareFragment;
import com.tang.xu.mysoul.fragment.StarFragment;
import com.tang.xu.mysoul.service.CloudService;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseUiActivity {

    private StarFragment starFragment;
    private SquareFragment squareFragment;
    private ChatFragment chatFragment;
    private MeFragment meFragment;
    private CommonTabLayout common;
    private String tokenPhoto;
    private String tokenNickName;
    private Disposable disposable;

    private ArrayList<CustomTabEntity> customTabEntities = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermiss();
        initView();
        initData();
        
        checkToken();

        //SimulationData.testData();

        show(starFragment);
        common.setTabData(customTabEntities);
        common.setCurrentTab(0);
        common.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position){
                    case 0:
                        show(starFragment);
                        break;
                    case 1:
                        show(squareFragment);
                        break;
                    case 2:
                        show(chatFragment);
                        break;
                    case 3:
                        show(meFragment);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    /**
     * 获取Token 检查是否存在
     */
    private void checkToken() {
        String token = SpUtils.getInstance().getString(Contants.token, "");
        if (!TextUtils.isEmpty(token)){
            //启动融云
            startYun();
        }else{
            tokenPhoto = FormWork.getInstance().getUser().getTokenPhoto();
            tokenNickName = FormWork.getInstance().getUser().getTokenNickName();
            if (!TextUtils.isEmpty(tokenPhoto)&&!TextUtils.isEmpty(tokenNickName)){
                createToken();
            }else{
                createDialog();
            }
        }
    }
    //启动融云
    private void startYun() {
        startService(new Intent(this, CloudService.class));
    }

    //连接融云创建token
    private void createToken() {
        HashMap<String,String> map = new HashMap<>();
        map.put("userId",FormWork.getInstance().getUser().getObjectId());
        map.put("name",FormWork.getInstance().getUser().getTokenNickName());
        map.put("portraitUri",FormWork.getInstance().getUser().getTokenPhoto());
        disposable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                HttpMessage httpMessage = new HttpMessage();
                String json = httpMessage.postToken(map);
                emitter.onNext(json);
                emitter.onComplete();
            }
            //线程调度
        }).subscribeOn(Schedulers.newThread())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<String>() {
                @Override
                public void accept(String s) throws Exception {
                    Log.e("token",s);
                    gsonToken(s);
                }
            });
    }

    private void gsonToken(String s) {
        Gson gson = new Gson();
        TokenEntity tokenEntity = gson.fromJson(s, TokenEntity.class);
        if (tokenEntity.getCode()==200){
            if (!TextUtils.isEmpty(tokenEntity.getToken())){
                SpUtils.getInstance().putString(Contants.token,tokenEntity.getToken());
                startYun();
            }
        }else{
            Toast.makeText(this, "失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void createDialog() {
        DilogViewRoom viewRoom = DilogMessage.getInstance().createViewRoom(this, R.layout.dialog_token);
        viewRoom.setCancelable(false);
        Button b = viewRoom.findViewById(R.id.go);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DilogMessage.getInstance().hide(viewRoom);
                Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                startActivityForResult(intent,1002);
            }
        });
        DilogMessage.getInstance().show(viewRoom);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1002&&resultCode== Activity.RESULT_OK){
            //上传头像成功
            checkToken();
        }
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment!=null&&fragment instanceof StarFragment){
            starFragment = (StarFragment) fragment;
        }
        if (fragment!=null&&fragment instanceof SquareFragment){
            squareFragment = (SquareFragment) fragment;
        }
        if (fragment!=null&&fragment instanceof ChatFragment){
            chatFragment = (ChatFragment) fragment;
        }
        if (fragment!=null&&fragment instanceof MeFragment){
            meFragment = (MeFragment) fragment;
        }
    }

    private void show(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .hide(starFragment)
                .hide(chatFragment)
                .hide(meFragment).
                hide(squareFragment)
                .show(fragment).commit();
    }

    private void initData() {
        customTabEntities.add(new CommonEntity("星球",R.drawable.img_star_p,R.drawable.img_star));
        customTabEntities.add(new CommonEntity("广场",R.drawable.img_square_p,R.drawable.img_square));
        customTabEntities.add(new CommonEntity("聊天",R.drawable.img_chat_p,R.drawable.img_chat));
        customTabEntities.add(new CommonEntity("我的",R.drawable.img_me_p,R.drawable.img_me));
    }

    private void initView() {

        common = findViewById(R.id.common);

        if (starFragment==null){
            starFragment = new StarFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fram,starFragment).commit();
        }
        if (squareFragment==null){
            squareFragment = new SquareFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fram,squareFragment).commit();
        }
        if (chatFragment==null){
            chatFragment = new ChatFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fram,chatFragment).commit();
        }
        if (meFragment==null){
            meFragment = new MeFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fram,meFragment).commit();
        }
    }

    private void requestPermiss() {
        if (!checkWindowPerssion()){
            requestWindowPerssion(1001);
        }

        request(1000, new OnAllpermission() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFail(ArrayList<String> mPermissionAll) {
                Toast.makeText(MainActivity.this, "失败"+mPermissionAll.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable.isDisposed()){
            disposable.dispose();
        }
    }
}