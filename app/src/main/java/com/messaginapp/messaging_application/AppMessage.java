package com.messaginapp.messaging_application;

public class AppMessage {

    private String bodyMessage;
    private String username;
    private String photoUrl;
    private String videoUrl;
    private String audioUrl;

    public AppMessage() {
    }

    public AppMessage(String bodyMessage, String username, String photoUrl, String videoUrl, String audioUrl) {
        this.bodyMessage = bodyMessage;
        this.username = username;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
    }

    public String getBodyMessage() {
        return bodyMessage;
    }

    private void setBodyMessage(String bodyMessage) {
        this.bodyMessage = bodyMessage;
    }

    public String getUsername() {
        return username;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    private void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
