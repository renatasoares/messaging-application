package com.messaginapp.messaging_application.model;

public class Acception {
    private String id;
    private String identifierSender;
    private Boolean response;

    public Acception() {

    }

    public Acception(String identifierSender, Boolean response, String id) {
        this.identifierSender = identifierSender;
        this.response = response;
        this.id = id;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public String getIdentifierSender() {
        return identifierSender;
    }

    public void setIdentifierSender(String identifierSender) {
        this.identifierSender = identifierSender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
