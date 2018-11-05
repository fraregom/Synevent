package com.orion.synevent.models;

public class UserInvitation {

    private Integer userId;
    private Integer invitationId;
    private Integer InvitationId;

    /*
    public UserInvitation(Integer userId, Integer invitationId){
        this.userId = userId;
        this.invitationId = invitationId;
    }*/

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInvitationId() {
        return invitationId;
    }

    public void setInvitationId(Integer invitationId) {
        this.invitationId = invitationId;
    }

}
