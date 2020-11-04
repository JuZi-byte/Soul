package com.tang.xu.formwork.entity;

import com.tang.xu.formwork.bean.UserBean;

import cn.bmob.v3.BmobObject;

public class FriendEntity extends BmobObject {
    private UserBean me;
    private UserBean friend;

    public FriendEntity(UserBean me, UserBean friend) {
        this.me = me;
        this.friend = friend;
    }

    public FriendEntity(String tableName, UserBean me, UserBean friend) {
        super(tableName);
        this.me = me;
        this.friend = friend;
    }

    public FriendEntity() {
    }

    public UserBean getMe() {
        return me;
    }

    public void setMe(UserBean me) {
        this.me = me;
    }

    public UserBean getFriend() {
        return friend;
    }

    public void setFriend(UserBean friend) {
        this.friend = friend;
    }
}
