package com.tang.xu.mysoul.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.cloud.CloudMessage;
import com.tang.xu.formwork.entity.LitePalEntity;
import com.tang.xu.formwork.event.EventMessage;
import com.tang.xu.formwork.event.EventUtils;
import com.tang.xu.formwork.utils.LitePalHelper;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class NewFriendActivity extends AppCompatActivity{

    private View include;
    private RecyclerView mNewFriendView;
    private CommonViewAdapter<LitePalEntity> commonViewAdapter;
    private ArrayList<LitePalEntity> arrayList = new ArrayList<>();
    private Disposable disposable;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend);
        initView();
    }

    private void queryFriend() {
        disposable = Observable.create(new ObservableOnSubscribe<List<LitePalEntity>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<LitePalEntity>> emitter) throws Exception {
                emitter.onNext(LitePalHelper.getInstance().queryFriend());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<LitePalEntity>>() {
                    @Override
                    public void accept(List<LitePalEntity> litePalEntities) throws Exception {
                        //更新UI
                        if (litePalEntities!=null&&litePalEntities.size()>0){
                            for (int i = 0; i < litePalEntities.size(); i++) {
                                LitePalEntity litePalEntity = litePalEntities.get(i);
                                LoginUtils.LogE("查询所有好友，更新UI"+litePalEntity.toString());
                            }
                            arrayList.addAll(litePalEntities);
                            commonViewAdapter.notifyDataSetChanged();
                        }else {
                            include.setVisibility(View.VISIBLE);
                            mNewFriendView.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void initView() {
        include = findViewById(R.id.include);
        mNewFriendView = findViewById(R.id.mNewFriendView);

        mNewFriendView.setLayoutManager(new LinearLayoutManager(this));
        mNewFriendView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        commonViewAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnBindListener<LitePalEntity>() {
            @Override
            public void onBindViewHolder(LitePalEntity molde, CommonViewHolder viewHolder, int type, int position) {
                FormWork.getInstance().queryUserId(molde.getUserId(), new FindListener<UserBean>() {
                    @Override
                    public void done(List<UserBean> list, BmobException e) {
                        if (e==null){
                            userBean = list.get(0);
                            viewHolder.setImgUrl(NewFriendActivity.this,R.id.iv_photo,userBean.getPhoto());
                            viewHolder.setText(R.id.tv_nickname,userBean.getNickName());
                            viewHolder.setText(R.id.tv_age,userBean.getAge()+"");
                            viewHolder.setText(R.id.tv_desc,userBean.getDesc());
                            viewHolder.setText(R.id.tv_msg,molde.getName());
                            LoginUtils.LogE("123"+molde.getIsAgree());
                            if (molde.getIsAgree()==0){
                                LoginUtils.LogE("0"+molde.getIsAgree());
                                viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                viewHolder.setText(R.id.tv_result,"已同意");
                            }else if (molde.getIsAgree()==1){
                                LoginUtils.LogE("1"+molde.getIsAgree());
                                viewHolder.getView(R.id.ll_agree).setVisibility(View.GONE);
                                viewHolder.getView(R.id.tv_result).setVisibility(View.VISIBLE);
                                viewHolder.setText(R.id.tv_result,"已拒绝");
                            }
                        }
                    }
                });

                viewHolder.getView(R.id.ll_yes).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updata(position, 0);
                        FormWork.getInstance().addfriend(userBean, new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e==null){
                                    //保存成功，通知对方
                                    CloudMessage.getInstance().sendTextMessage("",CloudMessage.TYPE_ADD_ARGEED, NewFriendActivity.this.userBean.getObjectId());
                                    EventUtils.getInstance().post(EventMessage.FRIEND_ADD);
                                }
                            }
                        });
                    }
                });
                viewHolder.getView(R.id.ll_no).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updata(position, 1);
                    }
                });
            }

            @Override
            public int gettype(int position) {
                return R.layout.layout_new_friend_item;
            }
        });

        mNewFriendView.setAdapter(commonViewAdapter);
        queryFriend();
    }

    private void updata(int position, int i) {
        LitePalEntity litePalEntity = arrayList.get(position);
        LoginUtils.LogE("updata01"+litePalEntity.toString());
        LitePalHelper.getInstance().upData(litePalEntity.getUserId(),i);
        litePalEntity.setIsAgree(i);
        arrayList.set(position,litePalEntity);
        commonViewAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
            if (disposable.isDisposed()) {
                disposable.dispose();
            }

    }

}