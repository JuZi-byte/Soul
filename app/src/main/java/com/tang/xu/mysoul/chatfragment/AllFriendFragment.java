package com.tang.xu.mysoul.chatfragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.entity.FriendEntity;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;
import com.tang.xu.mysoul.entity.AllFriendEntity;
import com.tang.xu.mysoul.view.UserInfoActivity;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class AllFriendFragment extends Fragment {

    private SmartRefreshLayout smart;
    private RecyclerView chatAllfriend;
    private View inflate;
    private CommonViewAdapter<AllFriendEntity> commonViewAdapter;
    private ArrayList<AllFriendEntity> arrayList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.layout_fragment_allfriend, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        smart = inflate.findViewById(R.id.smart);
        chatAllfriend = inflate.findViewById(R.id.chat_allfriend);
        chatAllfriend.setLayoutManager(new LinearLayoutManager(getContext()));
        chatAllfriend.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        commonViewAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnBindListener<AllFriendEntity>() {
            @Override
            public void onBindViewHolder(AllFriendEntity molde, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setText(R.id.tv_nickname,molde.getNickname());
                viewHolder.setText(R.id.tv_desc,molde.getMsg());
                viewHolder.setImgUrl(getContext(),R.id.iv_photo,molde.getPhoto());
                viewHolder.setresouce(R.id.iv_sex,molde.isSex()?R.drawable.img_boy_icon:R.drawable.img_girl_icon);
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserInfoActivity.startActivity(getContext(),molde.getUserId());
                    }
                });
            }

            @Override
            public int gettype(int position) {
                return R.layout.layout_all_friend_item;
            }
        });
        chatAllfriend.setAdapter(commonViewAdapter);
        quertAllFriend();
        commonViewAdapter.notifyDataSetChanged();
    }

    private void quertAllFriend() {
        FormWork.getInstance().queryFriend(new FindListener<FriendEntity>() {
            @Override
            public void done(List<FriendEntity> list, BmobException e) {
                if (e==null){
                    LoginUtils.LogE("allfriend"+list.size());
                    if (list!=null&&list.size()>0){
                        for (int i = 0; i < list.size(); i++) {
                            FriendEntity friendEntity = list.get(i);
                            UserBean userBean = friendEntity.getFriend();
                            LoginUtils.LogE("allfrienduserBean"+userBean.toString());
                            String objectId = userBean.getObjectId();
                            LoginUtils.LogE("allfrienduserBean"+objectId);
                            FormWork.getInstance().queryUserId(objectId, new FindListener<UserBean>() {
                                @Override
                                public void done(List<UserBean> list, BmobException e) {
                                    if (e==null){
                                        if (list!=null&&list.size()>0){
                                            UserBean userBean = list.get(0);
                                            AllFriendEntity allFriendEntity = new AllFriendEntity();
                                            allFriendEntity.setPhoto(userBean.getPhoto());
                                            allFriendEntity.setMsg(userBean.getDesc());
                                            allFriendEntity.setNickname(userBean.getNickName());
                                            allFriendEntity.setSex(userBean.isSex());
                                            allFriendEntity.setUserId(userBean.getObjectId());
                                            arrayList.add(allFriendEntity);
                                            commonViewAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }


}
