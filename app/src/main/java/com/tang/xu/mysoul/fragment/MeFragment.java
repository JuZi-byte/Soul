package com.tang.xu.mysoul.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.view.NewFriendActivity;
import com.tang.xu.mysoul.view.PrivateSetActivity;
import com.tang.xu.mysoul.view.ShareImgActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public
class MeFragment extends Fragment {

    private CircleImageView ivMePhoto;
    private TextView tvNickname;
    private TextView tvServerStatus;
    private LinearLayout llMeInfo;
    private LinearLayout llNewFriend;
    private LinearLayout llPrivateSet;
    private LinearLayout llShare;
    private LinearLayout llNotice;
    private View inflate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_me, container, false);
        initView();
        loadMeUser();
        llNewFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),NewFriendActivity.class));
            }
        });
        llShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ShareImgActivity.class));
            }
        });
        llPrivateSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), PrivateSetActivity.class));
            }
        });
        return inflate;
    }

    private void loadMeUser() {
        UserBean user = FormWork.getInstance().getUser();
        String photo = user.getPhoto();
        String nickName = user.getNickName();
        String status = user.getStatus();
        Glide.with(getActivity()).load(photo).into(ivMePhoto);
        tvNickname.setText(nickName);
        tvServerStatus.setText(status);
    }

    private void initView() {
        ivMePhoto = inflate.findViewById(R.id.iv_me_photo);
        tvNickname = inflate.findViewById(R.id.tv_nickname);
        tvServerStatus = inflate.findViewById(R.id.tv_server_status);
        llMeInfo = inflate.findViewById(R.id.ll_me_info);
        llNewFriend = inflate.findViewById(R.id.ll_new_friend);
        llPrivateSet = inflate.findViewById(R.id.ll_private_set);
        llShare = inflate.findViewById(R.id.ll_share);
        llNotice = inflate.findViewById(R.id.ll_notice);
    }
}
