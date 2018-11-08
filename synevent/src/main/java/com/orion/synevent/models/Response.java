package com.orion.synevent.models;

public class Response {
    private String token;
    private String msg;
    private User user;
    private Boolean deleted;
    private InvitationBody newInvitation;

    public String getToken() {
        return token;
    }
    public String getMsg() {
        return msg;
    }
    public User getUser() {
        return user;
    }
    public Boolean isDeleted() {
        return deleted;
    }

    public InvitationBody getNewInvitation() {
        return newInvitation;
    }

    public void setNewInvitation(InvitationBody newInvitation) {
        this.newInvitation = newInvitation;
    }
}
