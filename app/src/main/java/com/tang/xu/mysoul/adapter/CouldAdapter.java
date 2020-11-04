package com.tang.xu.mysoul.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.moxun.tagcloudlib.view.TagsAdapter;
import com.tang.xu.formwork.R;
import com.tang.xu.mysoul.entity.StarEntity;

import java.util.ArrayList;

public class CouldAdapter extends TagsAdapter {

    private Context context;
    private ArrayList<StarEntity> arrayList;
    private LayoutInflater layoutInflater;
    private ImageView imageView;
    private TextView textView;

    public CouldAdapter(Context context, ArrayList<StarEntity> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public View getView(Context context, int position, ViewGroup parent) {
        View inflate = layoutInflater.inflate(R.layout.cloud_tags,null);
        imageView = inflate.findViewById(R.id.image_cloud);
        textView = inflate.findViewById(R.id.text_cloud);
        textView.setText(arrayList.get(position).getUsername());
        Glide.with(context).load(arrayList.get(position).getPhotoUrl()).into(imageView);
        return inflate;
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public int getPopularity(int position) {
        return 7;
    }

    @Override
    public void onThemeColorChanged(View view, int themeColor) {

    }
}
