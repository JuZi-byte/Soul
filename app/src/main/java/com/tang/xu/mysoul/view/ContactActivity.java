package com.tang.xu.mysoul.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.AddFriendAdapter;
import com.tang.xu.mysoul.entity.AddFriendEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ContactActivity extends AppCompatActivity {

    private RecyclerView contactRecycle;
    private Map<String,String> map = new HashMap<>();
    private ArrayList<AddFriendEntity> arrayList = new ArrayList<>();
    private AddFriendAdapter addFriendAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        initView();
    }

    private void initView() {
        contactRecycle = findViewById(R.id.contactRecycle);
        contactRecycle.setLayoutManager(new LinearLayoutManager(this));
        contactRecycle.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        addFriendAdapter = new AddFriendAdapter(this,arrayList);
        contactRecycle.setAdapter(addFriendAdapter);

        addFriendAdapter.setOnclick(new AddFriendAdapter.Onclick() {
            @Override
            public void onClick(int position) {
                UserInfoActivity.startActivity(ContactActivity.this,
                        arrayList.get(position).getUserId());
            }
        });
        loadContact();
        loadUser();
    }

    private void loadUser() {
        if (map.size()>0&&map!=null){
            for (Map.Entry<String,String> entry:map.entrySet()) {
                FormWork.getInstance().queryUser(entry.getValue(), new FindListener<UserBean>() {
                    @Override
                    public void done(List<UserBean> list, BmobException e) {
                        if (e==null){
                            if (list.size()>0&&list!=null){
                                UserBean userBean = list.get(0);
                                addMsg(userBean,entry.getKey(),entry.getValue());
                            }
                        }
                    }
                });
            }
        }
    }

    private void loadContact() {
        Cursor query = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            ,null, null, null, null);
        String name;
        String phone;
        while (query.moveToNext()){
            name = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            LoginUtils.LogE(name);
            phone = query.getString(query.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            LoginUtils.LogE(phone);
            phone.replace(" ", "").replace("-", "");
            LoginUtils.LogE(phone);
            map.put(name,phone);
        }
    }

    private void addMsg(UserBean userBean,String name,String phone) {
        AddFriendEntity addFriendEntity = new AddFriendEntity();
        addFriendEntity.setAge(userBean.getAge());
        addFriendEntity.setMsg(userBean.getDesc());
        addFriendEntity.setUserId(userBean.getObjectId());
        addFriendEntity.setNickname(userBean.getNickName());
        addFriendEntity.setSex(userBean.isSex());
        addFriendEntity.setPhoto(userBean.getPhoto());
        addFriendEntity.setType(Contants.type_msg);
        addFriendEntity.setIscontact(true);
        addFriendEntity.setContactName(name);
        addFriendEntity.setContactPhone(phone);
        arrayList.add(addFriendEntity);
        addFriendAdapter.notifyDataSetChanged();
    }
}