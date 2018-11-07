package com.orion.synevent.models;

public class Participants {
    private Integer id;
    private String userName;
    private String email;
    private Object createdAt;
    private Object updatedAt;
    private UserInvitation UserInvitation;

    public void setUserInvitation(com.orion.synevent.models.UserInvitation userInvitation) {
        UserInvitation = userInvitation;
    }

    public Integer getId() {
        return id;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public String getEmail() {
        return email;
    }

    public String getUserName() {
        return userName;
    }

    public com.orion.synevent.models.UserInvitation getUserInvitation() {
        return UserInvitation;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
