package com.tang.xu.mysoul.entity;

import com.flyco.tablayout.listener.CustomTabEntity;

public class CommonEntity implements CustomTabEntity {

    private String msg;
    private int select;
    private int unselect;

    public CommonEntity(String msg, int select, int unselect) {
        this.msg = msg;
        this.select = select;
        this.unselect = unselect;
    }

    @Override
    public String getTabTitle() {
        return msg;
    }

    @Override
    public int getTabSelectedIcon() {
        return select;
    }

    @Override
    public int getTabUnselectedIcon() {
        return unselect;
    }
}
