package com.tang.xu.formwork.entity;

public
class ChatHistoryEntity {
    private String url;
    private String name;
    private String msg;
    private String time;
    private int unReadSize;
    private String userID;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getUnReadSize() {
        return unReadSize;
    }

    public void setUnReadSize(int unReadSize) {
        this.unReadSize = unReadSize;
    }
}
