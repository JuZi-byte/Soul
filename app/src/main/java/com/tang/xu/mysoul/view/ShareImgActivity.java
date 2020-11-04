package com.tang.xu.mysoul.view;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tang.xu.formwork.FormWork;
import com.tang.xu.formwork.base.BaseUiActivity;
import com.tang.xu.formwork.bean.UserBean;
import com.tang.xu.formwork.utils.FileMessage;
import com.tang.xu.formwork.view.LoadView;
import com.tang.xu.mysoul.R;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * FileName: ShareImgActivity
 * Founder: LiuGuiLin
 * Profile:
 */
public class ShareImgActivity extends BaseUiActivity implements View.OnClickListener {

    //头像
    private ImageView iv_photo;
    //昵称
    private TextView tv_name;
    //性别
    private TextView tv_sex;
    //年龄
    private TextView tv_age;
    //电话
    private TextView tv_phone;
    //简介
    private TextView tv_desc;
    //二维码
    private ImageView iv_qrcode;
    //根布局
    private LinearLayout ll_content;
    //下载
    private LinearLayout ll_download;

    private LoadView mLodingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_img);

        initView();
    }

    private void initView() {

        mLodingView = new LoadView(this);
        mLodingView.setTextViewMsg("正在保存");

        iv_photo = (ImageView) findViewById(R.id.iv_photo);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_sex = (TextView) findViewById(R.id.tv_sex);
        tv_age = (TextView) findViewById(R.id.tv_age);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_desc = (TextView) findViewById(R.id.tv_desc);
        iv_qrcode = (ImageView) findViewById(R.id.iv_qrcode);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_download = (LinearLayout) findViewById(R.id.ll_download);

        ll_download.setOnClickListener(this);

        loadInfo();
    }

    /**
     * 加载个人信息
     */
    private void loadInfo() {
        UserBean userBean = FormWork.getInstance().getUser();
        Glide.with(this).load(userBean.getPhoto()).into(iv_photo);
        tv_name.setText(userBean.getNickName());
        tv_sex.setText(userBean.isSex() ? "男" : "女");
        tv_age.setText(userBean.getAge() + " 岁 " );
        tv_phone.setText(userBean.getMobilePhoneNumber());
        tv_desc.setText(userBean.getDesc());
        createQRCode(userBean.getObjectId());
    }

    /**
     * 创建二维码
     */
    private void createQRCode(final String userId) {

        /**
         * View的绘制
         */

        iv_qrcode.post(new Runnable() {
            @Override
            public void run() {
                String textContent = "Meet#" + userId;
                Bitmap mBitmap = CodeUtils.createImage(textContent,
                        iv_qrcode.getWidth(), iv_qrcode.getHeight(), null);
                iv_qrcode.setImageBitmap(mBitmap);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_download:

                /**
                 * 1.View截图
                 * 2.创建一个Bitmap
                 * 3.保存到相册
                 */

                mLodingView.show("正在保存....");

                /**
                 * setDrawingCacheEnabled
                 * 保留我们的绘制副本
                 * 1.重新测量
                 * 2.重新布局
                 * 3.得到我们的DrawingCache
                 * 4.转换成Bitmap
                 */
                ll_content.setDrawingCacheEnabled(true);

                ll_content.measure(
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

                ll_content.layout(0, 0, ll_content.getMeasuredWidth(),
                        ll_content.getMeasuredHeight());

                Bitmap mBitmap = ll_content.getDrawingCache();

                if (mBitmap != null) {
                    saveBitmap(mBitmap);
                    mLodingView.hide();
                }
                break;
        }
    }

    private void saveBitmap(Bitmap mBitmap) {
        File rootPath = new File(Environment.getDownloadCacheDirectory()+"/Mysoul/");
        if (!rootPath.exists()){
            rootPath.mkdirs();
        }
        File file = new File(rootPath,System.currentTimeMillis()+".png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.PNG,90,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            updataBitmap(file.getPath());
            mLodingView.hide();
        } catch (FileNotFoundException e) {
            mLodingView.hide();
            e.printStackTrace();
        } catch (IOException e) {
            mLodingView.hide();
            e.printStackTrace();
        }

    }

    /**
     * 刷新图库
     * @param file
     */
    private void updataBitmap(String file) {
        //通过广播刷新图库
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse(file)));

        //通过数据库方式刷新
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Video.Media.TITLE,"");
        contentValues.put(MediaStore.Video.Media.MIME_TYPE,0);
        contentValues.put(MediaStore.Video.Media.DATA,file);
        contentValues.put(MediaStore.Video.Media.DURATION,0);
        getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,contentValues);

    }
}
