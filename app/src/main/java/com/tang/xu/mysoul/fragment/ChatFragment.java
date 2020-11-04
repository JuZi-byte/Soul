package com.tang.xu.mysoul.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.chatfragment.AllFriendFragment;
import com.tang.xu.mysoul.chatfragment.CallHistoryFragment;
import com.tang.xu.mysoul.chatfragment.ChatHistoryFragment;
import com.tang.xu.mysoul.entity.CommonEntity;

import java.util.ArrayList;

public
class ChatFragment extends Fragment {

    private CommonTabLayout common;
    private View inflate;
    private ArrayList<CustomTabEntity> customTabEntities = new ArrayList<>();
    private AllFriendFragment allFriendFragment;
    private CallHistoryFragment callHistoryFragment;
    private ChatHistoryFragment chatHistoryFragment;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.fragment_chat, container, false);
        initData();
        initView();
        return inflate;
    }

    private void initView() {
        common = inflate.findViewById(R.id.mTabLayout);
        if (chatHistoryFragment==null){
            chatHistoryFragment = new ChatHistoryFragment();
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.chat_fram,chatHistoryFragment).commit();
        }
        if (callHistoryFragment==null){
            callHistoryFragment = new CallHistoryFragment();
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.chat_fram,callHistoryFragment).commit();
        }
        if (allFriendFragment==null){
            allFriendFragment = new AllFriendFragment();
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.chat_fram,allFriendFragment).commit();
        }

        common.setTabData(customTabEntities);
        common.setCurrentTab(0);
        show(chatHistoryFragment);
        common.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position){
                    case 0:
                        show(chatHistoryFragment);
                        break;
                    case 1:
                        show(callHistoryFragment);
                        break;
                    case 2:
                        show(allFriendFragment);
                        break;

                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        if (fragment!=null&&fragment instanceof StarFragment){
            chatHistoryFragment = (ChatHistoryFragment) fragment;
        }
        if (fragment!=null&&fragment instanceof SquareFragment){
            callHistoryFragment = (CallHistoryFragment) fragment;
        }
        if (fragment!=null&&fragment instanceof ChatFragment){
            allFriendFragment = (AllFriendFragment) fragment;
        }

    }

    private void show(Fragment fragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .hide(chatHistoryFragment)
                .hide(callHistoryFragment)
                .hide(allFriendFragment)
                .show(fragment).commit();
    }

    private void initData() {
        customTabEntities.add(new CommonEntity("聊天记录",0,0));
        customTabEntities.add(new CommonEntity("通话记录",0,0));
        customTabEntities.add(new CommonEntity("全部好友",0,0));
    }
}
