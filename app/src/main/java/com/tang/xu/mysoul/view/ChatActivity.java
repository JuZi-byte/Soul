package com.tang.xu.mysoul.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseActivity;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.TestBean;
import com.tang.xu.formwork.cloud.CloudMessage;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.event.EventMessage;
import com.tang.xu.formwork.event.EventUtils;
import com.tang.xu.formwork.utils.FileMessage;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.formwork.utils.VoiceMessage;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;
import com.tang.xu.mysoul.entity.ChatEntity;
import com.tang.xu.mysoul.entity.MapMeaasge;
import com.tang.xu.mysoul.entity.VoiceEntity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import cn.bmob.v3.http.I;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;
import io.rong.message.TextMessage;

public class ChatActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llChatBg;
    private RecyclerView mChatView;
    private EditText etInputMsg;
    private Button btnSendMsg;
    private LinearLayout llVoice;
    private LinearLayout llCamera;
    private LinearLayout llPic;
    private LinearLayout llLocation;
    private String yourUserid;
    private String yourPhoto;
    private String yourName;
    private String myPhoto;
    private File fileUpload;

    public static final int LEFT_TEXT=0;
    public static final int LEFT_IMG=1;
    public static final int LEFT_LOCATION=2;

    public static final int RIGHT_TEXT=3;
    public static final int RIGHT_IMG=4;
    public static final int RIGHT_LOCATION=5;

    public static final int LOCATION=2000;
    public static final int INFO=2001;

    private CommonViewAdapter<ChatEntity> chatEntityCommonViewAdapter;
    private ArrayList<ChatEntity> arrayList = new ArrayList<>();

    public static void ChatActivity(Context context,String userid,String name,String userphoto){
        Intent intent = new Intent(context,ChatActivity.class);
        intent.putExtra(Contants.user_id,userid);
        intent.putExtra(Contants.user_photo,userphoto);
        intent.putExtra(Contants.user_name,name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();
    }

    private void initView() {
        //获取对方信息
        Intent intent = getIntent();
        yourUserid = intent.getStringExtra(Contants.user_id);
        yourPhoto = intent.getStringExtra(Contants.user_photo);
        yourName = intent.getStringExtra(Contants.user_name);
        getSupportActionBar().setTitle(yourName);
        //初始化控件
        llChatBg = findViewById(R.id.ll_chat_bg);
        mChatView = findViewById(R.id.mChatView);
        etInputMsg = findViewById(R.id.et_input_msg);
        btnSendMsg = findViewById(R.id.btn_send_msg);
        btnSendMsg.setOnClickListener(this);
        llVoice = findViewById(R.id.ll_voice);
        llCamera = findViewById(R.id.ll_camera);
        llPic = findViewById(R.id.ll_pic);
        llLocation = findViewById(R.id.ll_location);
        llVoice.setOnClickListener(this);
        llCamera.setOnClickListener(this);
        llPic.setOnClickListener(this);
        llLocation.setOnClickListener(this);
        //加载我的信息
        loadMyInfo();

        mChatView.setLayoutManager(new LinearLayoutManager(this));
        chatEntityCommonViewAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnMoreBindListenerr<ChatEntity>() {
            @Override
            public int getItemType(int position) {
                return arrayList.get(position).getType();
            }

            @Override
            public void onBindViewHolder(ChatEntity molde, CommonViewHolder viewHolder, int type, int position) {
                switch (molde.getType()){
                    case LEFT_TEXT:
                        viewHolder.setText(R.id.tv_left_text,molde.getText());
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_left_photo,yourPhoto);
                        break;
                    case LEFT_IMG:
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_left_img,molde.getImage());
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_left_photo,yourPhoto);
                    case LEFT_LOCATION:
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_left_photo,yourPhoto);
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_left_location_img,molde.getIaddress());
                        viewHolder.setText(R.id.tv_left_address,molde.getIaddress());
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                LocationActivity.startActivity(ChatActivity.this,false,molde.getIjin(),molde.getIwei(),molde.getIaddress(),LOCATION);
                            }
                        });
                        break;
                    case RIGHT_TEXT:
                        viewHolder.setText(R.id.tv_right_text,molde.getText());
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_right_photo,myPhoto);
                        break;
                    case RIGHT_IMG:
                        if (!TextUtils.isEmpty(molde.getImage())){
                            viewHolder.setImgUrl(ChatActivity.this,R.id.iv_right_img,molde.getImage());
                        }else{
                            viewHolder.setImgFile(ChatActivity.this,R.id.iv_right_img,molde.getFileload());
                        }
                        viewHolder.setImgUrl(ChatActivity.this,R.id.iv_right_photo,myPhoto);
                        break;
                    case RIGHT_LOCATION:
                        if (!TextUtils.isEmpty(molde.getMapUrl())&&!TextUtils.isEmpty(molde.getIaddress())){
                            viewHolder.setImgUrl(ChatActivity.this,R.id.iv_right_photo,myPhoto);
                            viewHolder.setImgUrl(ChatActivity.this,R.id.iv_right_location_img,molde.getMapUrl());
                            viewHolder.setText(R.id.tv_right_address,molde.getIaddress());
                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LocationActivity.startActivity(ChatActivity.this,false,molde.getIjin(),molde.getIwei(),molde.getIaddress(),LOCATION);
                                }
                            });
                        }
                        break;

                }
            }

            @Override
            public int gettype(int type) {
                if (type==LEFT_TEXT){
                    return R.layout.layout_chat_left_text;
                }else if (type==LEFT_IMG){
                    return R.layout.layout_chat_left_img;
                }else if (type==LEFT_LOCATION){
                    return R.layout.layout_chat_left_location;
                }else if (type==RIGHT_TEXT){
                    return R.layout.layout_chat_right_text;
                }else if (type==RIGHT_IMG){
                    return R.layout.layout_chat_right_img;
                }else if (type==RIGHT_LOCATION) {
                    return R.layout.layout_chat_right_location;
                }
                return 0;
            }
        });
        mChatView.setAdapter(chatEntityCommonViewAdapter);
        queryMessage();
    }

    private void loadMyInfo() {
        myPhoto = FormWork.getInstance().getUser().getPhoto();
    }

    private void queryMessage() {
        CloudMessage.getInstance().getHistoryMessage(yourUserid, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages!=null&&messages.size()>0){
                    parsing(messages);
                }else{
                    queryRemote();
                }
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }


    private void parsing(List<Message> messages){

        //倒序
        Collections.reverse(messages);

            for (int i = 0; i < messages.size(); i++) {
                Message message = messages.get(i);
                String objectName = message.getObjectName();
                if (objectName.equals(CloudMessage.MSG_TEXT_NAME)){
                    TextMessage textMessage = (TextMessage) message.getContent();
                    String content = textMessage.getContent();
                    TestBean testBean = new Gson().fromJson(content, TestBean.class);
                    if (testBean.getType().equals(CloudMessage.TYPE_TEXT)){
                        if (message.getSenderUserId().equals(yourUserid)){
                            addLeftText(testBean.getMsg());
                        }else {
                            addRightText(testBean.getMsg());
                        }
                    }
                }else if (objectName.equals(CloudMessage.MSG_IMAGE_NAME)){
                    ImageMessage imageMessage = (ImageMessage) message.getContent();
                    String s = imageMessage.getRemoteUri().toString();
                    if (s!=null){
                        if (message.getSenderUserId().equals(yourUserid)){
                            addLeftImage(s);
                        }else {
                            addRightImage(s);
                        }
                    }
                }else if (objectName.equals(CloudMessage.MSG_LOCATION_NAME)){
                    LocationMessage locationMessage = (LocationMessage) message.getContent();
                    if (message.getSenderUserId().equals(yourUserid)){
                        addLeftAdress(locationMessage.getLat(),locationMessage.getLng(),locationMessage.getPoi());
                    }else {
                        addRightAdress(locationMessage.getLat(),locationMessage.getLng(),locationMessage.getPoi());
                    }
                }
            }
    }

    private void queryRemote() {
        CloudMessage.getInstance().getRemoteMessage(yourUserid, new RongIMClient.ResultCallback<List<Message>>() {
            @Override
            public void onSuccess(List<Message> messages) {
                if (messages!=null&&messages.size()>0){
                    parsing(messages);
                }
            }
            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    public void addLeftAdress(double jin,double wei,String adress){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setIjin(jin);
        chatEntity.setIwei(wei);
        chatEntity.setType(LEFT_LOCATION);
        chatEntity.setIaddress(adress);
        chatEntity.setMapUrl(MapMeaasge.getInstance().getMapUrl(jin, wei));
        baseAddItem(chatEntity);
    }

    public void addRightAdress(double jin,double wei,String adress){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setIjin(jin);
        chatEntity.setType(RIGHT_LOCATION);
        chatEntity.setIwei(wei);
        chatEntity.setIaddress(adress);
        LoginUtils.LogE("Item+++++++++++++"+MapMeaasge.getInstance().getMapUrl(jin, wei));
        chatEntity.setMapUrl(MapMeaasge.getInstance().getMapUrl(jin, wei));
        baseAddItem(chatEntity);
    }

    public void addLeftText(String msg){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setText(msg);
        chatEntity.setType(LEFT_TEXT);
        arrayList.add(chatEntity);
        chatEntityCommonViewAdapter.notifyDataSetChanged();
        mChatView.scrollToPosition(arrayList.size()-1);
    }

    public void addRightText(String msg){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setText(msg);
        chatEntity.setType(RIGHT_TEXT);
        arrayList.add(chatEntity);
        chatEntityCommonViewAdapter.notifyDataSetChanged();
        mChatView.scrollToPosition(arrayList.size()-1);
    }

    public void addLeftImage(String url){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setImage(url);
        chatEntity.setType(LEFT_IMG);
        arrayList.add(chatEntity);
        chatEntityCommonViewAdapter.notifyDataSetChanged();
        mChatView.scrollToPosition(arrayList.size()-1);
    }

    public void addRightImage(String url){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setImage(url);
        chatEntity.setType(RIGHT_IMG);
        arrayList.add(chatEntity);
        chatEntityCommonViewAdapter.notifyDataSetChanged();
        mChatView.scrollToPosition(arrayList.size()-1);
    }

    public void addRightImage(File file){
        ChatEntity chatEntity = new ChatEntity();
        chatEntity.setFileload(file);
        chatEntity.setType(RIGHT_IMG);
        arrayList.add(chatEntity);
        chatEntityCommonViewAdapter.notifyDataSetChanged();
        mChatView.scrollToPosition(arrayList.size()-1);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send_msg:
                String trim = etInputMsg.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)){
                    CloudMessage.getInstance().sendTextMessage(trim,CloudMessage.TYPE_TEXT,yourUserid);
                    addRightText(trim);
                    etInputMsg.setText("");
                }else{
                    return;
                }
                break;
            case R.id.ll_voice:
                VoiceMessage.getInstance(this).startSpeak(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean b) {
                        String resultString = recognizerResult.getResultString();
                        LoginUtils.LogE(resultString+"讯飞");
                        if (!TextUtils.isEmpty(resultString)){
                            VoiceEntity voiceEntity = new Gson().fromJson(resultString, VoiceEntity.class);
                            if (voiceEntity.isLs()){
                                StringBuffer stringBuffer = new StringBuffer();
                                for (int i = 0; i < voiceEntity.getWs().size(); i++) {
                                    VoiceEntity.WsBean wsBean = voiceEntity.getWs().get(i);
                                    String w = wsBean.getCw().get(0).getW();
                                    stringBuffer.append(w);
                                }
                                LoginUtils.LogE(stringBuffer.toString()+"讯飞");
                                etInputMsg.setText(stringBuffer.toString());
                            }
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {

                    }
                });
                break;
            case R.id.ll_camera:
                FileMessage.getInstance().toCamera(this);
                break;
            case R.id.ll_pic:
                FileMessage.getInstance().toPhoto(this);
                break;
            case R.id.ll_location:
                LocationActivity.startActivity(this,true,0,0,"",LOCATION);
                break;
        }
    }

    private void baseAddItem(ChatEntity model) {
        arrayList.add(model);
        chatEntityCommonViewAdapter.notifyDataSetChanged();
        //滑动到底部
        mChatView.scrollToPosition(arrayList.size() - 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {

        switch (event.getType()){
            case EventMessage.FRIEND_SEND_TEXT:
                if (event.getUserId().equals(yourUserid)){
                    addLeftText(event.getText());
                    etInputMsg.setText("");
                }
                break;

            case EventMessage.FRIEND_SEND_IMAGE:
                if (event.getUserId().equals(yourUserid)){
                    addLeftImage(event.getImageUri());
                }
                break;

            case EventMessage.FRIEND_SEND_LOCATION:
                if (event.getUserId().equals(yourUserid)){
                    addLeftAdress(event.getJin(),event.getWei(),event.getAdress() );
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== Activity.RESULT_OK){
            if(requestCode==1003){
                fileUpload = FileMessage.getInstance().getTempFile();
            }else if (requestCode==1004){
                Uri uri = data.getData();
                if (uri!=null){
                    //String noUri = uri.getPath();
                    //获得真实的地址
                    String path = FileMessage.getInstance().gerResultFromUri(this, uri);
                    if (!TextUtils.isEmpty(path)){
                        fileUpload = new File(path);
                    }
                }
            }else if (requestCode==LOCATION){
                double jin = data.getDoubleExtra("jin", 0);
                double wei = data.getDoubleExtra("wei", 0);
                String adress = data.getStringExtra("adress");
                if (TextUtils.isEmpty(adress)) {
                    MapMeaasge.getInstance().poi2address(jin, wei, new MapMeaasge.OnGeocodeSearchpoi2address() {
                        @Override
                        public void poi2address(String address) {
                            //发送位置消息
                            CloudMessage.getInstance().sendAdress(yourUserid,jin,wei,adress);
                            addRightAdress(jin,wei,adress);
                        }
                    });

                }else {
                    CloudMessage.getInstance().sendAdress(yourUserid,jin,wei,adress);
                    addRightAdress(jin,wei,adress);
                }
                LoginUtils.LogE(adress+""+jin+""+wei+"requestcode");
            }
            if (fileUpload!=null){
                CloudMessage.getInstance().sentImage(fileUpload, yourUserid, new RongIMClient.SendImageMessageCallback() {
                    @Override
                    public void onAttached(Message message) {

                    }

                    @Override
                    public void onError(Message message, RongIMClient.ErrorCode errorCode) {

                    }

                    @Override
                    public void onSuccess(Message message) {

                    }

                    @Override
                    public void onProgress(Message message, int i) {

                    }
                });
                addRightImage(fileUpload);
                fileUpload=null;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}