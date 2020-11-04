package com.tang.xu.mysoul.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommonViewAdapter<T> extends RecyclerView.Adapter<CommonViewHolder> {

    private List<T> list;
    private OnBindListener<T> onBindListener;
    private OnMoreBindListenerr<T> onMoreBindListenerr;

    public CommonViewAdapter(List<T> list, OnBindListener<T> onBindListener) {
        this.list = list;
        this.onBindListener = onBindListener;
    }

    public CommonViewAdapter(List<T> list, OnMoreBindListenerr<T> onMoreBindListenerr) {
        this.list = list;
        this.onBindListener = onMoreBindListenerr;
        this.onMoreBindListenerr = onMoreBindListenerr;
    }

    //绑定数据
    public interface  OnBindListener<T>{
        void onBindViewHolder(T molde,CommonViewHolder viewHolder,int type,int position);
        int gettype(int position);
    }

    //绑定多类型数据
    public interface OnMoreBindListenerr<T> extends OnBindListener<T>{
        int getItemType(int position);
    }
    @NonNull
    @Override
    public CommonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int gettype = onBindListener.gettype(viewType);
        CommonViewHolder instance = CommonViewHolder.getInstance(parent, gettype);
        return instance;
    }

    @Override
    public int getItemViewType(int position) {
        if (onMoreBindListenerr!=null){
             return onMoreBindListenerr.getItemType(position);
        }
        return 0;
    }


    @Override
    public void onBindViewHolder(@NonNull CommonViewHolder holder, int position) {
        onBindListener.onBindViewHolder(list.get(position),holder,getItemViewType(position),position);
    }

    @Override
    public int getItemCount() {
        return list==null ? 0 : list.size();
    }
}
