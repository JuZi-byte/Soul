package com.tang.xu.mysoul.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.AddFriendAdapter;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;
import com.tang.xu.mysoul.entity.AddFriendEntity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AddFriendActivity extends BaseUiActivity implements View.OnClickListener {

    private ImageView back;
    private EditText id;
    private ImageView search;
    private RecyclerView recycle;
    //private AddFriendAdapter addFriendAdapter;
    private ArrayList<AddFriendEntity> addFriendEntities = new ArrayList<>();
    private ImageView include;
    private TextView contact;

    private CommonViewAdapter commonViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initView();

        recycle.setLayoutManager(new LinearLayoutManager(this));
        recycle.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        //addFriendAdapter = new AddFriendAdapter(this,addFriendEntities);
        //recycle.setAdapter(addFriendAdapter);
        LoginUtils.LogE(addFriendEntities.toString());
        commonViewAdapter = new CommonViewAdapter(addFriendEntities, new CommonViewAdapter.OnMoreBindListenerr<AddFriendEntity>() {
            @Override
            public int getItemType(int position) {
                return addFriendEntities.get(position).getType();
            }

            @Override
            public void onBindViewHolder(AddFriendEntity molde, CommonViewHolder viewHolder, int type, int position) {
                if (type==Contants.type_title){
                    viewHolder.setText(R.id.item_title_text,molde.getTitle());
                }else if (type==Contants.type_msg){
                    viewHolder.setText(R.id.item_msg_age,molde.getAge()+"");
                    viewHolder.setText(R.id.item_msg_desc,molde.getMsg());
                    viewHolder.setText(R.id.item_msg_name,molde.getNickname());
                    viewHolder.setImgUrl(AddFriendActivity.this,R.id.item_msg_image,molde.getPhoto());
                    viewHolder.setresouce(R.id.item_msg_sex,molde.isSex()?R.drawable.img_boy_icon:R.drawable.img_girl_icon);
                    if (molde.isIscontact()){
                        viewHolder.setText(R.id.item_contact_name,molde.getNickname());
                        viewHolder.setShow(R.id.item_contact_name,View.VISIBLE);
                    }
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            UserInfoActivity.startActivity(AddFriendActivity.this,
                                    molde.getUserId());
                        }
                    });
                }
            }
            @Override
            public int gettype(int position) {
                if (position== Contants.type_title){
                    return R.layout.item_title;
                }else if(position== Contants.type_msg){
                    return R.layout.item_msg;
                }
                return 0;
            }
        });

        recycle.setAdapter(commonViewAdapter);
        //addFriendAdapter.setOnclick(new AddFriendAdapter.Onclick() {
        //    @Override
        //    public void onClick(int position) {
        //        Toast.makeText(AddFriendActivity.this, ""+position , Toast.LENGTH_SHORT).show();
        //   }
        //});
    }

    private void initView() {
        back = findViewById(R.id.back);
        id = findViewById(R.id.id);
        search = findViewById(R.id.search);
        recycle = findViewById(R.id.recycle);
        include = findViewById(R.id.include);
        contact = findViewById(R.id.contact);
        back.setOnClickListener(this);
        contact .setOnClickListener(this);
        search.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.contact:
                if (checkPremission(Manifest.permission.READ_CONTACTS)){
                    startActivity(new Intent(this,ContactActivity.class));
                }else {
                    requestPermisstion(new String[]{Manifest.permission.READ_CONTACTS},1000);
                }
                break;
            case R.id.search:
                String userid = id.getText().toString().trim();
                if (!TextUtils.isEmpty(userid)){
                    queryUser(userid);
                    queryUserAll();
                }else{
                    Toast.makeText(this, "查询值不能为NUll", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }
    }


    private void queryUser(String userid) {
        FormWork.getInstance().queryUser(userid, new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if(list.size()>0&&list!=null){
                    //查询到的数据集合
                    UserBean userBean = list.get(0);
                    Log.e("txj",userBean.toString());
                    include.setVisibility(View.GONE);
                    recycle.setVisibility(View.VISIBLE);
                    addFriendEntities.clear();
                    addtitle("查询结果");
                    addMsg(userBean);
                    //addFriendAdapter.notifyDataSetChanged();
                    commonViewAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(AddFriendActivity.this, "未查询到类似人物", Toast.LENGTH_SHORT).show();
                    include.setVisibility(View.VISIBLE);
                    recycle.setVisibility(View.GONE);
                }
            }
        });
    }

    private void queryUserAll() {

        FormWork.getInstance().queryUserAll(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e!=null){
                    return;
                }
                if (list.size()>0&&list!=null){
                    addtitle("推介好友");
                    int num = (list.size()<=100)? list.size():100;
                    for (int i = 0; i < num; i++) {
                        addMsg(list.get(i));
                    }
                    commonViewAdapter.notifyDataSetChanged();
                    //addFriendAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void addtitle(String s) {
        AddFriendEntity addFriendEntity = new AddFriendEntity();
        addFriendEntity.setTitle(s);
        addFriendEntity.setType(Contants.type_title);
        addFriendEntities.add(addFriendEntity);
    }

    private void addMsg(UserBean userBean) {
        AddFriendEntity addFriendEntity = new AddFriendEntity();
        addFriendEntity.setAge(userBean.getAge());
        addFriendEntity.setMsg(userBean.getDesc());
        addFriendEntity.setUserId(userBean.getObjectId());
        addFriendEntity.setNickname(userBean.getNickName());
        addFriendEntity.setSex(userBean.isSex());
        addFriendEntity.setPhoto(userBean.getPhoto());
        addFriendEntity.setType(Contants.type_msg);
        addFriendEntities.add(addFriendEntity);
    }
}