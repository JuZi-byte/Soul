package com.tang.xu.mysoul.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.utils.DilogMessage;
import com.tang.xu.formwork.utils.FileMessage;
import com.tang.xu.formwork.utils.LoginUtils;
import com.tang.xu.formwork.view.DilogViewRoom;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.R;

import java.io.File;

import cn.bmob.v3.exception.BmobException;
import de.hdodenhof.circleimageview.CircleImageView;

public class DialogActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText name;
    private Button next;
    private CircleImageView image;
    private DilogViewRoom viewRoom;
    private Button playPicture;
    private Button playPhoto;
    private Button playBack;
    private File fileUpload=null;
    private LoadView loadView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        initView();
    }

    private void initView() {
        loadView = new LoadView(this);
        viewRoom = DilogMessage.getInstance().createViewRoom(this, R.layout.dialog_seclect, Gravity.BOTTOM);
        playPicture = viewRoom.findViewById(R.id.play_picture);
        playPhoto = viewRoom.findViewById(R.id.play_photo);
        playBack = viewRoom.findViewById(R.id.play_back);
        playPicture.setOnClickListener(this);
        playPhoto.setOnClickListener(this);
        playBack.setOnClickListener(this);
        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        next = findViewById(R.id.next);
        image.setOnClickListener(this);
        next.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.image:
                DilogMessage.getInstance().show(viewRoom);
                break;
            case R.id.next:
                String trim = name.getText().toString().trim();
                if (!TextUtils.isEmpty(trim)){
                    //上传头像昵称
                    loadView.show("正在上传...");
                    FormWork.getInstance().setUpload(trim, fileUpload, new FormWork.UpdateList() {
                        @Override
                        public void onSuccess() {
                            loadView.hide();
                            setResult(RESULT_OK);
                            finish();
                        }

                        @Override
                        public void onError(BmobException e) {
                            loadView.hide();
                            Toast.makeText(DialogActivity.this, ""+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.play_picture:
                DilogMessage.getInstance().hide(viewRoom);
                FileMessage.getInstance().toCamera(this);
                break;
            case R.id.play_photo:
                DilogMessage.getInstance().hide(viewRoom);
                FileMessage.getInstance().toPhoto(this);
                break;
            case R.id.play_back:
                DilogMessage.getInstance().hide(viewRoom);
                break;
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode== Activity.RESULT_OK){
            if(requestCode==1003){
                fileUpload = FileMessage.getInstance().getTempFile();
                LoginUtils.LogE(fileUpload.getPath());
            }else if (requestCode==1004){
                Uri uri = data.getData();
                if (uri!=null){
                    String noUri = uri.getPath();
                    String s = FileMessage.getInstance().gerResultFromUri(this, uri);
                    Bitmap bitmap = BitmapFactory.decodeFile(s);
                    image.setImageBitmap(bitmap);
                }
            }
            if (fileUpload!=null){
                LoginUtils.LogE(fileUpload.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(fileUpload.getPath());
                image.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}