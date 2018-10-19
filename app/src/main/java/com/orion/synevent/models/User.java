package com.orion.synevent.models;

public class User {

    private Integer id;
    private String userName;
    private String email;
    private String password;
    private String updatedAt;
    private String createdAt;
    private String token;
    private Integer iat;
    private Integer exp;

    public User(String username, String email, String password) {
        this.userName = username;
        this.email = email;
        this.password = password;
    }

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getEmail() {
        return email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getIat() {
        return iat;
    }

    public Integer getExp() {
        return exp;
    }
}

