package com.messaginapp.messaging_application;

public class AppMessage {

    private String bodyMessage;
    private String username;
    private String photoUrl;

    public AppMessage() {
    }

    public AppMessage(String bodyMessage, String username, String photoUrl) {
        this.bodyMessage = bodyMessage;
        this.username = username;
        this.photoUrl = photoUrl;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    public void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
