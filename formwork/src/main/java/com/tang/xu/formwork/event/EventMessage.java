package com.tang.xu.formwork.event;

import android.view.SurfaceView;

/**
 * EventMessage 事件类
 */
public
class EventMessage {


    public static final int FRIEND_ADD = 1000;
    //发送文本
    public static final int FRIEND_SEND_TEXT = 1001;

    //发送图片
    public static final int FRIEND_SEND_IMAGE = 1002;

    //发送位置数据
    public static final int FRIEND_SEND_LOCATION = 1003;

    //发送位置数据
    public static final int FRIEND_SEND_LOCATION_VIDEO = 1004;

    private int type;
    private String text;
    private String userId;
    private String imageUri;

    private int jin;
    private int wei;
    private String adress;

    //相机
    private SurfaceView surfaceView;

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        this.surfaceView = surfaceView;
    }

    public int getJin() {
        return jin;
    }

    public void setJin(int jin) {
        this.jin = jin;
    }

    public int getWei() {
        return wei;
    }

    public void setWei(int wei) {
        this.wei = wei;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public EventMessage(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public static int getFriendAdd() {
        return FRIEND_ADD;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
