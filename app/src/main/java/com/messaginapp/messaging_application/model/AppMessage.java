package com.messaginapp.messaging_application.model;

public class AppMessage {

    private String bodyMessage;
    private String username;
    private String photoUrl;
    private String videoUrl;
    private String audioUrl;
    private String idUser1;
    private String idUser2;

    public AppMessage() {
    }

    public AppMessage(String idUser1, String idUser2 , String bodyMessage, String username, String photoUrl, String videoUrl, String audioUrl) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.bodyMessage = bodyMessage;
        this.username = username;
        this.photoUrl = photoUrl;
        this.videoUrl = videoUrl;
        this.audioUrl = audioUrl;
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

    private void setUsername(String username) {
        this.username = username;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }
}
