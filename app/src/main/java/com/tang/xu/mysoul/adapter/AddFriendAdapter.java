package com.tang.xu.mysoul.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.entity.Contants;
import com.tang.xu.mysoul.R;
import com.tang.xu.mysoul.entity.AddFriendEntity;

import java.util.List;

public class AddFriendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<AddFriendEntity> addFriendEntities;
    private LayoutInflater layoutInflater;
    public Onclick onclick;

    public void setOnclick(Onclick onclick) {
        this.onclick = onclick;
    }

    public AddFriendAdapter(Context context, List<AddFriendEntity> addFriendEntities) {
        this.context = context;
        this.addFriendEntities = addFriendEntities;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType== Contants.type_title){
            return new TitleViewHolder(layoutInflater.inflate(R.layout.item_title,null));
        }else if (viewType==Contants.type_msg){
            return new MsgViewHolder(layoutInflater.inflate(R.layout.item_msg,null));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AddFriendEntity addFriendEntity = addFriendEntities.get(position);
        if (addFriendEntity.getType()==Contants.type_title){
            ((TitleViewHolder)holder).itemTitleText.setText(addFriendEntity.getTitle());
        }else if (addFriendEntity.getType()==Contants.type_msg){
            ((MsgViewHolder)holder).itemMsgAge.setText(addFriendEntity.getAge()+" ");
            ((MsgViewHolder)holder).itemMsgDesc.setText(addFriendEntity.getMsg());
            ((MsgViewHolder)holder).itemMsgName.setText(addFriendEntity.getNickname());
            Glide.with(context).load(addFriendEntity.getPhoto()).diskCacheStrategy(DiskCacheStrategy.ALL).into(((MsgViewHolder) holder).itemMsgImage);
            ((MsgViewHolder)holder).itemMsgSex.setImageResource(addFriendEntity.isSex()?R.drawable.img_boy_icon:R.drawable.img_girl_icon);
            if (addFriendEntity.isIscontact()){
                ((MsgViewHolder)holder).itemContactName.setText(addFriendEntity.getContactPhone());
                ((MsgViewHolder)holder).itemContactName.setVisibility(View.VISIBLE);
            }
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onclick!=null){
                    onclick.onClick(position);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return addFriendEntities.size();
    }

    @Override
    public int getItemViewType(int position) {
        return addFriendEntities.get(position).getType();
    }

    class MsgViewHolder extends RecyclerView.ViewHolder{

        private ImageView itemMsgImage;
        private TextView itemMsgName;
        private TextView itemMsgAge;
        private TextView itemMsgDesc;
        private ImageView itemMsgSex;
        private TextView itemContactName;
        public MsgViewHolder(@NonNull View itemView) {
            super(itemView);
            itemMsgImage = itemView.findViewById(R.id.item_msg_image);
            itemMsgName = itemView.findViewById(R.id.item_msg_name);
            itemMsgAge = itemView.findViewById(R.id.item_msg_age);
            itemMsgDesc = itemView.findViewById(R.id.item_msg_desc);
            itemMsgSex = itemView.findViewById(R.id.item_msg_sex);
            itemContactName = itemView.findViewById(R.id.item_contact_name);
        }
    }

    class TitleViewHolder extends RecyclerView.ViewHolder{

        private TextView itemTitleText;
        public TitleViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTitleText = itemView.findViewById(R.id.item_title_text);
        }
    }

    public interface Onclick{
        void onClick(int position);
    }
}
