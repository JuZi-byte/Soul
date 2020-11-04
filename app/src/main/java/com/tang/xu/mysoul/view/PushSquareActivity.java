package com.tang.xu.mysoul.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseBackActivity;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.SquareSet;
import com.tang.xu.formwork.utils.FileMessage;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.R;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * FileName: PushSquareActivity
 * Founder: LiuGuiLin
 * Profile: 发布广场
 */
public class PushSquareActivity extends BaseBackActivity implements View.OnClickListener {

    //输入框
    private EditText et_content;
    //文字数量
    private TextView tv_content_size;
    //清空
    private ImageView iv_error;
    //媒体路径
    private TextView tv_media_path;
    //已存媒体
    private LinearLayout ll_media;
    //相机
    private LinearLayout ll_camera;
    //相册
    private LinearLayout ll_ablum;
    //音乐
    private LinearLayout ll_music;
    //视频
    private LinearLayout ll_video;
    //媒体类型
    private LinearLayout ll_media_type;

    //要上传的文件
    private File uploadFile = null;
    //媒体类型
    private int MediaType = SquareSet.PUSH_TEXT;

    private LoadView mLodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_square);

        initView();
    }

    private void initView() {

        mLodingView = new LoadView(this);
        et_content = (EditText) findViewById(R.id.et_content);
        tv_content_size = (TextView) findViewById(R.id.tv_content_size);
        iv_error = (ImageView) findViewById(R.id.iv_error);
        tv_media_path = (TextView) findViewById(R.id.tv_media_path);
        ll_media = (LinearLayout) findViewById(R.id.ll_media);
        ll_camera = (LinearLayout) findViewById(R.id.ll_camera);
        ll_ablum = (LinearLayout) findViewById(R.id.ll_ablum);
        ll_music = (LinearLayout) findViewById(R.id.ll_music);
        ll_video = (LinearLayout) findViewById(R.id.ll_video);
        ll_media_type = (LinearLayout) findViewById(R.id.ll_media_type);

        iv_error.setOnClickListener(this);
        ll_camera.setOnClickListener(this);
        ll_ablum.setOnClickListener(this);
        ll_music.setOnClickListener(this);
        ll_video.setOnClickListener(this);

        //输入框监听
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_content_size.setText(s.length() + "/140");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_error:
                ll_media_type.setVisibility(View.VISIBLE);
                ll_media.setVisibility(View.GONE);
                uploadFile = null;
                MediaType = SquareSet.PUSH_TEXT;
                break;
            case R.id.ll_camera:
                FileMessage.getInstance().toCamera(this);
                break;
            case R.id.ll_ablum:
                FileMessage.getInstance().toAlbum(this);
                break;
            case R.id.ll_music:
                FileMessage.getInstance().toMusic(this);
                break;
            case R.id.ll_video:
                FileMessage.getInstance().toVideo(this);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case FileMessage.CAMEAR_REQUEST_CODE:
                    uploadFile = FileMessage.getInstance().getTempFile();
                    break;
                case FileMessage.ALBUM_REQUEST_CODE:
                case FileMessage.MUSIC_REQUEST_CODE:
                case FileMessage.VIDEO_REQUEST_CODE:
                    if (data != null) {
                        Uri uri = data.getData();
                        String path = FileMessage.getInstance().
                                gerResultFromUri(PushSquareActivity.this, uri);
                        if (!TextUtils.isEmpty(path)) {
                            if (path.endsWith(".jpg")
                                    || path.endsWith(".png")
                                    || path.endsWith(".jpeg")) {
                                MediaType = SquareSet.PUSH_IMAGE;
                                //图片
                                tv_media_path.setText("图片");
                            } else if (path.endsWith("mp3")) {
                                MediaType = SquareSet.PUSH_MUSIC;
                                //音乐
                                tv_media_path.setText("音乐");
                            } else if (path.endsWith("mp4") ||
                                    path.endsWith("wav") ||
                                    path.endsWith("avi")) {
                                MediaType = SquareSet.PUSH_VIDEO;
                                //视频
                                tv_media_path.setText("视频");
                            }
                            uploadFile = new File(path);
                            ll_media_type.setVisibility(View.GONE);
                            ll_media.setVisibility(View.VISIBLE);
                        }
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.input_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_input:
                inputSquare();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 发布到广场
     */
    private void inputSquare() {
        final String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(content) && uploadFile == null) {
            return;
        }
        mLodingView.show("发布");
        if (uploadFile != null) {
            //上传文件
            final BmobFile bmobFile = new BmobFile(uploadFile);
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        push(content, bmobFile.getFileUrl());
                    }
                }
            });
        } else {
            push(content, "");
        }
    }

    /**
     * 发表
     *
     * @param content
     * @param path
     */
    private void push(String content, String path) {
        //纯文本
        FormWork.getInstance()
                .pushSquare(MediaType, content, path, new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        mLodingView.hide();
                        if (e == null) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }else{
                        }
                    }
                });
    }

}
