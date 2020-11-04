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

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.entity.CallEntity;
import com.tang.xu.formwork.entity.ChatHistoryEntity;
import com.tang.xu.formwork.utils.LitePalHelper;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.adapter.CommonViewAdapter;
import com.tang.xu.mysoul.adapter.CommonViewHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CallHistoryFragment extends Fragment {

    private SmartRefreshLayout callSmart;
    private ImageView include;
    private RecyclerView callHistory;
    private View inflate;
    private Disposable disposable;
    private CommonViewAdapter<CallEntity> callAdapter;
    private ArrayList<CallEntity> arrayList = new ArrayList<>();
    private String mediaType;
    private String callType;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private String nickName;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflate = inflater.inflate(R.layout.layout_fragment_callhistory, container, false);
        initView();
        return inflate;
    }

    private void initView() {
        callSmart = inflate.findViewById(R.id.call_smart);
        include = inflate.findViewById(R.id.include);
        callHistory = inflate.findViewById(R.id.call_history);

        callHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        callHistory.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        callSmart.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                queryCallHistory();
                callSmart.finishRefresh(2000);
            }
        });

        callAdapter = new CommonViewAdapter<>(arrayList, new CommonViewAdapter.OnBindListener<CallEntity>() {
            @Override
            public void onBindViewHolder(CallEntity molde, CommonViewHolder viewHolder, int type, int position) {
                if (molde.getMediaType()==CallEntity.MEDIA_TYPE_AUDIO){
                    mediaType = "【音频】";
                }else if (molde.getMediaType()==CallEntity.MEDIA_TYPE_VIDEO){
                    mediaType = "【视频】";
                }
                if (molde.getCallStatus()==CallEntity.CALL_STATUS_ANSWER){
                    callType = "【未接电话】";
                    viewHolder.setresouce(R.id.iv_status_icon,R.drawable.img_un_answer_icon);
                }else if (molde.getCallStatus()==CallEntity.CALL_STATUS_DIAL){
                    callType = "【已拨电话】";
                    viewHolder.setresouce(R.id.iv_status_icon,R.drawable.img_dial);
                }else if (molde.getCallStatus()==CallEntity.CALL_STATUS_UN_ANSWER){
                    callType = "【已接电话】";
                    viewHolder.setresouce(R.id.iv_status_icon,R.drawable.img_answer);
                }
                viewHolder.setText(R.id.tv_type,mediaType+"  "+callType);
                viewHolder.setText(R.id.tv_time,dateFormat.format(molde.getCallTime())+"");

                FormWork.getInstance().queryUserId(molde.getUserId(), new FindListener<UserBean>() {
                    @Override
                    public void done(List<UserBean> list, BmobException e) {
                        if (e==null){
                            if (list!=null&&list.size()>0){
                                UserBean userBean = list.get(0);
                                nickName = userBean.getNickName();
                                viewHolder.setText(R.id.tv_nickname,nickName);
                            }
                        }
                    }
                });

            }

            @Override
            public int gettype(int position) {
                return R.layout.layout_call_record;
            }
        });

        callHistory.setAdapter(callAdapter);
        callAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryCallHistory();
    }

    private void queryCallHistory() {
        disposable = (Disposable) Observable.create(new ObservableOnSubscribe<List<CallEntity>>() {
            @Override
            public void subscribe(ObservableEmitter<List<CallEntity>> emitter) throws Exception {
                emitter.onNext(LitePalHelper.getInstance().queryCallEntity());
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<CallEntity>>() {
                    @Override
                    public void accept(List<CallEntity> callEntities) throws Exception {

                        if (callEntities!=null||callEntities.size()>0){
                            if (arrayList.size()>0){
                                arrayList.clear();
                            }
                            arrayList.addAll(callEntities);
                            callAdapter.notifyDataSetChanged();
                            include.setVisibility(View.GONE);
                            callHistory.setVisibility(View.VISIBLE);
                        }else {
                            include.setVisibility(View.VISIBLE);
                            callHistory.setVisibility(View.GONE);
                        }


                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable!=null){
            if (disposable.isDisposed()){
                disposable.dispose();
            }
        }
    }
}
