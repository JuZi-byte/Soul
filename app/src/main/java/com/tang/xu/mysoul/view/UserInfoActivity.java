package com.tang.xu.mysoul.view;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.cloud.CloudMessage;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.utils.DilogMessage;
import com.tang.xu.formwork.utils.WindowHelper;
import com.tang.xu.formwork.view.DilogViewRoom;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;
import com.tang.xu.formwork.entity.FriendEntity;
import com.tang.xu.mysoul.entity.UserInfoEntity;
import com.tang.xu.mysoul.service.CloudService;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserInfoActivity extends BaseUiActivity implements View.OnClickListener{

    private RelativeLayout llBack;
    private CircleImageView ivUserPhoto;
    private TextView tvNickname;
    private TextView tvDesc;
    private RecyclerView mUserInfoView;
    private Button btnAddFriend;
    private LinearLayout llIsFriend;
    private Button btnChat;
    private Button btnAudioChat;
    private Button btnVideoChat;
    private EditText etMsg;
    private TextView tvCancel;
    private TextView tvAddFriend;
    private String userid="";
    private DilogViewRoom viewRoom;
    private CommonViewAdapter<UserInfoEntity> commonViewAdapter;
    private ArrayList<UserInfoEntity> arrayList = new ArrayList<>();
    private UserBean userBean;

    public static void startActivity(Context context,String userId){
        Intent intent = new Intent(context,UserInfoActivity.class);
        intent.putExtra(Contants.user_id,userId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        initView();
        initAddDialog();
        queryUserId();
    }

    private void initAddDialog() {
        viewRoom = DilogMessage.getInstance().createViewRoom(this,
                R.layout.dialog_send_friend);
        etMsg = viewRoom.findViewById(R.id.et_msg);
        tvCancel = viewRoom.findViewById(R.id.tv_cancel);
        tvAddFriend = viewRoom.findViewById(R.id.tv_add_friend);
        btnChat = findViewById(R.id.btn_chat);
        btnAudioChat = findViewById(R.id.btn_audio_chat);
        btnVideoChat = findViewById(R.id.btn_video_chat);
        tvCancel.setOnClickListener(this);
        tvAddFriend.setOnClickListener(this);
        btnChat.setOnClickListener(this);
        btnAudioChat.setOnClickListener(this);
        btnVideoChat.setOnClickListener(this);
    }

    private void queryUserId() {
        if (TextUtils.isEmpty(userid)){
            return;
        }
        FormWork.getInstance().queryUserId(userid, new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e==null){
                    if (list!=null||list.size()>0){
                        userBean = list.get(0);
                        Log.e("userInfo",userBean.toString());
                        Glide.with(UserInfoActivity.this).load(userBean.getPhoto()).into(ivUserPhoto);
                        tvNickname.setText(userBean.getNickName());
                        tvDesc.setText(userBean.getDesc());
                        initData(Color.BLACK,"性别",userBean.isSex()?"男":"女");
                        initData(Color.BLUE,"年龄",userBean.getAge()+"岁");
                        initData(Color.RED,"生日",userBean.getBirthday());
                        initData(Color.RED,"星座",userBean.getConstellation());
                        initData(Color.BLUE,"爱好",userBean.getHappy());
                        initData(Color.BLACK,"单身状态",userBean.getStatus());
                        commonViewAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        //判断是否为好友
        FormWork.getInstance().queryFriend(new FindListener<FriendEntity>() {
            @Override
            public void done(List<FriendEntity> list, BmobException e) {
                if (e==null){
                    if (list!=null&&list.size()>0){
                        for (int i = 0; i < list.size(); i++) {
                            FriendEntity friendEntity = list.get(i);
                            if (friendEntity.getFriend().getObjectId().equals(userid)){
                                //是好友
                                btnAddFriend.setVisibility(View.GONE);
                                llIsFriend.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });
    }

    private void initData(int color,String title,String content) {
        UserInfoEntity userInfoEntity = new UserInfoEntity(color,title,content);
        arrayList.add(userInfoEntity);
    }

    private void initView() {
        userid = getIntent().getStringExtra(Contants.user_id);
        Log.e("userInfouser_id",userid);
        llBack = findViewById(R.id.ll_back);
        ivUserPhoto = findViewById(R.id.iv_user_photo);
        tvNickname = findViewById(R.id.tv_nickname);
        tvDesc = findViewById(R.id.tv_desc);
        mUserInfoView = findViewById(R.id.mUserInfoView);
        btnAddFriend = findViewById(R.id.btn_add_friend);
        llIsFriend = findViewById(R.id.ll_is_friend);
        btnChat = findViewById(R.id.btn_chat);
        btnAudioChat = findViewById(R.id.btn_audio_chat);
        btnVideoChat = findViewById(R.id.btn_video_chat);
        btnAddFriend.setOnClickListener(this);
        commonViewAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnBindListener<UserInfoEntity>() {
            @Override
            public void onBindViewHolder(UserInfoEntity molde, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_type,molde.getTitle());
                viewHolder.setText(R.id.tv_content,molde.getContent());
                viewHolder.setColor(R.id.ll_bg,molde.getColor());
            }

            @Override
            public int gettype(int position) {
                return R.layout.layout_user_info_item;
            }
        });
        mUserInfoView.setLayoutManager(new GridLayoutManager(this,3));
        mUserInfoView.setAdapter(commonViewAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_cancel:
                DilogMessage.getInstance().hide(viewRoom);
                break;
            case R.id.tv_add_friend:
                DilogMessage.getInstance().hide(viewRoom);
                String trim = etMsg.getText().toString().trim();
                if (TextUtils.isEmpty(trim)){
                    trim = "你好，我是"+FormWork.getInstance().getUser().getNickName();
                }
                CloudMessage.getInstance().sendTextMessage(trim,CloudMessage.TYPE_ADD_FRIEND,userid);
                Toast.makeText(this, "发送消息成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_add_friend:
                DilogMessage.getInstance().show(viewRoom);
                break;
            case R.id.btn_chat:
                ChatActivity.ChatActivity(UserInfoActivity.this,userBean.getObjectId(),userBean.getNickName(),userBean.getPhoto());
                break;
            case R.id.btn_audio_chat:                //检查设备是否可以使用
                if (!CloudMessage.getInstance().isVoIPEnable(this)){
                    return;
                }else {
                    CloudMessage.getInstance().startAudioCall(userid);
                }
                break;
            case R.id.btn_video_chat:
                if (!CloudMessage.getInstance().isVoIPEnable(this)){
                    return;
                }else {
                    CloudMessage.getInstance().startVideoCall(userid);
                }
                break;
        }
    }
}