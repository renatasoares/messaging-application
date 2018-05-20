package com.messaginapp.messaging_application.model;

public class Acception {
    public Acception(String identifierSender) {
        this.identifierSender = identifierSender;
    }

    public Acception() {

    }

    public String getIdentifierSender() {
        return identifierSender;
    }

    public void setIdentifierSender(String identifierSender) {
        this.identifierSender = identifierSender;
    }

    private String identifierSender;
}
