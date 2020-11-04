package com.tang.xu.mysoul.entity;

public class UserInfoEntity {

    private int color;
    private String title;
    private String content;


    public UserInfoEntity(int color, String title, String content) {
        this.color = color;
        this.title = title;
        this.content = content;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
