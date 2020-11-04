package com.tang.xu.mysoul.adapter;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tang.xu.formwork.utils.GlideHelper;

import java.io.File;

public class CommonViewHolder extends RecyclerView.ViewHolder {

    private SparseArray<View> sparseArray;
    private View mContentView;

    public CommonViewHolder(@NonNull View itemView) {
        super(itemView);
        sparseArray = new SparseArray<>();
        mContentView= itemView;
    }

    //commonviewholder 实体类
    public static CommonViewHolder getInstance(ViewGroup viewGroup,int layoutid){
        return new CommonViewHolder(View.inflate(viewGroup.getContext(),layoutid,null));
    }

    //提供外部访问View的方法
    public <T extends View> T getView(int viewId){
        View view = sparseArray.get(viewId);
        if (view==null){
            view = mContentView.findViewById(viewId);
            sparseArray.put(viewId,view);
        }
        return (T) view;
    }

    public CommonViewHolder setText(int id,String text){
        TextView view = getView(id);
        view.setText(text+"");
        return this;
    }

    public CommonViewHolder setImgUrl(Context context, int id, String url){
        ImageView view = getView(id);
        Glide.with(context).load(url).into(view);
        return this;
    }

    /**
     * 压缩
     *
     * @param mContext
     * @param viewId
     * @param url
     * @param w
     * @param h
     * @return
     */
    public CommonViewHolder setImageUrl(Context mContext, int viewId, String url, int w, int h) {
        ImageView iv = getView(viewId);
        GlideHelper.loadSmollUrl(mContext, url, w, h, iv);
        return this;
    }

    public CommonViewHolder setImgFile(Context context, int id, File url){
        ImageView view = getView(id);
        Glide.with(context).load(url).into(view);
        return this;
    }

    public CommonViewHolder setresouce(int id,int drawId){
        ImageView view = getView(id);
        view.setImageResource(drawId);
        return this;
    }

    public CommonViewHolder setShow(int item_contact_name, int visible) {
        TextView view = getView(item_contact_name);
        view.setVisibility(visible);
        return this;
    }

    public CommonViewHolder setColor(int item_contact_name, int color) {
        LinearLayout view = getView(item_contact_name);
        view.setBackgroundColor(color);
        return this;
    }
}
