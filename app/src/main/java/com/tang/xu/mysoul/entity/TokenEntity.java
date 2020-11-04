package com.tang.xu.mysoul.entity;

public
class TokenEntity {

    private Integer code;
    private String userId;
    private String token;

    public TokenEntity(Integer code, String userId, String token) {
        this.code = code;
        this.userId = userId;
        this.token = token;
    }

    @Override
    public String toString() {
        return "TokenEntity{" +
                "code=" + code +
                ", userId='" + userId + '\'' +
                ", token='" + token + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
