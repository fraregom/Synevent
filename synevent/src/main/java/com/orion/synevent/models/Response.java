package com.orion.synevent.models;

public class Response {
    private String token;
    private String msg;
    private User user;
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



    public InvitationBody getNewInvitation() {
        return newInvitation;
    }

    public void setNewInvitation(InvitationBody newInvitation) {
        this.newInvitation = newInvitation;
    }
}
