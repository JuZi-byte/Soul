package com.tang.xu.mysoul.chatfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.SimulationData;
import com.tang.xu.formwork.bean.TestBean;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.cloud.CloudMessage;
import com.tang.xu.formwork.entity.ChatHistoryEntity;
import com.tang.xu.formwork.event.EventMessage;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;
import com.tang.xu.mysoul.view.ChatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.TextMessage;

public class ChatHistoryFragment extends Fragment{

    private ImageView include;
    private SmartRefreshLayout smart;
    private RecyclerView chatHistory;
    private CommonViewAdapter<ChatHistoryEntity> chatHistoryEntityCommonViewAdapter;
    private ArrayList<ChatHistoryEntity> arrayList = new ArrayList<>();
    private View inflate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.chat_fragment_history, container, false);
        initView();
        initData();
        return inflate;
    }

    private void initData() {
        chatHistoryEntityCommonViewAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnBindListener<ChatHistoryEntity>() {
            @Override
            public void onBindViewHolder(ChatHistoryEntity molde, CommonViewHolder viewHolder, int type, int position) {
                viewHolder.setImgUrl(getActivity(),R.id.iv_photo,molde.getUrl());
                viewHolder.setText(R.id.tv_nickname,molde.getName());
                viewHolder.setText(R.id.tv_time,molde.getTime());
                viewHolder.setText(R.id.tv_content,molde.getMsg());
                if (molde.getUnReadSize()>0){
                    viewHolder.setShow(R.id.tv_un_read,View.VISIBLE);
                    viewHolder.setText(R.id.tv_un_read,molde.getUnReadSize()+"");
                }else{
                    viewHolder.setShow(R.id.tv_un_read,View.GONE);
                }
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChatActivity.ChatActivity(getActivity(),
                                molde.getUserID(),molde.getName(),molde.getUrl());
                    }
                });

            }

            @Override
            public int gettype(int position) {
                return R.layout.layout_chat_record_item;
            }
        });
        chatHistory.setAdapter(chatHistoryEntityCommonViewAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        queryChatHistory();
    }

    private void queryChatHistory() {
        CloudMessage.getInstance().queryChatHistory(new RongIMClient.ResultCallback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> conversations) {
                LoginUtils.LogE("onSuccess___queryChatHistory");
                if (conversations!=null&&conversations.size()>0){
                    if (arrayList.size()>0){
                        arrayList.clear();
                    }
                    for (int i = 0; i < conversations.size(); i++) {
                        Conversation conversation = conversations.get(i);
                        String targetId = conversation.getTargetId();
                        FormWork.getInstance().queryUserId(targetId, new FindListener<UserBean>() {
                            @Override
                            public void done(List<UserBean> list, BmobException e) {
                                if (e==null){
                                    if (list!=null&&list.size()>0){
                                        UserBean userBean = list.get(0);
                                        LoginUtils.LogE(userBean.toString());
                                        ChatHistoryEntity chatHistoryEntity = new ChatHistoryEntity();
                                        chatHistoryEntity.setName(userBean.getNickName());
                                        chatHistoryEntity.setUserID(userBean.getObjectId());
                                        chatHistoryEntity.setUrl(userBean.getPhoto());
                                        chatHistoryEntity.setTime(new SimpleDateFormat("HH:mm:ss").format(conversation.getReceivedTime()));
                                        chatHistoryEntity.setUnReadSize(conversation.getUnreadMessageCount());

                                        String objectName = conversation.getObjectName();
                                        if (objectName.equals(CloudMessage.MSG_TEXT_NAME)){
                                            TextMessage textMessage = (TextMessage) conversation.getLatestMessage();
                                            String content = textMessage.getContent();
                                            TestBean testBean = new Gson().fromJson(content, TestBean.class);
                                            if (testBean.getType().equals(CloudMessage.TYPE_TEXT)){
                                                chatHistoryEntity.setMsg(testBean.getMsg());
                                                arrayList.add(chatHistoryEntity);
                                            }
                                        }else if (objectName.equals(CloudMessage.MSG_IMAGE_NAME)){
                                            chatHistoryEntity.setMsg("[图片]");
                                            arrayList.add(chatHistoryEntity);
                                        }else if (objectName.equals(CloudMessage.MSG_LOCATION_NAME)){
                                            chatHistoryEntity.setMsg("[位置]");
                                            arrayList.add(chatHistoryEntity);
                                        }
                                        chatHistoryEntityCommonViewAdapter.notifyDataSetChanged();

                                        if(arrayList.size() > 0){
                                            include.setVisibility(View.GONE);
                                            chatHistory.setVisibility(View.VISIBLE);
                                        }else{
                                            include.setVisibility(View.VISIBLE);
                                            chatHistory.setVisibility(View.GONE);
                                        }
                                    }
                                }

                            }
                        });
                    }
                }else{
                    include.setVisibility(View.VISIBLE);
                    chatHistory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(RongIMClient.ErrorCode errorCode) {

            }
        });
    }

    private void initView() {
        include = inflate.findViewById(R.id.include);
        smart = inflate.findViewById(R.id.smart);
        chatHistory = inflate.findViewById(R.id.chat_history);
        chatHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        chatHistory.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        smart.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                queryChatHistory();
                smart.finishRefresh(2000);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventMessage event) {
        switch (event.getType()) {
            case EventMessage.FRIEND_SEND_TEXT:
                queryChatHistory();
                break;
        }
    }
}