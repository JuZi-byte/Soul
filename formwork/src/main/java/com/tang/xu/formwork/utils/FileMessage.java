package com.tang.xu.formwork.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

public class FileMessage {

    //相机
    public static final int CAMEAR_REQUEST_CODE = 1004;
    //相册
    public static final int ALBUM_REQUEST_CODE = 1005;
    //音乐
    public static final int MUSIC_REQUEST_CODE = 1006;
    //视频
    public static final int VIDEO_REQUEST_CODE = 1007;

    //裁剪结果
    public static final int CAMERA_CROP_RESULT = 1008;

    public static volatile  FileMessage fileMessage = null;
    private SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
    private File temp=null;
    private Uri imageUri;

    public File getTempFile() {
        return temp;
    }

    public static FileMessage getInstance(){
        if (fileMessage==null){
            synchronized (FileMessage.class){
                if (fileMessage==null){
                    fileMessage=new FileMessage();
                }
            }
        }
        return fileMessage;
    }


    /**
     * 调用相机
     */
    @SuppressLint("SimpleDateFormat")
    public void toCamera(Activity activity){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String filename = simpleDateFormat.format(new Date());
        //设置中转
        temp = new File(Environment.getExternalStorageDirectory(),filename+".jpg");
        //兼容Andorid7.0
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.N){
            //Uri.fromFile 获取真实的Uri地址
            imageUri = Uri.fromFile(temp);
        }else{
            //利用FileProvider
            imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", temp);
            //添加权限
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        LoginUtils.LogE(imageUri+"imageuri");
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        activity.startActivityForResult(intent,1003);
    }

    public void toPhoto(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activity.startActivityForResult(intent,1004);
    }

    //获取真实的Uri  通过Uri去系统里查找真实地址
    public String  gerResultFromUri(Context context, Uri uri){
        String [] pro = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(context,uri,pro,null,null,null);
        Cursor cursor = cursorLoader.loadInBackground();
        int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(columnIndex);
    }

    public void setUpload(String nickName, File file) {

        BmobFile bmobFile = new BmobFile(file);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    //上传成功
                }
            }
        });
    }

    /**
     * 获取网络视频第一帧
     *
     * @param videoUrl
     * @return
     */
    public Bitmap getNetVideoBitmap(String videoUrl) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            //根据url获取缩略图
            retriever.setDataSource(videoUrl, new HashMap());
            //获得第一帧图片
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } finally {
            retriever.release();
        }
        return bitmap;
    }


    /**
     * 保存Bitmap到相册
     *
     * @param mContext
     * @param mBitmap
     */
    public boolean saveBitmapToAlbum(Context mContext, Bitmap mBitmap) {
        //根布局
        File rootPath = new File(Environment.getExternalStorageDirectory() + "/Meet/");

        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }

        File file = new File(rootPath, System.currentTimeMillis() + ".png");
        try {
            FileOutputStream out = new FileOutputStream(file);
            //自带的保存方法
            mBitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            updateAlnum(mContext, file.getPath());
            return true;
        } catch (IOException e) {
            Toast.makeText(mContext, "保存失败", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    /**
     * 刷新图库
     */
    private void updateAlnum(Context mContext, String path) {
        /**
         * 存在兼容性的问题
         */
        try {
            //通过广播刷新图库
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(path)));

            //通过数据库的方式插入
            ContentValues values = new ContentValues(4);
            values.put(MediaStore.Video.Media.TITLE, "");
            values.put(MediaStore.Video.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Video.Media.DATA, path);
            values.put(MediaStore.Video.Media.DURATION, 0);
            mContext.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 跳转到相册
     *
     * @param mActivity
     */
    public void toAlbum(Activity mActivity) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        mActivity.startActivityForResult(intent, ALBUM_REQUEST_CODE);
    }

    /**
     * 跳转音乐
     *
     * @param mActivity
     */
    public void toMusic(Activity mActivity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        mActivity.startActivityForResult(intent, MUSIC_REQUEST_CODE);
    }

    /**
     * 跳转视频
     *
     * @param mActivity
     */
    public void toVideo(Activity mActivity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");
        mActivity.startActivityForResult(intent, VIDEO_REQUEST_CODE);
    }
}

