package com.tang.xu.formwork.bean;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UploadFileListener;

public class UserBean extends BmobUser {

    //Token 头像地址 昵称
    private String tokenPhoto;
    private String tokenNickName;
    //昵称 头像
    private String NickName;
    private String Photo;
    //性别
    private boolean sex = true;
    //简历
    private String desc;
    //年龄
    private int age=0;
    //生日
    private String birthday;
    //星座
    private String constellation;
    //爱好
    private String happy;
    //单身状态
    private String status;
    private String phone;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTokenPhoto() {
        return tokenPhoto;
    }

    public void setTokenPhoto(String tokenPhoto) {
        this.tokenPhoto = tokenPhoto;
    }

    public String getTokenNickName() {
        return tokenNickName;
    }

    public void setTokenNickName(String tokenNickName) {
        this.tokenNickName = tokenNickName;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }

    public boolean isSex() {
        return sex;
    }

    public void setSex(boolean sex) {
        this.sex = sex;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getHappy() {
        return happy;
    }

    public void setHappy(String happy) {
        this.happy = happy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UserBean{" +
                "tokenPhoto='" + tokenPhoto + '\'' +
                ", tokenNickName='" + tokenNickName + '\'' +
                ", NickName='" + NickName + '\'' +
                ", Photo='" + Photo + '\'' +
                ", sex=" + sex +
                ", desc='" + desc + '\'' +
                ", age=" + age +
                ", birthday='" + birthday + '\'' +
                ", constellation='" + constellation + '\'' +
                ", happy='" + happy + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
