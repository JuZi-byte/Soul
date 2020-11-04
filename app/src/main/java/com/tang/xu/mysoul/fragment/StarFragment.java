package com.tang.xu.mysoul.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.moxun.tagcloudlib.view.TagCloudView;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.utils.PairFriendHelper;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.adapter.CouldAdapter;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.entity.StarEntity;
import com.tang.xu.mysoul.view.AddFriendActivity;
import com.tang.xu.mysoul.view.ChatActivity;
import com.tang.xu.mysoul.view.QrCodeActivity;
import com.tang.xu.mysoul.view.UserInfoActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public
class StarFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_CODE = 1235;

    private TagCloudView tagcloud;
    private ImageView camera;
    private ImageView add;
    private View inflate;
    private ArrayList<StarEntity> arrayList = new ArrayList<>();
    private CouldAdapter couldAdapter;
    private LinearLayout llRandom;
    private LinearLayout llSoul;
    private LinearLayout llFate;
    private LinearLayout llLove;
    private List<UserBean> allArrlist = new ArrayList<>();
    private LoadView loadView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        inflate = inflater.inflate(R.layout.fragment_star, container, false);
        initView();
        couldAdapter = new CouldAdapter(getActivity(),arrayList);
        tagcloud.setAdapter(couldAdapter);
        loadStarUser();
        tagcloud.setOnTagClickListener(new TagCloudView.OnTagClickListener() {
            @Override
            public void onItemClick(ViewGroup parent, View view, int position) {
                Toast.makeText(getContext(), ""+arrayList.get(position), Toast.LENGTH_SHORT).show();
                UserInfoActivity.startActivity(getContext(),arrayList.get(position).getUserId());
            }
        });
        return inflate;
    }

    private void loadStarUser() {
        FormWork.getInstance().queryUserAll(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> list, BmobException e) {
                if (e==null){
                    if (list!=null&&list.size()>0){
                        arrayList.clear();
                        allArrlist.clear();
                        allArrlist.addAll(list);
                        int index=100;
                        if (list.size()<=index){
                            index = list.size();
                        }
                        for (int i = 0; i < index; i++) {
                            UserBean userBean = list.get(i);
                            StarEntity starEntity = new StarEntity();
                            starEntity.setUserId(userBean.getObjectId());
                            starEntity.setUsername(userBean.getNickName());
                            starEntity.setPhotoUrl(userBean.getPhoto());
                            arrayList.add(starEntity);
                        }
                        couldAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

        PairFriendHelper.getInstance().setOnPairResultListener(new PairFriendHelper.OnPairResultListener() {
            @Override
            public void OnPairListener(String userId) {
                loadView.hide();
                UserInfoActivity.startActivity(getContext(),userId);
            }

            @Override
            public void OnPairFailListener() {
                loadView.hide();
                Toast.makeText(getContext(), "匹配失败", Toast.LENGTH_SHORT).show();
            }
        });
        couldAdapter.notifyDataSetChanged();
    }

    private void initView() {
        loadView = new LoadView(getContext());
        tagcloud = inflate.findViewById(R.id.tagcloud);
        camera = inflate.findViewById(R.id.camera);
        add = inflate.findViewById(R.id.add);
        camera.setOnClickListener(this);
        add.setOnClickListener(this);
        llRandom = inflate.findViewById(R.id.ll_random);
        llSoul = inflate.findViewById(R.id.ll_soul);
        llFate = inflate.findViewById(R.id.ll_fate);
        llLove = inflate.findViewById(R.id.ll_love);
        llRandom.setOnClickListener(this);
        llSoul.setOnClickListener(this);
        llFate.setOnClickListener(this);
        llLove.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.camera:
                Intent intent = new Intent(getActivity(), QrCodeActivity.class);
                startActivityForResult(intent,REQUEST_CODE);
                break;
            case R.id.add:
                startActivity(new Intent(getActivity(), AddFriendActivity.class));
                break;
            case R.id.ll_random:
                pairUser(0,allArrlist);
                break;
            case R.id.ll_soul:
                pairUser(1,allArrlist);
                break;
            case R.id.ll_fate:
                pairUser(2,allArrlist);
                break;
            case R.id.ll_love:
                pairUser(3,allArrlist);
                break;
        }
    }

    private void pairUser(int index,List<UserBean> list) {
        switch (index){
            case 0:
                loadView.show("随机匹配...");
                break;
            case 1:
                loadView.show("灵魂匹配...");
                break;
            case 2:
                loadView.show("缘分匹配");
                break;
            case 3:
                loadView.show("恋爱铃匹配...");
                break;
        }
        if (allArrlist!=null&&allArrlist.size()>0){
            PairFriendHelper.getInstance().pairUser(index,allArrlist);
        }else {
            loadView.hide();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    //Meet#c7a9b4794f
                    if (!TextUtils.isEmpty(result)) {
                        //是我们自己的二维码
                        if (result.startsWith("Meet")) {
                            String[] split = result.split("#");
                            if (split != null && split.length >= 2) {
                                try {
                                    UserInfoActivity.startActivity(getActivity(), split[1]);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(),"错误的二维码", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(),"错误的二维码", Toast.LENGTH_SHORT).show();
                    }

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getActivity(),"解析失败", Toast.LENGTH_LONG).show();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
