package com.tang.xu.formwork.bean;

public
class TestBean {

    private String msg;
    private String type;

    public TestBean(String msg, String type) {
        this.msg = msg;
        this.type = type;
    }

    public TestBean() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
