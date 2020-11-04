package com.tang.xu.formwork.entity;

import org.litepal.crud.LitePalSupport;

public class LitePalEntity extends LitePalSupport {

    //留言
    private String name;
    //ID
    private String userId;
    //时间
    private String saveTime;
    //状态
    private int isAgree=-1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(String saveTime) {
        this.saveTime = saveTime;
    }

    public int getIsAgree() {
        return isAgree;
    }

    public void setIsAgree(int isAgree) {
        this.isAgree = isAgree;
    }

    @Override
    public String toString() {
        return "LitePalEntity{" +
                "name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", saveTime='" + saveTime + '\'' +
                ", isAgree=" + isAgree +
                '}';
    }
}
