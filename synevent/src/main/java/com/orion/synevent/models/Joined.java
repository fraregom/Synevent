package com.orion.synevent.models;

public class Joined {
    private Integer userId;
    private Integer InvitationId;

    public void setInvitationId(Integer invitationId) {
        InvitationId = invitationId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getInvitationId() {
        return InvitationId;
    }

    public Integer getUserId() {
        return userId;
    }
}
