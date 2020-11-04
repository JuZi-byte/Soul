package com.tang.xu.mysoul.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.PrivateSet;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.R;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;


/**
 * FileName: PrivateSetActivity
 * Founder: LiuGuiLin
 * Profile: 隐私设置
 */
public class PrivateSetActivity extends BaseUiActivity implements View.OnClickListener {


    /**
     * 私有库的创建：
     * 1.创建一个BmobObject PrivateSet
     * 2.通过查询PrivateSet里面是否存在自己来判断开关
     * 3.开关的一些操作
     * 打开：则将自己添加到PrivateSet
     * 关闭：则将自己在PrivateSet中删除
     * 4.在查询联系人的时候通过查询PrivateSet过滤
     */

    private Switch sw_kill_contact;

    private LoadView mLodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_set);

        initView();
    }

    private void initView() {

        mLodingView = new LoadView(this);

        sw_kill_contact = (Switch) findViewById(R.id.sw_kill_contact);
        sw_kill_contact.setOnClickListener(this);

        queryPrivateSet();
    }

    //是否选中
    private boolean isCheck = false;

    //当前ID
    private String currentId = "";


    /**
     * 查询私有库
     */
    private void queryPrivateSet() {
        FormWork.getInstance().queryPrivateSet(new FindListener<PrivateSet>() {
            @Override
            public void done(List<PrivateSet> list, BmobException e) {
                if (e == null) {
                    if (list!=null&&list.size()>0) {
                        for (int i = 0; i < list.size(); i++) {
                            PrivateSet set = list.get(i);
                            if (set.getUserId().equals(FormWork.getInstance().getUser().getObjectId())) {
                                currentId = set.getObjectId();
                                //我存在表中
                                isCheck = true;
                                break;
                            }
                        }
                        sw_kill_contact.setChecked(isCheck);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sw_kill_contact:
                isCheck = !isCheck;
                sw_kill_contact.setChecked(isCheck);
                if (isCheck) {
                    //添加
                    addPrivateSet();
                } else {
                    //删除
                    delPrivateSet();
                }

                break;
        }
    }

    /**
     * 添加
     */
    private void addPrivateSet() {
        mLodingView.show("添加....");
        FormWork.getInstance().addPrivateSet(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                mLodingView.hide();
                if (e == null) {
                    currentId = s;
                    Toast.makeText(PrivateSetActivity.this, "添加成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 删除
     */
    private void delPrivateSet() {
        mLodingView.show("删除");
        FormWork.getInstance().delPrivateSet(currentId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                mLodingView.hide();
                if (e == null) {
                    Toast.makeText(PrivateSetActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
