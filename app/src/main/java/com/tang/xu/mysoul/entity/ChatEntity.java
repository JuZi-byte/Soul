package com.tang.xu.mysoul.entity;

import java.io.File;

public class ChatEntity {

    //类型
    private int type;
    //文本
    private String text;
    //图片
    private String image;

    private File fileload;

    private double Ijin;
    private double Iwei;
    private String Iaddress;
    private String mapUrl;

    public double getIjin() {
        return Ijin;
    }

    public void setIjin(double ijin) {
        Ijin = ijin;
    }

    public double getIwei() {
        return Iwei;
    }

    public void setIwei(double iwei) {
        Iwei = iwei;
    }

    public String getIaddress() {
        return Iaddress;
    }

    public void setIaddress(String iaddress) {
        Iaddress = iaddress;
    }

    public String getMapUrl() {
        return mapUrl;
    }

    public void setMapUrl(String mapUrl) {
        this.mapUrl = mapUrl;
    }

    public File getFileload() {
        return fileload;
    }

    public void setFileload(File fileload) {
        this.fileload = fileload;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
