package com.orion.synevent.models;

public class Invitations {

    private Integer id;
    private String name;
    private Integer author;
    private String finishBy;
    private Object finishAt;
    private Boolean state;
    private String shortId;
    private String createdAt;
    private String updatedAt;
    private UserInvitation UserInvitation;

    Invitations(Integer id,String name, Integer author, String shortId){
        this.id=id;
        this.name=name;
        this.author=author;
        this.shortId=shortId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getFinishBy() {
        return finishBy;
    }

    public void setFinishBy(String finishBy) {
        this.finishBy = finishBy;
    }

    public Object getFinishAt() {
        return finishAt;
    }

    public void setFinishAt(Object finishAt) {
        this.finishAt = finishAt;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getShortId() {
        return shortId;
    }

    public void setShortId(String shortId) {
        this.shortId = shortId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UserInvitation getUserInvitation() {
        return UserInvitation;
    }

    public void setUserInvitation(UserInvitation userInvitation) {
        this.UserInvitation = userInvitation;
    }

}
